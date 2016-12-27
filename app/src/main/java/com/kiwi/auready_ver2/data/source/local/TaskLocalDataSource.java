package com.kiwi.auready_ver2.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kiwi.auready_ver2.data.Task;
import com.kiwi.auready_ver2.data.source.TaskDataSource;
import com.kiwi.auready_ver2.data.source.local.PersistenceContract.DBExceptionTag;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry.COLUMN_COMPLETED;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry.COLUMN_DESCRIPTION;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry.COLUMN_HEAD_ID;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry.COLUMN_ID;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry.COLUMN_MEMBER_ID;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry.COLUMN_ORDER;
import static com.kiwi.auready_ver2.data.source.local.PersistenceContract.TaskEntry.TABLE_NAME;

/**
 * Created by kiwi on 8/25/16.
 */
public class TaskLocalDataSource extends BaseDBAdapter
        implements TaskDataSource {

    private static TaskLocalDataSource INSTANCE;

    private TaskLocalDataSource(Context context) {
        open(context);
    }

    public static TaskLocalDataSource getInstance(@NonNull Context context) {
        checkNotNull(context);
        if (INSTANCE == null) {
            INSTANCE = new TaskLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void deleteTasks(@NonNull String taskHeadId, @NonNull String memberId) {

        String whereClause = COLUMN_HEAD_ID + " LIKE? AND " + COLUMN_MEMBER_ID + " LIKE?";
        String[] whereArgs = {taskHeadId, memberId};

        delete(whereClause, whereArgs);
    }

    @Override
    public void deleteTasks(@NonNull String taskHeadId) {

        String whereClause = COLUMN_HEAD_ID + " LIKE?";
        String[] whereArgs = {taskHeadId};

        delete(whereClause, whereArgs);
    }

    @Override
    public void deleteTask(@NonNull String id) {

        String whereClause = COLUMN_ID + " LIKE?";
        String[] whereArgs = {id};

        delete(whereClause, whereArgs);
    }

    @Override
    public void getTasks(@NonNull String taskHeadId, @NonNull String memberId, @NonNull LoadTasksCallback callback) {
        List<Task> tasks = new ArrayList<>(0);

        String[] columns = {
                COLUMN_ID,
                COLUMN_DESCRIPTION,
                COLUMN_COMPLETED,
                COLUMN_ORDER
        };

        String selection = COLUMN_HEAD_ID + " LIKE? AND " + COLUMN_MEMBER_ID + " LIKE?";
        String[] selectionArgs = {taskHeadId, memberId};

        Cursor c = sDb.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndex(COLUMN_ID));
                String description = c.getString(c.getColumnIndex(COLUMN_DESCRIPTION));
                boolean completed = (c.getInt(c.getColumnIndex(COLUMN_COMPLETED))) != 0;
                int order = c.getInt(c.getColumnIndex(COLUMN_ORDER));

                Task task = new Task(id, taskHeadId, memberId, description, completed, order);
                tasks.add(task);
            }
        }
        if (c != null) {
            c.close();
        }
        if (tasks.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(tasks);
        }
    }

    @Override
    public void getTasks(@NonNull String taskHeadId, @NonNull LoadTasksCallback callback) {
        List<Task> tasks = new ArrayList<>(0);

        String[] columns = {
                COLUMN_ID,
                COLUMN_DESCRIPTION,
                COLUMN_COMPLETED,
                COLUMN_ORDER
        };

        String selection = COLUMN_HEAD_ID + " LIKE?";
        String[] selectionArgs = {taskHeadId};

        Cursor c = sDb.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndex(COLUMN_ID));
                String memberId = c.getString(c.getColumnIndex(COLUMN_MEMBER_ID));
                String description = c.getString(c.getColumnIndex(COLUMN_DESCRIPTION));
                boolean completed = (c.getInt(c.getColumnIndex(COLUMN_COMPLETED))) != 0;
                int order = c.getInt(c.getColumnIndex(COLUMN_ORDER));

                Task task = new Task(id, taskHeadId, memberId, description, completed, order);
                tasks.add(task);
            }
        }
        if (c != null) {
            c.close();
        }
        if (tasks.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(tasks);
        }

    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);

        sDb.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, task.getId());
            values.put(COLUMN_HEAD_ID, task.getTaskHeadId());
            values.put(COLUMN_MEMBER_ID, task.getMemberId());
            values.put(COLUMN_DESCRIPTION, task.getDescription());
            values.put(COLUMN_COMPLETED, task.getCompleted());
            values.put(COLUMN_ORDER, task.getOrder());

            sDb.insert(PersistenceContract.TaskEntry.TABLE_NAME, null, values);
            sDb.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e(DBExceptionTag.TAG_SQLITE, "Error insert new one to (" + TABLE_NAME + "). ", e);
        } finally {
            sDb.endTransaction();
        }
    }

    private void delete(String whereClause, String[] whereArgs) {
        sDb.beginTransaction();
        try {
            sDb.delete(TABLE_NAME, whereClause, whereArgs);
            sDb.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(BaseDBAdapter.TAG, "Could not delete the column in ( " + DATABASE_NAME + "). ", e);
        } finally {
            sDb.endTransaction();
        }
    }
}
