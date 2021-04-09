package com.dawabag.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE cart (id INTEGER PRIMARY KEY, productId INTEGER, productName TEXT, prize DOUBLE, image TEXT, qty INTEGER, total DOUBLE, unit TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS cart";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dawabag.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addRecord(ContentValues values) {
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert("cart", null, values);
    }

    public Cursor readRecords() {
        Cursor cursor = db.rawQuery("select * from cart", null);
        return cursor;
    }

    public Cursor readRecord(int productId) {
        Cursor cursor = db.rawQuery("select * from cart where productId="+productId, null);
        return cursor;
    }

    public void updateRecord(String query) {
        //db.execSQL("update cart set productName='veer' where productId=11");
        db.execSQL(query);
    }

    public void deleteRecord(String query) {
        //db.execSQL("update cart set productName='veer' where productId=11");
        db.execSQL(query);
    }

    public void clearRecords() {
        //Cursor cursor = db.rawQuery("delete from cart", null);
        db.delete("cart", null, null);
    }

    public void openDb() {
        db = getWritableDatabase();
    }

    public void closeDb() {
        close();
    }


}
