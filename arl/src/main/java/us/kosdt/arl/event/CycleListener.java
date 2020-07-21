package us.kosdt.arl.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CycleListener implements ParentListener {

    private static int nextID = 0;

    private final Queue<Message> messageQueue;
    private final Map<Class<? extends Message>, List<Listener>> receivers;
    private final Map<Integer, MessageListenerPair> receiverID;

    public CycleListener(){
        messageQueue = new ConcurrentLinkedQueue();
        receivers = new ConcurrentHashMap();
        receiverID = new ConcurrentHashMap();
    }

    @Override
    public <M extends Message> int addListener(Class<M> messageType, Listener<M> listener) {
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

    @Override
    public void removeListener(int id) {
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

    public void cycle(){
        while(true){
            Message m = messageQueue.remove();
            if(m != null) {
                receivers.get(m.getClass()).forEach(l -> l.receiveMessage(m));
            }
            if(messageQueue.isEmpty() || m instanceof CycleMessage){
                messageQueue.add(new CycleMessage());
                break;
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

    public class CycleMessage implements Message {}
}
