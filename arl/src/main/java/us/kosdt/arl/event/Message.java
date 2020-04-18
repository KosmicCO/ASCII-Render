package us.kosdt.arl.event;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A type to spread information between Listeners.
 *
 * @author Cruz Barnum
 */
public interface Message {

    /**
     * Checks whether the given message is of the type of MessageType. If so,
     * the handler is run on the given message.
     *
     * @param <M> The type to run the handler on.
     * @param message The given message to check.
     * @param messageType The type to run the handler on.
     * @param handler The handler to be run if the message is of the correct
     * type.
     * @return Returns whether the message was handled.
     */
    public static <M extends Message> boolean onMessageType(Message message, Class<M> messageType, Consumer<M> handler){
        if(message.getClass().equals(messageType)){
            handler.accept((M) message);
            return true;
        }
        return false;
    }

    /**
     * Checks whether the given message is of the type of MessageType. If so,
     * the handler is run on the given message.
     *
     * @param <M> The type to run the handler on.
     * @param message The given message to check.
     * @param messageType The type to run the handler on.
     * @param handler The handler to be run if the message is of the correct
     * type.
     * @return Returns false if the message was not handled, else returns the
     * output from the handler.
     */
    public static <M extends Message> boolean onMessageType(Message message, Class<M> messageType, Predicate<M> handler) {
        if (message.getClass().equals(messageType)) {
            return handler.test((M) message);
        }
        return false;
    }
}
