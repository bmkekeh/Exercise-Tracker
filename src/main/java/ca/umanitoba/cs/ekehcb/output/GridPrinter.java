package ca.umanitoba.cs.ekehcb.output;

import ca.umanitoba.cs.ekehcb.model.*;
import java.util.List;

public class GridPrinter {

    public void printMap(Grid grid, List<Activity> activitiesToHighlight) {
        if (grid == null) {
            System.out.println("Error: No map to display.");
            return;
        }
        printLegend();
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Points p = new Points(x, y);
                char symbol = '.';
                if (grid.isObstacle(p)) {
                    symbol = '*';
                } else if (isPointInAnyRoute(p, activitiesToHighlight)) {
                    symbol = '>';
                }
                System.out.print(symbol + " ");
            }
            System.out.println();
        }
    }

    private void printLegend() {
        System.out.println("\nLegend: . = Empty  * = Obstacle  > = Route");
        System.out.println("-------------------------------------------");
    }

    private boolean isPointInAnyRoute(Points p, List<Activity> activities) {
        if (activities == null) return false;
        for (Activity a : activities) {
            if (a.getPath().contains(p)) return true;
        }
        return false;
    }
}