package us.kosdt.arl.serialization;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * A class for defining streams to deserialize objects from. This includes general deserialization points such as Socket
 * packet deserialization or file deserialization among other things.
 */
public interface Deserializer {

    /**
     * Deserializes object of the given type from registered types.
     * @param c The class to deserialize.
     * @param <T> The type of the deserialized object.
     * @return The deserialized object.
     * @throws IOException If there is a problem reading.
     */
    default <T> T read(Class<T> c) throws IOException {
        if(!SerializationUtil.isRegistered(c)){
            throw new RuntimeException("Reader not found for data type: " + c.toString());
        }
        if(SerializationUtil.isAliased(c)){
            throw new RuntimeException("Cannot read aliased type");
        }
        return SerializationUtil.getReader(c).read(this);
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
     * Returns the 'InputStream' {@link java.io.InputStream} of this.
     * @return The 'InputStream' {@link java.io.InputStream} of this.
     */
    DataInputStream getInputStream();

    /**
     * Closes the deserializer and associated streams.
     * @throws IOException If there is a problem closing.
     */
    default void close() throws IOException {
        getInputStream().close();
    }
}
