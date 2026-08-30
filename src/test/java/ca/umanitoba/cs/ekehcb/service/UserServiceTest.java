package ca.umanitoba.cs.ekehcb.service;

import ca.umanitoba.cs.ekehcb.exceptions.*;
import ca.umanitoba.cs.ekehcb.model.*;

public class UserServiceTest {

    private int passed = 0;
    private int failed = 0;

    public void runAll() {
        System.out.println("\n--- Testing UserService ---");

        testCreateUser();
        testCreateUserBlankName();
        testCreateUserDuplicateName();
        testSelectUser();
        testSelectUserNotFound();
        testUpdateProfile();
        testUpdateProfileBlankName();
        testUpdateProfileDuplicateName();
        testFollowUser();
        testFollowYourself();
        testFollowAlreadyFollowing();
        testUnfollowUser();
        testUnfollowNotFollowing();
        testGetAllUsers();

        System.out.println("  >> " + passed + " passed, " + failed + " failed");
    }

    // -------------------------
    // createUser tests
    // -------------------------

    private void testCreateUser() {
        try {
            UserService service = freshService();
            User user = service.createUser("Alice");
            check("createUser() returns user with correct name",
                    "Alice".equals(user.getName()));
            check("createUser() registers user in tracker",
                    service.getAllUsers().contains(user));
        } catch (Exception e) { fail("createUser() valid name", e); }
    }

    private void testCreateUserBlankName() {
        try {
            UserService service = freshService();
            service.createUser("   ");
            check("createUser() blank name throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("createUser() blank name throws InvalidInputException", true);
        } catch (Exception e) { fail("createUser() blank name", e); }
    }


    private void testCreateUserDuplicateName() {
        try {
            UserService service = freshService();
            service.createUser("Alice");
            service.createUser("Alice");
            check("createUser() duplicate name throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("createUser() duplicate name throws InvalidInputException", true);
        } catch (Exception e) { fail("createUser() duplicate name", e); }
    }

    // -------------------------
    // selectUser tests
    // -------------------------

    private void testSelectUser() {
        try {
            UserService service = freshService();
            service.createUser("Bob");
            User found = service.selectUser("Bob");
            check("selectUser() returns correct user",
                    "Bob".equals(found.getName()));
        } catch (Exception e) { fail("selectUser() existing user", e); }
    }

    private void testSelectUserNotFound() {
        try {
            UserService service = freshService();
            service.selectUser("Nobody");
            check("selectUser() unknown name throws UserNotFoundException", false);
        } catch (UserNotFoundException e) {
            check("selectUser() unknown name throws UserNotFoundException", true);
        } catch (Exception e) { fail("selectUser() not found", e); }
    }

    // -------------------------
    // updateProfile tests
    // -------------------------

    private void testUpdateProfile() {
        try {
            UserService service = freshService();
            User user = service.createUser("Alice");
            service.updateProfile(user, "Alicia");
            check("updateProfile() changes user name",
                    "Alicia".equals(user.getName()));
        } catch (Exception e) { fail("updateProfile() valid name", e); }
    }

    private void testUpdateProfileBlankName() {
        try {
            UserService service = freshService();
            User user = service.createUser("Alice");
            service.updateProfile(user, "");
            check("updateProfile() blank name throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("updateProfile() blank name throws InvalidInputException", true);
        } catch (Exception e) { fail("updateProfile() blank name", e); }
    }

    private void testUpdateProfileDuplicateName() {
        try {
            UserService service = freshService();
            User alice = service.createUser("Alice");
            service.createUser("Bob");
            service.updateProfile(alice, "Bob");
            check("updateProfile() taken name throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("updateProfile() taken name throws InvalidInputException", true);
        } catch (Exception e) { fail("updateProfile() duplicate name", e); }
    }

    // -------------------------
    // followUser tests
    // -------------------------

    private void testFollowUser() {
        try {
            UserService service = freshService();
            User alice = service.createUser("Alice");
            service.createUser("Bob");
            service.followUser(alice, "Bob");
            check("followUser() adds user to following list",
                    alice.getFollowing().size() == 1);
            check("followUser() correct user is followed",
                    "Bob".equals(alice.getFollowing().get(0).getName()));
        } catch (Exception e) { fail("followUser() valid", e); }
    }

    private void testFollowYourself() {
        try {
            UserService service = freshService();
            User alice = service.createUser("Alice");
            service.followUser(alice, "Alice");
            check("followUser() self throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("followUser() self throws InvalidInputException", true);
        } catch (Exception e) { fail("followUser() self", e); }
    }

    private void testFollowAlreadyFollowing() {
        try {
            UserService service = freshService();
            User alice = service.createUser("Alice");
            service.createUser("Bob");
            service.followUser(alice, "Bob");
            service.followUser(alice, "Bob");
            check("followUser() already following throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("followUser() already following throws InvalidInputException", true);
        } catch (Exception e) { fail("followUser() already following", e); }
    }

    // -------------------------
    // unfollowUser tests
    // -------------------------

    private void testUnfollowUser() {
        try {
            UserService service = freshService();
            User alice = service.createUser("Alice");
            service.createUser("Bob");
            service.followUser(alice, "Bob");
            service.unfollowUser(alice, "Bob");
            check("unfollowUser() removes user from following list",
                    alice.getFollowing().isEmpty());
        } catch (Exception e) { fail("unfollowUser() valid", e); }
    }

    private void testUnfollowNotFollowing() {
        try {
            UserService service = freshService();
            User alice = service.createUser("Alice");
            service.createUser("Bob");
            service.unfollowUser(alice, "Bob");
            check("unfollowUser() not following throws InvalidInputException", false);
        } catch (InvalidInputException e) {
            check("unfollowUser() not following throws InvalidInputException", true);
        } catch (Exception e) { fail("unfollowUser() not following", e); }
    }

    // -------------------------
    // getAllUsers tests
    // -------------------------

    private void testGetAllUsers() {
        try {
            UserService service = freshService();
            service.createUser("Alice");
            service.createUser("Bob");
            service.createUser("Charlie");
            check("getAllUsers() returns 3 users",
                    service.getAllUsers().size() == 3);
        } catch (Exception e) { fail("getAllUsers()", e); }
    }

    // -------------------------
    // Helpers
    // -------------------------

    private UserService freshService() {
        return new UserService(new ExerciseTypeTracker());
    }

    private void check(String testName, boolean condition) {
        if (condition) {
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName);
            failed++;
        }
    }

    private void fail(String testName, Exception e) {
        System.out.println("  FAIL: " + testName + " (crashed: " + e.getMessage() + ")");
        failed++;
    }
}
