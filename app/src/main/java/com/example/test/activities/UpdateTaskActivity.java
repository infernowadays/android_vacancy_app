package com.example.test.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.example.test.R;
import com.example.test.models.Task;
import com.example.test.services.AlarmReceiver;

import java.lang.ref.WeakReference;
import java.text.ParseException;

import butterknife.OnClick;

public class UpdateTaskActivity extends BaseTaskActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set EditText Views
        loadTask();
    }

    @Override
    void makeTask() {
        task.setTitle(title);
        task.setDescription(description);
        try {
            task.setDate(getMilliseconds());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        task.setPriority(Integer.parseInt(priority));

        Intent intent = new Intent(UpdateTaskActivity.this, AlarmReceiver.class);
        intent.putExtra("id", String.valueOf(task.getId()));
        intent.putExtra("title", task.getTitle());
        intent.putExtra("description", task.getDescription());

        PendingIntent old = PendingIntent.getBroadcast(getApplicationContext(), task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            // Remove old notification, because the date has changed
            alarmManager.cancel(old);

            // Setup notification with a new date
            PendingIntent pendingIntent = PendingIntent.getBroadcast(UpdateTaskActivity.this, task.getId(), intent, 0);

            // Run a new one alarm notification
            alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDate(), pendingIntent);
        }
    }

    private void loadTask() {
        Intent intent = getIntent();
        if (intent.hasExtra("EXTRA_TASK_ID")) {
            new RetrieveSingleTask(this, intent.getIntExtra("EXTRA_TASK_ID", 0)).execute();
        }
    }

    @OnClick(R.id.button_save)
    void update() {
        if (isFilled()) {
            makeTask();
            new UpdateTask(this, task).execute();
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setViews() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(task.getDate());

        editTitle.setText(task.getTitle());
        editDescription.setText(task.getDescription());
        editDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR));
        editTime.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        editPriority.setText(String.valueOf(task.getPriority()));
    }

    private static class RetrieveSingleTask extends AsyncTask<Void, Void, Task> {

        private WeakReference<UpdateTaskActivity> activityReference;
        private int id;

        // only retain a weak reference to the activity
        RetrieveSingleTask(UpdateTaskActivity context, int id) {
            activityReference = new WeakReference<>(context);
            this.id = id;
        }

        @Override
        protected Task doInBackground(Void... voids) {
            if (activityReference.get() != null)
                return activityReference.get().taskDatabase.getTaskDao().getTask(this.id);
            else
                return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Task task) {
            activityReference.get().task = task;
            activityReference.get().setViews();
        }
    }

    private static class UpdateTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<UpdateTaskActivity> activityReference;
        private Task task;

        UpdateTask(UpdateTaskActivity context, Task task) {
            activityReference = new WeakReference<>(context);
            this.task = task;
        }

        @Override
        protected Boolean doInBackground(Void... objects) {
            activityReference.get().taskDatabase.getTaskDao().update(task);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                activityReference.get().setResult();
            }
        }
    }
}