/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.kosdt.arl.event.messages.gui;


import us.kosdt.arl.event.Message;
import us.kosdt.arl.util.math.Vec2d;

/**
 * Transmits the new mouse position and the distance traveled.
 *
 * @author TARS
 */
public class MousePosition implements Message {

    /**
     * New position of the mouse.
     */
    public final Vec2d position;

    /**
     * Vector representing change from the old mouse position.
     */
    public final Vec2d delta;

    /**
     * Default constructor.
     *
     * @param pos The new position of the mouse.
     * @param d The delta vector from the old mouse position.
     */
    public MousePosition(Vec2d pos, Vec2d d) {
        position = pos;
        delta = d;
    }

    public String toString() {
        return (new StringBuilder("{"))
                .append("delta: ").append(delta)
                .append(", position: ").append(position)
                .append("}").toString();
    }
}
