// UserDatabaseManager.java
package com.example.charger_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDatabaseManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public UserDatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long registerUser(String username, String password) {
        open(); // Ensure the database is open before performing operations

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, username);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);

        long result = database.insert(DatabaseHelper.TABLE_USERS, null, values);

        close(); // Close the database after operations
        return result;
    }

    public boolean checkUser(String username, String password) {
        open(); // Ensure the database is open before performing operations

        String[] columns = { DatabaseHelper.COLUMN_ID };
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        close(); // Close the database after operations

        return cursorCount > 0;
    }
}
