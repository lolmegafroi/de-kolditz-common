/**
 * 
 */
package de.kolditz.common;

/**
 * 
 * @author Till Kolditz - Till.Kolditz@GoogleMail.com
 */
public interface IObservable<E> {
    /**
     * Registers an observer to this object's list of observers.
     * 
     * @param observer
     *            the {@link IObserver}
     * @return whether the observer could be added
     */
    boolean registerObserver(IObserver<E> observer);

    /**
     * Unregisters an observer fromthis object's list of observers.
     * 
     * @param observer
     *            the {@link IObserver}
     * @return whether the observer could be added
     */
    boolean unregisterObserver(IObserver<E> observer);
}
