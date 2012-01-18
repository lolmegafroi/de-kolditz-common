/**
 * created on 13.12.2011 at 14:31:07
 */
package de.kolditz.common.ui.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.kolditz.common.IObservable;
import de.kolditz.common.IObservableBackend;
import de.kolditz.common.IObserver;
import de.kolditz.common.Pair;
import de.kolditz.common.ui.GetInUIThread.GetTextValue;
import de.kolditz.common.ui.SetInUIThread.SetTextValue;

/**
 * Preferences Text field
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class TextField extends Composite implements IObservable<String>, ModifyListener, FocusListener {
    protected Label label;
    protected Text text;
    protected String labelString;
    protected String null_hint;
    protected IObservableBackend<String> backEnd;
    protected boolean doUpdateBackEnd;
    protected Pair<String, String> pair;
    protected SetTextValue setter;
    protected GetTextValue getter;

    /**
     * @param parent
     * @param style
     * @param label
     */
    public TextField(Composite parent, int style, String label) {
        this(parent, style, label, "");
    }

    /**
     * @param parent
     * @param style
     * @param label
     * @param null_hint
     */
    public TextField(Composite parent, int style, String label, String null_hint) {
        super(parent, style);
        labelString = label;
        this.null_hint = null_hint != null ? null_hint : ""; //$NON-NLS-1$

        create();
        setLabels();
        addListeners();

        backEnd = new IObservableBackend<String>(this);
        doUpdateBackEnd = true;
        pair = new Pair<String, String>(null, null);
        setter = new SetTextValue(text);
        getter = new GetTextValue(text);
    }

    protected void create() {
        GridLayout gl = new GridLayout(2, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        setLayout(gl);
        label = new Label(this, SWT.NONE);
        text = new Text(this, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    protected void setLabels() {
        doUpdateBackEnd = false;
        label.setText(labelString);
        text.setText(null_hint);
        doUpdateBackEnd = true;
    }

    protected void addListeners() {
        text.addModifyListener(this);
        text.addFocusListener(this);
    }

    public void setValue(String value) {
        setValue(value, true);
    }

    public void setValue(String value, boolean triggerUpdate) {
        String actualVal = value;
        if (value == null || value.length() == 0) {
            actualVal = null_hint;
        }
        doUpdateBackEnd = triggerUpdate;
        setter.setValue(getDisplay(), pair.first(actualVal).second(actualVal), false);
        doUpdateBackEnd = true;
    }

    public String getValue() {
        String str = getter.get();
        if (str.equals(null_hint)) {
            return null;
        }
        return str;
    }

    @Override
    public void setEnabled(boolean enabled) {
        text.setEnabled(enabled);
        // super.setEnabled(enabled);
    }

    /**
     * Registers an {@link IObserver} for modification events. The data object is the new text.
     */
    @Override
    public boolean registerObserver(IObserver<String> observer) {
        return backEnd.registerObserver(observer);
    }

    /**
     * Unregisters an {@link IObserver} from modification events.
     */
    @Override
    public boolean unregisterObserver(IObserver<String> observer) {
        return backEnd.unregisterObserver(observer);
    }

    @Override
    public void focusGained(FocusEvent e) {
        doUpdateBackEnd = false;
        if (text.getText().equals(null_hint)) {
            text.setText("");
        }
        doUpdateBackEnd = true;
    }

    @Override
    public void focusLost(FocusEvent e) {
        doUpdateBackEnd = false;
        if (text.getText().equals("")) {
            text.setText(null_hint);
        }
        doUpdateBackEnd = true;
    }

    @Override
    public void modifyText(ModifyEvent e) {
        if (text.getText().equals(null_hint)) {
            text.setForeground(getDisplay().getSystemColor(SWT.COLOR_GRAY));
        } else {
            text.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
        }
        if (doUpdateBackEnd) {
            backEnd.update(getValue());
        }
    }

    /**
     * Sets this {@link TextField}'s text
     * 
     * @param text
     *            the text
     */
    public void setText(String text) {
        this.text.setText(text);
    }

    /**
     * @return this {@link TextField}'s text
     */
    public String getText() {
        return text.getText();
    }
}
