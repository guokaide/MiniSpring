package com.minis.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装 ArgumentValue
 */
public class ArgumentValues {
    private final List<ArgumentValue> argumentValues = new ArrayList<>();

    public ArgumentValues() {
    }

    public void addArgumentValue(ArgumentValue argumentValue) {
        this.argumentValues.add(argumentValue);
    }

    public ArgumentValue get(int index) {
        return this.argumentValues.get(index);
    }

    public int size() {
        return this.argumentValues.size();
    }

    public boolean isEmpty() {
        return this.argumentValues.isEmpty();
    }
}
