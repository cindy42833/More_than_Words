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

    private static final String DB_Name = "words.db";
    private static final int DB_Version = 1;
    Context context;

    public FindWord(Context context) {
        this.context = context;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected String findTranslation(String word) {
        if(word != null) {
            DBHelper dbHelper = new DBHelper(context, DB_Name, DB_Version);

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
            String lowerWord = word.toLowerCase();
            Cursor cursor;
            try {
                cursor = db.rawQuery("SELECT * FROM words where eng=?", new String [] {lowerWord});
            } catch (SQLException e) {
                throw new Error("SQL find exception");
            }

            if(cursor.moveToFirst()) {
                return cursor.getString(2);
            }
        }
        return null;
    }

    protected Bitmap findImage(String word) {

        DBHelper dbHelper = new DBHelper(context, DB_Name, DB_Version);

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
        String lowerWord = word.toLowerCase();
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * FROM words where eng=?", new String [] {lowerWord});
        } catch (SQLException e) {
            throw new Error("SQL find exception");
        }
        String imageName = "unknown", imageExtensionName = "";
        String extStorageDirectory = context.getExternalFilesDir("Images").toString();

        if(!cursor.moveToFirst()) {
            imageExtensionName = "unknown.png";
        }
        else {
            imageExtensionName = cursor.getString(3);
            if(imageExtensionName.equals("0")) {
                imageExtensionName = "unknown.png";
            }
            else {
                imageName = imageExtensionName.split("\\.")[0];
            }
        }

        File file = new File(extStorageDirectory + '/' + imageExtensionName);
        Log.i("PATH", extStorageDirectory);
        if (!file.exists()) {
            try {
                Bitmap bm = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(imageName, "drawable", context.getPackageName()));
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

        String path = extStorageDirectory + '/' + imageExtensionName;
        return BitmapFactory.decodeFile(path);
    }
}
