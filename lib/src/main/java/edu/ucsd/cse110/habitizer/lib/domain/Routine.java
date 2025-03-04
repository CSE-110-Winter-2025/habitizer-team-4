package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Routine {
    private final @Nullable Integer id;
    private @NonNull String title;
    private int sortOrder;
    private List<RoutineTask> tasks;

    private boolean isInProgress;
    private boolean isInEdit;
    private boolean isDone;

    private int routineElapsedTime;
    private int taskElapsedTime;
    private int goalTime;

    public Routine(@NonNull int id, @NonNull String title, int sortOrder, boolean isInProgress, boolean isInEdit, boolean isdone,
                   int routineElapsedTime, int taskElapsedTime, int goalTime) {
        if (title.isBlank()) {
            throw new IllegalArgumentException("Routine title cannot be blank");
        }
        this.id = id;
        this.title = title;
        this.sortOrder = sortOrder;
        this.isInProgress = isInProgress;
        this.isInEdit = isInEdit;
        this.isDone = isdone;

        this.routineElapsedTime = routineElapsedTime;
        this.taskElapsedTime = taskElapsedTime;
        this.goalTime = goalTime;

        this.tasks = new ArrayList<>();
    }

    public int id() { return id; }
    public String title() { return title; }
    public int sortOrder() { return sortOrder; }
    public boolean isInProgress() { return isInProgress; }
    public boolean isInEdit() { return isInEdit; }
    public boolean isDone() { return isDone; }
    public int routineElapsedTime() { return routineElapsedTime; }
    public int taskElapsedTime() { return taskElapsedTime; }
    public int goalTime() { return goalTime; }
    public List<RoutineTask> tasks() { return List.copyOf(tasks); }

    public void setTasks(List<RoutineTask> tasks) {
        var newTasks = new ArrayList<RoutineTask>();
        for (var task: tasks) {
            task.setRoutineId(id);
            newTasks.add(task);
        }
        this.tasks = List.copyOf(newTasks);
    }

    public void addTask(RoutineTask newTask) {
        var newTasks = new ArrayList<RoutineTask>();
        for (var task : tasks) {
            newTasks.add(task);
        }
        newTasks.add(newTask);
        setTasks(newTasks);
    }

    public void setElapsedTime(int routineElapsedTime, int taskElapsedTime) {
        this.routineElapsedTime = routineElapsedTime;
        this.taskElapsedTime = taskElapsedTime;
    }

    public void setGoalTime(int newTime) {
        this.goalTime = newTime;
    }
    public void setIsDone(boolean newIsDone) {
        isDone = newIsDone;
    }
    public void setInProgress(boolean newInProgress) {
        isInProgress = newInProgress;
    }
    public void setInEdit(boolean newInEdit) {
        isInEdit = newInEdit;
    }

    public void initialize() {
        isDone = false;
        isInProgress = false;
        isInEdit = false;

        for (var task : tasks) {
            task.initialize();
        }

        this.routineElapsedTime = 0;
        this.taskElapsedTime = 0;
    }

    public Routine removeTask(RoutineTask task) {
        List<RoutineTask> updatedTasks = new ArrayList<>(tasks);
        updatedTasks.remove(task);
        return new Routine(this.id, this.title, updatedTasks);
    }
}
