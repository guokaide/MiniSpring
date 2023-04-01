package com.minis.context;

import java.util.ArrayList;
import java.util.List;

public class SimpleApplicationEventPublisher implements ApplicationEventPublisher {
    private final List<ApplicationEventListener> listeners = new ArrayList<>();

    @Override
    public void publishEvent(ApplicationEvent event) {
        for (ApplicationEventListener listener : listeners) {
            listener.onApplicationEvent(event);
        }
    }

    @Override
    public void addEventListener(ApplicationEventListener listener) {
        this.listeners.add(listener);
    }
}
