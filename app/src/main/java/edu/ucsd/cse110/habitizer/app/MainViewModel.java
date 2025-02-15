package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.ElapsedTimer;
import edu.ucsd.cse110.habitizer.lib.domain.MockElapsedTimer;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineTask;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class MainViewModel extends ViewModel {
    private final RoutineRepository routineRepository;
    private final Subject<List<RoutineTask>> taskList;
    private final Subject<List<Routine>> routineList;
    private int currentTaskId; // track the current task id (task the time is working)
    private final Subject<Boolean> isRoutineDone; // track if the routine is done.
    private Runnable currentRunner; // track runner for timer so that it can be stopped when checkoff.
    private String routineName;

    private final ElapsedTimer timer; // ElapsedTimer instance to track routine elapsed time
    private final Subject<String> elapsedTime;  // Subject to store and update elapsed time dynamically (Lab 5)
    private final ElapsedTimer taskTimer; // ElapsedTimer instance to track task elapsed time
    private final Subject<String> taskElapsedTime; // Subject to store and update task elapsed time dynamically (Lab 5)

    // Subject to store and update goal time
    private final Subject<String> goalTime;
    private final Handler timerHandler = new Handler(Looper.getMainLooper()); // Handler for periodic timer updates (Lab 4 )
  
    private static final long TIMER_INTERVAL_MS = 1000; // Updates every second

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getRoutineRepository());
                    });

    // Initialize elapsed time tracking and timer in constructor
    public MainViewModel(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
        this.taskList = new Subject<>();
        this.routineList = new Subject<>();

        this.isRoutineDone = new Subject<>(); // Initialize subject for checking the status for routine.

        this.timer = MockElapsedTimer.immediateTimer(); // Initialize MockElapsedTimer for testing
        this.elapsedTime = new Subject<>();  // Initialize elapsed time tracking
        this.taskTimer = MockElapsedTimer.immediateTimer(); // Initialize MockElapsedTimer for testing
        this.taskElapsedTime = new Subject<>();  // Initialize elapsed time tracking
        this.goalTime = new Subject<>(); // Initialize goal time tracking


        // Set initial values
        this.currentTaskId = 0; // Initialize the first task id as 0.
        this.routineName = "Morning";
        goalTime.setValue("60");

        taskList.setValue(routineRepository.getTaskList(this.routineName));
        routineList.setValue(routineRepository.getRoutineList());
        isRoutineDone.setValue(false);

        // Initialize timers.
        taskElapsedTime.setValue("00:00");
        elapsedTime.setValue("00:00");
    }

    public void startRoutine() {
        // Start updating elapsed time periodically
        timer.resetTimer();
        timer.startTimer();
        startTimerUpdates();

        // Start updating task elapsed time periodically
        taskTimer.resetTimer();
        taskTimer.startTimer();
        currentRunner = startTaskTimerUpdates();
    }

    // load a list of tasks
    public Subject<List<RoutineTask>> loadTaskList() {
        return taskList;
    }
    // load a list of routines
    public Subject<List<Routine>> loadRoutineList() {
        return routineList;
    }

    // set routine name
    public void setRoutineName(String name) {
        this.routineName = name;
        taskList.setValue(routineRepository.getTaskList(routineName));
    }

    public String getRoutineName() {
        return routineName;
    }

    // New Method: Add Task to Routine**
    public void addTaskToRoutine(String routineName, String taskName) {
        routineRepository.addTaskToRoutine(routineName, taskName);
        taskList.setValue(routineRepository.getTaskList(routineName)); // Refresh task list
    }

    // check off a task with id
    public void checkOffTask(int id) {
        // if you try to check off previous tasks or if the routine is already done,
        // it just return immediately since it is invalid.
        if ((id < currentTaskId) || isRoutineDone.getValue()) {
            return;
        }

        // Stop the timer for previous task.
        timerHandler.removeCallbacks(currentRunner);

        // Given id, find corresponding task and check it off
        var task = routineRepository.getTaskWithIdandName(this.routineName, id);
        task.checkOff(taskTimer.getTime());

        // Increment current task id by 1.
        int nextTaskId = id + 1;
        var nextTask = routineRepository.getTaskWithIdandName(this.routineName, nextTaskId);

        // Check if there is a next task remaining
        if (nextTask == null) {
            isRoutineDone.setValue(true);
        } else {
            // reset and restart timer
            taskTimer.resetTimer();
            taskTimer.startTimer();
            currentRunner = startTaskTimerUpdates();
        }

        // update current task id and task list
        currentTaskId = nextTaskId;
        taskList.setValue(routineRepository.getTaskList(this.routineName));
    }

    // Get the current task id
    public int getCurrentTaskId() {
        return currentTaskId;
    }

    // get if a routine is already done.
    public Subject<Boolean> getIsRoutineDone() {
        return isRoutineDone;
    }

    // Get Timer object
    public ElapsedTimer getTimer() {
        return this.timer;
    }
    public ElapsedTimer getTaskTimer() {
        return this.taskTimer;
    }

    // Expose elapsed time for UI updates
    public Subject<String> getElapsedTime() {
        return elapsedTime;
    }
    public Subject<String> getTaskElapsedTime() {
        return taskElapsedTime;
    }

    // Stop timer
    public void stopRoutineTimer() {
        timer.stopTimer();
        updateElapsedTime();
    }
    public void stopTaskTimer() {
        taskTimer.stopTimer();
        updateTaskElapsedTime();
    }

    // Manually advance routine timer by 30 seconds (For US3c)
    public void advanceRoutineTimer() {
        timer.advanceTimer();
        updateElapsedTime();
    }
    public void advanceTaskTimer() {
        taskTimer.advanceTimer();
        updateTaskElapsedTime();
    }

    // Periodically update elapsed time every second
    private void startTimerUpdates() {
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                elapsedTime.setValue(timer.getTime());
                timerHandler.postDelayed(this, TIMER_INTERVAL_MS);
            }
        }, 0);
    }
    public Runnable startTaskTimerUpdates() {
        var runner = new Runnable() {
            @Override
            public void run() {
                taskElapsedTime.setValue(taskTimer.getTime());
                timerHandler.postDelayed(this, TIMER_INTERVAL_MS);
            }
        };
        timerHandler.postDelayed(runner, 0);
        return runner;
    }

    // Helper function to update elapsed time for UI
    private void updateElapsedTime() {
        elapsedTime.setValue(timer.getTime());
    }
    private void updateTaskElapsedTime() {
        taskElapsedTime.setValue(taskTimer.getTime());
    }

    public void updateGoalTime(String time) {
        System.out.println("Time received: " + time);
        goalTime.setValue(time);
    }

    public Subject<String> getGoalTime() {
        return goalTime;
    }

    // Updates task name when in edit task dialog
    public void updateTaskName(String newTitle, int id) {
        var task = routineRepository.getTaskWithIdandName(this.routineName, id);
        if (task!= null){
            task.updateTitle(newTitle);
            taskList.setValue(routineRepository.getTaskList(this.routineName));
        }
    }

    // stop two timers when finishing routine
    public void endRoutine() {
        stopRoutineTimer();
        stopTaskTimer();
    }
}
