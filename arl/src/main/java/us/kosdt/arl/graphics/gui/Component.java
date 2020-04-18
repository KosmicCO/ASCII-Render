package us.kosdt.arl.graphics.gui;

import us.kosdt.arl.event.Message;
import us.kosdt.arl.util.math.Vec2d;

/**
 * An interface for a renderable component which reacts to user input.
 *
 * @author Cruz Barnum
 */
public interface Component {

    /**
     * Determines whether v is contained by the component.
     *
     * @param v The vector to check.I
     * @return Whether the vector v is contained by this.
     */
    public boolean contains(Vec2d v);

    /**
     * Gets a message to handle.
     *
     * @param message The message to handle.
     * @return A mask on the handled message. If it returns true, an ordered
     * container will not pass the message onto later components.
     */
    public boolean handleMessage(Message message);

    /**
     * Renders the component.
     */
    public void render();
}
