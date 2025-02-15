package edu.ucsd.cse110.habitizer.app.ui.tasklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.ListItemTaskBinding;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineTask;

public class TaskListAdapter extends ArrayAdapter<RoutineTask> {
    private final MainViewModel activityModel;
    public TaskListAdapter(Context context, List<RoutineTask> tasks, MainViewModel activityModel) {
        super(context, 0, new ArrayList<>(tasks));
        this.activityModel = activityModel;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        var task = getItem(position);
        assert task != null;

        ListItemTaskBinding binding;
        if (convertView != null) {
            binding = ListItemTaskBinding.bind(convertView);
        } else {
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemTaskBinding.inflate(layoutInflater, parent, false);
        }

        // Set a tile of a task.
        binding.taskFrontText.setText(task.title());

        // This if-else block updates opacity of checkmark in UI
        // so that checkmark appears when clicked
        if (task.isChecked()) {
            binding.taskCheck.setImageAlpha(255);
        } else {
            binding.taskCheck.setImageAlpha(0);
        }

        // When task is clicked, the hidden button is clicked
        // and the events for checking off task are called
        binding.taskButton.setOnClickListener(v -> {
            var id = task.id();
            activityModel.checkOffTask(id);
        });

        // When the elapsed time changes for task, update task_view text view.
        activityModel.getTaskElapsedTime().observe(time -> {
            if (task.id() == activityModel.getCurrentTaskId()) {
                // sync with timer if task id is matched with current id.
                binding.taskTime.setText(time);
            } else {
                // sync with elapsed time field inside RoutineTask object
                // if it is already done or not yet started.
                binding.taskTime.setText(task.getElapsedTime());
            }
        });

        return binding.getRoot();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var task = getItem(position);
        assert task != null;

        var id = task.id();
        assert id != null;

        return id;
    }
}