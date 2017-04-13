package com.business.peter.shoeseller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Victor on 06/07/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "internal_user";

    // Login table name
    private static final String TABLE_LOGIN = "login";
    private static final String TABLE_TYPES = "types";
    private static final String TABLE_SIZES = "sizes";

    // Login Table Columns names
    private static final String KEY_ID = "id";

    private static final String KEY_USERNAME = "uname";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_UID = "uid";
    private static final String KEY_PLAIN_ID = "plain_id";
    private static final String KEY_CREATED_AT = "created_at";

    private static final String KEY_TYPE_NAME = "tname";

    private static final String KEY_SHOE_SIZE = "size";
    private static final String KEY_USER_ID = "uid";
    private static final String KEY_SHOE_ID = "sid";

    private static final String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USERNAME + " TEXT,"
            + KEY_PHONE + " TEXT,"
            + KEY_UID + " TEXT,"
            + KEY_PLAIN_ID + " TEXT,"
            + KEY_CREATED_AT + " TEXT" + ")";
    private static final String CREATE_TYPE_TABLE = "CREATE TABLE " + TABLE_TYPES + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_TYPE_NAME + " TEXT" + ")";
    private static final String CREATE_SIZES_TABLE = "CREATE TABLE " + TABLE_SIZES + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USER_ID + " TEXT,"
            + KEY_SHOE_ID + " TEXT,"
            + KEY_SHOE_SIZE + " TEXT" + ")";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_TYPE_TABLE);
        db.execSQL(CREATE_SIZES_TABLE);
    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIZES);
        // Create tables again
        onCreate(db);
    }
    /**
     * Storing user details in database
     * */
    public void addUser( String phone,String uname, String uid, String plain_id, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_USERNAME, uname); // UserName
        values.put(KEY_PHONE, phone); // Phone
        values.put(KEY_UID, uid); // random Id
        values.put(KEY_PLAIN_ID, plain_id); // id
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection

    }
    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("uname", cursor.getString(1));
            user.put("phone", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("plain_id", cursor.getString(4));
            user.put("created_at", cursor.getString(5));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }
    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Storing user details in database
     * */
    public void addType(String tname) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        db.beginTransaction();
        try {
            values.put(KEY_TYPE_NAME, tname); // UserName
             // Inserting Row
            db.insert(TABLE_TYPES, null, values);
            db.setTransactionSuccessful();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close(); // Closing database connection

        }
    }

    /**
     * Storing user details in database
     * */
    public void addSize(String size,String sid,String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        db.beginTransaction();
        try {
            values.put(KEY_USER_ID, uid); // size
            values.put(KEY_SHOE_ID, sid); // size
            values.put(KEY_SHOE_SIZE, size); // size
            // Inserting Row
            db.insert(TABLE_SIZES, null, values);
            db.setTransactionSuccessful();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close(); // Closing database connection

        }
    }

    public ArrayList <String> getAllTypes(){
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + TABLE_TYPES;
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("tname"));
                    list.add(name);
                }
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return list;
    }

    public ArrayList <String> getSizes(String uid, String sid){
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + TABLE_SIZES + " WHERE "+KEY_SHOE_ID+ "= '" +sid+ "'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String sz = cursor.getString(cursor.getColumnIndex("size"));
                    list.add(sz);
                }
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return list;
    }
    public ArrayList <String> getDeleteSizes(String sid){
        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + TABLE_SIZES + " WHERE "+KEY_SHOE_ID+ "= '" +sid+ "'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    list.add(id);
                }
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return list;
    }

    public String get_size_id(String size, String sid){
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            String size_id = "";
            String selectQuery = "SELECT * FROM " + TABLE_SIZES + " WHERE "  +KEY_SHOE_SIZE+ " = '" + size +"' AND " +KEY_SHOE_ID+ "= '" +sid+ "'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                    size_id = cursor.getString(cursor.getColumnIndex("id"));
                    }

            db.setTransactionSuccessful();
            return size_id;

        }

        finally {
            db.endTransaction();
            db.close();
        }
    }


    public Integer delete_size(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SIZES, "id = ?", new String[] {id});
    }

    public Integer delete_type(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TYPES, "id = ?", new String[] {id});
    }

    /**
     * Re crate database
     * Delete all tables and create them again
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
    }
}
