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
    private MainViewModel activityModel;
    public TaskListAdapter(Context context, List<RoutineTask> tasks, MainViewModel activityModel) {
        super(context, 0, new ArrayList<>(tasks));
        this.activityModel = activityModel;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
            binding.taskCheck.setAlpha(255);
        } else {
            binding.taskCheck.setAlpha(0);
        }

        // When task is clicked, the hidden button is clicked
        // and the events for checking off task are called
        binding.taskButton.setOnClickListener(v -> {
            activityModel.checkOffTask(task);
        });

        activityModel.getIsRoutineDone().observe(isRoutineDone -> {
            if (isRoutineDone) {
                binding.taskButton.setEnabled(false);
            }
        });

        binding.taskTime.setText(activityModel.getRoundedDownTime(task.elapsedTime()));

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