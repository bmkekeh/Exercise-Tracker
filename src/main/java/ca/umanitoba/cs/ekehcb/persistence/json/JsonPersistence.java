package ca.umanitoba.cs.ekehcb.persistence.json;

import ca.umanitoba.cs.ekehcb.exceptions.InvalidInputException;
import ca.umanitoba.cs.ekehcb.model.*;
import ca.umanitoba.cs.ekehcb.persistence.Persistence;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.google.common.base.Preconditions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonPersistence implements Persistence {

    private static final String FILE_PATH = "tracker-state.json";
    private final ObjectMapper mapper = new ObjectMapper();

    // pre:  tracker != null, grid != null
    // post: state written to tracker-state.json
    @Override
    public void save(ExerciseTypeTracker tracker, Grid grid) {
        checkNotNull(tracker, "Tracker cannot be null");
        checkNotNull(grid, "Grid cannot be null");

        try {
            ObjectNode root = mapper.createObjectNode();

            // save grid dimensions
            ObjectNode gridNode = mapper.createObjectNode();
            gridNode.put("width", grid.getWidth());
            gridNode.put("height", grid.getHeight());

            // save obstacles
            ArrayNode obstaclesNode = mapper.createArrayNode();
            for (Points p : grid.getObstacles()) {
                ObjectNode pointNode = mapper.createObjectNode();
                pointNode.put("x", p.getPointX());
                pointNode.put("y", p.getPointY());
                obstaclesNode.add(pointNode);
            }
            gridNode.set("obstacles", obstaclesNode);
            root.set("grid", gridNode);

            // save users
            ArrayNode usersNode = mapper.createArrayNode();
            for (User user : tracker.getUsers()) {
                ObjectNode userNode = mapper.createObjectNode();
                userNode.put("name", user.getName());

                // save following list as names
                ArrayNode followingNode = mapper.createArrayNode();
                for (User followed : user.getFollowing()) {
                    followingNode.add(followed.getName());
                }
                userNode.set("following", followingNode);

                // save activities
                ArrayNode activitiesNode = mapper.createArrayNode();
                for (Activity activity : user.getActivities()) {
                    ObjectNode activityNode = mapper.createObjectNode();
                    activityNode.put("id", activity.getId());
                    activityNode.put("duration", activity.getDuration());
                    activityNode.put("type", activity.getType().name());

                    // save path points
                    ArrayNode pointsNode = mapper.createArrayNode();
                    for (Points p : activity.getPath().getPoints()) {
                        ObjectNode pointNode = mapper.createObjectNode();
                        pointNode.put("x", p.getPointX());
                        pointNode.put("y", p.getPointY());
                        pointsNode.add(pointNode);
                    }
                    activityNode.set("path", pointsNode);
                    activitiesNode.add(activityNode);
                }
                userNode.set("activities", activitiesNode);
                usersNode.add(userNode);
            }
            root.set("users", usersNode);

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), root);

        } catch (IOException e) {
            System.out.println("Warning: could not save state — " + e.getMessage());
        }
    }

    // pre:  tracker != null, grid != null
    // post: tracker and grid populated from file, no-op if file does not exist
    @Override
    public void load(ExerciseTypeTracker tracker, Grid grid) {
        checkNotNull(tracker, "Tracker cannot be null");
        checkNotNull(grid, "Grid cannot be null");

        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return; // no saved state yet — fresh start
        }

        try {
            JsonNode root = mapper.readTree(file);

            // load obstacles
            JsonNode obstaclesNode = root.path("grid").path("obstacles");
            for (JsonNode pointNode : obstaclesNode) {
                int x = pointNode.get("x").asInt();
                int y = pointNode.get("y").asInt();
                Points p = new Points(x, y);
                if (grid.isInside(p) && !grid.isObstacle(p)) {
                    grid.addObstacle(p);
                }
            }

            // first pass — create all users
            JsonNode usersNode = root.path("users");
            List<User> loadedUsers = new ArrayList<>();
            for (JsonNode userNode : usersNode) {
                String name = userNode.get("name").asText();
                User user = new User(name);

                // load activities
                for (JsonNode activityNode : userNode.path("activities")) {
                    int id = activityNode.get("id").asInt();
                    int duration = activityNode.get("duration").asInt();
                    ExerciseType type = ExerciseType.valueOf(
                            activityNode.get("type").asText());

                    Path path = new Path();
                    for (JsonNode pointNode : activityNode.path("path")) {
                        int x = pointNode.get("x").asInt();
                        int y = pointNode.get("y").asInt();
                        path.addPoint(new Points(x, y));
                    }

                    // only add if path is valid
                    if (!path.isEmpty()) {
                        try {
                            Activity activity = new Activity(id, path, duration, type, grid);
                            user.addActivity(activity);
                        } catch (Exception e) {
                            System.out.println("Warning: skipped invalid activity — "
                                    + e.getMessage());
                        }
                    }
                }

                tracker.registerUser(user);
                loadedUsers.add(user);
            }

            // second pass — restore following relationships
            // (needs all users to exist first)
            int i = 0;
            for (JsonNode userNode : usersNode) {
                User user = loadedUsers.get(i++);
                for (JsonNode followedName : userNode.path("following")) {
                    String name = followedName.asText();
                    for (User other : loadedUsers) {
                        if (other.getName().equals(name)
                                && !other.equals(user)
                                && !user.getFollowing().contains(other)) {
                            user.follow(other);
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Warning: could not load state — " + e.getMessage());
        }
    }
}
