package edu.ucsd.cse110.habitizer.lib.data;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.RegularTimer;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineTask;

public class InMemoryDataSource {
    private List<Routine> routines = new ArrayList<>();

    public InMemoryDataSource() {
    }

    ;

    // Todo: make this default routine have two routines (Morning and Evening)
    public void initializeDefaultRoutine() {
        RegularTimer timer = new RegularTimer();
        Routine DEFAULT_MORNING_ROUTINE = new Routine(0, "Morning", 0);
        DEFAULT_MORNING_ROUTINE.setTasks(List.of(
                new RoutineTask(0, "Wake Up", false, 0),
                new RoutineTask(1, "Eat Breakfast", false, 1),
                new RoutineTask(2, "Brush Teeth", false, 2)
        ));

        Routine DEFAULT_EVENING_ROUTINE = new Routine(0, "Evening", 0);
        DEFAULT_EVENING_ROUTINE.setTasks(List.of(
                new RoutineTask(0, "Eat Dinner", false, 0),
                new RoutineTask(1, "Brush Teeth", false, 1),
                new RoutineTask(2, "Go To Bed", false, 2)
        ));
        // Use ArrayList instead of `List.of()`, which is immutable.
        routines = new ArrayList<>();
        routines.add(DEFAULT_MORNING_ROUTINE);
        routines.add(DEFAULT_EVENING_ROUTINE);


    }

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        data.initializeDefaultRoutine();
        return data;
    }

    public Routine getRoutine(String name) {
        for (var routine : routines) {
            if (routine.title() == name) {
                return routine;
            }
        }
        return null;
    }

    // return List of RoutineTask of Routine object from HashMap (routines).
    public List<Routine> getRoutineList() {
        return routines;
    }

    // return List of RoutineTask of Routine object from HashMap (routines).
    public List<RoutineTask> getTaskList(String name) {
        Routine routine = getRoutine(name);
        return (routine != null) ? routine.tasks() : new ArrayList<>();
    }

    // Made updateRoutine()
    public void updateRoutine(Routine updatedRoutine) {
        for (int i = 0; i < routines.size(); i++) {
            if (routines.get(i).title().equals(updatedRoutine.title())) {
                List<Routine> updatedRoutines = new ArrayList<>(routines);
                updatedRoutines.set(i, updatedRoutine);
                routines = updatedRoutines; // Ensure the reference is updated
                return;
            }
        }
    }
}


