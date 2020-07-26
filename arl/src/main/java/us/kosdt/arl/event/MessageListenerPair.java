package us.kosdt.arl.event;

class MessageListenerPair<M extends Message> {

    public Class<M> mType;
    public Listener<M> lstn;

    public MessageListenerPair(Class<M> message, Listener<M> listener) {
        mType = message;
        lstn = listener;
    }
}
