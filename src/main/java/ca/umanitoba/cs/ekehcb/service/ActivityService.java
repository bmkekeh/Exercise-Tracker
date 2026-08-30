package ca.umanitoba.cs.ekehcb.service;

import ca.umanitoba.cs.ekehcb.exceptions.*;
import ca.umanitoba.cs.ekehcb.model.*;
import static com.google.common.base.Preconditions.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityService {
    private final Grid grid;

    public ActivityService(Grid grid) {
        checkNotNull(grid, "Grid cannot be null");
        this.grid = grid;
        checkInvariants();
    }

    // pre: user != null, path != null and not empty, duration > 0, type != null
    // post: new Activity added to user
    public Activity addActivity(User user, Path path, int duration, ExerciseType type)
            throws InvalidInputException {
        checkNotNull(user, "User cannot be null");
        if (path == null || path.isEmpty()) {
            throw new InvalidInputException("Route cannot be empty. Please add at least one point.");
        }
        if (duration <= 0) {
            throw new InvalidInputException("Duration must be greater than 0. Please enter a positive number.");
        }
        checkNotNull(type, "Exercise type cannot be null");

        for (Points p : path.getPoints()) {
            if (!grid.isInside(p)) {
                throw new InvalidInputException("Point " + p + " is outside the map boundaries.");
            }
            if (grid.isObstacle(p)) {
                throw new InvalidInputException("Point " + p + " is an obstacle. Routes cannot pass through obstacles.");
            }
        }

        int newId = generateId(user);
        Activity activity = new Activity(newId, path, duration, type, grid);
        user.addActivity(activity);
        return activity;
    }

    // pre: user != null, activityId is valid id in user's activities
    // post: returns a new Path copied from the selected activity
    public Path duplicateRoute(User user, int activityId) throws ActivityNotFoundException {
        checkNotNull(user, "User cannot be null");
        for (Activity a : user.getActivities()) {
            if (a.getId() == activityId) {
                Path newPath = new Path();
                for (Points p : a.getPath().getPoints()) {
                    newPath.addPoint(p);
                }
                return newPath;
            }
        }
        throw new ActivityNotFoundException("No activity with ID " + activityId + " found in your profile.");
    }

    // post: returns combined list of currentUser's activities + all followed users' activities
    public List<Activity> getFeed(User currentUser) {
        checkNotNull(currentUser, "User cannot be null");
        List<Activity> feed = new ArrayList<>(currentUser.getActivities());
        for (User followed : currentUser.getFollowing()) {
            feed.addAll(followed.getActivities());
        }
        return feed;
    }

    // pre: user != null, activityId is valid id in user's activities
    // post: activity removed from user
    public void removeActivity(User user, int activityId) throws ActivityNotFoundException {
        checkNotNull(user, "User cannot be null");
        for (Activity a : user.getActivities()) {
            if (a.getId() == activityId) {
                user.removeActivity(a);
                return;
            }
        }
        throw new ActivityNotFoundException("No activity with ID " + activityId + " found in your profile.");
    }

    // post: returns next available unique ID for this user
    private int generateId(User user) {
        int max = 0;
        for (Activity a : user.getActivities()) {
            if (a.getId() >= max) max = a.getId() + 1;
        }
        return max;
    }

    private void checkInvariants() {
        checkState(grid != null, "Invariant violated: grid cannot be null");
    }
}