package edu.ucsd.cse110.habitizer.app.ui.editlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.databinding.ListItemEditTaskBinding;
import edu.ucsd.cse110.habitizer.app.databinding.ListItemRoutineBinding;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.TaskListFragment;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.dialog.EditTaskNameDialogFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.app.ui.editlist.EditListFragment;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineTask;

public class EditListAdapter extends ArrayAdapter<RoutineTask> {
    private MainViewModel activityModel; // main view model for changing routine name subject
    private FragmentActivity modelOwner; // model owner for swapping fragment
    public EditListAdapter(Context context, List<RoutineTask> tasks, MainViewModel activityModel) {
        super(context, 0, new ArrayList<>(tasks));
        this.activityModel = activityModel;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        var task = getItem(position);
        assert task != null;

        ListItemEditTaskBinding binding;
        if (convertView != null) {
            binding = ListItemEditTaskBinding.bind(convertView);
        } else {
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemEditTaskBinding.inflate(layoutInflater, parent, false);
        }

        // Set a tile of a task.
        binding.taskFrontText.setText(task.title());

        binding.renameTaskButton.setOnClickListener(v -> {
            EditTaskNameDialogFragment dialog = EditTaskNameDialogFragment.newInstance(task.id(), task.title());
            dialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "EditTaskNameDialogFragment");
        });

        return binding.getRoot();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var routine = getItem(position);
        assert routine != null;

        var id = routine.id();

        return id;
    }
}