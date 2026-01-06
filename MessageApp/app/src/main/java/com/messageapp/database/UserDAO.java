package com.messageapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.messageapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Insert a new user
    public long insertUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DEVICE_ID, user.getDeviceId());
        values.put(DatabaseHelper.COLUMN_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_EMAIL, user.getEmail());

        long id = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        return id;
    }

    // Get user by device ID
    public User getUserByDeviceId(String deviceId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        String selection = DatabaseHelper.COLUMN_DEVICE_ID + " = ?";
        String[] selectionArgs = {deviceId};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }

        return user;
    }

    // Get user by email
    public User getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;

        String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }

        return user;
    }

    // Search users by device ID (partial match)
    public List<User> searchUsersByDeviceId(String searchQuery) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<User> users = new ArrayList<>();

        String selection = DatabaseHelper.COLUMN_DEVICE_ID + " LIKE ?";
        String[] selectionArgs = {"%" + searchQuery + "%"};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                DatabaseHelper.COLUMN_NAME + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                users.add(cursorToUser(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return users;
    }

    // Get all users
    public List<User> getAllUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<User> users = new ArrayList<>();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_NAME + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                users.add(cursorToUser(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return users;
    }

    // Check if device ID exists
    public boolean deviceIdExists(String deviceId) {
        return getUserByDeviceId(deviceId) != null;
    }

    // Check if email exists
    public boolean emailExists(String email) {
        return getUserByEmail(email) != null;
    }

    // Helper method to convert cursor to User object
    private User cursorToUser(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID);
        int deviceIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DEVICE_ID);
        int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME);
        int emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);
        int createdAtIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT);

        return new User(
                cursor.getInt(idIndex),
                cursor.getString(deviceIdIndex),
                cursor.getString(nameIndex),
                cursor.getString(emailIndex),
                cursor.getString(createdAtIndex)
        );
    }
}
