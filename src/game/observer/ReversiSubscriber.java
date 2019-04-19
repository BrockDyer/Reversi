package game.observer;

/**
 * Defines the functionality of a reversi event subscriber.
 *
 * @author Brock Dyer.
 */
public interface ReversiSubscriber {

    /**
     * Called with a reversi observer to subscribe that observer to event from this.
     *
     * @param observer the observer that wants to be receive updates.
     */
    void register(ReversiObserver observer);

    /**
     * Remove an observer from the list of observers.
     *
     * @param observer the observer to remove.
     */
    void deregister(ReversiObserver observer);
}
