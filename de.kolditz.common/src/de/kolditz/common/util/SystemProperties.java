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
package de.kolditz.common.util;

/**
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 */
public interface SystemProperties
{
    public static final String FILE_ENC = System.getProperty("file.encoding"); //$NON-NLS-1$
    public static final String FILE_ENC_PKG = System.getProperty("file.encoding.pkg"); //$NON-NLS-1$
    public static final String FILE_SEP = System.getProperty("file.separator"); //$NON-NLS-1$
    public static final String JAVA_CLASS_PATH = System.getProperty("java.class.path"); //$NON-NLS-1$
    public static final String JAVA_CLASS_VERSION = System.getProperty("java.class.version"); //$NON-NLS-1$
    public static final String JAVA_COMPILER = System.getProperty("java.compiler"); //$NON-NLS-1$
    public static final String JAVA_HOME = System.getProperty("java.home"); //$NON-NLS-1$
    public static final String JAVA_IO_TMPDIR = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
    public static final String JAVA_LIBRARY_PATH = System.getProperty("java.library.path"); //$NON-NLS-1$
    public static final String JAVA_VERSION = System.getProperty("java.version"); //$NON-NLS-1$
    public static final String JAVA_VENDOR = System.getProperty("java.vendor"); //$NON-NLS-1$
    public static final String JAVA_VENDOR_URL = System.getProperty("java.vendor.url"); //$NON-NLS-1$
    public static final String LINE_SEP = System.getProperty("line.separator"); //$NON-NLS-1$
    public static final String OS_NAME = System.getProperty("os.name"); //$NON-NLS-1$
    public static final String OS_ARCH = System.getProperty("os.arch"); //$NON-NLS-1$
    public static final String OS_VERSION = System.getProperty("os.version"); //$NON-NLS-1$
    public static final String PATH_ENC = System.getProperty("path.separator"); //$NON-NLS-1$
    public static final String USER_DIR = System.getProperty("user.dir"); //$NON-NLS-1$
    public static final String USER_HOME = System.getProperty("user.home"); //$NON-NLS-1$
    public static final String USER_LANG = System.getProperty("user.lang"); //$NON-NLS-1$
    public static final String USER_NAME = System.getProperty("user.name"); //$NON-NLS-1$
    public static final String USER_REGION = System.getProperty("user.region"); //$NON-NLS-1$
    public static final String USER_TIMEZONE = System.getProperty("user.timezone"); //$NON-NLS-1$
}
