package com.lazysecs.nota.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.lazysecs.nota.models.Task;
import com.lazysecs.nota.utils.Constants;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM " + Constants.TABLE_NAME)
    List<Task> getAll();

    @Query("SELECT * FROM " + Constants.TABLE_NAME + " WHERE id=:id ")
    Task getTask(int id);

    @Query("SELECT * FROM " + Constants.TABLE_NAME + " ORDER BY id DESC LIMIT 0, 1")
    Task getMaxId();

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM " + Constants.TABLE_NAME)
    void clear();
}
