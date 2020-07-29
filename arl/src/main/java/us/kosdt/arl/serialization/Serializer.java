package us.kosdt.arl.serialization;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A class for defining streams to serialize objects into. This includes general serialization points such as Socket
 * packet serialization or file serialization among other things.
 */
public interface Serializer {

    /**
     * Deserializes object of the given type from registered types.
     * @param c The class to deserialize.
     * @param <T> The type of the deserialized object.
     * @return The deserialized object.
     * @throws IOException If there is a problem reading.
     */
    default <T> T read(Class<T> c) throws IOException {
        if(!SerializationUtil.isSerializable(c)){
            throw new RuntimeException("Reader not found for data type: " + c.toString());
        }
        if(SerializationUtil.isAliased(c)){
            throw new RuntimeException("Cannot read aliased type");
        }
        return SerializationUtil.getReader(c).read(this);
    }

    /**
     * Serializes the given objects using registered type serialization methods.
     * @param oa The objects to serialize.
     * @throws IOException If there is a problem writing.
     */
    default void write(Object ... oa) throws IOException {
        for (Object o : oa) {
            if (SerializationUtil.isSerializable(o.getClass())) {
                SerializationUtil.getWriter((Class) o.getClass()).write(this, o);
            } else {
                throw new RuntimeException("Writer not found for data type: " + o.getClass());
            }
        }
    }

    /**
     * Deserializes object of the given type using the provided method by id.
     * @param alg The id of the deserialization method to use.
     * @return The deserialized object.
     * @throws IOException If there is a problem reading.
     */
    default Object readAlg(int alg) throws IOException {
        return SerializationUtil.getReader(alg).read(this);
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
     * Returns the 'InputStream' {@link java.io.InputStream} of this.
     * @return The 'InputStream' {@link java.io.InputStream} of this.
     */
    InputStream getInputStream();

    /**
     * Returns the 'OutputStream' {@link java.io.OutputStream} of this.
     * @return The 'OutputStream' {@link java.io.OutputStream} of this.
     */
    OutputStream getOutputStream();

    /**
     * Closes the serializer and associated streams.
     * @throws IOException If there is a problem closing.
     */
    default void close() throws IOException {
        getInputStream().close();
        getOutputStream().close();
    }
}
