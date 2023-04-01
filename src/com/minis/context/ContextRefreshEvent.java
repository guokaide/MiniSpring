package com.minis.context;

import java.io.Serial;

public class ContextRefreshEvent extends ApplicationEvent {
    @Serial
    private static final long serialVersionUID = 1L;

    public ContextRefreshEvent(Object source) {
        super(source);
    }

    @Override
    public String toString() {
        return this.msg;
    }
}
