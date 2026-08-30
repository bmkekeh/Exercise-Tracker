package ca.umanitoba.cs.ekehcb.model;
import static com.google.common.base.Preconditions.*;
public class Activity {

    private final int id ;
    private final Path path;
    private final Grid grid;
    private int duration;
    private final ExerciseType exerciseType;

    public Activity(int id, Path path, int duration, ExerciseType exerciseType, Grid grid){
        //precondition check
        checkArgument(id >= 0, "ID must be >= 0");
        checkArgument(duration > 0, "Duration must be > 0");
        checkNotNull(path, "Path cannot be null");
        checkNotNull(exerciseType, "ExerciseType cannot be null");
        checkNotNull(grid, "Grid cannot be null");


        this.id = id;
        this.path = path;
        this.duration = duration;
        this.exerciseType = exerciseType;
        this.grid = grid;
        checkInvariants();
    }

    public int getId() {
        return id;
    }
    public Path getPath() {
        return path;
    }
    public int getDuration() {
        return duration;
    }
    public ExerciseType getType() {
        return exerciseType;
    }

    public void setDuration(int duration) {
        checkArgument(duration > 0, "Duration must be > 0");
        this.duration = duration;
        checkInvariants();
    }

    private void checkInvariants() {

        checkArgument(id >= 0, "ID must be >= 0");

        checkArgument(duration > 0, "Duration must be > 0");

        checkNotNull(path, "Path cannot be null");
        checkArgument(!path.isEmpty(), "Path cannot be empty");

        checkNotNull(exerciseType, "ExerciseType cannot be null");

        checkNotNull(grid, "Grid cannot be null");

        for (Points p : path.getPoints()) {
            checkArgument(grid.isInside(p), "Point outside grid: %s", p);
        }
    }
}
