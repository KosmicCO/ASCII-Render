/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.kosdt.arl.event.messages.gui;

import us.kosdt.arl.event.Message;

/**
 * Transmits a submitted character.
 *
 * @author TARS
 */
public class CharSubmit implements Message {

    /**
     * Character submitted to type.
     */
    public final int key;

    /**
     * Mode keys held down during key press.
     */
    public final int mods;

    /**
     * Default constructor.
     *
     * @param c Character submitted to type.
     * @param m Mod keys held down during key press.
     */
    public CharSubmit(int c, int m) {
        key = c;
        mods = m;
    }

    public String toString() {
        return (new StringBuilder("{"))
                .append("key: ").append(key)
                .append(", mods: ").append(mods)
                .append("}").toString();
    }
}

