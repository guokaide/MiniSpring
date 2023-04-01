package com.minis.context;

import java.util.EventListener;

public class ApplicationEventListener implements EventListener {
    void onApplicationEvent(ApplicationEvent event) {
        System.out.println(event.toString());
    }
}
