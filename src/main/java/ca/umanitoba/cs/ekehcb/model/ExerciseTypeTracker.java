package ca.umanitoba.cs.ekehcb.model;
import static com.google.common.base.Preconditions.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExerciseTypeTracker {
    private final List<User> users;

    public ExerciseTypeTracker() {
        this.users = new ArrayList<>();
        checkInvariant();
    }

    public void registerUser(User u) {
        checkNotNull(u, "Cannot register a null user");
        users.add(u);
        checkInvariant();
    }

    public void removeUser(User u) {
        checkArgument(users.contains(u), "User not found in tracker");
        users.remove(u);
        checkInvariant();
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    private void checkInvariant() {
        checkState(users != null, "Invariant Violated: users list cannot be null");
    }
}
