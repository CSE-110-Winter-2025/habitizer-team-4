package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.app.util.LiveDataSubjectAdapter;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineTask;
import edu.ucsd.cse110.habitizer.lib.util.Subject;


public class RoomRoutineRepository implements RoutineRepository {
    private final RoutineDao routineDao;
    private final RoutineTaskDao routineTaskDao;

    public RoomRoutineRepository(RoutineDao routineDao, RoutineTaskDao routineTaskDao) {
        this.routineDao = routineDao;
        this.routineTaskDao = routineTaskDao;
    }

    @Override
    public Subject<List<Routine>> findRoutineList() {
        var entitiesLiveData = routineDao.findRoutineList();
        var routineLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(RoutineEntity::toRoutine)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(routineLiveData);
    }

    public List<RoutineTask> findTaskList(int routineId) {
        var tasks = routineTaskDao.findTaskList(routineId).stream()
                    .map(RoutineTaskEntity::toRoutineTask)
                    .collect(Collectors.toList());
        return tasks;
    }
    public void saveRoutine(Routine routine) {
        routineDao.insert(RoutineEntity.fromRoutine(routine));
        var tasks = routine.tasks().stream().map(RoutineTaskEntity::fromRoutineTask).collect(Collectors.toList());
        routineTaskDao.insert(tasks);
    }

    public void deleteRoutines() {
        routineDao.deleteRoutines();
        routineTaskDao.deleteTasks();
    }
}

