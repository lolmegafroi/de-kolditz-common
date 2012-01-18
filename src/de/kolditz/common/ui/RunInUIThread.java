/**
 * 
 */
package de.kolditz.common.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * A simple abstract {@link Runnable} that is used to run some application-specific code in the SWT UI thread. Clients
 * need not call the {@link #run()} method because the {@link #execute(Display)} or {@link #execute(Display, boolean)}
 * methods take care of running the code in the UI thread.
 * <p>
 * This class is intended to be used for (highly) asynchronous or multi-threaded applications by giving a simple
 * abstract mechanism for running code appropriately in the {@link SWT} {@link Display}'s thread
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public abstract class RunInUIThread implements Runnable {
    protected Logger log = Logger.getLogger(getClass().getSimpleName());

    /**
     * The run method will either be called directly when the current thread is the UI thread or, otherwise,
     * asynchronously in the UI thread.
     */
    public void execute() {
        execute(null, true);
    }

    /**
     * The run method will either be called directly when the current thread is the UI thread or, otherwise,
     * asynchronously in the UI thread.
     * 
     * @param display
     *            the {@link Display}
     */
    public void execute(Display display) {
        execute(display, true);
    }

    /**
     * The run method will either be called directly when the current thread is the UI thread or, otherwise, depending
     * on async (a)synchronously in the UI thread.
     * 
     * @param display
     *            The {@link Display}.
     * @param async
     *            If set to false this method will block until the UI thread has executed this runner's {@link #run()}
     *            method. Otherwise the {@link #run()} method will be called by the display's
     *            {@link Display#asyncExec(Runnable)} method.
     */
    public void execute(Display display, boolean async) {
        Display d = display;
        if (d == null) {
            log.trace("display is null");
            d = Display.getDefault();
        }
        if (d.isDisposed()) {
            log.debug("display is disposed - aborting");
            return;
        }
        if (d.getThread() == Thread.currentThread()) {
            log.trace("running in UI thread");
            run();
        } else if (async) {
            log.trace("running async");
            d.asyncExec(this);
        } else {
            log.trace("running sync");
            d.syncExec(this);
        }
    }
}
