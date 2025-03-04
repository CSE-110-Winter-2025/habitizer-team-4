package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class RoutineTest {

    @Test
    public void tasksGetter() {
        // GIVEN a list of tasks
        var expected = List.of(
                new RoutineTask(0, 0,"Brush Teeth", false, 0),
                new RoutineTask(1, 0,"Shower", false, 1)
        );

        // WHEN I construct a Routine with those tasks
        Routine routine = new Routine(0, "My Routine", 0, false, false, false, 0, 0, 0);
        routine.setTasks(expected);

        // THEN .tasks() returns the same tasks I created it with
        var actual = routine.tasks();
        assertEquals(expected, actual);
    }

    @Test
    public void blankTitleFailsValidation() {
        // GIVEN ...
        // WHEN I try to create a Routine with a blank title.
        try {
            new Routine(0, " ", 0, false, false, false, 0, 0, 0);
            fail("Expected: IllegalArgumentException, blank title");
        }
        // THEN an IllegalArgumentException is thrown.
        catch (IllegalArgumentException e) {
            assertEquals("Routine title cannot be blank", e.getMessage());
        }

    }

    @Test
    public void titleIsCorrect() {
        // GIVEN the title is "Test Routine"
        Routine routine = new Routine(0, "Test Routine", 0, false, false, false, 0, 0, 0);
        routine.setTasks(List.of(
                new RoutineTask(1, 0,"Brush Teeth", false, 0)
        ));

        var expected = "Test Routine";
        // WHEN I get the routine title
        // THEN "Test Routine" is returned
        assertEquals(expected, routine.title());
    }

    @Test
    public void checkId() {
        // GIVEN the title is "Test Routine"
        Routine routine = new Routine(0, "Test Routine", 0, false, false, false, 0, 0, 0);
        routine.setTasks(List.of(
                new RoutineTask(1, 0, "Brush Teeth", false, 0)
        ));

        var expected = 0;
        // WHEN I get the routine title
        // THEN "Test Routine" is returned
        assertEquals(expected, routine.id());
    }

    @Test
    public void checkSortOrder() {
        int expectedSortOrder;

        Routine routine;

        routine = new Routine(0, "Test Routine", 0, false, false, false, 0, 0, 0);
        expectedSortOrder = 0;
        assertEquals(expectedSortOrder, routine.sortOrder());

        routine = new Routine(0, "Test Routine", 3, false, false, false, 0, 0, 0);
        expectedSortOrder = 3;
        assertEquals(expectedSortOrder, routine.sortOrder());

    }

    @Test
    public void routineSortOrder() {
        // GIVEN three routines with different sort orders
        Routine routine1 = new Routine(0, "Morning Routine", 2, false, false, false, 0, 0, 0);
        Routine routine2 = new Routine(1, "Evening Routine", 1, false, false, false, 0, 0, 0);
        Routine routine3 = new Routine(2, "Workout Routine", 3, false, false, false, 0, 0, 0);

        List<Routine> routines = Arrays.asList(routine1, routine2, routine3);

        // WHEN sorting the routines by sortOrder
        routines.sort(Comparator.comparingInt(Routine::sortOrder));

        // THEN they should be in the correct order: routine2, routine1, routine3
        assertEquals("Evening Routine", routines.get(0).title());
        assertEquals("Morning Routine", routines.get(1).title());
        assertEquals("Workout Routine", routines.get(2).title());
    }

}