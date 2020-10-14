package com.example.test.activities;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;
import com.example.test.models.Task;
import com.example.test.room.TaskDatabase;
import com.example.test.utils.CalendarHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class BaseTaskActivity extends AppCompatActivity {

    @BindView(R.id.edit_title)
    EditText editTitle;

    @BindView(R.id.edit_description)
    EditText editDescription;

    @BindView(R.id.edit_priority)
    EditText editPriority;

    @BindView(R.id.edit_date)
    EditText editDate;

    @BindView(R.id.edit_time)
    EditText editTime;

    TaskDatabase taskDatabase;
    Task task;

    String title;
    String description;
    String date;
    String time;
    String priority;

    String pattern = "dd-M-yyyy hh:mm";

    abstract void makeTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_task);
        ButterKnife.bind(this);
        createNotificationChannel();

        // Init Database
        taskDatabase = TaskDatabase.getInstance(this);
    }

    long getMilliseconds() throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        String dateString = editDate.getText().toString() + " " + editTime.getText().toString();
        Date date = sdf.parse(dateString);

        return date != null ? date.getTime() : 0;
    }

    @OnClick(R.id.edit_date)
    void setDate() {
        CalendarHandler.setDate(this, editDate);
    }

    @OnClick(R.id.edit_time)
    void setTime() {
        CalendarHandler.setTime(this, editTime);
    }

    boolean isFilled() {
        title = editTitle.getText().toString();
        description = editDescription.getText().toString();
        date = editDate.getText().toString();
        time = editTime.getText().toString();
        priority = editPriority.getText().toString();

        if (title.matches("") || description.matches("") || date.matches("") ||
                time.matches("") || priority.matches("")) {
            Toast.makeText(this, "Provide all the data", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }

    void setResult() {
        Toast.makeText(this, "Task has been saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            CharSequence name = "channel";
            NotificationChannel channel = new NotificationChannel("taskExpired", name, importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}