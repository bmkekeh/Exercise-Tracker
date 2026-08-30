package ca.umanitoba.cs.ekehcb.output;

import ca.umanitoba.cs.ekehcb.model.Activity;
import ca.umanitoba.cs.ekehcb.model.User;
import java.util.List;

public class ActivityPrinter {

    public void printActivities(List<Activity> activities) {
        System.out.println("\n--- Your Activities ---");
        if (activities.isEmpty()) {
            System.out.println("No activities found.");
            return;
        }
        for (Activity a : activities) {
            System.out.println("ID: " + a.getId()
                    + " | Type: " + a.getType()
                    + " | Duration: " + a.getDuration() + " min"
                    + " | Points: " + a.getPath().getPoints().size());
        }
    }

    public void printFeed(List<Activity> feed, User currentUser) {
        System.out.println("\n--- Feed ---");
        for (Activity a : feed) {
            boolean mine = currentUser.getActivities().contains(a);
            String owner = mine ? currentUser.getName() : resolveOwner(a, currentUser);
            System.out.println("[" + owner + "] "
                    + a.getType()
                    + " | " + a.getDuration() + " min"
                    + " | " + a.getPath().getPoints().size() + " points");
        }
    }

    public void printSummary(List<Activity> activities) {
        System.out.println("\n--- Summary ---");
        int total = 0;
        for (Activity a : activities) total += a.getDuration();
        System.out.println("Total activities: " + activities.size());
        System.out.println("Total duration:   " + total + " min");
    }

    private String resolveOwner(Activity a, User currentUser) {
        for (User followed : currentUser.getFollowing()) {
            if (followed.getActivities().contains(a)) return followed.getName();
        }
        return "Unknown";
    }
}
