package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class RoutineTask {
    private @Nullable Integer id;
    private Integer routineId;
    private @NonNull String title;
    private boolean isChecked;
    private int elapsedTime;
    private int sortOrder;

    public RoutineTask(@Nullable Integer id, Integer routineId, @NonNull String title, boolean isChecked, int sortOrder) {
        if (title.isBlank()) {
            throw new IllegalArgumentException("RoutineTask title cannot be blank");
        }

        this.id = id;
        this.routineId = routineId;
        this.title = title;
        this.isChecked = isChecked;
        this.elapsedTime = 0;
        this.sortOrder = sortOrder;
    }

    public @Nullable Integer id() { return id; }
    public String title() { return title; }
    public int sortOrder() { return sortOrder; }
    public Integer routineId() { return this.routineId; }
    public boolean isChecked() { return isChecked; }
    public int elapsedTime() { return elapsedTime; }

    public void initialize() {
        this.isChecked = false;
        this.elapsedTime = 0;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    public void checkOff(int elapsedTime) {
        this.isChecked = true;
        setElapsedTime(elapsedTime); // set elapsed time when task is done
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    // Updates task name when in edit task dialog
    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoutineTask compare = (RoutineTask) o;
        return title.equals(compare.title()) &&
                isChecked == compare.isChecked();
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, isChecked);
    }
}
