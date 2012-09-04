/**
 * 
 */
package de.kolditz.common.ui.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import de.kolditz.common.ui.ButtonBar;

/**
 * Further abstraction of an {@link Action} that allows to create {@link Button}s on {@link Composite}s and
 * {@link NewButtonBar}s as well as registering buttons at instances of subclasses to be called when the button is
 * selected.
 * <p>
 * <b>Note:</b> the default implementation calls {@link #run()} when {@link #widgetSelected(SelectionEvent)} or
 * {@link #widgetDefaultSelected(SelectionEvent)} are called!
 * </p>
 * <p>
 * Look at the important functions under the "see also" section!
 * </p>
 * 
 * @see #registerButton(Button)
 * @see #unregisterButton(Button)
 * @see #adaptButton(Button)
 * @see #updateButtonsTexts()
 * @see #createButton(Composite, int)
 * @see #createButton(NewButtonBar, int)
 * @see #isRegisteredFor(Button)
 * @see #isAdaptingButton(Button)
 * 
 * @author Till Kolditz - till.kolditz@jexam.de
 * 
 * @version $Revision: 1.1 $; $Author: sprasse $; $Date: 2012-03-30 07:11:39 $
 */
public abstract class ButtonAction extends Action implements SelectionListener
{
    /**
     * Buttons --> isAdapted?
     */
    protected Map<Button, Boolean> buttons;
    private Image image;

    /**
     * 
     */
    public ButtonAction()
    {
        super();
        init();
    }

    /**
     * @param text
     */
    public ButtonAction(String text)
    {
        super(text);
        init();
    }

    /**
     * @param text
     * @param image
     */
    public ButtonAction(String text, ImageDescriptor image)
    {
        super(text, image);
        init();
    }

    /**
     * @param text
     * @param style
     */
    public ButtonAction(String text, int style)
    {
        super(text, style);
        init();
    }

    protected void init()
    {
        buttons = new HashMap<Button, Boolean>();
    }

    /**
     * Adds this action to the list of selection listeners of the given button and keeps a reference to the button.
     * 
     * @see #unregisterButton(Button)
     * @param button
     */
    public void registerButton(Button button)
    {
        buttons.put(button, Boolean.FALSE);
        button.addSelectionListener(this);
    }

    /**
     * Removes this action from the list of selection listeners of the given button and removes the reference to the
     * button from the internal list
     * 
     * @see #registerButton(Button)
     * @param button
     */
    public void unregisterButton(Button button)
    {
        button.removeSelectionListener(this);
        buttons.remove(button);
    }

    /**
     * Creates and registers a {@link Button} with the action's properties: text, tooltip and image. also automatically
     * registers it with this action.
     * 
     * @param parent
     *            the parent {@link Composite}
     * @param style
     *            the button's style
     * @return the button
     * @see Button
     * @see #adaptButton(Button)
     * @see #registerButton(Button)
     * @see #unregisterButton(Button)
     */
    public Button createButton(Composite parent, int style)
    {
        Button button = new Button(parent, style);
        adaptButton(button);
        registerButton(button);
        return button;
    }

    /**
     * Creates a button for this {@link IButtonBar} and adapts and registers it.
     * 
     * @param buttonBar
     *            the button bar in which to create the button
     * @param id
     *            the button's id. do not use double ids for the same button bar!
     * @return the button
     * @see IButtonBar
     * @see #adaptButton(Button)
     * @see #registerButton(Button)
     * @see #unregisterButton(Button)
     */
    public Button createButton(ButtonBar buttonBar, int id)
    {
        buttonBar.createButton(id, getText(), false);
        Button button = buttonBar.getButton(id);
        adaptButton(button);
        registerButton(button);
        return button;
    }

    /**
     * adapts the button to have the action's properties: text, tooltip and image. does NOT register the button.
     * 
     * @param button
     *            the button to adapt
     */
    public void adaptButton(Button button)
    {
        String str = getText();
        if(str != null) button.setText(str);
        str = getToolTipText();
        if(str != null) button.setToolTipText(str);
        ImageDescriptor id = getImageDescriptor();
        if(image == null && id != null)
        {
            image = id.createImage();
        }
        button.setImage(image);
        if(isRegisteredFor(button))
        {
            buttons.put(button, Boolean.TRUE);
        }
    }

    /**
     * Updates the registered buttons' texts and tooltip texts with the action's accordant values. You should call
     * {@link #setText(String)} and/or {@link #setToolTipText(String)} before this function to achieve some effect.
     */
    public void updateButtonsTexts()
    {
        for(Entry<Button, Boolean> entry : buttons.entrySet())
        {
            // if the button is adapted
            if(entry.getValue().booleanValue())
            {
                entry.getKey().setText(getText());
                entry.getKey().setToolTipText(getText());
            }
        }
    }

    public void setButtonsEnabled(boolean enabled)
    {
        for(Entry<Button, Boolean> entry : buttons.entrySet())
        {
            // enable/disable all registered buttons
            entry.getKey().setEnabled(enabled);
        }
    }

    /**
     * Checks if the given button is registeres at this instance
     * 
     * @param button
     *            the button
     * @return true if this action instance is a selection listener of this button
     */
    public boolean isRegisteredFor(Button button)
    {
        return buttons.containsKey(button);
    }

    public boolean isAdaptingButton(Button button)
    {
        return isRegisteredFor(button) && buttons.get(button).booleanValue() == true;
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        if(isRegisteredFor((Button)e.getSource())) run();
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
        if(isRegisteredFor((Button)e.getSource())) run();
    }

    @Override
    protected void finalize() throws Throwable
    {
        if(image != null)
        {
            image.dispose();
        }
        super.finalize();
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        Button b;
        for(Entry<Button, Boolean> entry : buttons.entrySet())
        {
            b = entry.getKey();
            if(!b.isDisposed()) b.setEnabled(enabled);
        }
    }
}