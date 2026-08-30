package ca.umanitoba.cs.ekehcb.model;
import static com.google.common.base.Preconditions.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Grid {
    public final int width;
    public final int height;
    public final List<Points> obstacles;

    public Grid(int width, int height) {
        //checks the width and height
        checkArgument(width > 0, "Grid width must be > 0.", width);
        checkArgument(height > 0, "Grid height must be > 0.", height);

        this.width = width;
        this.height = height;
        this.obstacles = new ArrayList<>();
        // Invariant check
        checkInvariants();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public void addObstacle(Points p) {
        // Preconditions: p != null AND isInside(p)
        checkNotNull(p, "Point cannot be null.");
        checkArgument(isInside(p), "Obstacle must be within grid boundaries.", p.getPointX(), p.getPointY());
        checkArgument(!isObstacle(p), "An obstacle already exists at point.", p.getPointX(), p.getPointY());


        obstacles.add(p);

        // Postcondition: obstacles includes p
        checkInvariants();
    }



    // Remove an obstacle point
    public void removeObstacle(int id) {
        checkArgument(id >= 0 && id < obstacles.size(), "Invalid Obstacle ID. Please check the list of obstacles.", id);
        obstacles.remove(id);

        // Postcondition: obstacles does not contain the removed point
        checkInvariants();
    }


    public boolean isInside(Points p) {
        checkNotNull(p, "Point cannot be null");

        return p.getPointX() >= 0 &&
                p.getPointX() < width &&
                p.getPointY() >= 0 &&
                p.getPointY() < height;
    }


    // Check if a point is an obstacle
    public boolean isObstacle(Points p) {
        return obstacles.contains(p);
    }



    // Return an unmodifiable copy of obstacles
    public List<Points> getObstacles() {
        return Collections.unmodifiableList(obstacles);
    }

    private void checkInvariants() {
        checkState(width > 0, "Invariant Violated: width must be positive.");
        checkState(height > 0, "Invariant Violated: height must be positive.");
        checkState(obstacles != null, "Invariant Violated: obstacles list cannot be null.");
    }

}
