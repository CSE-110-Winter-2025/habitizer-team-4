package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;

@Entity(tableName = "routines")
public class RoutineEntity {
    @PrimaryKey()
    @ColumnInfo(name = "id")
    public Integer id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    @ColumnInfo(name = "is_in_progress")
    public boolean isInProgress;

    @ColumnInfo(name = "is_in_edit")
    public boolean isInEdit;

    @ColumnInfo(name = "is_done")
    public boolean isDone;

    @ColumnInfo(name = "routine_elapsed_time")
    public int routineElapsedTime;

    @ColumnInfo(name = "task_elapsed_time")
    public int taskElapsedTime;

    @ColumnInfo(name = "goal_time")
    public int goalTime;


    RoutineEntity(
            int id, String title, int sortOrder, boolean isInProgress, boolean isInEdit, boolean isDone,
            int routineElapsedTime, int taskElapsedTime, int goalTime){
        this.id = id;
        this.title = title;
        this.sortOrder = sortOrder;
        this.isInProgress = isInProgress;
        this.isInEdit = isInEdit;
        this.isDone = isDone;
        this.routineElapsedTime = routineElapsedTime;
        this.taskElapsedTime = taskElapsedTime;
        this.goalTime = goalTime;
    }

    public static RoutineEntity fromRoutine(@NonNull Routine routine){
        var routineEntity = new RoutineEntity(
                routine.id(), routine.title(), routine.sortOrder(),
                routine.isInProgress(), routine.isInEdit(), routine.isDone(),
                routine.routineElapsedTime(), routine.taskElapsedTime(), routine.goalTime());
        return routineEntity;
    }

    public Routine toRoutine(){
        return new Routine(id, title, sortOrder, isInProgress, isInEdit, isDone,
                routineElapsedTime, taskElapsedTime, goalTime);
    }
}