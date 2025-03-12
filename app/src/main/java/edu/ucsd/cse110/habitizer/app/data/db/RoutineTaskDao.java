package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoutineTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(RoutineTaskEntity task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<RoutineTaskEntity> tasks);

    @Query("SELECT * FROM tasks WHERE routine_id = :routineId ORDER BY sort_order")
    List<RoutineTaskEntity> findTaskList(int routineId);

    @Query("DELETE FROM tasks")
    void deleteTasks();

    @Query("DELETE FROM tasks WHERE routine_id==:routineId")
    void deleteTasksInRoutine(int routineId);

    @Query("UPDATE tasks SET is_checked = 0 WHERE routine_id = :routineId")
    void resetCheckedTasks(int routineId);
}
