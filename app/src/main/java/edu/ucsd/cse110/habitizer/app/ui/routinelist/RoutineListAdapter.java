package edu.ucsd.cse110.habitizer.app.ui.routinelist;

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
import edu.ucsd.cse110.habitizer.app.databinding.ListItemRoutineBinding;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.TaskListFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.app.ui.editlist.EditListFragment;

public class RoutineListAdapter extends ArrayAdapter<Routine> {
    private MainViewModel activityModel; // main view model for changing routine name subject
    private FragmentActivity modelOwner; // model owner for swapping fragment
    public RoutineListAdapter(Context context, List<Routine> tasks, MainViewModel activityModel, FragmentActivity modelOwner) {
        super(context, 0, new ArrayList<>(tasks));
        this.activityModel = activityModel;
        this.modelOwner = modelOwner;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        var routine = getItem(position);
        assert routine != null;

        ListItemRoutineBinding binding;
        if (convertView != null) {
            binding = ListItemRoutineBinding.bind(convertView);
        } else {
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemRoutineBinding.inflate(layoutInflater, parent, false);
        }

        // set a title
        binding.routineFrontText.setText(routine.title() + " Routine");

        // swap fragment when click the element
        binding.routineButton.setOnClickListener(v -> {
            activityModel.updateInProgressRoutine(routine, true);
            activityModel.startRoutine();
            this.modelOwner.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, TaskListFragment.newInstance())
                    .commit();
        });

        //swaping to editing fragment when clicking the edit button
        binding.editRoutineButton.setOnClickListener(v -> {
            activityModel.updateInEditRoutine(routine, true);
            this.modelOwner.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, EditListFragment.newInstance())
                    .commit();
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