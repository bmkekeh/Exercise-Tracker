package ca.umanitoba.cs.ekehcb.app;

import ca.umanitoba.cs.ekehcb.model.*;
import ca.umanitoba.cs.ekehcb.persistence.Persistence;
import ca.umanitoba.cs.ekehcb.persistence.json.JsonPersistence;
import ca.umanitoba.cs.ekehcb.service.*;
import ca.umanitoba.cs.ekehcb.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        // hard-code the world map — starts empty, 20x20
        Grid worldMap = new Grid(20, 20);

        // wire up services
        ExerciseTypeTracker tracker = new ExerciseTypeTracker();
        UserService userService = new UserService(tracker);
        ActivityService activityService = new ActivityService(worldMap);
        MapService mapService = new MapService(worldMap);

        // wire up persistence
        Persistence persistence = new JsonPersistence();

        // load saved state before launching
        persistence.load(tracker, worldMap);

        // launch UI
        ConsoleUI ui = new ConsoleUI(userService, activityService, mapService, persistence, tracker, worldMap);
        ui.start();
    }
}