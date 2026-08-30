package ca.umanitoba.cs.ekehcb.ui;

import ca.umanitoba.cs.ekehcb.exceptions.*;
import ca.umanitoba.cs.ekehcb.model.*;
import ca.umanitoba.cs.ekehcb.output.*;
import ca.umanitoba.cs.ekehcb.persistence.Persistence;
import ca.umanitoba.cs.ekehcb.service.*;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final UserService userService;
    private final ActivityService activityService;
    private final MapService mapService;
    private final Persistence persistence;
    private final ExerciseTypeTracker tracker;
    private final Grid grid;
    private final GridPrinter gridPrinter = new GridPrinter();
    private final ActivityPrinter activityPrinter = new ActivityPrinter();
    private final ObstaclePrinter obstaclePrinter = new ObstaclePrinter();
    private User currentUser = null;

    public ConsoleUI(UserService userService, ActivityService activityService,
                     MapService mapService, Persistence persistence,
                     ExerciseTypeTracker tracker, Grid grid) {
        this.userService = userService;
        this.activityService = activityService;
        this.mapService = mapService;
        this.persistence = persistence;
        this.tracker = tracker;
        this.grid = grid;
    }

    public void start() {
        System.out.println("=== Exercise Tracker ===");
        loginMenu();
    }

    // -------------------------
    // Login menu
    // -------------------------
    private void loginMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Welcome ---");
            System.out.println("1. Create profile");
            System.out.println("2. Select profile");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> createProfile();
                case "2" -> {
                    if (selectProfile()) mainMenu();
                }
                case "3" -> running = false;
                default -> System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }
    }

    private void createProfile() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        try {
            User user = userService.createUser(name);
            persistence.save(tracker, grid);
            System.out.println("Profile created for " + user.getName() + ".");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private boolean selectProfile() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No profiles exist yet. Please create one first.");
            return false;
        }
        System.out.println("\n--- Profiles ---");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName());
        }
        System.out.print("Enter name to select: ");
        String name = scanner.nextLine().trim();
        try {
            currentUser = userService.selectUser(name);
            System.out.println("Welcome back, " + currentUser.getName() + "!");
            return true;
        } catch (UserNotFoundException | InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // -------------------------
    // Main menu
    // -------------------------
    private void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu [" + currentUser.getName() + "] ---");
            System.out.println("1.  Add activity");
            System.out.println("2.  View feed");
            System.out.println("3.  Show map");
            System.out.println("4.  Find route");
            System.out.println("5.  Add obstacle");
            System.out.println("6.  Show obstacles");
            System.out.println("7.  Remove obstacle");
            System.out.println("8.  Remove activity");
            System.out.println("9.  Follow a user");
            System.out.println("10. Unfollow a user");
            System.out.println("11. Update profile");
            System.out.println("12. Sign out");
            System.out.print("Choose: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1"  -> addActivity();
                case "2"  -> viewFeed();
                case "3"  -> showMap();
                case "4"  -> findRoute();
                case "5"  -> addObstacle();
                case "6"  -> obstaclePrinter.printObstacleList(mapService.getGrid());
                case "7"  -> removeObstacle();
                case "8"  -> removeActivity();
                case "9"  -> followUser();
                case "10" -> unfollowUser();
                case "11" -> updateProfile();
                case "12" -> running = false;
                default   -> System.out.println("Invalid option. Please enter a number between 1 and 12.");
            }
        }
    }

    // -------------------------
    // Menu actions
    // -------------------------
    private void addActivity() {
        try {
            Path path;
            System.out.print("Duplicate a previous route? (yes/no): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("yes")) {
                List<Activity> myActivities = currentUser.getActivities();
                if (myActivities.isEmpty()) {
                    System.out.println("You have no previous activities to duplicate.");
                    return;
                }
                activityPrinter.printActivities(myActivities);
                System.out.print("Enter activity ID to duplicate: ");
                int id = readInt();
                path = activityService.duplicateRoute(currentUser, id);
                System.out.println("Route duplicated.");
            } else {
                path = new Path();
                System.out.println("Enter route points as 'x y'. Type 'done' when finished.");
                while (true) {
                    System.out.print("Point: ");
                    String line = scanner.nextLine().trim();
                    if (line.equalsIgnoreCase("done")) break;
                    String[] parts = line.split("\\s+");
                    if (parts.length != 2) {
                        System.out.println("Invalid format. Please enter as 'x y' (e.g. 2 3).");
                        continue;
                    }
                    try {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        if (x < 0 || y < 0) {
                            System.out.println("Coordinates must be 0 or greater.");
                            continue;
                        }
                        path.addPoint(new Points(x, y));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Coordinates must be whole numbers (e.g. 2 3).");
                    }
                }
                if (path.isEmpty()) {
                    System.out.println("No points entered. Activity not saved.");
                    return;
                }
            }

            System.out.print("Duration (minutes): ");
            int duration = readInt();

            System.out.print("Exercise type (RUN, WALK, BIKE, SWIM): ");
            ExerciseType type = readExerciseType();

            activityService.addActivity(currentUser, path, duration, type);
            persistence.save(tracker, grid);
            System.out.println("Activity saved successfully.");

        } catch (InvalidInputException | ActivityNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewFeed() {
        List<Activity> feed = activityService.getFeed(currentUser);
        if (feed.isEmpty()) {
            System.out.println("Your feed is empty. Add activities or follow other users.");
            return;
        }
        activityPrinter.printFeed(feed, currentUser);
    }

    private void showMap() {
        gridPrinter.printMap(mapService.getGrid(), currentUser.getActivities());
        activityPrinter.printSummary(currentUser.getActivities());
    }

    private void findRoute() {
        try {
            System.out.print("Start point (x y): ");
            Points start = readPoint();
            System.out.print("End point (x y): ");
            Points end = readPoint();

            System.out.println("Search using:");
            System.out.println("1. Only my personal routes");
            System.out.println("2. All routes in my feed");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            boolean personalOnly;
            if (choice.equals("1")) {
                personalOnly = true;
            } else if (choice.equals("2")) {
                personalOnly = false;
            } else {
                System.out.println("Invalid option. Please enter 1 or 2.");
                return;
            }

            Path result = mapService.findRoute(start, end, personalOnly, currentUser);
            System.out.println("Route found!");
            gridPrinter.printMap(mapService.getGrid(), List.of(
                    new Activity(0, result, 1, ExerciseType.WALK, mapService.getGrid())
            ));

        } catch (InvalidInputException | PathNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addObstacle() {
        try {
            System.out.print("Obstacle location (x y): ");
            Points p = readPoint();
            mapService.addObstacle(p);
            System.out.println("Obstacle added.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeObstacle() {
        obstaclePrinter.printObstacleList(mapService.getGrid());
        if (mapService.getGrid().getObstacles().isEmpty()) return;
        System.out.print("Enter obstacle ID to remove: ");
        try {
            int id = readInt();
            mapService.removeObstacle(id);
            System.out.println("Obstacle removed.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeActivity() {
        List<Activity> activities = currentUser.getActivities();
        if (activities.isEmpty()) {
            System.out.println("You have no activities to remove.");
            return;
        }
        activityPrinter.printActivities(activities);
        System.out.print("Enter activity ID to remove: ");
        try {
            int id = readInt();
            activityService.removeActivity(currentUser, id);
            System.out.println("Activity removed.");
        } catch (ActivityNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void followUser() {
        List<User> allUsers = userService.getAllUsers();
        System.out.println("\n--- Users ---");
        for (User u : allUsers) {
            if (!u.equals(currentUser)) {
                System.out.println("- " + u.getName());
            }
        }
        System.out.print("Enter username to follow: ");
        String name = scanner.nextLine().trim();
        try {
            userService.followUser(currentUser, name);
            persistence.save(tracker, grid);
            System.out.println("You are now following " + name + ".");
        } catch (UserNotFoundException | InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void unfollowUser() {
        List<User> following = currentUser.getFollowing();
        if (following.isEmpty()) {
            System.out.println("You are not following anyone.");
            return;
        }
        System.out.println("\n--- Following ---");
        for (User u : following) {
            System.out.println("- " + u.getName());
        }
        System.out.print("Enter username to unfollow: ");
        String name = scanner.nextLine().trim();
        try {
            userService.unfollowUser(currentUser, name);
            System.out.println("You have unfollowed " + name + ".");
        } catch (UserNotFoundException | InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateProfile() {
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine().trim();
        try {
            userService.updateProfile(currentUser, newName);
            persistence.save(tracker, grid);
            System.out.println("Profile updated. Welcome, " + currentUser.getName() + "!");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // -------------------------
    // Input helpers
    // -------------------------
    private int readInt() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a whole number: ");
            }
        }
    }

    private Points readPoint() {
        while (true) {
            String line = scanner.nextLine().trim();
            String[] parts = line.split("\\s+");
            if (parts.length != 2) {
                System.out.print("Invalid format. Please enter as 'x y' (e.g. 2 3): ");
                continue;
            }
            try {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                if (x < 0 || y < 0) {
                    System.out.print("Coordinates must be 0 or greater. Try again: ");
                    continue;
                }
                return new Points(x, y);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Coordinates must be whole numbers (e.g. 2 3): ");
            }
        }
    }

    private ExerciseType readExerciseType() {
        while (true) {
            String line = scanner.nextLine().trim().toUpperCase();
            try {
                return ExerciseType.valueOf(line);
            } catch (IllegalArgumentException e) {
                System.out.print("Invalid type. Please enter RUN, WALK, BIKE, or SWIM: ");
            }
        }
    }
}