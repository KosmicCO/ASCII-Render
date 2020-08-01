package us.kosdt.arl.event;

class MessageListenerPair<M extends Message> {

    public final Class<M> mType;
    public final Listener<M> lstn;

    public MessageListenerPair(Class<M> message, Listener<M> listener) {
        mType = message;
        lstn = listener;
    }
}
