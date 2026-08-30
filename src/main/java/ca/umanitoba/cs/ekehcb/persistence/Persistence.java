package ca.umanitoba.cs.ekehcb.persistence;

import ca.umanitoba.cs.ekehcb.model.ExerciseTypeTracker;
import ca.umanitoba.cs.ekehcb.model.Grid;

public interface Persistence {

    // pre:  tracker != null, grid != null
    // post: state written to file
    void save(ExerciseTypeTracker tracker, Grid grid);

    // pre:  file exists and is valid
    // post: tracker and grid populated with saved state
    void load(ExerciseTypeTracker tracker, Grid grid);
}