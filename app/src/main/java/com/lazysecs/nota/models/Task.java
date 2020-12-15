package com.lazysecs.nota.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.lazysecs.nota.utils.Constants;

@Entity(tableName = Constants.TABLE_NAME)
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private boolean status;
    private int priority;
    private long date;
    private int position;

    public Task(int id, String title, String description, boolean status, int priority, long date, int position) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.date = date;

        this.position = position == 0 ? id : position;
    }

    @Ignore
    public Task(String title, String description, boolean status, int priority, long date) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.date = date;
        this.position = 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int noteId) {
        this.id = noteId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
