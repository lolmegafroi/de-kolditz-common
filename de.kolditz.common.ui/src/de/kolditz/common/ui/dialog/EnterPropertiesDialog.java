/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 29.08.2012 at 20:09:25
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.ui.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.kolditz.common.ui.widgets.ButtonBar;
import de.kolditz.common.util.IValidator;

/**
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public class EnterPropertiesDialog extends Dialog
{
    private class EditValidator implements IValidator<String>
    {
        private String key;

        public void setKey(String key)
        {
            this.key = key;
        }

        @Override
        public String validate(String object)
        {
            if (object == null || object.isEmpty())
            {
                return "The key must not be empty"; // TODO i18n
            }
            if (key.equals(object))
            {
                return null;
            }
            if (properties.containsKey(object))
            {
                return "This key already exists"; // TODO i18n
            }
            return null;
        }
    }

    private TableViewer viewer;
    private ButtonBar buttonBar;
    private Map<String, String> properties = null;
    private IValidator<String> keyValidator = new IValidator<String>()
    {
        @Override
        public String validate(String object)
        {
            if (object == null || object.isEmpty())
            {
                return "The key must not be empty"; // TODO i18n
            }
            if (properties.containsKey(object))
            {
                return "This key already exists"; // TODO i18n
            }
            return null;
        }
    };
    private IValidator<String> valueValidator = new IValidator<String>()
    {
        @Override
        public String validate(String object)
        {
            if (object == null || object.isEmpty())
            {
                return "Value must not be null"; // TODO i18n
            }
            return null;
        }
    };
    private EditValidator editValidator = new EditValidator();

    /**
     * @param parentShell
     */
    public EnterPropertiesDialog(Shell parentShell)
    {
        super(parentShell);
        setBlockOnOpen(true);
    }

    /**
     * @param parentShell
     */
    public EnterPropertiesDialog(IShellProvider parentShell)
    {
        super(parentShell);
        setBlockOnOpen(true);
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite dialogArea = (Composite) super.createDialogArea(parent);

        viewer = new TableViewer(dialogArea, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.setContentProvider(new ArrayContentProvider());

        TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEAD);
        col.setLabelProvider(new ColumnLabelProvider()
        {
            @SuppressWarnings("unchecked")
            @Override
            public String getText(Object element)
            {
                return ((Entry<String, String>) element).getKey();
            }
        });

        col = new TableViewerColumn(viewer, SWT.LEAD);
        col.setLabelProvider(new ColumnLabelProvider());
        col.setLabelProvider(new ColumnLabelProvider()
        {
            @SuppressWarnings("unchecked")
            @Override
            public String getText(Object element)
            {
                return ((Entry<String, String>) element).getValue();
            }
        });

        buttonBar = new ButtonBar(dialogArea, SWT.NONE);
        Button b = buttonBar.createButton(0, "Add"); // TODO i18n
        b.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (properties == null)
                {
                    properties = new HashMap<String, String>();
                }
                EnterPropertyDialog dialog = new EnterPropertyDialog(getShell());
                dialog.setKeyValidator(keyValidator);
                dialog.setValueValidator(valueValidator);
                if (dialog.open() == Window.OK)
                {
                    properties.put(dialog.getKey(), dialog.getValue());
                    buttonBar.getButton(1).setEnabled(true);
                }
            }
        });

        b = buttonBar.createButton(1, "Edit"); // TODO i18n
        b.addSelectionListener(new SelectionAdapter()
        {
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                Object o = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
                Entry<String, String> entry = (Entry<String, String>) o;
                EnterPropertyDialog dialog = new EnterPropertyDialog(getShell());
                editValidator.setKey(entry.getKey());
                dialog.setKey(entry.getKey());
                dialog.setValue(entry.getValue());
                dialog.setKeyValidator(editValidator);
                dialog.setValueValidator(valueValidator);
                if (dialog.open() == Window.OK)
                {
                    properties.put(dialog.getKey(), dialog.getValue());
                }
            }
        });
        b.setEnabled(false);

        b = buttonBar.createButton(2, "Delete"); // TODO i18n
        b.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                Object[] array = ((IStructuredSelection) viewer.getSelection()).toArray();
                for (Object o : array)
                {
                    properties.remove(o);
                }
                viewer.remove(array);
            }
        });
        b.setEnabled(false);

        viewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                boolean enable = !event.getSelection().isEmpty();
                buttonBar.getButton(1).setEnabled(enable);
                buttonBar.getButton(2).setEnabled(enable);
            }
        });

        if (properties != null)
        {
            viewer.setInput(properties.entrySet());
        }

        return dialogArea;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = new HashMap<String, String>(properties);
    }
}
