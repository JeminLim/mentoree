package com.mentoree.global.logging;

public interface LogTrace {

    void begin();
    void complete();
    void error();
    TraceStatus getCurrentTrace();

}
