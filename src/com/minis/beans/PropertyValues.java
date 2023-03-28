package com.minis.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装 Property
 */
public class PropertyValues {
    private final List<PropertyValue> propertyValues = new ArrayList<>();

    public PropertyValues() {
    }

    public void addPropertyValue(PropertyValue propertyValue) {
        this.propertyValues.add(propertyValue);
    }

    public PropertyValue get(int index) {
        return this.propertyValues.get(index);
    }

    public int size() {
        return this.propertyValues.size();
    }

    public boolean isEmpty() {
        return this.propertyValues.isEmpty();
    }
}
