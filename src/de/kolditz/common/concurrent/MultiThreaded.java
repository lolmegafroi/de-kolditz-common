/**
 * created on 06.12.2011 at 14:34:37
 */
package de.kolditz.common.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.swt.SWT;

/**
 * Indicates that this method or type is assumed to be run in a multi-threaded environment and if there are any
 * thread-constraints like in SWT, the implementation must be aware of, or take necessary steps to adhere to, these
 * constraints.
 * <p>
 * Note: This is a hint for developers.
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 * @see SWT
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface MultiThreaded {
}
