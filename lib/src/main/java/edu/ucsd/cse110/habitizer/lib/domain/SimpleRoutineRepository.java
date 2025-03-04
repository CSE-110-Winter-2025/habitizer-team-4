package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class SimpleRoutineRepository implements RoutineRepository {
    private final InMemoryDataSource dataSource;

    public SimpleRoutineRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Subject<List<Routine>> findRoutineList() {
        return dataSource.findRoutineList();
    }

    @Override
    public List<RoutineTask> findTaskList(int routineId) {
        return dataSource.findTaskList(routineId);
    }

    @Override
    public void saveRoutine(Routine routine) {
        dataSource.saveRoutine(routine);

    }

    @Override
    public void deleteRoutines() {
        dataSource.deleteRoutines();
    }
}

