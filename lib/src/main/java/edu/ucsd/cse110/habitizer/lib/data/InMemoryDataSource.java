package edu.ucsd.cse110.habitizer.lib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineTask;
import edu.ucsd.cse110.habitizer.lib.util.MutableSubject;
import edu.ucsd.cse110.habitizer.lib.util.SimpleSubject;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class InMemoryDataSource {
    private final Map<Integer, Routine> routinesMap = new HashMap<>();
    private List<Routine> routinesList = List.of();
    private MutableSubject<List<Routine>> routineSubjects = new SimpleSubject<>();

    public InMemoryDataSource() {
    }


    public void initializeDefaultRoutine() {
        Routine DEFAULT_MORNING_ROUTINE = new Routine(1, "Morning", 0,
                false, false, false, 0, 0, 60);
        DEFAULT_MORNING_ROUTINE.setTasks(List.of(
                new RoutineTask(1, 1, "Wake Up", false, 0),
                new RoutineTask(2, 1, "Eat Breakfast", false, 1),
                new RoutineTask(3, 1, "Brush Teeth", false, 2)
        ));
        routinesMap.put(1, DEFAULT_MORNING_ROUTINE);

        Routine DEFAULT_EVENING_ROUTINE = new Routine(2, "Evening", 0,
                false, false, false, 0, 0, 60);
        DEFAULT_EVENING_ROUTINE.setTasks(List.of(
                new RoutineTask(4, 2, "Eat Dinner", false, 0),
                new RoutineTask(5, 2, "Brush Teeth", false, 1),
                new RoutineTask(6, 2, "Go To Bed", false, 2)
        ));
       routinesMap.put(2, DEFAULT_EVENING_ROUTINE);
       routinesList = List.of(DEFAULT_MORNING_ROUTINE, DEFAULT_EVENING_ROUTINE);
       routineSubjects.setValue(List.of(DEFAULT_MORNING_ROUTINE, DEFAULT_EVENING_ROUTINE));
    }
    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        data.initializeDefaultRoutine();
        return data;
    }

    public Subject<List<Routine>> findRoutineList() {
        return routineSubjects;
    }

    public List<RoutineTask> findTaskList(int routineId) {
        return routinesMap.get(routineId).tasks();
    }

    public void saveRoutine(Routine newRoutine) {
        routinesMap.put(newRoutine.id(), newRoutine);

        ArrayList<Routine> newRoutines = new ArrayList<>();
        for (var routine : routinesList) {
            if (routine.id() == newRoutine.id()) {
                newRoutines.add(newRoutine);
            } else {
                newRoutines.add(routine);
            }
        }
        routinesList = List.copyOf(newRoutines);
        routineSubjects.setValue(newRoutines);
    }

    public void deleteRoutines() {
        routinesMap.clear();
        routinesList = List.of();
        routineSubjects = new SimpleSubject<>();
    }
}


