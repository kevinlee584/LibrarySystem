package com.example.demo.common;

public class Pair <L, R>{
    private L Left;
    private R Right;

    public Pair(L left, R right) {
        Left = left;
        Right = right;
    }

    public L getLeft() {
        return Left;
    }

    public void setLeft(L left) {
        Left = left;
    }

    public R getRight() {
        return Right;
    }

    public void setRight(R right) {
        Right = right;
    }
}
