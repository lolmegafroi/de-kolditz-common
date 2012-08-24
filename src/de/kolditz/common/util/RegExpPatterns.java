/*******************************************************************************
 * Copyright (c) 2012 Till Kolditz.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 * created on 21.08.2012 at 17:38:55
 * 
 *  Contributors:
 *      Till Kolditz
 *******************************************************************************/
package de.kolditz.common.util;

import java.util.regex.Pattern;

/**
 * Some common regular expression patterns.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public final class RegExpPatterns {
    public static final String IPV4 = "^(0|[1-9][0-9]?|1[0-9][0-9]|2([0-4][0-9]|5[0-5]))(\\.(0|[1-9][0-9]?|1[0-9][0-9]|2([0-4][0-9]|5[0-5]))){3}$"; //$NON-NLS-1$

    public static Pattern ipV4() {
        return Pattern.compile(IPV4);
    }
}
