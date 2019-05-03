package com.factor18.oss;

public class CyclicDependencyException extends Exception {
    CyclicDependencyException(String s) {
        super(s);
    }
}
