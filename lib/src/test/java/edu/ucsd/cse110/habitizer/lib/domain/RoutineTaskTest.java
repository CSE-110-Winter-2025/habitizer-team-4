package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class RoutineTaskTest {

    @Test
    public void title() {
        var task = new RoutineTask(0, 0, "Brush Teeth", false, 0);
        assertEquals("Brush Teeth", task.title());

        // Try-Catch for blank title
        try {
            new RoutineTask(0, 0, " ", false, 0);
            fail("Expected: IllegalArgumentException, blank title");
        } catch (IllegalArgumentException e) {
            assertEquals("RoutineTask title cannot be blank", e.getMessage());
        }

    }

    @Test
    public void isChecked() {
        // Check: Initializing isChecked == False
        var falseIsCheckedTask = new RoutineTask(0, 0,"Brush Teeth", false, 0);
        assertFalse(falseIsCheckedTask.isChecked());

        // Check: Initializing isChecked == True
        var trueIsCheckedTask = new RoutineTask(0, 0,"Brush Teeth", true, 0);
        assertTrue(trueIsCheckedTask.isChecked());
    }

    @Test
    public void id() {
        Integer expectedId;

        var taskWithZeroId = new RoutineTask(0, 0,"Brush Teeth", false, 0);
        expectedId = 0;
        assertEquals(expectedId, taskWithZeroId.id());

        var taskWithNonZeroId = new RoutineTask(3, 0,"Brush Teeth", false, 0);
        expectedId = 3;
        assertEquals(expectedId, taskWithNonZeroId.id());

        var taskWithNegativeId = new RoutineTask(-2, 0,"Brush Teeth", false, 0);
        expectedId = -2;
        assertEquals(expectedId, taskWithNegativeId.id());

        var taskWithNullId = new RoutineTask(null, 0,"Brush Teeth", false, 0);
        expectedId = null;
        assertEquals(expectedId, taskWithNullId.id());
    }

    @Test
    public void checkOff() {
        RegularTimer timer = new RegularTimer();
        String time = timer.getTime();  // Returns "00:00"

        // Convert "HH:MM" into an integer format
        String[] parts = time.split(":");
        int totalMinutes = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);

        var task = new RoutineTask(0, 0, "Brush Teeth", false, 0);

        assertFalse(task.isChecked());
        task.checkOff(totalMinutes);  // Pass converted integer time
        assertTrue(task.isChecked());
    }


    @Test
    public void checkOffTwice() {
        // GIVEN I have a task "Brush Teeth"
        RegularTimer timer = new RegularTimer();

        // Convert "HH:MM" into an integer format (total minutes)
        String time = timer.getTime();
        String[] parts = time.split(":");
        int totalMinutes = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);

        var task = new RoutineTask(0, 0, "Brush Teeth", false, 0);

        // WHEN I check off "Brush Teeth"
        task.checkOff(totalMinutes);
        // THEN "Brush Teeth" is checked
        assertTrue(task.isChecked());
        // WHEN I check off "Brush Teeth" again
        task.checkOff(totalMinutes);
        // THEN "Brush Teeth" is (still) checked
        assertTrue(task.isChecked());
    }

    @Test
    public void checkSortOrder() {
        int expectedSortOrder;

        RoutineTask task;

        task = new RoutineTask(0, 0,"Brush Teeth", false, 0);
        expectedSortOrder = 0;
        assertEquals(expectedSortOrder, task.sortOrder());

        task = new RoutineTask(0, 0,"Brush Teeth", false, 3);
        expectedSortOrder = 3;
        assertEquals(expectedSortOrder, task.sortOrder());

    }

    @Test
    public void routineTaskSortOrder() {
        // GIVEN three tasks with different sort orders
        RoutineTask task1 = new RoutineTask(0, 0, "Brush Teeth", false, 2);
        RoutineTask task2 = new RoutineTask(0, 0, "Shower", false, 1);
        RoutineTask task3 = new RoutineTask(0, 0, "Read", false, 3);

        List<RoutineTask> tasks = Arrays.asList(task1, task2, task3);

        // WHEN sorting the tasks by sortOrder
        tasks.sort(Comparator.comparingInt(RoutineTask::sortOrder));

        // THEN they should be in the correct order: task2, task1, task3
        assertEquals("Shower", tasks.get(0).title());
        assertEquals("Brush Teeth", tasks.get(1).title());
        assertEquals("Read", tasks.get(2).title());
    }

}