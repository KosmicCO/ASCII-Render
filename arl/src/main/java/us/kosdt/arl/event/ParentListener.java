package us.kosdt.arl.event;

public interface ParentListener extends Listener {

    /**
     * Adds a listener to be alerted when a message that it subscribed to was
     * received.
     *
     * @param <M> The message type to subscribe to.
     * @param messageType The message type to subscribe to.
     * @param listener A listener which specifically takes the message type M.
     * @return The id of the listener, which can be used to remove it.
     */
    <M extends Message> int addListener(Class<M> messageType, Listener<M> listener);

    /**
     * Removes the listener referred to by the id.
     *
     * @param id The id of the listener.
     */
    void removeListener(int id);
}
