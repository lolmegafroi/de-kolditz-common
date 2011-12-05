package de.kolditz.common.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.custom.StyledText;

/**
 * An appender that logs to a text field. Does not handle Exceptions.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class TextAppender extends AppenderSkeleton {
    private class AppendRunner implements Runnable {
        String text;

        AppendRunner(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            if (tfLog != null) {
                if (text != null) {
                    tfLog.append(text);
                } else {
                    tfLog.setText(""); //$NON-NLS-1$
                }
            }
        }
    }

    public static final String COMPLEX = PatternLayout.TTCC_CONVERSION_PATTERN;
    public static final String COMPLEX_NAME = "Complex";
    public static final String SIMPLE = "%-5p - %m%n"; //$NON-NLS-1$
    public static final String SIMPLE_NAME = "Simple";

    private final PatternLayout COMPLEX_LAYOUT = new PatternLayout(COMPLEX);
    private final PatternLayout SIMPLE_LAYOUT = new PatternLayout(SIMPLE);

    private StyledText tfLog;
    private List<LoggingEvent> events;

    public TextAppender() {
        this(null);
    }

    public TextAppender(StyledText tfLog) {
        this.tfLog = tfLog;
        // use the same as the BasicConfigurator sets on ConsoleAppender
        setLayout(SIMPLE_LAYOUT);
        super.setThreshold(Level.ALL);
        // store them to change log style
        events = new ArrayList<LoggingEvent>();
    }

    public void setTfLog(StyledText tfLog) {
        this.tfLog = tfLog;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    @Override
    public synchronized void doAppend(LoggingEvent event) {
        events.add(event);
        super.doAppend(event);
    }

    @Override
    protected synchronized void append(LoggingEvent event) {
        update(layout.format(event));
    }

    private void update(String text) {
        if (tfLog == null || tfLog.isDisposed()) {
            return;
        }
        tfLog.getDisplay().asyncExec(new AppendRunner(text));
    }

    public void clear() {
        update(null);
    }

    private void recreate() {
        if (tfLog != null && !tfLog.isDisposed()) {
            tfLog.getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    // again check if disposed as we run "async" in display thread
                    if (tfLog != null && !tfLog.isDisposed()) {
                        tfLog.setRedraw(false);
                        tfLog.setText("");
                        for (LoggingEvent e : events) {
                            if (isAsSevereAsThreshold(e.getLevel()))
                                tfLog.append(layout.format(e));
                        }
                        tfLog.setRedraw(true);
                    }
                }
            });
        }
    }

    @Override
    public void setThreshold(Priority threshold) {
        super.setThreshold(threshold);
        recreate();
    }

    public void setStyle(String name) {
        if (name.equalsIgnoreCase(COMPLEX_NAME)) {
            setLayout(COMPLEX_LAYOUT);
        } else {
            setLayout(SIMPLE_LAYOUT);
        }
        recreate();
    }
}