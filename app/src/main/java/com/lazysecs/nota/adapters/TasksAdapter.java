package com.lazysecs.nota.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.lazysecs.nota.R;
import com.lazysecs.nota.activities.UpdateTaskActivity;
import com.lazysecs.nota.models.Task;
import com.lazysecs.nota.room.TaskDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> implements Filterable {
    private List<Task> tasks;
    private List<Task> tasksFull;
    private Context context;

    private Filter tasksFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Task> filteredTasks = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredTasks.addAll(tasksFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Task task : tasksFull) {
                    if (task.getTitle().toLowerCase().contains(filterPattern))
                        filteredTasks.add(task);
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredTasks;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tasks.clear();
            tasks.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public TasksAdapter(List<Task> tasks, Context context) {
        this.tasks = tasks;
        this.tasksFull = new ArrayList<>(tasks);
        this.context = context;
    }

    @Override
    public Filter getFilter() {
        return tasksFilter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Task task = tasks.get(position);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(task.getDate());

        holder.textTitle.setText(task.getTitle());
        holder.textDescription.setText(task.getDescription());
        holder.textDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR));
        holder.textTime.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
        holder.checkStatus.setChecked(task.isStatus());
        strikeText(task.isStatus(), holder);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateTaskActivity.class);
            intent.putExtra("EXTRA_TASK_ID", task.getId());
            context.startActivity(intent);
        });

        holder.checkStatus.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            task.setStatus(checked);
            strikeText(checked, holder);

            TaskDatabase.getInstance(context).getTaskDao().update(task);
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private void strikeText(boolean checked, ViewHolder holder) {
        if (checked)
            holder.textTitle.setPaintFlags(holder.textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else
            holder.textTitle.setPaintFlags(0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_title)
        TextView textTitle;

        @BindView(R.id.text_description)
        TextView textDescription;

        @BindView(R.id.text_date)
        TextView textDate;

        @BindView(R.id.text_time)
        TextView textTime;

        @BindView(R.id.check_status)
        CheckBox checkStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
