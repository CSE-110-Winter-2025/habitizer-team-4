package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.ucsd.cse110.habitizer.lib.domain.ElapsedTimer;
import edu.ucsd.cse110.habitizer.lib.domain.RegularTimer;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineTask;
import edu.ucsd.cse110.habitizer.lib.util.MutableSubject;
import edu.ucsd.cse110.habitizer.lib.util.SimpleSubject;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class MainViewModel extends ViewModel {
    private final RoutineRepository routineRepository;

    private MutableSubject<Routine> currentRoutine;
    private MutableSubject<List<Routine>> routineList;
    private MutableSubject<List<RoutineTask>> taskList;

    private Routine routine;
    private int numTasks;
    private MutableSubject<String> routineElapsedTime;
    private MutableSubject<String> taskElapsedTime;
    private MutableSubject<String> goalTime;
    private MutableSubject<Boolean> isRoutineDone;

    private boolean isFirstRun;
    private final ElapsedTimer routineTimer;
    private final ElapsedTimer taskTimer;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private static final long TIMER_INTERVAL_MS = 1000;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getRoutineRepository());
                    });

    public MainViewModel(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;

        routineList = new SimpleSubject<>();
        currentRoutine = new SimpleSubject<>();
        taskList = new SimpleSubject<>();

        routineElapsedTime = new SimpleSubject<>();
        taskElapsedTime = new SimpleSubject<>();
        goalTime = new SimpleSubject<>();
        isRoutineDone = new SimpleSubject<>();

        isFirstRun = true;

        this.routineTimer = new RegularTimer();
        this.taskTimer = new RegularTimer();

        routineRepository.findRoutineList().observe(routines -> {
            if (routines == null) return;
            routineList.setValue(routines);
        });

        routineList.observe(routines -> {
            if (routines == null) return;
            for (var routine : routines){
                routine.setTasks(routineRepository.findTaskList(routine.id()));
                numTasks += routine.tasks().size();
                if (routine.isInProgress() || routine.isInEdit()) {
                    currentRoutine.setValue(routine);
                }
            }
        });

        currentRoutine.observe(routine -> {
            if (routine == null) return;
            this.routine = routine;

            isRoutineDone.setValue(routine.isDone());
            goalTime.setValue(String.valueOf(routine.goalTime()));
            routineElapsedTime.setValue(routineTimer.getRoundedDownTime());
            taskElapsedTime.setValue(taskTimer.getRoundedDownTime());
            taskList.setValue(routine.tasks());
        });
    }

    public Subject<List<Routine>> loadRoutineList() {
        return routineList;
    }
    public MutableSubject<Routine> getCurrentRoutine() {
        return currentRoutine;
    }
    public Subject<List<RoutineTask>> loadTaskList() {
        return taskList;
    }

    public void saveRoutineTask(RoutineTask newTask) {
        var newTasks = new ArrayList<RoutineTask>();

        boolean duplicate = false;
        for (var task : this.routine.tasks()) {
            if (task.id() == newTask.id()) {
                newTasks.add(newTask);
                duplicate = true;
            } else {
                newTasks.add(task);
            }
        }
        if (!duplicate) {
            newTask.setId(numTasks + 1);
            newTask.setRoutineId(this.routine.id());
            newTasks.add(newTask);
        }
        this.routine.setTasks(newTasks);
        saveRoutine(this.routine);
    }
    public void saveRoutine(Routine routine) {
        routineRepository.saveRoutine(routine);
    }

    public boolean getIsFirstRun() {
        return isFirstRun;
    }
    public void setIsFirstRun() {
        isFirstRun = false;
    }

    public void checkOffTask(RoutineTask task) {
        if (!task.isChecked()) {
            task.checkOff(taskTimer.getSeconds());
            saveRoutineTask(task);

            if (checkIsRoutineDone()) {
                this.routine.setIsDone(true);
                saveRoutine(this.routine);
            }

            timerHandler.removeMessages(0);
            taskTimer.resetTimer();

            updateTime();

            taskTimer.startTimer();
            startTimerUpdates();
        }
    }

    public boolean checkIsRoutineDone() {
        boolean isDone = true;
        for (var task : this.routine.tasks()) {
            isDone = isDone && task.isChecked();
        }
        return isDone;
    }

    public Subject<String> getGoalTime() {
        return goalTime;
    }
    public Subject<Boolean> getIsRoutineDone() {
        return isRoutineDone;
    }

    public void updateInProgressRoutine(Routine routine, boolean newInProgress) {
        routine.setInProgress(newInProgress);
        saveRoutine(routine);

    }
    public void updateInEditRoutine(Routine routine, boolean newInEdit) {
        routine.setInEdit(newInEdit);
        saveRoutine(routine);
    }

    private void updateTime() {
        this.routine.setElapsedTime(routineTimer.getSeconds(), taskTimer.getSeconds());
        saveRoutine(this.routine);
    }
    public void updateGoalTime(int newTime) {
        this.routine.setGoalTime(newTime);
        saveRoutine(this.routine);
    }
    public void addRoutineTask(String taskName) {
        RoutineTask task = new RoutineTask(null, null, taskName, false, -1);
        saveRoutineTask(task);
    }
    public void updateTaskName(int taskId, String newTitle) {
        for (var task : this.routine.tasks()) {
            if (task.id() == taskId) {
                task.setTitle(newTitle);
                saveRoutineTask(task);
                return;
            }
        }
    }
    public void updateIsDone(boolean newIsDone) {
        this.routine.setIsDone(newIsDone);
        saveRoutine(this.routine);
    }

    public void initializeRoutineState() {
        this.routine.initialize();
        saveRoutine(this.routine);
        endRoutine();
    }


    // Get Timer object
    public ElapsedTimer getRoutineTimer() {
        return routineTimer;
    }
    public ElapsedTimer getTaskTimer() {
        return taskTimer;
    }
    public String getRoundedDownTime(int seconds) {
        int minutes = (seconds % 3600) / 60;

        if (minutes == 0) {
            return "-";
        }

        return String.format(Locale.getDefault(), "%01d", minutes);
    }
    public void startRoutine() {
        // Start updating elapsed time periodically
        routineTimer.resetTimer();
        // Start updating task elapsed time periodically
        taskTimer.resetTimer();
        startTimerUpdates();
    }
    public void stopRoutineTimer() {
        routineTimer.stopTimer();
    }
    public void stopTaskTimer() {
        taskTimer.stopTimer();
    }

    // Manually advance routine timer by 30 seconds (For US3c)
    public void advanceRoutineTimer() {
        routineTimer.advanceTimer();
        updateTime();
    }
    public void advanceTaskTimer() {
        taskTimer.advanceTimer();
        updateTime();
    }

    // stop two timers when finishing routine
    public void endRoutine() {
        stopRoutineTimer();
        stopTaskTimer();
        timerHandler.removeMessages(0);
    }

    // Periodically update elapsed time every second
    public void startTimerUpdates() {
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTime();
                timerHandler.postDelayed(this, TIMER_INTERVAL_MS);
            }
        }, 0);
    }
}
