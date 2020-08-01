/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.kosdt.arl.event.messages.gui;


import us.kosdt.arl.event.Message;
import us.kosdt.arl.util.math.Vec2d;

/**
 * Transmits the state of the mouse buttons.
 *
 * @author TARS
 */
public class MouseButton implements Message {

    /**
     * The button to which this message applies.
     */
    public final int button;

    /**
     * The state of the mouse button.
     */
    public final boolean state;

    /**
     * Whether the mouse button changed just now.
     */
    public final boolean changed;

    /**
     * The mouse position at the time of the click;
     */
    public final Vec2d position;

    /**
     * Default constructor.
     *
     * @param b The button.
     * @param s The state.
     * @param c Whether the state just changed.
     * @param mouse The mouse position at the time of the button press.
     */
    public MouseButton(int b, boolean s, boolean c, Vec2d mouse) {
        button = b;
        state = s;
        changed = c;
        position = mouse;
    }

    public String toString() {
        return (new StringBuilder("{"))
                .append("button: ").append(button)
                .append(", state: ").append(state)
                .append(", changed: ").append(changed)
                .append(", position: ").append(position)
                .append("}").toString();
    }
}
