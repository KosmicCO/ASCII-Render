/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package us.kosdt.arl.event;

/**
 * Interface for creating an object which does something with a message that it
 * receives.
 *
 * @author Cruz Barnum
 * @param <M> The message type that the listener accepts.
 */
public interface Listener<M extends Message> {

    /**
     * Does something with the message it receives.
     *
     * @param message Receiving message. It is left as M extends Message so that
     * listeners can use generic assignment to auto-cast messages.
     */
    public void receiveMessage(M message);
}
