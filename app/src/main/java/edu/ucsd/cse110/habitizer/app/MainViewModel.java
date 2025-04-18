package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

    private final MutableSubject<Routine> currentRoutine;
    private final MutableSubject<List<Routine>> routineList;
    private final MutableSubject<List<RoutineTask>> taskList;

    private Routine routine;
    private List<Routine> routines;
    private int numTasks;
    private int numRoutines;
    private final MutableSubject<String> routineElapsedTime;
    private final MutableSubject<String> taskElapsedTime;
    private final MutableSubject<String> goalTime;
    private final MutableSubject<Boolean> isRoutineDone;
    private final MutableSubject<Boolean> isRoutinePaused;

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
        isRoutinePaused = new SimpleSubject<>();

        isFirstRun = true;

        this.routineTimer = new RegularTimer();
        this.taskTimer = new RegularTimer();

        routineRepository.findRoutineList().observe(routines -> {
            if (routines == null) return;
            routineList.setValue(routines);
        });

        routineList.observe(routines -> {
            SystemClock.sleep(500);
            if (routines == null) return;
            numRoutines = routines.size();
            this.routines = routines;
            for (var routine : routines) {
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
            isRoutinePaused.setValue(routine.isPaused());
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
            if (Objects.equals(task.id(), newTask.id())) {
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
        if (!routineTimer.isRunning()) {
            return;
        }
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

    public void removeTask(RoutineTask task) {
        this.routine.removeTask(task);
        this.routineRepository.deleteRoutine(this.routine.id());

        saveRoutine(this.routine);
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
    public Subject<Boolean> getIsRoutinePaused() {
        return isRoutinePaused;
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
    public void addRoutine(String routineName) {
        Routine routine = new Routine(numRoutines + 1, routineName, numRoutines + 1,
                false, false, false, false,
                0, 0, 60);
        saveRoutine(routine);
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

    public void updateTaskOrder(List<RoutineTask> newTaskOrder) {
        for (int i = 0; i < newTaskOrder.size(); i++) {
            newTaskOrder.get(i).setSortOrder(i);
        }
        this.routine.setTasks(newTaskOrder);
        saveRoutine(this.routine);
        taskList.setValue(newTaskOrder);
    }

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

    public String getRoundedUpTime(int seconds) {
        if (seconds < 55) {
            if (seconds % 5 == 0) {
                return String.format(Locale.getDefault(), "%01ds", seconds);
            }
            int temp = (seconds / 5)+1;
            int rounded_seconds = temp*5;
            return String.format(Locale.getDefault(), "%01ds", rounded_seconds);
        }
        int minutes = (seconds % 3600) / 60;
        return String.format(Locale.getDefault(), "%01d", minutes+1);
    }

    public void startRoutine() {
        routineTimer.resetTimer();
        taskTimer.resetTimer();
        startTimerUpdates();
    }
    public void stopRoutineTimer() {
        routineTimer.stopTimer();
    }
    public void stopTaskTimer() {
        taskTimer.stopTimer();
    }
    public void advanceRoutineTimer() {
        routineTimer.advanceTimer();
        updateTime();
    }
    public void advanceTaskTimer() {
        taskTimer.advanceTimer();
        updateTime();
    }
    public void endRoutine() {
        if (!routineTimer.isRunning()) { // Prevent ending if paused
            return;
        }

        stopRoutineTimer();
        stopTaskTimer();
        timerHandler.removeMessages(0);
    }

    public void pauseRoutine() {
        routine.setIsPaused(true);
        saveRoutine(routine);
        pauseRoutineTimer();
        pauseTaskTimer();
    }
    public void pauseRoutineTimer() {
        routineTimer.pauseTimer();
        timerHandler.removeCallbacksAndMessages(null);
    }

    public void pauseTaskTimer() {
        taskTimer.pauseTimer();
        timerHandler.removeCallbacksAndMessages(null);
    }
    public void resumeRoutine() {
        routine.setIsPaused(false);
        saveRoutine(routine);
        resumeRoutineTimer();
    }
    public void resumeRoutineTimer() {
        routineTimer.resumeTimer();
        startTimerUpdates();
    }
    public void startTimerUpdates() {
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateTime();
                timerHandler.postDelayed(this, TIMER_INTERVAL_MS);
            }
        }, 0);
    }

    public void deleteRoutine() {
        routineRepository.deleteRoutines();

        if (this.routine == null) return;
        var routineId = 1;
        var taskId = 1;
        for (var routine : routines) {
            if (routine.id() != this.routine.id()) {
                routine.setId(routineId);
                List<RoutineTask> newTasks = new ArrayList<>();
                for (var task : routine.tasks()) {
                    task.setRoutineId(routineId);
                    task.setId(taskId);
                    newTasks.add(task);
                    taskId++;
                }

                routine.setTasks(newTasks);
                saveRoutine(routine);
                routineId++;
            }
        }

    }
}
