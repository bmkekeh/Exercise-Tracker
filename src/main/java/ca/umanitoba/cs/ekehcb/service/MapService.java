package ca.umanitoba.cs.ekehcb.service;

import ca.umanitoba.cs.ekehcb.exceptions.*;
import ca.umanitoba.cs.ekehcb.model.*;
import static com.google.common.base.Preconditions.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapService {
    private final Grid grid;
    private final PathFinder pathFinder;

    public MapService(Grid grid) {
        checkNotNull(grid, "Grid cannot be null");
        this.grid = grid;
        this.pathFinder = new PathFinder();
        checkInvariants();
    }

    // pre: start != null, end != null, both inside grid
    // pre: personalOnly == true uses only currentUser's routes
    // pre: personalOnly == false uses currentUser + all followed users' routes
    // post: returns a Path from start to end, or throws PathNotFoundException
    public Path findRoute(Points start, Points end, boolean personalOnly, User currentUser)
            throws InvalidInputException, PathNotFoundException {
        checkNotNull(start, "Start point cannot be null");
        checkNotNull(end, "End point cannot be null");
        checkNotNull(currentUser, "Current user cannot be null");

        if (!grid.isInside(start)) {
            throw new InvalidInputException("Start point " + start + " is outside the map boundaries.");
        }
        if (!grid.isInside(end)) {
            throw new InvalidInputException("End point " + end + " is outside the map boundaries.");
        }
        if (start.equals(end)) {
            throw new InvalidInputException("Start and end points cannot be the same.");
        }

        Set<Points> coveredPoints = buildCoveredPoints(personalOnly, currentUser);

        if (coveredPoints.isEmpty()) {
            throw new PathNotFoundException("No routes have been recorded yet. Record some activities first.");
        }

        Path result = pathFinder.findPath(grid, coveredPoints, start, end);

        if (result == null) {
            throw new PathNotFoundException(
                    "No path found from " + start + " to " + end + " using " +
                            (personalOnly ? "your personal routes" : "your feed routes") + ".");
        }
        return result;
    }

    // post: returns set of all Points covered by relevant activities
    private Set<Points> buildCoveredPoints(boolean personalOnly, User currentUser) {
        Set<Points> covered = new HashSet<>();
        for (Activity a : currentUser.getActivities()) {
            covered.addAll(a.getPath().getPoints());
        }
        if (!personalOnly) {
            for (User followed : currentUser.getFollowing()) {
                for (Activity a : followed.getActivities()) {
                    covered.addAll(a.getPath().getPoints());
                }
            }
        }
        return covered;
    }

    public Grid getGrid() {
        return grid;
    }

    // pre: point != null, inside grid, not already an obstacle
    // post: obstacle added to grid
    public void addObstacle(Points point) throws InvalidInputException {
        checkNotNull(point, "Point cannot be null");
        if (!grid.isInside(point)) {
            throw new InvalidInputException("Point " + point + " is outside the map. Please enter coordinates within the map boundaries.");
        }
        if (grid.isObstacle(point)) {
            throw new InvalidInputException("There is already an obstacle at " + point + ".");
        }
        grid.addObstacle(point);
    }

    public void removeObstacle(int id) throws InvalidInputException {
        if (id < 0 || id >= grid.getObstacles().size()) {
            throw new InvalidInputException(
                    "Obstacle ID " + id + " does not exist. "
                            + "Use 'SHOW OBSTACLES' to see valid obstacle IDs.");
        }
        grid.removeObstacle(id);
    }

    private void checkInvariants() {
        checkState(grid != null, "Invariant violated: grid cannot be null");
        checkState(pathFinder != null, "Invariant violated: pathFinder cannot be null");
    }
}
