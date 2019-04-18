package common.observer;

import client.gui.events.ReversiEvent;

/**
 * An interface defining the methods any observer of the Reversi game should have.
 *
 * @author Brock Dyer.
 */
public interface ReversiObserver {

    /**
     * Handle a Reversi event.
     * @param re the Reversi event that needs to be handled by the observer.
     */
    void handle(ReversiEvent re);
}
