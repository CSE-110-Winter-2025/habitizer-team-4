package edu.ucsd.cse110.habitizer.app.ui.routinelist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentRoutineListBinding;
import edu.ucsd.cse110.habitizer.app.ui.editlist.EditListFragment;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.TaskListFragment;

public class RoutineListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentActivity modelOwner;
    private RoutineListAdapter adapter; // adapter for ListView
    private FragmentRoutineListBinding view;

    public RoutineListFragment() {}

    public static RoutineListFragment newInstance() {
        RoutineListFragment fragment = new RoutineListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);

        modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        this.adapter = new RoutineListAdapter(requireContext(), List.of(), activityModel, modelOwner);

        activityModel.loadRoutineList().observe(routines -> {
            if (routines == null) return;

            adapter.clear();
            adapter.addAll(new ArrayList<>(routines));
            adapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentRoutineListBinding.inflate(inflater, container, false);
        view.routineList.setAdapter(adapter);

        activityModel.getCurrentRoutine().observe(routine -> {
            if (activityModel.getIsFirstRun() && routine != null) {
                activityModel.setIsFirstRun();

                if (routine.isInProgress()) {

                    var routineTimer = activityModel.getRoutineTimer();
                    var taskTimer = activityModel.getTaskTimer();
                    routineTimer.setSeconds(routine.routineElapsedTime());
                    taskTimer.setSeconds(routine.taskElapsedTime());

                    routineTimer.resumeTimer();
                    taskTimer.resumeTimer();

                    activityModel.startTimerUpdates();

                    modelOwner.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, TaskListFragment.newInstance())
                            .commit();
                } else if (routine.isInEdit()) {
                    modelOwner.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, EditListFragment.newInstance())
                            .commit();
                }
            }
        });

        activityModel.loadRoutineList().observe(routines -> {
            if (routines == null) return;

            adapter.clear();
            adapter.addAll(new ArrayList<>(routines));
            adapter.notifyDataSetChanged();
        });

        return view.getRoot();

    }
}
