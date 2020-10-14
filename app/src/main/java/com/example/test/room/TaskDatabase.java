package com.example.test.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.test.utils.Constants;
import com.example.test.models.Task;

@Database(entities = Task.class, version = 1)
public abstract class TaskDatabase extends RoomDatabase {

    private static TaskDatabase taskDB;

    public static TaskDatabase getInstance(Context context) {
        if (null == taskDB) {
            taskDB = buildDatabaseInstance(context);
        }
        return taskDB;
    }

    private static TaskDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                TaskDatabase.class,
                Constants.DB_NAME)
                .allowMainThreadQueries().build();
    }

    public abstract TaskDao getTaskDao();

    public void cleanUp() {
        taskDB = null;
    }
}
