/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.kosdt.arl.graphics.gui.components;


import us.kosdt.arl.event.Message;
import us.kosdt.arl.graphics.gui.Component;

import us.kosdt.arl.event.messages.gui.*;

/**
 * A template button class which handles the button logic.
 *
 * @author Cruz Barnum
 */
public abstract class Button implements Component {

    /**
     * Signals that the button is disabled and will not change state based on
     * user input.
     */
    public static final int DISABLED = 0;

    /**
     * Signals that the button is not pressed, but hovered over.
     */
    public static final int HOVERED = 2;

    /**
     * Signals that the button has been pressed.
     */
    public static final int PRESSED = 3;
    
    /**
     * Signals that the button is not hovered over.
     */
    public static final int UNPRESSED = 1;
    
    private boolean hover;
    private int state;

    /**
     * Creates a new button with the given state.
     *
     * @param initialState The int id of the initial state of the button.
     */
    public Button(int initialState) {
        state = initialState;
        hover = false;
    }
    
    /**
     * Returns the state of the button.
     *
     * @return The state of the button as an int id.
     */
    public int getState() {
        return state;
    }
    
    @Override
    public boolean handleMessage(Message m, boolean first) {

        if(!first){
            return false;
        }

        boolean caught = Message.onMessageType(m, MouseButton.class, mb -> {
            switch (state) {
                case HOVERED:
                    if (mb.state && mb.changed) {
                        state = PRESSED;
                        pressed();
                    }
                    break;
                case PRESSED:
                    if (!mb.state && mb.changed) {
                        if (hover) {
                            state = HOVERED;
                        } else {
                            state = UNPRESSED;
                        }
                        unpressed();
                    }
                    break;
            }
            return contains(mb.position);
        });

        caught |= Message.onMessageType(m, MousePosition.class, mp -> {
            hover = contains(mp.position);
            switch (state) {
                case UNPRESSED:
                    if (hover) {
                        state = HOVERED;
                    }
                    break;
                case HOVERED:
                    if (!hover) {
                        state = UNPRESSED;
                    }
                    break;
            }
            return false;
        });

        return caught;
    }

    /**
     * Runs when the button is pressed.
     */
    public abstract void pressed();

    /**
     * Sets the state of the button.
     *
     * @param newState The int id of the new state of the button.
     */
    public void setState(int newState) {
        state = newState;
    }
    
    /**
     * Runs when the button is unpressed.
     */
    public abstract void unpressed();

}
