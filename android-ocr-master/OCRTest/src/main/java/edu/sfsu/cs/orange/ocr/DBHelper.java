package edu.sfsu.cs.orange.ocr;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {
    private static final String PACKAGE_NAME = "edu.sfsu.cs.orange.ocr"; //包名
    private static final String DB_PATH =  "/data" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/databases/";
    private static final String DB_NAME = "words";
    private static final String COMPLETE_PATH = DB_PATH + DB_NAME;
    private final Context context;

    public DBHelper(Context context, String DB_Name, int version) {
        super(context, DB_Name, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int i, int i1) {

    }

    public SQLiteDatabase openDB() throws SQLException {
        SQLiteDatabase db;
        db = SQLiteDatabase.openDatabase(COMPLETE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        Log.i("open DB......",db.toString());
        return db;
    }

    public void copyDB() throws IOException{
        try {
            File folder = new File(DB_PATH);
            if (!folder.exists()) {
                folder.mkdir();
            }
            InputStream ip =  context.getAssets().open(DB_NAME+".db");
            Log.i("Input Stream....",ip+"");
            OutputStream output = new FileOutputStream(COMPLETE_PATH);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = ip.read(buffer))>0){
                output.write(buffer, 0, length);
                Log.i("Content.... ",length+"");
            }
            output.flush();
            output.close();
            ip.close();
        }
        catch (IOException e) {
            Log.v("error", e.toString());
        }
    }
}
