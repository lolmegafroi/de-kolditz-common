/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * A simple popup list.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public class PopupList
{
    /**
     * Opens at the given display coordinates with the given orientation (must be either {@link SWT#UP} or
     * {@link SWT#DOWN}).
     * 
     * @param location
     *            display-relative location
     * @param orientation
     *            {@link SWT#UP} or {@link SWT#DOWN}
     */
    public void open(Point location, int orientation)
    {
    }

    /**
     * Opens relative to the given control. Use {@link SWT#TOP}, {@link SWT#LEFT}, {@link SWT#RIGHT}, {@link SWT#CENTER}
     * , and {@link SWT#BOTTOM} for the alignments. Some examples:
     * 
     * <pre>
     * open(SWT.TOP, SWT.LEFT, ...)           open(SWT.RIGHT, SWT.BOTTOM, ...)
     * 
     * +-------+                              +---------+
     * | popup |                              |         |
     * +-------+                              |         |
     * +---------+                            | control |+-------+
     * | control |                            |         || popup |
     * +---------+                            +---------++-------+
     * </pre>
     * 
     * @param firstAlignment
     * @param secondAlignment
     * @param control
     */
    public void open(int firstAlignment, int secondAlignment, Control control)
    {
    }
}
