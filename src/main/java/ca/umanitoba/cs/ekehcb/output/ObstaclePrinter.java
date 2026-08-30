package ca.umanitoba.cs.ekehcb.output;

import ca.umanitoba.cs.ekehcb.model.Grid;
import ca.umanitoba.cs.ekehcb.model.Points;
import java.util.List;
// This class is only responsible for listing obstacles with their IDs for the REMOVE OBSTACLE command.
public class ObstaclePrinter {

    public void printObstacleList(Grid grid) {
        if (grid == null) return;

        List<Points> obstacles = grid.getObstacles();
        System.out.println("\n--- Map Obstacles ---");
        for (int i = 0; i < obstacles.size(); i++) {
            Points p = obstacles.get(i);
            System.out.println("ID [" + i + "]: Location (" + p.getPointX() + ", " + p.getPointY() + ")");
        }
    }
}
