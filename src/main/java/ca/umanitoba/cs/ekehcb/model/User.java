package ca.umanitoba.cs.ekehcb.model;
import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {
    private String name;
    private final List<Activity> activities;
    private final List<User> following;

    public User(String name) {
        checkArgument(name != null && !name.isBlank(), "User name cannot be null or blank");
        this.name = name;
        this.activities = new ArrayList<>();
        this.following = new ArrayList<>();
        checkInvariants();
    }

    public void addActivity(Activity a) {
        // Precondition
        checkNotNull(a, "Activity cannot be null");

        this.activities.add(a);

        // Postcondition
        checkInvariants();
    }

    public void removeActivity(Activity a) {
        // Precondition
        checkArgument(activities.contains(a), "Activity not found in user list", a);

        this.activities.remove(a);

        // Postcondition
        checkInvariants();
    }

    // Method to follow another user
    public void follow(User u) {
        checkNotNull(u, "User to follow cannot be null");
        checkArgument(!u.equals(this), "A user cannot follow themselves");
        checkArgument(!following.contains(u), "You are already following " + u.getName());
        following.add(u);
        checkInvariants();
    }

    // Method to unfollow another user
    public void unfollow(User other) {
        checkNotNull(other, "Cannot unfollow a null user");
        checkArgument(following.contains(other), "You are not following " + other.getName());
        following.remove(other);
        checkInvariants();
    }

    public void updateName(String newName) {
        checkArgument(newName != null && !newName.isBlank(), "Name cannot be null or blank");
        this.name = newName;
        checkInvariants();
    }

    public List<Activity> getActivities() {
        return Collections.unmodifiableList(activities);
    }

    public List<User> getFollowing() { return Collections.unmodifiableList(following); }

    public String getName() { return name; }

    private void checkInvariants() {
        checkState(name != null && !name.isBlank(), "Error: name is null or blank");
        checkState(activities != null, "Error: activities list is null");
        checkState(following != null, "Error: following list is null");
        checkState(!following.contains(this), "Error: user cannot follow themselves");
    }
}
