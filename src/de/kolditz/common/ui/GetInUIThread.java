/**
 * created on 13.12.2011 at 14:56:08
 */
package de.kolditz.common.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * A simple abstract {@link Runnable} that is used to retrieve some widget(s)'s value(s) from inside the SWT UI thread.
 * Clients need not call the {@link #run()} method because the {@link #get(Display)} method takes care of running the
 * code in the UI thread.
 * <p>
 * This class is intended to be used for (highly) asynchronous or multi-threaded applications by giving a simple
 * abstract mechanism for running code appropriately in the {@link SWT} {@link Display}'s thread
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public abstract class GetInUIThread<E> implements Runnable {
    /**
     * Convenience class for single {@link Text} widgets.
     * 
     * @see GetInUIThread
     * @author Till Kolditz - Till.Kolditz@GoogleMail.com
     */
    public static class GetTextValue extends GetInUIThread<String> {
        private String str;
        private Text text;

        public GetTextValue(Text text) {
            this.text = text;
        }

        @Override
        public void run() {
            str = text.getText();
        }

        /**
         * Blocking call.
         * 
         * @return the {@link Text}'s text.
         */
        public String get() {
            get(text.getDisplay());
            return str;
        }
    }

    protected Logger log = Logger.getLogger(getClass().getSimpleName());

    /**
     * Retrieves the value for this abstract runner. This call is blocking.
     * 
     * @param display
     *            The {@link Display}.
     */
    protected void get(Display display) {
        if (display == null) {
            log.trace("display is null");
            return;
        } else if (display.isDisposed()) {
            log.trace("display is disposed");
            return;
        } else if (display.getThread() == Thread.currentThread()) {
            log.trace("running in UI thread");
            run();
        } else {
            log.trace("running sync");
            display.syncExec(this);
        }
    }
}
