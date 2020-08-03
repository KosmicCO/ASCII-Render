package us.kosdt.arl.serialization.serializers;

import us.kosdt.arl.serialization.Deserializer;
import us.kosdt.arl.serialization.Serializer;

import java.io.*;

/**
 * Simple 'Deserializer' {@link Deserializer} which uses the bare minimum to set up deserialization.
 */
public class StreamDeserializer implements Deserializer {

    private final DataInputStream input;

    /**
     * Constructs this using an input stream to deserialize from.
     * @param is The input stream to deserialize from.
     */
    public StreamDeserializer(InputStream is){
        input = new DataInputStream(is);
    }

    /**
     * Returns a 'StreamDeserializer' {@link StreamDeserializer} to deserialize from a file.
     * @param file The file to deserialize from.
     * @return The 'StreamDeserializer' {@link StreamDeserializer} associated with the file.
     * @throws FileNotFoundException If the given file is not found
     */
    public static StreamDeserializer fileDeserializer(File file) throws FileNotFoundException {
        return new StreamDeserializer(new FileInputStream(file));
    }

    /**
     * Returns a 'StreamDeserializer' {@link StreamDeserializer} to deserialize from a file.
     * @param path The string path to a file to deserialize from.
     * @return The 'StreamDeserializer' {@link StreamDeserializer} associated with the file.
     * @throws FileNotFoundException If the given file is not found
     */
    public static StreamDeserializer fileDeserializer(String path) throws FileNotFoundException {
        return new StreamDeserializer(new FileInputStream(path));
    }

    @Override
    public DataInputStream getInputStream() {
        return input;
    }
}
