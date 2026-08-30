package ca.umanitoba.cs.ekehcb.service;

import ca.umanitoba.cs.ekehcb.exceptions.*;
import ca.umanitoba.cs.ekehcb.model.*;

public class MapServiceTest {

    private int passed = 0;
    private int failed = 0;

    public void runAll() {
        System.out.println("\n--- Testing MapService ---");

        testAddObstacle();
        testAddObstacleOutsideGrid();
        testAddObstacleDuplicate();
        testRemoveObstacle();
        testRemoveObstacleInvalidId();
        testFindRoutePersonalOnly();
        testFindRouteFeedRoutes();
        testFindRouteNoPath();
        testFindRouteStartOutsideGrid();
        testFindRouteEndOutsideGrid();

        System.out.println("  >> " + passed + " passed, " + failed + " failed");
    }

    // -------------------------
    // addObstacle tests
    // -------------------------

    private void testAddObstacle() {
        try {
            MapService service = freshService();
            service.addObstacle(new Points(2, 2));
            check("addObstacle() adds obstacle to grid",
                    service.getGrid().isObstacle(new Points(2, 2)));
        } catch (Exception e) { fail("addObstacle() valid", e); }
    }


    private void testAddObstacleOutsideGrid() {
        try {
            MapService service = freshService();
            service.addObstacle(new Points(99, 99));
            check("addObstacle() outside grid throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("addObstacle() outside grid throws InvalidInputException", true);
        } catch (Exception e) { fail("addObstacle() outside grid", e); }
    }


    private void testAddObstacleDuplicate() {
        try {
            MapService service = freshService();
            service.addObstacle(new Points(1, 1));
            service.addObstacle(new Points(1, 1));
            check("addObstacle() duplicate throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("addObstacle() duplicate throws InvalidInputException", true);
        } catch (Exception e) { fail("addObstacle() duplicate", e); }
    }

    // -------------------------
    // removeObstacle tests
    // -------------------------


    private void testRemoveObstacle() {
        try {
            MapService service = freshService();
            service.addObstacle(new Points(3, 3));
            service.removeObstacle(0);
            check("removeObstacle() removes obstacle from grid",
                    !service.getGrid().isObstacle(new Points(3, 3)));
        } catch (Exception e) { fail("removeObstacle() valid", e); }
    }


    private void testRemoveObstacleInvalidId() {
        try {
            MapService service = freshService();
            service.removeObstacle(999);
            check("removeObstacle() invalid ID throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("removeObstacle() invalid ID throws InvalidInputException", true);
        } catch (Exception e) { fail("removeObstacle() invalid ID", e); }
    }

    // -------------------------
    // findRoute tests
    // -------------------------


    private void testFindRoutePersonalOnly() {
        try {
            MapService service = freshService();
            User alice = new User("Alice");

            // add a path that connects (0,0) to (3,0)
            Path path = new Path();
            path.addPoint(new Points(0, 0));
            path.addPoint(new Points(1, 0));
            path.addPoint(new Points(2, 0));
            path.addPoint(new Points(3, 0));
            ActivityService activityService = new ActivityService(service.getGrid());
            activityService.addActivity(alice, path, 10, ExerciseType.WALK);

            Path result = service.findRoute(
                    new Points(0, 0), new Points(3, 0), true, alice);
            check("findRoute() personal only finds a path",
                    result != null && !result.isEmpty());
        } catch (Exception e) { fail("findRoute() personal only", e); }
    }


    private void testFindRouteFeedRoutes() {
        try {
            MapService service = freshService();
            User alice = new User("Alice");
            User bob = new User("Bob");
            alice.follow(bob);


            Path path = new Path();
            path.addPoint(new Points(0, 0));
            path.addPoint(new Points(1, 0));
            path.addPoint(new Points(2, 0));
            path.addPoint(new Points(3, 0));
            ActivityService activityService = new ActivityService(service.getGrid());
            activityService.addActivity(bob, path, 10, ExerciseType.WALK);


            Path result = service.findRoute(
                    new Points(0, 0), new Points(3, 0), false, alice);
            check("findRoute() feed routes finds path through followed user",
                    result != null && !result.isEmpty());
        } catch (Exception e) { fail("findRoute() feed routes", e); }
    }


    private void testFindRouteNoPath() {
        try {
            MapService service = freshService();
            User alice = new User("Alice");
            service.findRoute(
                    new Points(0, 0), new Points(5, 5), true, alice);
            check("findRoute() no path throws PathNotFoundException", false);
        } catch (PathNotFoundException e) {
            check("findRoute() no path throws PathNotFoundException", true);
        } catch (Exception e) { fail("findRoute() no path", e); }
    }


    private void testFindRouteStartOutsideGrid() {
        try {
            MapService service = freshService();
            User alice = new User("Alice");
            service.findRoute(
                    new Points(99, 99), new Points(0, 0), true, alice);
            check("findRoute() start outside grid throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("findRoute() start outside grid throws InvalidInputException", true);
        } catch (Exception e) { fail("findRoute() start outside grid", e); }
    }


    private void testFindRouteEndOutsideGrid() {
        try {
            MapService service = freshService();
            User alice = new User("Alice");
            service.findRoute(
                    new Points(0, 0), new Points(99, 99), true, alice);
            check("findRoute() end outside grid throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("findRoute() end outside grid throws InvalidInputException", true);
        } catch (Exception e) { fail("findRoute() end outside grid", e); }
    }

    // -------------------------
    // Helpers
    // -------------------------

    private MapService freshService() {
        return new MapService(new Grid(10, 10));
    }

    private void check(String testName, boolean condition) {
        if (condition) {
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName);
            failed++;
        }
    }

    private void fail(String testName, Exception e) {
        System.out.println("  FAIL: " + testName + " (crashed: " + e.getMessage() + ")");
        failed++;
    }
}