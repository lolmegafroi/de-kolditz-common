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
package de.kolditz.common.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this method or type is assumed to be run in a multi-threaded environment and if there are any
 * thread-constraints like in SWT, the implementation must be aware of, or take necessary steps to adhere to, these
 * constraints.
 * <p>
 * Note: This is a hint for developers.
 * 
 * @author Till Kolditz - Till.Kolditz@gmail.com
 * @see SWT
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MultiThreaded {
}
