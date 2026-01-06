package com.messageapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.messageapp.models.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    private DatabaseHelper dbHelper;

    public MessageDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Insert a new message
    public long insertMessage(Message message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SENDER_ID, message.getSenderId());
        values.put(DatabaseHelper.COLUMN_RECEIVER_ID, message.getReceiverId());
        values.put(DatabaseHelper.COLUMN_CONTENT, message.getContent());
        values.put(DatabaseHelper.COLUMN_IS_READ, message.isRead() ? 1 : 0);

        long id = db.insert(DatabaseHelper.TABLE_MESSAGES, null, values);
        return id;
    }

    // Get conversation between two users
    public List<Message> getConversation(String userId1, String userId2) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Message> messages = new ArrayList<>();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_MESSAGES +
                " WHERE (" + DatabaseHelper.COLUMN_SENDER_ID + " = ? AND " + DatabaseHelper.COLUMN_RECEIVER_ID + " = ?)" +
                " OR (" + DatabaseHelper.COLUMN_SENDER_ID + " = ? AND " + DatabaseHelper.COLUMN_RECEIVER_ID + " = ?)" +
                " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP + " ASC";

        String[] selectionArgs = {userId1, userId2, userId2, userId1};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                messages.add(cursorToMessage(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return messages;
    }

    // Get all conversations for a user (last message from each conversation)
    public List<Message> getRecentConversations(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Message> messages = new ArrayList<>();

        String query = "SELECT * FROM " + DatabaseHelper.TABLE_MESSAGES +
                " WHERE " + DatabaseHelper.COLUMN_SENDER_ID + " = ? OR " + DatabaseHelper.COLUMN_RECEIVER_ID + " = ?" +
                " GROUP BY CASE WHEN " + DatabaseHelper.COLUMN_SENDER_ID + " = ? THEN " + DatabaseHelper.COLUMN_RECEIVER_ID +
                " ELSE " + DatabaseHelper.COLUMN_SENDER_ID + " END" +
                " ORDER BY " + DatabaseHelper.COLUMN_TIMESTAMP + " DESC";

        String[] selectionArgs = {userId, userId, userId};

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                messages.add(cursorToMessage(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return messages;
    }

    // Mark messages as read
    public void markMessagesAsRead(String senderId, String receiverId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IS_READ, 1);

        String whereClause = DatabaseHelper.COLUMN_SENDER_ID + " = ? AND " + DatabaseHelper.COLUMN_RECEIVER_ID + " = ?";
        String[] whereArgs = {senderId, receiverId};

        db.update(DatabaseHelper.TABLE_MESSAGES, values, whereClause, whereArgs);
    }

    // Get unread message count for a user
    public int getUnreadMessageCount(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = 0;

        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_MESSAGES +
                " WHERE " + DatabaseHelper.COLUMN_RECEIVER_ID + " = ? AND " + DatabaseHelper.COLUMN_IS_READ + " = 0";

        Cursor cursor = db.rawQuery(query, new String[]{userId});

        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }

        return count;
    }

    // Helper method to convert cursor to Message object
    private Message cursorToMessage(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_ID);
        int senderIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_SENDER_ID);
        int receiverIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_RECEIVER_ID);
        int contentIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT);
        int timestampIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP);
        int isReadIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_READ);

        return new Message(
                cursor.getInt(idIndex),
                cursor.getString(senderIdIndex),
                cursor.getString(receiverIdIndex),
                cursor.getString(contentIndex),
                cursor.getString(timestampIndex),
                cursor.getInt(isReadIndex) == 1
        );
    }
}
