/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 21.08.2012 at 17:51:29
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui;

import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.GestureListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TouchListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

/**
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
abstract class AbstractControl {
    private Control control;

    public AbstractControl() {
    }

    protected void setControl(Control control) {
        this.control = control;
    }

    public void update() {
        control.update();
    }

    public void addControlListener(ControlListener listener) {
        control.addControlListener(listener);
    }

    public void addDragDetectListener(DragDetectListener listener) {
        control.addDragDetectListener(listener);
    }

    public void addFocusListener(FocusListener listener) {
        control.addFocusListener(listener);
    }

    public void addGestureListener(GestureListener listener) {
        control.addGestureListener(listener);
    }

    public void addHelpListener(HelpListener listener) {
        control.addHelpListener(listener);
    }

    public void addKeyListener(KeyListener listener) {
        control.addKeyListener(listener);
    }

    public void addMenuDetectListener(MenuDetectListener listener) {
        control.addMenuDetectListener(listener);
    }

    public void addMouseListener(MouseListener listener) {
        control.addMouseListener(listener);
    }

    public void addMouseTrackListener(MouseTrackListener listener) {
        control.addMouseTrackListener(listener);
    }

    public void addMouseMoveListener(MouseMoveListener listener) {
        control.addMouseMoveListener(listener);
    }

    public void addMouseWheelListener(MouseWheelListener listener) {
        control.addMouseWheelListener(listener);
    }

    public void addPaintListener(PaintListener listener) {
        control.addPaintListener(listener);
    }

    public void addTouchListener(TouchListener listener) {
        control.addTouchListener(listener);
    }

    public void addTraverseListener(TraverseListener listener) {
        control.addTraverseListener(listener);
    }

    public Point computeSize(int wHint, int hHint) {
        return control.computeSize(wHint, hHint);
    }

    public Point computeSize(int wHint, int hHint, boolean changed) {
        return control.computeSize(wHint, hHint, changed);
    }

    public boolean dragDetect(Event event) {
        return control.dragDetect(event);
    }

    public boolean dragDetect(MouseEvent event) {
        return control.dragDetect(event);
    }

    public boolean forceFocus() {
        return control.forceFocus();
    }

    public Accessible getAccessible() {
        return control.getAccessible();
    }

    public Color getBackground() {
        return control.getBackground();
    }

    public Image getBackgroundImage() {
        return control.getBackgroundImage();
    }

    public int getBorderWidth() {
        return control.getBorderWidth();
    }

    public Rectangle getBounds() {
        return control.getBounds();
    }

    public Cursor getCursor() {
        return control.getCursor();
    }

    public boolean getDragDetect() {
        return control.getDragDetect();
    }

    public boolean getEnabled() {
        return control.getEnabled();
    }

    public Font getFont() {
        return control.getFont();
    }

    public Color getForeground() {
        return control.getForeground();
    }

    public Object getLayoutData() {
        return control.getLayoutData();
    }

    public void removeControlListener(ControlListener listener) {
        control.removeControlListener(listener);
    }

    public void setEnabled(boolean enabled) {
        control.setEnabled(enabled);
    }

    public boolean setFocus() {
        return control.setFocus();
    }

    public void setLayoutData(Object layoutData) {
        control.setLayoutData(layoutData);
    }
}
