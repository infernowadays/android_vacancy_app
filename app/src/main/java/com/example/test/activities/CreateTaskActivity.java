package com.example.test.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.test.R;
import com.example.test.models.Task;
import com.example.test.services.AlarmReceiver;

import java.lang.ref.WeakReference;
import java.text.ParseException;

import butterknife.OnClick;

public class CreateTaskActivity extends BaseTaskActivity {

    @OnClick(R.id.button_save)
    void save() {
        if (isFilled()) {
            makeTask();
            new InsertTask(this, task).execute();
        }
    }

    @Override
    void makeTask() {
        try {
            task = new Task(title, description, false, Integer.parseInt(priority), getMilliseconds());
            int maxId = 0;

            if(taskDatabase.getTaskDao().getMaxId() != null)
                maxId = taskDatabase.getTaskDao().getMaxId().getId() + 1;

            // Create Extras for detailed notification
            Intent intent = new Intent(CreateTaskActivity.this, AlarmReceiver.class);
            intent.putExtra("id", String.valueOf(maxId));
            intent.putExtra("title", task.getTitle());
            intent.putExtra("description", task.getDescription());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(CreateTaskActivity.this, maxId, intent, 0);

            // Run alarm notification
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDate(), pendingIntent);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static class InsertTask extends AsyncTask<Void, Void, Boolean> {

        private WeakReference<CreateTaskActivity> activityReference;
        private Task task;

        InsertTask(CreateTaskActivity context, Task task) {
            activityReference = new WeakReference<>(context);
            this.task = task;
        }

        @Override
        protected Boolean doInBackground(Void... objects) {
            activityReference.get().taskDatabase.getTaskDao().insert(task);
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