package com.lazysecs.nota.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.nota.R;
import com.lazysecs.nota.adapters.TasksAdapter;
import com.lazysecs.nota.models.Task;
import com.lazysecs.nota.room.TaskDatabase;
import com.lazysecs.nota.utils.Sort;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    TasksAdapter tasksAdapter;
    @BindView(R.id.floating_action_button)
    FloatingActionButton floatingActionButton;
    private TaskDatabase taskDatabase;
    private Menu menu;
    private List<Task> tasks;
    private ItemTouchHelper itemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initObjects();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new RetrieveTask(this).execute();
    }

    private void initObjects() {
        // Setup ButterKnife
        ButterKnife.bind(this);

        // Init Database
        taskDatabase = TaskDatabase.getInstance(this);

        // Load Tasks from Room
        new RetrieveTask(this).execute();

        // Setup RecyclerView
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && floatingActionButton.getVisibility() == View.VISIBLE) {
                    floatingActionButton.hide();
                } else if (dy < 0 && floatingActionButton.getVisibility() != View.VISIBLE) {
                    floatingActionButton.show();
                }
            }
        });

        // Drag & Drop
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int positionDragged = dragged.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();

                Collections.swap(tasks, positionDragged, positionTarget);
                tasksAdapter.notifyItemMoved(positionDragged, positionTarget);

                Task taskDragged = tasks.get(positionDragged);
                positionDragged = taskDragged.getPosition();

                Task taskTarget = tasks.get(positionTarget);
                positionTarget = taskTarget.getPosition();

                taskDragged.setPosition(positionTarget);
                taskDatabase.getTaskDao().update(taskDragged);

                taskTarget.setPosition(positionDragged);
                taskDatabase.getTaskDao().update(taskTarget);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
    }

    @OnClick(R.id.floating_action_button)
    void createTask() {
        Intent intent = new Intent(this, CreateTaskActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (tasks.size() == 0)
            return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.menu_filter_date:
                Sort.sortByDate(tasks);
                tasksAdapter.notifyDataSetChanged();

                return true;

            case R.id.menu_filter_position:
                Sort.sortByPosition(tasks);
                tasksAdapter.notifyDataSetChanged();

                return true;

            case R.id.menu_filter_status:
                Sort.sortByStatus(tasks);
                tasksAdapter.notifyDataSetChanged();

                return true;

            case R.id.menu_filter_title:
                Sort.sortByTitle(tasks);
                tasksAdapter.notifyDataSetChanged();

                return true;

            case R.id.menu_filter_priority:
                Sort.sortByPriority(tasks);
                tasksAdapter.notifyDataSetChanged();

                return true;

            case R.id.action_search:
                openSearchDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSearchDialog() {
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tasksAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private static class RetrieveTask extends AsyncTask<Void, Void, List<Task>> {

        private WeakReference<MainActivity> activityReference;

        // only retain a weak reference to the activity
        RetrieveTask(MainActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {
            if (activityReference.get() != null)
                return activityReference.get().taskDatabase.getTaskDao().getAll();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Task> tasks) {
            if (tasks != null && tasks.size() > 0) {
                activityReference.get().tasks = tasks;

                // Default sort by priority
                Sort.sortByPosition(tasks);

                // Create and set the adapter on RecyclerView instance to display list
                activityReference.get().tasksAdapter = new TasksAdapter(tasks, activityReference.get());
                activityReference.get().recyclerView.setAdapter(activityReference.get().tasksAdapter);
                activityReference.get().itemTouchHelper.attachToRecyclerView(activityReference.get().recyclerView);

            } else
                activityReference.get().tasks = new ArrayList<>();
        }
    }
}