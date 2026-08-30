package ca.umanitoba.cs.ekehcb.service;

import ca.umanitoba.cs.ekehcb.exceptions.*;
import ca.umanitoba.cs.ekehcb.model.*;

public class ActivityServiceTest {

    private int passed = 0;
    private int failed = 0;

    public void runAll() {
        System.out.println("\n--- Testing ActivityService ---");

        testAddActivity();
        testAddActivityEmptyPath();
        testAddActivityZeroDuration();
        testAddActivityNegativeDuration();
        testDuplicateRoute();
        testDuplicateRouteNotFound();
        testGetFeedOwnActivities();
        testGetFeedIncludesFollowed();
        testGetFeedEmpty();
        testRemoveActivity();
        testRemoveActivityNotFound();

        System.out.println("  >> " + passed + " passed, " + failed + " failed");
    }

    // -------------------------
    // addActivity tests
    // -------------------------


    private void testAddActivity() {
        try {
            ActivityService service = freshService();
            User user = new User("Alice");
            Path path = new Path();
            path.addPoint(new Points(0, 0));
            path.addPoint(new Points(1, 0));
            Activity activity = service.addActivity(user, path, 30, ExerciseType.RUN);
            check("addActivity() returns activity with correct duration",
                    activity.getDuration() == 30);
            check("addActivity() returns activity with correct type",
                    ExerciseType.RUN.equals(activity.getType()));
            check("addActivity() adds activity to user",
                    user.getActivities().contains(activity));
        } catch (Exception e) { fail("addActivity() valid", e); }
    }


    private void testAddActivityEmptyPath() {
        try {
            ActivityService service = freshService();
            User user = new User("Alice");
            Path path = new Path();
            service.addActivity(user, path, 30, ExerciseType.RUN);
            check("addActivity() empty path throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("addActivity() empty path throws InvalidInputException", true);
        } catch (Exception e) { fail("addActivity() empty path", e); }
    }


    private void testAddActivityZeroDuration() {
        try {
            ActivityService service = freshService();
            User user = new User("Alice");
            Path path = new Path();
            path.addPoint(new Points(0, 0));
            service.addActivity(user, path, 0, ExerciseType.WALK);
            check("addActivity() zero duration throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("addActivity() zero duration throws InvalidInputException", true);
        } catch (Exception e) { fail("addActivity() zero duration", e); }
    }


    private void testAddActivityNegativeDuration() {
        try {
            ActivityService service = freshService();
            User user = new User("Alice");
            Path path = new Path();
            path.addPoint(new Points(0, 0));
            service.addActivity(user, path, -5, ExerciseType.BIKE);
            check("addActivity() negative duration throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("addActivity() negative duration throws InvalidInputException", true);
        } catch (Exception e) { fail("addActivity() negative duration", e); }
    }

    // -------------------------
    // duplicateRoute tests
    // -------------------------

    private void testDuplicateRoute() {
        try {
            ActivityService service = freshService();
            User user = new User("Alice");
            Path path = new Path();
            path.addPoint(new Points(0, 0));
            path.addPoint(new Points(1, 0));
            Activity activity = service.addActivity(user, path, 20, ExerciseType.WALK);
            Path duplicate = service.duplicateRoute(user, activity.getId());
            check("duplicateRoute() returns path with same points",
                    duplicate.getPoints().equals(path.getPoints()));
            check("duplicateRoute() returns a new Path object",
                    duplicate != path);
        } catch (Exception e) { fail("duplicateRoute() valid", e); }
    }

    private void testDuplicateRouteNotFound() {
        try {
            ActivityService service = freshService();
            User user = new User("Alice");
            service.duplicateRoute(user, 999);
            check("duplicateRoute() invalid ID throws ActivityNotFoundException", false);
        } catch (ActivityNotFoundException e) {
            check("duplicateRoute() invalid ID throws ActivityNotFoundException", true);
        } catch (Exception e) { fail("duplicateRoute() not found", e); }
    }

    // -------------------------
    // getFeed tests
    // -------------------------

    private void testGetFeedOwnActivities() {
        try {
            ActivityService service = freshService();
            User user = new User("Alice");
            Path path = new Path();
            path.addPoint(new Points(0, 0));
            service.addActivity(user, path, 10, ExerciseType.RUN);
            check("getFeed() includes own activities",
                    service.getFeed(user).size() == 1);
        } catch (Exception e) { fail("getFeed() own activities", e); }
    }

    private void testGetFeedIncludesFollowed() {
        try {
            ActivityService service = freshService();
            User alice = new User("Alice");
            User bob = new User("Bob");

            Path path1 = new Path();
            path1.addPoint(new Points(0, 0));
            service.addActivity(alice, path1, 10, ExerciseType.RUN);

            Path path2 = new Path();
            path2.addPoint(new Points(1, 0));
            service.addActivity(bob, path2, 15, ExerciseType.WALK);

            alice.follow(bob);
            check("getFeed() includes followed user activities",
                    service.getFeed(alice).size() == 2);
        } catch (Exception e) { fail("getFeed() includes followed", e); }
    }

    private void testGetFeedEmpty() {
        try {
            ActivityService service = freshService();
            User user = new User("Alice");
            check("getFeed() returns empty list when no activities",
                    service.getFeed(user).isEmpty());
        } catch (Exception e) { fail("getFeed() empty", e); }
    }

    // -------------------------
    // removeActivity tests
    // -------------------------

    private void testRemoveActivity() {
        try {
            ActivityService service = freshService();
            User user = new User("Alice");
            Path path = new Path();
            path.addPoint(new Points(0, 0));
            Activity activity = service.addActivity(user, path, 10, ExerciseType.RUN);
            service.removeActivity(user, activity.getId());
            check("removeActivity() removes activity from user",
                    user.getActivities().isEmpty());
        } catch (Exception e) { fail("removeActivity() valid", e); }
    }


    private void testRemoveActivityNotFound() {
        try {
            ActivityService service = freshService();
            User user = new User("Alice");
            service.removeActivity(user, 999);
            check("removeActivity() invalid ID throws ActivityNotFoundException", false);
        } catch (ActivityNotFoundException e) {
            check("removeActivity() invalid ID throws ActivityNotFoundException", true);
        } catch (Exception e) { fail("removeActivity() not found", e); }
    }

    // -------------------------
    // Helpers
    // -------------------------

    private ActivityService freshService() {
        return new ActivityService(new Grid(10, 10));
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