package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public record Routine(@NonNull int id, @NonNull String title, @NonNull List<RoutineTask> tasks) {
    public Routine(@Nullable int id, @NonNull String title, List<RoutineTask> tasks) {
        if (title.isBlank()) {
            throw new IllegalArgumentException("Routine title cannot be blank");
        }
        this.id = id;
        this.title = title;
        this.tasks = List.copyOf(tasks);
    }
    public int id() { return id; }
    public String title() { return title; }
    public List<RoutineTask> tasks() { return new ArrayList<>(tasks); } // Return a copy for safety

    // Add a task to the routine
    public Routine addTask(RoutineTask task) {
        List<RoutineTask> updatedTasks = new ArrayList<>(tasks);
        updatedTasks.add(task);
        return new Routine(this.id, this.title, updatedTasks); // Return a new Routine object
    }

    public Routine removeTask(RoutineTask task) {
        List<RoutineTask> updatedTasks = new ArrayList<>(tasks);
        updatedTasks.remove(task);
        return new Routine(this.id, this.title, updatedTasks);
    }
}
