package edu.sfsu.cs.orange.ocr;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FindWord extends Activity {

    private static final String DB_Name = "test.db";
    private static final int DB_Version = 1;
    private static final String Table_Name = "dictionaries";
    Context context;

    public FindWord(Context context) {
        this.context = context;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        dbHelper = new DBHelper(context, DB_Name, DB_Version);
//        try {
//            dbHelper.openDB();
//        } catch (SQLException sqle) {
//            try {
//                dbHelper.copyDB();
//            } catch (IOException e) {
//                throw new Error("Error copying database");
//            }
//        }
    }

    protected String findTranslation(String word) {
        DBHelper dbHelper = new DBHelper(context, DB_Name, DB_Version);
        if(word != null) {
            try {
                dbHelper.openDB();
            } catch (SQLException sqle) {
                try {
                    dbHelper.copyDB();
                } catch (IOException e) {
                    throw new Error("Error copying database");
                }
            }
            SQLiteDatabase db;
            db = dbHelper.openDB();
            Cursor cursor;
            try {
                cursor = db.rawQuery("SELECT * FROM dictionaries where name=?", new String [] {word});
            } catch (SQLException e) {
                throw new Error("SQL find exception");
            }

            if(cursor.moveToFirst()) {
                return cursor.getString(1);
            }
        }
        return null;
    }

    protected Bitmap findImage(String word) {
        DBHelper dbHelper = new DBHelper(context, DB_Name, DB_Version);
        SQLiteDatabase db;
        db = dbHelper.openDB();

        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * FROM dictionaries where name=?", new String [] {word});
        } catch (SQLException e) {
            throw new Error("SQL find exception");
        }

        if(cursor.moveToFirst()) {
            String imageName, translation;
            String extStorageDirectory = context.getExternalFilesDir("Images").toString();
            cursor.moveToFirst();
            translation = cursor.getString(1);
            imageName = cursor.getString(2);
            File file = new File(extStorageDirectory + '/' + imageName);
            Log.i("PATH", extStorageDirectory);
            if (!file.exists()) {
                try {
                    Bitmap bm = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(translation, "drawable", context.getPackageName()));
                    FileOutputStream outStream = new FileOutputStream(file);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);

                    try {
                        outStream.flush();
                        outStream.close();
                    } catch (IOException e) {
                        throw new Error("Error flush IO");
                    }

                } catch (FileNotFoundException ex) {
                    throw new Error("FileNotFoundException");
                }
            } else {
                Log.i("Exist", "file exist");
            }


            String path = extStorageDirectory + '/' + imageName;
//        Bitmap im = BitmapFactory.decodeFile(path);
            return BitmapFactory.decodeFile(path);
        }
        return null;
    }
}
