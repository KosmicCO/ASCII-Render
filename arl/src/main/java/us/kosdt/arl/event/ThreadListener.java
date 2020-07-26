package us.kosdt.arl.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadListener implements ParentListener {

    private int nextID = 0;

    private volatile Thread thread;
    private final Queue<Message> messageQueue;
    private final Map<Class<? extends Message>, List<Listener>> receivers;
    private final Map<Integer, MessageListenerPair> receiverID;
    private boolean running;

    public ThreadListener() {
        messageQueue = new ConcurrentLinkedQueue();
        receivers = new ConcurrentHashMap();
        receiverID = new ConcurrentHashMap();
        running = false;
    }

    /**
     * Adds a listener to be alerted when a message that it subscribed to was
     * received.
     *
     * @param <M> The message type to subscribe to.
     * @param messageType The message type to subscribe to.
     * @param listener A listener which specifically takes the message type M.
     * @return The id of the listener, which can be used to remove it.
     */
    @Override
    public synchronized <M extends Message> int addListener(Class<M> messageType, Listener<M> listener){
        if (receivers.containsKey(messageType)) {
            if (!receivers.get(messageType).contains(listener)) {
                receivers.get(messageType).add(listener);
            }
        } else {
            List listenerList = new ArrayList();
            listenerList.add(listener);
            receivers.put(messageType, listenerList);
        }
        int id = nextID;
        nextID++;
        receiverID.put(id, new MessageListenerPair(messageType, listener));
        return id;
    }

    /**
     * Removes the listener referred to by the id.
     *
     * @param id The id of the listener.
     */
    @Override
    public synchronized void removeListener(int id) {
        MessageListenerPair entry = receiverID.get(id);
        if (entry == null) {
            throw new IllegalArgumentException("Given id does not match a listener: " + id);
        }

        List<Listener> list = receivers.get(entry.mType);
        if (list == null) {
            throw new IllegalArgumentException("Given id does not match a listener: " + id);
        }
        list.remove(entry.lstn);
        if (list.isEmpty()) {
            receivers.remove(entry.mType);
        }
        receiverID.remove(id);
    }

    /**
     * Joins with the client thread.
     */
    public void join() {
        Thread t = thread;
        if (t != null) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void receiveMessage(Message message) {
        if (!receivers.containsKey(message.getClass())) {
            receivers.put(message.getClass(), new ArrayList());
        }
        messageQueue.add(message);
    }

    /**
     * Starts the CLIENT_LISTENER thread.
     *
     * @param init The initial code to be run.
     * @param close The final code to be run when stop() is called.
     */
    public void start(Runnable init, Runnable close) {
        if (thread != null) {
            return;
        }
        running = true;
        thread = new Thread(() -> {
            init.run();
            while (running) {
                if (!messageQueue.isEmpty()) {
                    Message m = messageQueue.remove();
                    if (m != null) {
                        receivers.get(m.getClass()).forEach(l -> l.receiveMessage(m));
                    }
                }
            }
            close.run();
            thread = null;
        });
        thread.start();
    }

    /**
     * Signals the listener thread to close.
     */
    public void stop() {
        running = false;
    }

    public boolean isRunning(){
        return running;
    }
}
