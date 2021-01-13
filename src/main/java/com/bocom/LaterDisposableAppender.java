package com.bocom;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Log message may be discarded later
 */
public class LaterDisposableAppender extends UnsynchronizedAppenderBase<ILoggingEvent> implements AppenderAttachable<ILoggingEvent>  {

    AppenderAttachableImpl<ILoggingEvent> aai = new AppenderAttachableImpl<ILoggingEvent>();

    public static String FLUSH_KEY_WORD = "DisposableAppender__FLUSH__";
    public static String DISCARD_KEY_WORD = "DisposableAppender__DISCARD__";
    // incase cache is too large
    private static int MAX_CACHE = 10000;
    private static ThreadLocal<List<ILoggingEvent>> eventThreadLocal = new ThreadLocal<List<ILoggingEvent>>();

    static{
        eventThreadLocal.set(new ArrayList<ILoggingEvent>());
    }

    private int appenderCount;


    protected void append(ILoggingEvent eventObject) {
        // 强制flush
        if(eventThreadLocal.get().size() >= MAX_CACHE){
            flush();
        }

        // cache eventObject
        eventThreadLocal.get().add(eventObject);
//        eventObje

        // determine whether to flush or discard
        if(FLUSH_KEY_WORD.equals(eventObject.getLoggerName())){
            flush();
        }else if(DISCARD_KEY_WORD.equals(eventObject.getLoggerName())){
            discard();

        }
    }

    /**
     * trigger discard
     */
    private void discard() {
        eventThreadLocal.get().clear();
    }

    /**
     * trigger flush
     */
    private void flush(){
        for(ILoggingEvent event: eventThreadLocal.get()){
            aai.appendLoopOnAppenders(event);
        }
    }

    public void addAppender(Appender newAppender) {
        if (appenderCount == 0) {
            appenderCount++;
            addInfo("Attaching appender named [" + newAppender.getName() + "] to LaterDisposableAppender.");
            aai.addAppender(newAppender);
        } else {
            addWarn("One and only one appender may be attached to LaterDisposableAppender.");
            addWarn("Ignoring additional appender named [" + newAppender.getName() + "]");
        }
    }
    public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() {
        return aai.iteratorForAppenders();
    }

    public Appender<ILoggingEvent> getAppender(String name) {
        return aai.getAppender(name);
    }

    public boolean isAttached(Appender<ILoggingEvent> eAppender) {
        return aai.isAttached(eAppender);
    }

    public void detachAndStopAllAppenders() {
        aai.detachAndStopAllAppenders();
    }

    public boolean detachAppender(Appender<ILoggingEvent> eAppender) {
        return aai.detachAppender(eAppender);
    }

    public boolean detachAppender(String name) {
        return aai.detachAppender(name);
    }
}
