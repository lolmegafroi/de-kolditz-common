/**
 * created on 11.01.2012 at 12:57:41
 */
package de.kolditz.common.ui.preferences;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * A {@link FileField} that points to an executable file and contains an additional button for running it.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class RunnableFileField extends FileField {
    protected Button btnRun;

    /**
     * @param parent
     * @param style
     * @param label
     */
    public RunnableFileField(Composite parent, int style, String label) {
        super(parent, style, label);
    }

    public RunnableFileField(Composite parent, int style, String label, String null_hint) {
        super(parent, style, label, null_hint);
    }

    @Override
    protected void create() {
        super.create();
        ++((GridLayout) getLayout()).numColumns;

        btnRun = new Button(this, SWT.PUSH);
    }

    @Override
    protected void setLabels() {
        super.setLabels();
        btnRun.setText("Run");
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        btnRun.addSelectionListener(this);
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        if (e.widget == btnRun) {
            if (!cdFile.isVisible()) {
                try {
                    Runtime.getRuntime().exec(text.getText());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else {
            super.widgetSelected(e);
        }
    }
}
