package ca.umanitoba.cs.ekehcb.service;

import ca.umanitoba.cs.ekehcb.exceptions.*;
import ca.umanitoba.cs.ekehcb.model.*;
import static com.google.common.base.Preconditions.*;
import java.util.List;

public class UserService {
    private final ExerciseTypeTracker tracker;

    public UserService(ExerciseTypeTracker tracker) {
        checkNotNull(tracker, "Tracker cannot be null");
        this.tracker = tracker;
        checkInvariants();
    }

    // pre: name != null and not blank
    // post: new User added to tracker, returns the created User
    public User createUser(String name) throws InvalidInputException {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("Name cannot be blank. Please enter a valid name.");
        }
        for (User u : tracker.getUsers()) {
            if (u.getName().equalsIgnoreCase(name)) {
                throw new InvalidInputException("A user named '" + name + "' already exists. Please choose a different name.");
            }
        }
        User user = new User(name);
        tracker.registerUser(user);
        return user;
    }

    // pre: name != null and not blank
    // post: returns User with matching name
    public User selectUser(String name) throws UserNotFoundException, InvalidInputException {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("Name cannot be blank. Please enter a valid name.");
        }
        for (User u : tracker.getUsers()) {
            if (u.getName().equalsIgnoreCase(name)) {
                return u;
            }
        }
        throw new UserNotFoundException("No user named '" + name + "' found. Please create a profile first.");
    }

    // pre: user != null, newName != null and not blank
    // post: user's name is updated
    public void updateProfile(User user, String newName) throws InvalidInputException {
        if (newName == null || newName.isBlank()) {
            throw new InvalidInputException("New name cannot be blank. Please enter a valid name.");
        }
        for (User u : tracker.getUsers()) {
            if (u != user && u.getName().equalsIgnoreCase(newName)) {
                throw new InvalidInputException("A user named '" + newName + "' already exists. Please choose a different name.");
            }
        }
        user.updateName(newName);
    }

    // pre: currentUser != null, targetName != null and not blank
    // post: currentUser follows target user
    public void followUser(User currentUser, String targetName) throws UserNotFoundException, InvalidInputException {
        if (targetName == null || targetName.isBlank()) {
            throw new InvalidInputException("Username cannot be blank. Please enter a valid name.");
        }
        if (currentUser.getName().equalsIgnoreCase(targetName)) {
            throw new InvalidInputException("You cannot follow yourself.");
        }
        User target = selectUser(targetName);
        for (User u : currentUser.getFollowing()) {
            if (u.getName().equalsIgnoreCase(targetName)) {
                throw new InvalidInputException("You are already following '" + targetName + "'.");
            }
        }
        currentUser.follow(target);
    }

    // pre: currentUser != null, targetName != null and not blank
    // post: currentUser unfollows target user
    public void unfollowUser(User currentUser, String targetName) throws UserNotFoundException, InvalidInputException {
        if (targetName == null || targetName.isBlank()) {
            throw new InvalidInputException("Username cannot be blank. Please enter a valid name.");
        }
        if (currentUser.getName().equalsIgnoreCase(targetName)) {
            throw new InvalidInputException("You cannot unfollow yourself.");
        }
        User target = selectUser(targetName);
        for (User u : currentUser.getFollowing()) {
            if (u.getName().equalsIgnoreCase(targetName)) {
                currentUser.unfollow(target);
                return;
            }
        }
        throw new InvalidInputException("You are not following '" + targetName + "'. Use 'SHOW FOLLOWING' to see who you follow.");
    }


    public List<User> getAllUsers() {
        return tracker.getUsers();
    }

    private void checkInvariants() {
        checkState(tracker != null, "Invariant violated: tracker cannot be null");
    }
}