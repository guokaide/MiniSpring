package com.minis.beans.factory.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装 ArgumentValue
 */
public class ConstructArgumentValues {
    private final List<ConstructorArgumentValue> argumentValues = new ArrayList<>();

    public ConstructArgumentValues() {
    }

    public void addArgumentValue(ConstructorArgumentValue argumentValue) {
        this.argumentValues.add(argumentValue);
    }

    public ConstructorArgumentValue get(int index) {
        return this.argumentValues.get(index);
    }

    public int size() {
        return this.argumentValues.size();
    }

    public boolean isEmpty() {
        return this.argumentValues.isEmpty();
    }
}
