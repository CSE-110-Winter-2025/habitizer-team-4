package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.List;
import java.util.ArrayList;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;

public class RoutineRepository {
    private final InMemoryDataSource dataSource;

    public RoutineRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // return a List of Routine
    public List<Routine> getRoutineList() {
        return dataSource.getRoutineList();
    }

    // return a List of RoutineTask
    public List<RoutineTask> getTaskList(String name) {
        return dataSource.getTaskList(name);
    }

    public RoutineTask getTaskWithIdandName(String name, int id) {
        for (var task : this.getTaskList(name)) {
            if (task.id() == id) {
                return task;
            }

        }
        return null;
    }

    // Add a new task to a routine
    public void addTaskToRoutine(String routineName, String taskName) {
        Routine routine = dataSource.getRoutine(routineName);
        if (routine != null) {
            // Generate a new task ID (increment from the last task)
            int newTaskId = routine.tasks().isEmpty() ? 0 : routine.tasks().get(routine.tasks().size() - 1).id() + 1;
            RoutineTask newTask = new RoutineTask(newTaskId, taskName, 1, false);

            // Create a new updated Routine instance
            Routine updatedRoutine = routine.addTask(newTask);

            // Save the updated routine
            dataSource.updateRoutine(updatedRoutine);
        }
    }

    public void removeTaskFromRoutine(String routineName, int id) {
        Routine routine = dataSource.getRoutine(routineName);
        RoutineTask task = null;
        if (routine != null) {
            var tasks = dataSource.getTaskList(routineName);
            for (var t : tasks) {
                if (t.id() == id) {
                    task = t;
                }
            }

            // Create a new updated Routine instance
            Routine updatedRoutine = routine.removeTask(task);

            // Save the updated routine
            dataSource.updateRoutine(updatedRoutine);
        }
    }
}

