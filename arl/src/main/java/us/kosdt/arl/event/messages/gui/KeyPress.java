/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.kosdt.arl.event.messages.gui;

import org.w3c.dom.ls.LSOutput;
import us.kosdt.arl.event.Message;

/**
 * Transmits the new state of a certain key.
 *
 * @author TARS
 */
public class KeyPress implements Message {

    /**
     * The key to which this message applies.
     */
    public final int key;

    /**
     * The state of the button.
     */
    public final boolean state;

    /**
     * Whether the state of the key just changed.
     */
    public final boolean changed;

    /**
     * Mode keys held down during key press.
     */
    public final int mods;

    /**
     * Default constructor.
     *
     * @param k The key.
     * @param s The state.
     * @param c Whether the state just changed.
     * @param m Mod keys held down during key press.
     */
    public KeyPress(int k, boolean s, boolean c, int m) {
        key = k;
        state = s;
        changed = c;
        mods = m;
    }

    public String toString() {
        return (new StringBuilder("{"))
                .append("key: ").append(key)
                .append(", state: ").append(state)
                .append(", changed: ").append(changed)
                .append(", mods: ").append(mods)
                .append("}").toString();
    }
}
