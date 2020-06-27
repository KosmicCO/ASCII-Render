/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.kosdt.arl.graphics.gui;


import java.util.ArrayList;
import java.util.List;

import us.kosdt.arl.event.Message;
import us.kosdt.arl.util.math.Vec2d;

/**
 * A component which holds other components in an order.
 *
 * @author TARS
 */
public class Container implements Component {

    private final List<Component> components;

    /**
     * Constructs a component container with no elements.
     */
    public Container() {
        components = new ArrayList();
    }

    /**
     * Adds the component to render and receive messages first.
     *
     * @param comp The component to add to top.
     */
    public Container addTop(Component comp) {
        components.add(comp);
        return this;
    }

    @Override
    public boolean contains(Vec2d v) {
        return components.stream().anyMatch(c -> c.contains(v));
    }

    @Override
    public boolean handleMessage(Message message, boolean first) {
        for (int i = components.size() - 1; i >= 0; i--) {
            first |= !components.get(i).handleMessage(message, first);
        }
        return !first;
    }

    /**
     * Removes the given component from the container.
     *
     * @param comp The component to remove from the container.
     */
    public Container remove(Component comp) {
        components.remove(comp);
        return this;
    }

    @Override
    public void render() {
        components.forEach(Component::render);
    }
}
