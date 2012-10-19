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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.kolditz.common.util.SystemProperties;

public class TextStream extends OutputStream
{
    public static final String NL = SystemProperties.LINE_SEP;

    private PrintStream out, err;
    private Display disp;
    private Text text;
    private StringBuilder sb = new StringBuilder();
    private int messageNum;
    private boolean wasLastErrorOutput = false;

    private Runnable worker = new Runnable()
    {
        @Override
        public void run()
        {
            if (!text.isDisposed())
                text.setText(sb.toString());
        }
    };

    TextStream(Shell shell, Text text, boolean overtakeSystemStreams)
    {
        messageNum = 0;
        disp = shell.getDisplay();
        this.text = text;
        PrintStream p = new PrintStream(this, true);
        out = System.out;
        err = System.err;
        if (overtakeSystemStreams)
        {
            System.setOut(p);
            System.setErr(p);
        }
    }

    public void write(int b)
    {
        synchronized (sb)
        {
            sb.append((char) b);
            if (wasLastErrorOutput)
            {
                err.write(b);
                wasLastErrorOutput = false;
            }
            else
            {
                out.write(b);
            }
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
        write(new String(b, off, len));
    }

    @Override
    public void flush() throws IOException
    {
        synchronized (sb)
        {
            update();
            out.flush();
            err.flush();
        }
    }

    public void write(String string)
    {
        synchronized (sb)
        {
            StringBuilder sb2 = new StringBuilder();
            if (!string.equals(NL))
                sb2.append(String.format("%4d | ", Integer.valueOf(++messageNum)));
            sb2.append(string);
            sb.append(sb2);
            update();
            if (string.contains("Exception") || wasLastErrorOutput)
            {
                err.print(sb2.toString());
                wasLastErrorOutput = !wasLastErrorOutput;
            }
            else
            {
                out.print(sb2.toString());
            }
        }
    }

    public void writeln(String string)
    {
        synchronized (sb)
        {
            StringBuilder sb2 = new StringBuilder();
            if (!string.equals(NL))
                sb2.append(String.format("%4d | ", Integer.valueOf(++messageNum)));
            sb2.append(string).append(NL);
            sb.append(sb2);
            update();
            if (string.contains("Exception") || wasLastErrorOutput)
            {
                err.print(sb2.toString());
                wasLastErrorOutput = !wasLastErrorOutput;
            }
            else
            {
                out.print(sb2.toString());
            }
        }
    }

    private void update()
    {
        if (text.isDisposed())
        {
            return;
        }
        disp.asyncExec(worker);
    }

    public void clear()
    {
        sb.delete(0, sb.length());
        messageNum = 0;
        update();
    }
}
