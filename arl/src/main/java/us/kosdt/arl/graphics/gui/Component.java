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
     * @param first   Whether this component received the message without components before returning true on their handleMessage usage.
     * @return A mask on the handled message. For ordered containers, the output will be negated and then applied to a count with or so that on returning true, later components will receive false as first.
     */
    public boolean handleMessage(Message message, boolean first);

    /**
     * Renders the component.
     */
    public void render();
}
