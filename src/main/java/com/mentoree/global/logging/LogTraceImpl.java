package com.mentoree.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogTraceImpl implements LogTrace{

    private ThreadLocal<TraceStatus> traceHolder = new ThreadLocal<>();

    @Override
    public void begin() {
        TraceStatus curTraceStatus;

        if(traceHolder.get() == null) {
            curTraceStatus = new TraceStatus();
        } else {
            curTraceStatus = traceHolder.get().next();
        }
        traceHolder.set(curTraceStatus);
    }

    @Override
    public void complete() {
        TraceStatus traceStatus = traceHolder.get();
        if(traceStatus.isFirstLevel()) {
            traceHolder.remove();
        } else {
            traceHolder.set(traceStatus.previous());
        }
    }

    @Override
    public void error() {
        traceHolder.remove();
    }

    @Override
    public TraceStatus getCurrentTrace() {
        return traceHolder.get();
    }

}
