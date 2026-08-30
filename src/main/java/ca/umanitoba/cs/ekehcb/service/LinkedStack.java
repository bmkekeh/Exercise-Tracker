package ca.umanitoba.cs.ekehcb.service;

import ca.umanitoba.cs.comp2450.stack.Stack;

import static com.google.common.base.Preconditions.*;

public class LinkedStack<T> implements Stack<T> {
    private StackNode<T> top;
    private int size;

    public LinkedStack() {
        this.top = null;
        this.size = 0;
        checkInvariants();
    }

    @Override
    public void push(T data) {
        checkNotNull(data, "Cannot push null onto stack");
        StackNode<T> node = new StackNode<>(data);
        node.next = top;
        top = node;
        size++;
        checkInvariants();
    }

    @Override
    public T pop() {
        checkState(!isEmpty(), "Cannot pop from an empty stack");
        T data = top.data;
        top = top.next;
        size--;
        checkInvariants();
        return data;
    }

    @Override
    public T peek() {
        checkState(!isEmpty(), "Cannot peek an empty stack");
        return top.data;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private void checkInvariants() {
        checkState(size >= 0, "Invariant violated: size cannot be negative");
        checkState((size == 0) == (top == null), "Invariant violated: top must be null if and only if size is 0");
    }
}