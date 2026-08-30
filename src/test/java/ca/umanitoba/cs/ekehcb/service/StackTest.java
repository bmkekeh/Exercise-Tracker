package ca.umanitoba.cs.ekehcb.service;

import ca.umanitoba.cs.comp2450.stack.Stack;
import ca.umanitoba.cs.comp2450.stack.impl.*;

public class StackTest {

    private int passed = 0;
    private int failed = 0;

    public void runAll() {
        System.out.println("\n--- Testing your LinkedStack ---");
        runSuite(new LinkedStack<>(), "LinkedStack");

        System.out.println("\n--- Testing BadStack1 ---");
        runSuite(new BadStack1<>(), "BadStack1");

        System.out.println("\n--- Testing BadStack2 ---");
        runSuite(new BadStack2<>(), "BadStack2");

        System.out.println("\n--- Testing BadStack3 ---");
        runSuite(new BadStack3<>(), "BadStack3");

        System.out.println("\n--- Testing BadStack4 ---");
        runSuite(new BadStack4<>(), "BadStack4");

        System.out.println("\n--- Testing BadStack5 ---");
        runSuite(new BadStack5<>(), "BadStack5");
    }

    private void runSuite(Stack<String> stack, String name) {
        passed = 0;
        failed = 0;

        check(name + " - isEmpty() on new stack returns true",
                stack.isEmpty());

        try {
            Stack<String> s = newStack(name);
            s.push("a");
            check(name + " - isEmpty() returns false after push", !s.isEmpty());
        } catch (Exception e) { fail(name + " - isEmpty() returns false after push", e); }


        try {
            Stack<String> s = newStack(name);
            s.push("a");
            s.pop();
            check(name + " - isEmpty() returns true after popping all", s.isEmpty());
        } catch (Exception e) { fail(name + " - isEmpty() returns true after popping all", e); }


        try {
            check(name + " - size() on new stack returns 0",
                    newStack(name).size() == 0);
        } catch (Exception e) { fail(name + " - size() on new stack returns 0", e); }


        try {
            Stack<String> s = newStack(name);
            s.push("a"); s.push("b"); s.push("c");
            check(name + " - size() returns 3 after 3 pushes", s.size() == 3);
        } catch (Exception e) { fail(name + " - size() returns 3 after 3 pushes", e); }

        try {
            Stack<String> s = newStack(name);
            s.push("a"); s.push("b");
            s.pop();
            check(name + " - size() returns 1 after 2 pushes and 1 pop", s.size() == 1);
        } catch (Exception e) { fail(name + " - size() returns 1 after 2 pushes and 1 pop", e); }


        try {
            Stack<String> s = newStack(name);
            s.push("hello");
            check(name + " - push() one element: size is 1", s.size() == 1);
            check(name + " - push() one element: isEmpty() is false", !s.isEmpty());
            check(name + " - push() one element: peek() returns 'hello'", "hello".equals(s.peek()));
        } catch (Exception e) { fail(name + " - push() one element", e); }


        try {
            Stack<String> s = newStack(name);
            s.push("first"); s.push("second");
            check(name + " - push() multiple: last pushed is on top", "second".equals(s.peek()));
        } catch (Exception e) { fail(name + " - push() multiple: last pushed is on top", e); }


        try {
            Stack<String> s = newStack(name);
            s.push("a"); s.push("b");
            check(name + " - peek() returns top element", "b".equals(s.peek()));
        } catch (Exception e) { fail(name + " - peek() returns top element", e); }


        try {
            Stack<String> s = newStack(name);
            s.push("a"); s.push("b");
            s.peek();
            check(name + " - peek() does not remove element, size still 2", s.size() == 2);
        } catch (Exception e) { fail(name + " - peek() does not remove element", e); }


        try {
            Stack<String> s = newStack(name);
            s.push("x"); s.push("y");
            check(name + " - pop() returns top element 'y'", "y".equals(s.pop()));
        } catch (Exception e) { fail(name + " - pop() returns top element", e); }


        try {
            Stack<String> s = newStack(name);
            s.push("a"); s.push("b");
            s.pop();
            check(name + " - pop() reduces size by 1", s.size() == 1);
        } catch (Exception e) { fail(name + " - pop() reduces size by 1", e); }


        try {
            Stack<String> s = newStack(name);
            s.push("first"); s.push("second"); s.push("third");
            check(name + " - pop() LIFO: returns 'third' first", "third".equals(s.pop()));
            check(name + " - pop() LIFO: returns 'second' next", "second".equals(s.pop()));
            check(name + " - pop() LIFO: returns 'first' last", "first".equals(s.pop()));
        } catch (Exception e) { fail(name + " - pop() LIFO order", e); }


        try {
            Stack<String> s = newStack(name);
            s.push("a"); s.pop();
            check(name + " - stack empty after popping all", s.isEmpty());
        } catch (Exception e) { fail(name + " - stack empty after popping all", e); }


        try {
            Stack<String> s = newStack(name);
            s.pop();
            check(name + " - pop() on empty stack throws exception", false);
        } catch (Exception e) {
            check(name + " - pop() on empty stack throws exception", true);
        }


        try {
            Stack<String> s = newStack(name);
            s.peek();
            check(name + " - peek() on empty stack throws exception", false);
        } catch (Exception e) {
            check(name + " - peek() on empty stack throws exception", true);
        }

        System.out.println("  >> " + passed + " passed, " + failed + " failed");
    }

    private void fail(String testName, Exception e) {
        System.out.println("  FAIL: " + testName + " (crashed: " + e.getMessage() + ")");
        failed++;
    }

    private Stack<String> newStack(String name) {
        return switch (name) {
            case "LinkedStack" -> new LinkedStack<>();
            case "BadStack1"   -> new ca.umanitoba.cs.comp2450.stack.impl.BadStack1<>();
            case "BadStack2"   -> new ca.umanitoba.cs.comp2450.stack.impl.BadStack2<>();
            case "BadStack3"   -> new ca.umanitoba.cs.comp2450.stack.impl.BadStack3<>();
            case "BadStack4"   -> new ca.umanitoba.cs.comp2450.stack.impl.BadStack4<>();
            case "BadStack5"   -> new ca.umanitoba.cs.comp2450.stack.impl.BadStack5<>();
            default -> throw new IllegalArgumentException("Unknown stack: " + name);
        };
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
}