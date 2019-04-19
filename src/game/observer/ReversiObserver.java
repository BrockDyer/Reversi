package game.observer;

import gui.events.ReversiEvent;

/**
 * An interface defining the methods any observer of the reversi game should have.
 *
 * @author Brock Dyer.
 */
public interface ReversiObserver {

    /**
     * Handle a reversi event.
     * @param re the reversi event that needs to be handled by the observer.
     */
    void handle(ReversiEvent re);
}
