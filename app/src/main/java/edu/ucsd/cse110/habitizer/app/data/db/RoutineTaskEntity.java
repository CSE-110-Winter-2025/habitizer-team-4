package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.habitizer.lib.domain.RoutineTask;

@Entity(tableName = "tasks")
public class RoutineTaskEntity {
    @PrimaryKey()
    @ColumnInfo(name = "id")
    public Integer id;

    @ColumnInfo(name = "routine_id")
    public Integer routineId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "is_checked")
    public boolean isChecked;

    @ColumnInfo(name = "elapsed_time")
    public int elapsedTime;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    RoutineTaskEntity(@NonNull Integer id, @NonNull Integer routineId, @NonNull String title,
                      @NonNull boolean isChecked, @NonNull int elapsedTime, @NonNull int sortOrder) {
        this.id = id;
        this.routineId = routineId;
        this.title = title;
        this.isChecked = isChecked;
        this.elapsedTime = elapsedTime;
        this.sortOrder = sortOrder;
    }

    public static RoutineTaskEntity fromRoutineTask(@NonNull RoutineTask task){
        var routineTaskEntity = new RoutineTaskEntity(task.id(), task.routineId(), task.title(),
                task.isChecked(), task.elapsedTime(), task.sortOrder());
        return routineTaskEntity;
    }

    public @NonNull RoutineTask toRoutineTask(){
        var task = new RoutineTask(id, routineId, title, isChecked, sortOrder);
        task.setElapsedTime(elapsedTime);
        return task;
    }
}