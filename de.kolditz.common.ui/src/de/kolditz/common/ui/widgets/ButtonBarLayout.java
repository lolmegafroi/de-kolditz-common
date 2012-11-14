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
package de.kolditz.common.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/**
 * Instances of this class lay out the control children of a {@link ButtonBar}. Possible values for alignment are
 * {@link SWT#LEAD} or {@link SWT#TRAIL}.
 * <p>
 * The power of <code>ButtonBarLayout</code> lies in the ability to detect the need for multi-lined layout of a
 * ButtonBar
 * </p>
 * 
 * @see ButtonBar
 * @author Till Kolditz - till.kolditz@jexam.de $Revision: 1.4 $; $Author: tkolditz $; $Date: 2010-07-28 14:25:53 $
 */
public class ButtonBarLayout extends Layout
{
    /**
     * makeButtonsEqualWidth specifies whether all buttons in the layout will be forced to have the same width, which
     * will be the width of the button having the largest width. The default value is false.
     */
    public boolean makeButtonsEqualWidth = false;

    /**
     * makeButtonsEqualHeight specifies whether all buttons in the layout will be forced to have the same height, which
     * will be the height of the button having the largest height. The default value is true.
     */
    public boolean makeButtonsEqualHeight = true;

    /**
     * marginWidth specifies the number of pixels of horizontal margin that will be placed along the left and right
     * edges of the layout. The default value is 5.
     */
    public int marginWidth = 5;

    /**
     * marginHeight specifies the number of pixels of vertical margin that will be placed along the top and bottom edges
     * of the layout. The default value is 5.
     */
    public int marginHeight = 5;

    /**
     * marginLeft specifies the number of pixels of horizontal margin that will be placed along the left edge of the
     * layout. The default value is 0.
     * 
     * @since 3.1
     */
    public int marginLeft = 0;

    /**
     * marginTop specifies the number of pixels of vertical margin that will be placed along the top edge of the layout.
     * The default value is 0.
     * 
     * @since 3.1
     */
    public int marginTop = 0;

    /**
     * marginRight specifies the number of pixels of horizontal margin that will be placed along the right edge of the
     * layout. The default value is 0.
     * 
     * @since 3.1
     */
    public int marginRight = 0;

    /**
     * marginBottom specifies the number of pixels of vertical margin that will be placed along the bottom edge of the
     * layout. The default value is 0.
     * 
     * @since 3.1
     */
    public int marginBottom = 0;

    /**
     * horizontalSpacing specifies the number of pixels between the right edge of one cell and the left edge of its
     * neighbouring cell to the right. The default value is 10.
     */
    public int horizontalSpacing = 10;

    /**
     * verticalSpacing specifies the number of pixels between the bottom edge of one cell and the top edge of its
     * neighbouring cell underneath. The default value is 5.
     */
    public int verticalSpacing = 5;

    /**
     * shall not be accessable publicly to ensure that its only values will be SWT.LEAD and SWT.TRAIL
     */
    private int alignment = SWT.LEAD;

    private int cacheWidth = SWT.DEFAULT, cacheHeight = SWT.DEFAULT;
    private Rectangle[][] cacheGrid;
    private Point cacheSize;
    private boolean debug = false;

    /**
     * Constructs a new instance of this class.<br>
     * 
     * <pre>
     * makeButtonsEqualWidth = false<br>makeButtonsEqualHeight = false
     * </pre>
     */
    public ButtonBarLayout()
    {
    }

    /**
     * Constructs a new instance of this class. Possible values for alignment are {@link SWT#LEAD} or {@link SWT#TRAIL}
     * .</br>
     * 
     * <pre>
     * makeButtonsEqualWidth = false<br>makeButtonsEqualHeight = false
     * </pre>
     * 
     * @param alignment
     *            SWT.LEAD or SWT.TRAIL for overall leading or trailing alignment if bounds are greater than needed
     */
    public ButtonBarLayout(int alignment)
    {
        setAlignment(alignment);
    }

    /**
     * Constructs a new instance of this class.
     * 
     * @param makeButtonsEqualWidth
     *            whether or not the buttons will have equal width
     * @param makeButtonsEqualHeight
     *            whether or not the buttons will have equal height
     */
    public ButtonBarLayout(boolean makeButtonsEqualWidth, boolean makeButtonsEqualHeight)
    {
        this.makeButtonsEqualWidth = makeButtonsEqualWidth;
        this.makeButtonsEqualHeight = makeButtonsEqualHeight;
    }

    /**
     * @param alignment
     *            SWT.LEAD or SWT.TRAIL for overall leading or trailing alignment if bounds are greater than needed
     */
    public void setAlignment(int alignment)
    {
        if (alignment == SWT.TRAIL)
            this.alignment = SWT.TRAIL;
        else
            this.alignment = SWT.LEAD;
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Layout#computeSize(org.eclipse.swt.widgets.Composite, int, int, boolean)
     */
    @Override
    protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache)
    {
        if (flushCache)
            flushCache();
        return layout(composite, false, 0, 0, wHint, hHint);
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.widgets.Layout#layout(org.eclipse.swt.widgets.Composite, boolean)
     */
    @Override
    protected void layout(Composite composite, boolean flushCache)
    {
        if (flushCache)
            flushCache();
        Rectangle rect = composite.getClientArea();
        layout(composite, true, rect.x, rect.y, rect.width, rect.height);
    }

    private void flushCache()
    {
        cacheHeight = SWT.DEFAULT;
        cacheWidth = SWT.DEFAULT;
        cacheGrid = null;
        cacheSize = null;
    }

    private Point layout(Composite composite, boolean move, int x, int y, int width, int height)
    {
        Control[] children = composite.getChildren();
        if (children.length == 0)
        {
            return new Point(marginLeft + marginWidth * 2 + marginRight, marginTop + marginHeight * 2 + marginBottom);
        }

        if (((cacheWidth != SWT.DEFAULT) && ((width == SWT.DEFAULT) || (width == cacheWidth)))
                && ((cacheHeight != SWT.DEFAULT) && ((height == SWT.DEFAULT) || (height == cacheHeight)))
                && cacheGrid != null && cacheSize != null)
        {
            if (move)
                moveControles(cacheGrid, children);
            return cacheSize;
        }

        Rectangle[] buttonAreas = new Rectangle[children.length];
        // reduce amount of computation by assuming that we don't have too many buttons so that the
        // allocation would fail.
        Rectangle[][] grid = new Rectangle[children.length][children.length];
        Rectangle area;
        Point p;

        // get the buttons' sizes
        for (int i = 0; i < children.length; ++i)
        {
            p = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT);
            buttonAreas[i] = new Rectangle(0, 0, p.x, p.y);
        }

        // check for equal width. We don't need to do this with just 1 button
        if (makeButtonsEqualWidth && (children.length > 1))
        {
            int maxWidth = SWT.DEFAULT;
            for (Rectangle r : buttonAreas)
            {
                maxWidth = Math.max(maxWidth, r.width);
            }

            for (Rectangle r : buttonAreas)
            {
                r.width = maxWidth;
            }
        }

        // check for equal height. We don't need to do this with just 1 button
        if (makeButtonsEqualHeight && (children.length > 1))
        {
            int maxHeight = SWT.DEFAULT;
            for (Rectangle r : buttonAreas)
            {
                maxHeight = Math.max(maxHeight, r.height);
            }

            for (Rectangle r : buttonAreas)
            {
                r.height = maxHeight;
            }
        }

        int fullLeftMargin = marginLeft + marginWidth, fullTopMargin = marginTop + marginHeight;
        if (debug)
        {
            System.out.println("horizontal margins: " + marginLeft + ":" + marginWidth + ":" + marginRight);
            System.out.println("vertical margins: " + marginTop + ":" + marginHeight + ":" + marginBottom);
        }
        int curLineWidth = fullLeftMargin, fullWidth = fullLeftMargin, curX = fullLeftMargin, numRows = 0;
        int curLineHeight = fullTopMargin, fullHeight = fullTopMargin, curY = y + fullTopMargin, curCol = 0;
        // compute locations
        for (int i = 0; i < children.length; ++i)
        {
            area = buttonAreas[i];
            curLineWidth += area.width;

            // if we have a constraint on the width and the buttons will overlap the line, then wrap
            // note that we will have at least one button on each line
            if ((width != SWT.DEFAULT) && (grid[numRows][0] != null) && (curLineWidth > width))
            {
                fullHeight += verticalSpacing + curLineHeight;
                curX = x + fullLeftMargin;
                curY += verticalSpacing + curLineHeight;
                curLineWidth = fullLeftMargin + area.width;
                curLineHeight = 0;
                ++numRows;
                curCol = 0;
            }

            area.x = curX;
            area.y = curY;
            grid[numRows][curCol] = area;

            fullWidth = Math.max(curLineWidth, fullWidth);
            curLineWidth += horizontalSpacing;
            curX += area.width + horizontalSpacing;
            curLineHeight = Math.max(curLineHeight, area.height);
            ++curCol;
        }
        // add the last row's line height since it is not added above
        fullHeight += curLineHeight + marginHeight + marginBottom;
        // fullWidth, though, is taken care of in each loop cycle and needs no further action
        fullWidth += marginWidth + marginRight;

        // if alignment is SWT.TRAIL and we have larger bounds than needed, realign
        if ((width > fullWidth) && (alignment == SWT.TRAIL))
        {
            int offset = width - fullWidth;
            for (Rectangle[] rs : grid)
            {
                for (Rectangle r : rs)
                {
                    if (r == null)
                        break;
                    r.x += offset;
                }
            }
        }

        // position the buttons
        if (move)
        {
            moveControles(grid, children);
        }

        cacheWidth = fullWidth;
        cacheHeight = fullHeight;
        cacheGrid = grid;
        cacheSize = new Point(fullWidth, fullHeight);

        if (debug)
        {
            System.out.println("x=" + x + " y=" + y + " width=" + width + " height=" + height + " | fullwidth="
                    + fullWidth + " - fullheight=" + fullHeight);
        }

        return new Point(fullWidth, fullHeight);
    }

    private void moveControles(Rectangle[][] grid, Control[] children)
    {
        int i = 0;
        for (int row = 0; (row < grid.length) && (grid[row] != null); ++row)
        {
            for (int col = 0; (col < grid[row].length) && (grid[row][col] != null); ++col)
            {
                children[i++].setBounds(grid[row][col]);
            }
        }
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }
}
