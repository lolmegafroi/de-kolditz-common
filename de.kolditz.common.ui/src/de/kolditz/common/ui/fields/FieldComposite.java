/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 13.07.2012 at 11:45:57
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.fields;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Parent composite for {@link AbstractField}s. When creating a widget tree (whether including fields or not) clients
 * should use Composites' {@link Composite#setLayoutDeferred(boolean)} function for reducing the drawing overhead.
 * Whenever a field is added to this FieldComposite using {@link #registerField(AbstractField)}, the
 * {@link #updateLayout()} method will be called. Using deferred layout will reduce the preformance impact!
 * <p>
 * It is possible to add children which are not instances of AbstractField.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 * @see #registerField(AbstractField)
 */
public class FieldComposite
{
    private Composite comp;
    private GridLayout layout;
    private List<AbstractField<?>> fields;

    /**
     * @param parent
     *            the parent Composite
     * @param style
     *            this Composite's style
     */
    public FieldComposite(Composite parent, int style)
    {
        comp = new Composite(parent, style);
        layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        comp.setLayout(layout);

        fields = new ArrayList<AbstractField<?>>();
    }

    public void registerField(AbstractField<?> field)
    {
        fields.add(field);
        updateLayout();
    }

    protected Composite getComposite()
    {
        return comp;
    }

    public void updateLayout()
    {
        int columns = 0;
        for (AbstractField<?> field : fields)
        {
            columns = Math.max(columns, field.getColumnsRequired());
        }
        layout.numColumns = columns;
        for (AbstractField<?> field : fields)
        {
            field.setColumns(columns);
        }
        comp.layout();
    }

    public int getColumns()
    {
        return ((GridLayout) comp.getLayout()).numColumns;
    }

    public boolean isDisposed()
    {
        return comp.isDisposed();
    }

    public Display getDisplay()
    {
        return comp.getDisplay();
    }

    public void setLayoutData(Object layoutData)
    {
        comp.setLayoutData(layoutData);
    }
}
