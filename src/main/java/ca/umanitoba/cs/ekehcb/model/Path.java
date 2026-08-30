package ca.umanitoba.cs.ekehcb.model;
import static com.google.common.base.Preconditions.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path {
    private final List<Points> points;

    public Path() {
        this.points = new ArrayList<>();
    }

    // This method is used by ActivityService to duplicate a route
    public Path(Path other) {
        checkNotNull(other, "Cannot copy a null path");
        this.points = new ArrayList<>(other.points);    // deep copy of the list
        checkInvariants();
    }

    public void addPoint(Points p) {
        checkNotNull(p, "Point cannot be null");
        this.points.add(p);
    }

    public List<Points> getPoints() {
        //Collections.unmodifiableList() prevents the point from being modified,
        //it allows us to get the points without any modification
        //java will throw a run time error if its tried to be modified
        return Collections.unmodifiableList(points);
    }

    public boolean isContinuous() {
        // Placeholder for logic if you need to check if points are adjacent
        return !points.isEmpty();
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public boolean contains(Points p) {
        return points.contains(p);
    }

    // Invariant check for Phase 2 rubric
    public void validateInvariants() {
        assert !points.isEmpty() : "Invariants: points size must be >= 1";
    }

    //Single method for all invariant checks
    private void checkInvariants() {
        checkNotNull(points, "Points list cannot be null");
        checkArgument(!points.isEmpty(), "Path cannot be empty");

        for (Points p : points) {
            checkNotNull(p, "Path cannot contain null points");
        }
    }

}
