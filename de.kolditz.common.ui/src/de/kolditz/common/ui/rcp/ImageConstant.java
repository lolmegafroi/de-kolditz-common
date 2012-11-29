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
package de.kolditz.common.ui.rcp;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation type for image constants.<br>
 * When using RefType LOCAL you only need to assign {@link #folder()}.<br>
 * When using RefType REMOTE_PLUGIN you must assign the plugin's name to {@link #remotePlugin()}.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 * @see RefType
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageConstant
{
    public static final String SEPERATOR = "/"; //$NON-NLS-1$
    public static final String PLATFORM_PLUGIN_PATH = "platform:/plugin/"; //$NON-NLS-1$

    /**
     * <table>
     * <tr>
     * <th>value</th>
     * <th>description</th>
     * </tr>
     * <tr>
     * <td>LOCAL</td>
     * <td>The annotated string denotes a plugin's local path. No assumptions are made.</td>
     * </tr>
     * <tr>
     * <td style="vertical-align:top;">PLATFORM_PLUGIN</td>
     * <td>Reference to a plugin in the current platform. "platform:/plugin/" and the value of remotePlugin() will be
     * prepended to the base string. Example:<br>
     * Annotated String: <code>pin_editor.gif</code><br>
     * remotePlugin(): <code>org.eclipse.ui</code><br>
     * folder(): <code>icons/full/etool16</code><br>
     * is resolved to <code>platform:/plugin/org.eclipse.ui/icons/full/etool16/pin_editor.gif</code></td>
     * </tr>
     * </table>
     * 
     * @author Till Kolditz - Till.Kolditz@gmail.com
     */
    public enum RefType
    {
        /**
         * The annotated string denotes a plugin's local path. No assumptions are made.
         */
        LOCAL,
        /**
         * Reference to a plugin in the current platform. "platform:/plugin/" and the value of remotePlugin() will be
         * prepended to the base string. Example:<br>
         * Annotated String: <code>pin_editor.gif</code><br>
         * remotePlugin(): <code>org.eclipse.ui</code><br>
         * folder(): <code>icons/full/etool16</code><br>
         * is resolved to <code>platform:/plugin/org.eclipse.ui/icons/full/etool16/pin_editor.gif</code>
         */
        PLATFORM_PLUGIN
    }

    /**
     * @return the {@link RefType}
     */
    RefType value();

    /**
     * @return the remote plugin's name
     */
    String remotePlugin() default ""; //$NON-NLS-1$

    /**
     * @return the plugin-relative folder (need not contain a starting path seperator)
     */
    String folder(); //$NON-NLS-1$
}
