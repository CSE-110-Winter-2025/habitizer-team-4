package edu.ucsd.cse110.habitizer.app.ui.tasklist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.ui.routinelist.RoutineListFragment;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.dialog.ConfirmInitializeRoutineFragment;
import edu.ucsd.cse110.habitizer.app.ui.tasklist.dialog.GoalTimeDialogFragment;
import edu.ucsd.cse110.habitizer.lib.domain.MockElapsedTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentTaskListBinding;

public class TaskListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentTaskListBinding view;
    private TaskListAdapter adapter;

    public TaskListFragment() {}

    public static TaskListFragment newInstance() {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        this.adapter = new TaskListAdapter(requireContext(), List.of(), activityModel);

        activityModel.loadTaskList().observe(tasks -> {
            if (tasks == null || tasks.size() == 0) {
                activityModel.updateIsDone(true);
                return;
            }
            adapter.clear();
            adapter.addAll(new ArrayList<>(tasks));
            adapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentTaskListBinding.inflate(inflater, container, false);
        view.taskList.setAdapter(adapter);

        activityModel.getCurrentRoutine().observe(routine -> {
            if (routine == null) return;
            view.routineText.setText(routine.title() + " Routine");
        });

        // Bind routine_updating_timer to elapsed time from MainViewModel
        activityModel.getCurrentRoutine().observe(routine -> {
            int seconds = routine.routineElapsedTime();
            view.routineUpdatingTimer.setText(activityModel.getRoundedDownTime(seconds));
        });

        // Pause Button functionality
        // For Resume and Pause I know you have to use R and add it to string xml but couldn't get it to work
        view.routinePauseTimeButton.setOnClickListener(v -> {
            if (activityModel.getRoutineTimer() instanceof MockElapsedTimer) {
                MockElapsedTimer routineTimer = (MockElapsedTimer) activityModel.getRoutineTimer();
                MockElapsedTimer taskTimer = (MockElapsedTimer) activityModel.getTaskTimer();
                if (routineTimer.isRunning()) {
                    routineTimer.pauseTimer();
                    taskTimer.pauseTimer();
                    view.routinePauseTimeButton.setText("Resume");
                } else {
                    routineTimer.resumeTimer();
                    taskTimer.resumeTimer();
                    view.routinePauseTimeButton.setText("Pause");
                }
            }
        });

        // Add Elapse Time Button functionality
        view.routineAdd30SecButton.setOnClickListener(v -> {
            activityModel.advanceRoutineTimer();
            activityModel.advanceTaskTimer();
        });

        // Add Goal Time Button functionality
        view.routineTotalTimeButton.setOnClickListener(v -> {
            var dialogFragment = GoalTimeDialogFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "GoalTimeDialogFragment");
        });

        activityModel.getGoalTime().observe(time -> {
            view.routineTotalTime.setText(time); // Updates UI dynamically
        });

        // End Routine Button functionality
        view.endRoutineButton.setOnClickListener(v -> {
            activityModel.updateIsDone(true); // Mark a routine as done
        });

        //When routine is marked as done, disable button.
        activityModel.getIsRoutineDone().observe(isTaskDone -> {
            if (isTaskDone) {
                activityModel.endRoutine(); // Ends routine and stop timers
                view.endRoutineButton.setText("Routine Ended"); // Updates button text
                view.endRoutineButton.setEnabled(false); // Disables button to prevent multiple presses
                view.routinePauseTimeButton.setEnabled(false);
                view.routineAdd30SecButton.setEnabled(false);
            }
        });

        view.backButton.setOnClickListener(v -> {
            var dialogFragment = ConfirmInitializeRoutineFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "ConfirmInitializeRoutineFragment");
        });

        activityModel.loadTaskList().observe(tasks -> {
            if (tasks == null) return;
            if (tasks.size() == 0) {
                activityModel.updateIsDone(true);
            }

            adapter.clear();
            adapter.addAll(new ArrayList<>(tasks));
            adapter.notifyDataSetChanged();
        });

        return view.getRoot();
    }

}