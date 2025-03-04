package edu.ucsd.cse110.habitizer.app;

import android.app.Application;

import androidx.room.Room;

import edu.ucsd.cse110.habitizer.app.data.db.HabitizerDatabase;
import edu.ucsd.cse110.habitizer.app.data.db.RoomRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.SimpleRoutineRepository;

public class HabitizerApplication extends Application {
    private InMemoryDataSource dataSource;
    private RoutineRepository routineRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        this.dataSource = InMemoryDataSource.fromDefault();

        boolean useRoom = true;

        if (useRoom) {
            var database = Room.databaseBuilder(
                            getApplicationContext(),
                            HabitizerDatabase.class,
                            "habitizer-database")
                    .allowMainThreadQueries()
                    .build();
            routineRepository = new RoomRoutineRepository(
                    database.routineDao(), database.routineTaskDao());

            var sharedPreferences = getSharedPreferences("habitizer", MODE_PRIVATE);
            var isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

            if (isFirstRun) {
                var routines = dataSource.findRoutineList().getValue();

                for (var routine : routines) {
                    routineRepository.saveRoutine(routine);
                }
                sharedPreferences.edit().putBoolean("isFirstRun", false).apply();
            }
        } else {
            routineRepository = new SimpleRoutineRepository(dataSource);
        }
    }

    public RoutineRepository getRoutineRepository() {

        return routineRepository;
    }
}
