/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 14.11.2012 at 19:51:18
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.widgets;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.widgets.Composite;

import de.kolditz.common.ui.IValidationControl;
import de.kolditz.common.ui.IValidationListener;
import de.kolditz.common.ui.i18n.I18N;
import de.kolditz.common.util.GenericListenerList;

/**
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class AbstractValidationControl extends AbstractControl implements IValidationControl
{
    /**
     * Internal field for caching the validation state.
     */
    private boolean isValid = false;

    /**
     * The {@link GenericListenerList} of {@link IValidationListener}s.
     */
    protected GenericListenerList<IValidationListener> validationListeners = new GenericListenerList<IValidationListener>(
            IValidationListener.class, GenericListenerList.IDENTITY);

    /**
     * The optional {@link ControlDecoration}.
     */
    protected ControlDecoration cd;

    /**
     * 
     */
    public AbstractValidationControl()
    {
    }

    protected void setValid(boolean valid)
    {
        isValid = valid;
    }

    @Override
    public boolean isValid()
    {
        return isValid;
    }

    /**
     * Uses {@link #isValid} as the notification state.
     * 
     * @see #setValid(boolean)
     */
    protected void notifyValidationListeners()
    {
        for (IValidationListener vl : validationListeners.getListeners())
        {
            vl.valid(isValid);
        }
    }

    @Override
    public void addValidationListener(IValidationListener listener)
    {
        validationListeners.add(listener);
    }

    @Override
    public void removeValidationListener(IValidationListener listener)
    {
        validationListeners.add(listener);
    }

    /**
     * May be called only once. Further calls have no effect.
     * 
     * @param position
     */
    public void createControlDecoration(int position)
    {
        if (cd != null)
        {
            cd = new ControlDecoration(getControl(), position);
            adaptCD();
        }
    }

    /**
     * May be called only once. Further calls have no effect.
     * 
     * @param position
     * @param drawComposite
     */
    public void createControlDecoration(int position, Composite drawComposite)
    {
        if (cd != null)
        {
            cd = new ControlDecoration(getControl(), position, drawComposite);
            adaptCD();
        }
    }

    private void adaptCD()
    {
        cd.setDescriptionText(I18N.get().getString(I18N.CONTROLS_IPTEXT_INVALID));
        cd.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
                .getImage());
        cd.setMarginWidth(2);
        cd.hide();
    }
}
