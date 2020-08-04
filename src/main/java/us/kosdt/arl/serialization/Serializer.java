package us.kosdt.arl.serialization;

import java.io.*;

/**
 * A class for defining streams to serialize objects into. This includes general serialization points such as Socket
 * packet serialization or file serialization among other things.
 */
public interface Serializer {

    /**
     * Serializes the given objects using registered type serialization methods.
     * @param oa The objects to serialize.
     * @throws IOException If there is a problem writing.
     */
    default void write(Object ... oa) throws IOException {
        for (Object o : oa) {
            if (SerializationUtil.isRegistered(o.getClass())) {
                SerializationUtil.getWriter((Class) o.getClass()).write(this, o);
            } else {
                throw new RuntimeException("Writer not found for data type: " + o.getClass());
            }
        }
    }

    /**
     * Serializes the given object using the provided method by id.
     * @param alg The id of the serialization method to use.
     * @param o The object to serialize.
     * @param <T> The type of the object to serialize.
     * @throws IOException If there is a problem writing.
     */
    default <T> void writeAlg(int alg, T o) throws IOException {
        ((SerializationUtil.Writer<Serializer, T>) SerializationUtil.getWriter(alg)).write(this, o);
    }

    /**
     * Returns the 'OutputStream' {@link java.io.OutputStream} of this.
     * @return The 'OutputStream' {@link java.io.OutputStream} of this.
     */
    DataOutputStream getOutputStream();

    /**
     * Closes the serializer and associated streams.
     * @throws IOException If there is a problem closing.
     */
    default void close() throws IOException {
        getOutputStream().close();
    }
}
