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
package de.kolditz.common.ui.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class TabField<K> extends PreferenceField<K>
{
    public final class TabFieldItem
    {
        private K key = null;
        private TabItem ti = null;
        private PreferencesComposite comp = null;

        private TabFieldItem(K key)
        {
            this.key = key;
            ti = new TabItem(folder, SWT.NONE);
        }

        public K getKey()
        {
            return key;
        }

        public void setText(String text)
        {
            ti.setText(text);
        }

        public void setToolTipText(String tooltip)
        {
            ti.setToolTipText(tooltip);
        }

        public PreferencesComposite getPreferencesComposite()
        {
            if(comp == null)
            {
                comp = new PreferencesComposite(folder, SWT.NONE);
                ti.setControl(comp.getComposite());
            }
            return comp;
        }
    }

    /**
     * The field's label
     */
    private Label label;

    /**
     * The parent TabFolder widget
     */
    private TabFolder folder;

    /**
     * The label widget's text
     */
    private String labelString;

    /**
     * The TabFolder's style
     */
    private int tabStyle;

    /**
     * The currently selected key
     */
    private K currentKey;

    private Map<K, TabFieldItem> items;

    /**
     * @param parent
     *            parent Composite
     * @param style
     *            composite style
     * @param label
     *            label text
     * @param keys
     *            the keys for the {@link TabItem}s
     * @param labels
     *            the labels for the {@link TabItem}s
     */
    public TabField(PreferencesComposite parent, int style, String label)
    {
        this(parent, style, label, SWT.TOP);
    }

    /**
     * @param parent
     *            parent Composite
     * @param style
     *            composite style
     * @param label
     *            label text
     * @param keys
     *            the keys for the {@link TabItem}s
     * @param labels
     *            the labels for the {@link TabItem}s
     * @param tabStyle
     *            {@link SWT#TOP} or {@link SWT#BOTTOM}
     */
    public TabField(PreferencesComposite parent, int style, String label, int tabStyle)
    {
        super(parent, style);

        assert parent != null : new IllegalArgumentException("parent = null"); //$NON-NLS-1$
        assert label != null : new IllegalArgumentException("label = null"); //$NON-NLS-1$

        this.labelString = label;
        this.tabStyle = tabStyle;

        create();
        setLabels();
        addListeners();
    }

    @Override
    protected void create()
    {
        label = new Label(getComposite(), SWT.NONE);
        folder = new TabFolder(getComposite(), tabStyle);
        folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    @Override
    protected void setLabels()
    {
        label.setText(labelString);
    }

    @Override
    protected void addListeners()
    {
        folder.addSelectionListener(new SelectionAdapter()
        {
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                int idx = folder.getSelectionIndex();
                if(idx > -1)
                {
                    setValue0((K)folder.getItem(idx).getData(), true);
                }
                else
                {
                    setValue0(null, true);
                }
            }
        });
    }

    @Override
    protected int getColumnsRequired()
    {
        return 2;
    }

    @Override
    protected void setColumns(int columns)
    {
        ((GridData)folder.getLayoutData()).horizontalSpan = columns - 1;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
    }

    @Override
    public K getValue()
    {
        return currentKey;
    }

    @SuppressWarnings("unchecked")
    @Override
    public K setValue(K value, boolean doNotifyObservers)
    {
        TabItem[] items = folder.getItems();
        for(int i = 0; i < items.length; ++i)
        {
            if(((K)items[i].getData()) == value)
            {
                folder.setSelection(i); // no notification sent when setting programmatically
                break;
            }
        }
        return setValue0(value, doNotifyObservers);
    }

    private K setValue0(K value, boolean doNotifyObservers)
    {
        K oldKey = currentKey;
        currentKey = value;
        if(doNotifyObservers)
        {
            notifyObservers(currentKey);
        }
        return oldKey;
    }

    /**
     * Used to create a {@link TabFieldItem} to which further {@link PreferenceField} can be added
     * 
     * @param key
     *            the key
     * @return the TabFieldItem
     */
    public TabFieldItem createItem(K key)
    {
        if(items == null)
        {
            items = new HashMap<K, TabField<K>.TabFieldItem>();
        }
        TabFieldItem tfi = new TabFieldItem(key);
        tfi.ti.setData(key);
        items.put(key, tfi);
        tfi.getPreferencesComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        return tfi;
    }

    /**
     * @param key
     *            the key
     * @return the associated {@link TabFieldItem} or null otherwise
     */
    public TabFieldItem getItem(K key)
    {
        if(items != null)
        {
            return items.get(key);
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean isDisposed()
    {
        return folder.isDisposed();
    }
}
