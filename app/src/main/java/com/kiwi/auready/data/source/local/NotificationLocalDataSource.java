package com.kiwi.auready.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.kiwi.auready.data.Notification;
import com.kiwi.auready.data.source.NotificationDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready.data.source.local.PersistenceContract.DBExceptionTag;
import static com.kiwi.auready.data.source.local.PersistenceContract.NotificationEntry.COLUMN_FROM_USERID;
import static com.kiwi.auready.data.source.local.PersistenceContract.NotificationEntry.COLUMN_FROM_USERNAME;
import static com.kiwi.auready.data.source.local.PersistenceContract.NotificationEntry.COLUMN_ID;
import static com.kiwi.auready.data.source.local.PersistenceContract.NotificationEntry.COLUMN_MESSAGE;
import static com.kiwi.auready.data.source.local.PersistenceContract.NotificationEntry.COLUMN_TYPE;
import static com.kiwi.auready.data.source.local.PersistenceContract.NotificationEntry.COLUMN_iSNEW;
import static com.kiwi.auready.data.source.local.PersistenceContract.NotificationEntry.TABLE_NAME;

/**
 * Local Data Source of Notification
 */

public class NotificationLocalDataSource implements NotificationDataSource {

    private static NotificationLocalDataSource INSTANCE = null;
    private SQLiteDBHelper mDbHelper;

    private NotificationLocalDataSource(Context context) {
        mDbHelper = SQLiteDBHelper.getInstance(context);
    }

    public static NotificationLocalDataSource getInstance(@NonNull Context context) {
        checkNotNull(context);
        if (INSTANCE == null) {
            INSTANCE = new NotificationLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void saveNotification(@NonNull Notification notification, @NonNull SaveCallback callback) {
        checkNotNull(notification);
        checkNotNull(callback);

        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, notification.getType());
        values.put(COLUMN_iSNEW, notification.getIsNewInteger());
        values.put(COLUMN_FROM_USERID, notification.getFromUserId());
        values.put(COLUMN_FROM_USERNAME, notification.getFromUserName());
        values.put(COLUMN_MESSAGE, notification.getMessage());

        long isSuccess = mDbHelper.insert(TABLE_NAME, null, values);
        if (isSuccess != DBExceptionTag.INSERT_ERROR) {
            callback.onSaveSuccess();
        } else {
            callback.onSaveFailed();
        }
    }

    @Override
    public void loadNotifications(@NonNull LoadNotificationsCallback callback) {

        List<Notification> notifications = new ArrayList<>();

        Cursor c = mDbHelper.query(TABLE_NAME, null, null, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow(COLUMN_ID));
                int type = c.getInt(c.getColumnIndexOrThrow(COLUMN_TYPE));
                int isNew = c.getInt(c.getColumnIndexOrThrow(COLUMN_iSNEW));
                String fromUserId = c.getString(c.getColumnIndexOrThrow(COLUMN_FROM_USERID));
                String fromUserName = c.getString(c.getColumnIndexOrThrow(COLUMN_FROM_USERNAME));
                String message = c.getString(c.getColumnIndexOrThrow(COLUMN_MESSAGE));

                Notification notification = new Notification(id, type, isNew, fromUserId, fromUserName, message);
                notifications.add(notification);
            }
        }
        if (c != null) {
            c.close();
        }

        if (notifications.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onLoaded(notifications);
        }
    }

    @Override
    public void readNotification(@NonNull int id) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_iSNEW, 0);    // set isNew to false
        String selection = COLUMN_ID + " LIKE?";
        String[] selectionArgs = {String.valueOf(id)};
        db.update(TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void deleteNotification(@NonNull int id) {
        checkNotNull(id);

        String selection = COLUMN_ID + " LIKE?";
        String[] selectionArgs = {String.valueOf(id)};
        mDbHelper.delete(TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void getNotificationsCount(@NonNull GetCountCallback callback) {
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor c = mDbHelper.rawQuery(query, null);
        if(c!=null && c.getCount() > 0) {
            callback.onSuccessGetCount(c.getCount());
            c.close();
        } else {
            callback.onDataNotAvailable();
        }
    }

    /*
    * Used to force {@link #getInstance(Context)} to create a new instance
    * next time it's called.
    * */
    public static void destroyInstance() {
        INSTANCE = null;
    }


}
