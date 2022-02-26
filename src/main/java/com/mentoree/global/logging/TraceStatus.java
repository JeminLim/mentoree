package com.mentoree.global.logging;

import java.util.UUID;

public class TraceStatus {

    private String id;
    private int level;

    public TraceStatus() {
        this.id = createId();
        this.level = 0;
    }

    private TraceStatus(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public TraceStatus next() {
        return new TraceStatus(id, level+1);
    }

    public TraceStatus previous() {
        return new TraceStatus(id, level-1);
    }

    public boolean isFirstLevel() {
        return level == 0;
    }

    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

}
