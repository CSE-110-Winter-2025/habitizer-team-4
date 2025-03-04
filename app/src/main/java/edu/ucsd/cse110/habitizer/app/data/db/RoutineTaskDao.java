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
    List<Long> insert(List<RoutineTaskEntity> tasks);

    @Query("SELECT * FROM tasks WHERE routine_id = :routineId")
    List<RoutineTaskEntity> findTaskList(int routineId);

    @Query("DELETE FROM tasks")
    void deleteTasks();
}
