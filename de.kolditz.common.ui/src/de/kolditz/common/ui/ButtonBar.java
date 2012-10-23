/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import de.kolditz.common.util.GenericListenerList;

/**
 * This new button bar will use and take advantage of the {@link ButtonBarLayout}.
 * 
 * @author Till Kolditz - till.kolditz@jexam.de
 * @version $Revision: 1.3 $; $Author: tkolditz $; $Date: 2010-07-28 14:25:53 $
 */
public class ButtonBar extends Composite
{
    public static final String BUTTON_ID = "BUTTON_ID"; //$NON-NLS-1$

    private class InternalSelectionListener implements SelectionListener
    {
        public void widgetDefaultSelected(SelectionEvent e)
        {
            for (SelectionListener o : selectionListeners.getListeners())
            {
                o.widgetDefaultSelected(e);
            }
        }

        public void widgetSelected(SelectionEvent e)
        {
            for (SelectionListener o : selectionListeners.getListeners())
            {
                o.widgetSelected(e);
            }
        }
    }

    /* Default size for widgets */
    static final int DEFAULT_WIDTH = 64;
    static final int DEFAULT_HEIGHT = 64;

    protected HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
    protected GenericListenerList<SelectionListener> selectionListeners = new GenericListenerList<SelectionListener>(
            SelectionListener.class);
    protected SelectionListener selectionListener = new InternalSelectionListener();

    /**
     * This may be used to control enabled state on newly created buttons.
     */
    protected boolean defaultEnabled = true;

    /**
     * Constructs a new instance of this class given its parent and a style value describing its behavior and
     * appearance.
     * <p>
     * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to
     * instances of this class, or must be built by <em>bitwise OR</em>'ing together (that is, using the
     * <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists
     * the style constants that are applicable to the class. Style bits are also inherited from superclasses.
     * </p>
     * 
     * @param parent
     *            a widget which will be the parent of the new instance (cannot be null)
     * @param style
     *            the style of widget to construct
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
     *                </ul>
     * @see ButtonBarLayout
     * @see SWT#NO_BACKGROUND
     * @see SWT#NO_FOCUS
     * @see SWT#NO_MERGE_PAINTS
     * @see SWT#NO_REDRAW_RESIZE
     * @see SWT#NO_RADIO_GROUP
     * @see SWT#EMBEDDED
     * @see SWT#DOUBLE_BUFFERED
     * @see Widget#getStyle
     */
    public ButtonBar(Composite parent, int style)
    {
        super(parent, style);
        init(new ButtonBarLayout());
    }

    /**
     * Constructs a new instance of this class given its parent and a style value describing its behavior and
     * appearance.
     * <p>
     * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to
     * instances of this class, or must be built by <em>bitwise OR</em>'ing together (that is, using the
     * <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists
     * the style constants that are applicable to the class. Style bits are also inherited from superclasses.
     * </p>
     * 
     * @param parent
     *            a widget which will be the parent of the new instance (cannot be null)
     * @param style
     *            the style of widget to construct
     * @param makeButtonsEqualWidth
     * @param makeButtonsEqualHeight
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
     *                </ul>
     * @see SWT#NO_BACKGROUND
     * @see SWT#NO_FOCUS
     * @see SWT#NO_MERGE_PAINTS
     * @see SWT#NO_REDRAW_RESIZE
     * @see SWT#NO_RADIO_GROUP
     * @see SWT#EMBEDDED
     * @see SWT#DOUBLE_BUFFERED
     * @see Widget#getStyle
     */
    public ButtonBar(Composite parent, int style, boolean makeButtonsEqualWidth, boolean makeButtonsEqualHeight)
    {
        super(parent, style);
        init(new ButtonBarLayout(makeButtonsEqualWidth, makeButtonsEqualHeight));
    }

    /**
     * Constructs a new instance of this class given its parent and a style value describing its behavior and
     * appearance.
     * <p>
     * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to
     * instances of this class, or must be built by <em>bitwise OR</em>'ing together (that is, using the
     * <code>int</code> "|" operator) two or more of those <code>SWT</code> style constants. The class description lists
     * the style constants that are applicable to the class. Style bits are also inherited from superclasses.
     * </p>
     * 
     * @param parent
     *            a widget which will be the parent of the new instance (cannot be null)
     * @param style
     *            the style of widget to construct
     * @param alignment
     *            {@link ButtonBarLayout#setAlignment(int)}
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
     *                </ul>
     * @see SWT#NO_BACKGROUND
     * @see SWT#NO_FOCUS
     * @see SWT#NO_MERGE_PAINTS
     * @see SWT#NO_REDRAW_RESIZE
     * @see SWT#NO_RADIO_GROUP
     * @see SWT#EMBEDDED
     * @see SWT#DOUBLE_BUFFERED
     * @see Widget#getStyle
     */
    public ButtonBar(Composite parent, int style, int alignment)
    {
        super(parent, style);
        init(new ButtonBarLayout(alignment));
    }

    private void init(ButtonBarLayout bbl)
    {
        super.setLayout(bbl);
    }

    /**
     * does nothing
     */
    @Override
    public void setLayout(Layout layout)
    {
    }

    @Override
    public ButtonBarLayout getLayout()
    {
        return (ButtonBarLayout) super.getLayout();
    }

    public Button createButton(int id, String label)
    {
        return createButton(id, label, null, false, SWT.PUSH);
    }

    public Button createButton(int id, String label, boolean defaultButton)
    {
        return createButton(id, label, null, defaultButton, SWT.PUSH);
    }

    public Button createButton(int id, String label, boolean defaultButton, int buttonStyle)
    {
        return createButton(id, label, null, defaultButton, buttonStyle);
    }

    public Button createButton(int id, String label, Image image, boolean defaultButton)
    {
        return createButton(id, label, image, defaultButton, SWT.PUSH);
    }

    public Button createButton(int id, String label, Image image, boolean defaultButton, int buttonStyle)
    {
        if (buttons.containsKey(id))
        {
            throw new RuntimeException("there is already another button using this id (" + id + ")!"); //$NON-NLS-1$
        }
        Button button = new Button(this, buttonStyle);
        buttons.put(id, button);
        if (label != null)
            button.setText(label);
        if (image != null)
            button.setImage(image);
        button.setData(BUTTON_ID, id);
        button.addSelectionListener(selectionListener);
        if (defaultButton)
        {
            Shell shell = getShell();
            if (shell != null)
            {
                shell.setDefaultButton(button);
            }
        }
        button.setEnabled(defaultEnabled);
        return button;
    }

    @Override
    public Point computeSize(int widthHint, int heightHint, boolean changed)
    {
        if (buttons.size() == 0)
        {
            Rectangle rect = computeTrim(0, 0, 1, 1);
            return new Point(rect.width, rect.height);
        }
        return super.computeSize(widthHint, heightHint, changed);
    }

    public void addSelectionListener(SelectionListener listener)
    {
        selectionListeners.add(listener);
    }

    public void removeSelectionListener(SelectionListener listener)
    {
        selectionListeners.remove(listener);
    }

    public Button getButton(int id)
    {
        return buttons.get(id);
    }

    public Control getControl()
    {
        return this;
    }

    /**
     * does nothing
     */
    public void setMessage(int style, String message)
    {
    }

    public void setDebug(boolean debug)
    {
        ((ButtonBarLayout) getLayout()).setDebug(debug);
    }

    /**
     * @return the default enabled state for newly created buttons
     */
    public boolean isDefaultEnabled()
    {
        return defaultEnabled;
    }

    /**
     * @param defaultEnabled
     *            the default enabled state for newly created buttons
     */
    public void setDefaultEnabled(boolean defaultEnabled)
    {
        this.defaultEnabled = defaultEnabled;
    }
}
