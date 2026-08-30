package ca.umanitoba.cs.ekehcb.model;
import static com.google.common.base.Preconditions.*;
public class Points {
    private final int pointX;
    private final int pointY;

    public Points(int pointX, int pointY){
        //checks pointX and pointY
        checkArgument(pointX >= 0, "X coordinate must be >= 0.", pointX);
        checkArgument(pointY >= 0, "Y coordinate must be >= 0.", pointY);
        this.pointX = pointX;
        this.pointY = pointY;
        checkInvariants();
    }

    public int getPointX(){
        return pointX;
    }

    public int getPointY(){
        return pointY;
    }

    //this method returns an int that represents any object for hash-based collections
    @Override
    public int hashCode() {
        return 31 * pointX + pointY;
    }

    @Override
    public String toString() {
        return "(" + pointX + "," + pointY + ")";
    }

    @Override
    public boolean equals(Object o) {
        //checks if o is the same object
        if (this == o) return true;
        //checking if o is an instance of p
        if (o instanceof Points p){
            return pointX == p.pointX && pointY == p.pointY;
        }
        return false;
    }
    private void checkInvariants() {
        checkState(pointX >= 0, "Error: X coordinate is negative.");
        checkState(pointY >= 0, "Error: Y coordinate is negative.");
    }
}
