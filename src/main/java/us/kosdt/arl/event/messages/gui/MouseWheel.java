/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.kosdt.arl.event.messages.gui;


import us.kosdt.arl.event.Message;
import us.kosdt.arl.util.math.Vec2d;

/**
 * Transmits the state of the mouse wheel.
 *
 * @author TARS
 */
public class MouseWheel implements Message {

    /**
     * The current wheel offset.
     */
    public final Vec2d wheelOffset;

    /**
     * The delta vector of the offset.
     */
    public final Vec2d wheelDelta;

    /**
     * Default constructor.
     *
     * @param wheel The current wheel offset.
     * @param delta The delta of the wheel offset.
     */
    public MouseWheel(Vec2d wheel, Vec2d delta) {
        wheelOffset = wheel;
        wheelDelta = delta;
    }

    public String toString() {
        return (new StringBuilder("{"))
                .append("wheelOffset: ").append(wheelOffset)
                .append(", wheelDelta: ").append(wheelDelta)
                .append("}").toString();
    }
}
