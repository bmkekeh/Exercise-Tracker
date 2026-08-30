package ca.umanitoba.cs.ekehcb.service;

import ca.umanitoba.cs.ekehcb.model.*;
import ca.umanitoba.cs.comp2450.stack.Stack;
import static com.google.common.base.Preconditions.*;
import java.util.*;

public class PathFinder {
    private final Stack<Points> stack;

    public PathFinder() {
        this.stack = new LinkedStack<>();
        checkInvariants();
    }

    // pre: grid != null, start != null, end != null, coveredPoints != null
    // pre: grid.isInside(start), grid.isInside(end)
    // post: returns a Path from start to end using only coveredPoints, or null if none exists
    public Path findPath(Grid grid, Set<Points> coveredPoints, Points start, Points end) {
        checkNotNull(grid, "Grid cannot be null");
        checkNotNull(coveredPoints, "Covered points cannot be null");
        checkNotNull(start, "Start point cannot be null");
        checkNotNull(end, "End point cannot be null");
        checkArgument(grid.isInside(start), "Start point is outside the grid");
        checkArgument(grid.isInside(end), "End point is outside the grid");

        Set<Points> visited = new HashSet<>();
        Map<Points, Points> cameFrom = new HashMap<>();

        stack.push(start);
        visited.add(start);

        while (!stack.isEmpty()) {
            Points current = stack.pop();

            if (current.equals(end)) {
                return reconstructPath(cameFrom, start, end);
            }

            for (Points neighbor : getNeighbors(current, grid)) {
                if (!visited.contains(neighbor)
                        && coveredPoints.contains(neighbor)
                        && !grid.isObstacle(neighbor)) {
                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);
                    stack.push(neighbor);
                }
            }
        }

        return null; // no path found
    }

    // post: returns list of 4 orthogonal neighbors that are inside the grid
    private List<Points> getNeighbors(Points p, Grid grid) {
        List<Points> neighbors = new ArrayList<>();
        int x = p.getPointX();
        int y = p.getPointY();

        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        for (int[] d : directions) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (nx >= 0 && ny >= 0) {
                Points neighbor = new Points(nx, ny);
                if (grid.isInside(neighbor)) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    // post: returns a Path tracing back from end to start via cameFrom map
    private Path reconstructPath(Map<Points, Points> cameFrom, Points start, Points end) {
        List<Points> route = new ArrayList<>();
        Points current = end;

        while (!current.equals(start)) {
            route.add(current);
            current = cameFrom.get(current);
        }
        route.add(start);
        Collections.reverse(route);

        Path path = new Path();
        for (Points p : route) {
            path.addPoint(p);
        }
        return path;
    }

    private void checkInvariants() {
        checkState(stack != null, "Invariant violated: stack cannot be null");
    }
}