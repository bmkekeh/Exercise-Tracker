package ca.umanitoba.cs.ekehcb;

import ca.umanitoba.cs.ekehcb.service.ActivityServiceTest;
import ca.umanitoba.cs.ekehcb.service.MapServiceTest;
import ca.umanitoba.cs.ekehcb.service.StackTest;
import ca.umanitoba.cs.ekehcb.service.UserServiceTest;

public class TestHarness {

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("   COMP 2450 Test Harness        ");
        System.out.println("=================================");

        new StackTest().runAll();
        new UserServiceTest().runAll();
        new ActivityServiceTest().runAll();
        new MapServiceTest().runAll();

        System.out.println("\n=================================");
        System.out.println("       All tests complete         ");
        System.out.println("=================================");
    }
}