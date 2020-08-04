package us.kosdt.arl.serialization.serializers;

import us.kosdt.arl.serialization.Serializer;

import java.io.*;

/**
 * Simple 'Serializer' {@link Serializer} which uses the bare minimum to set up serialization.
 */
public class StreamSerializer implements Serializer {

    private final DataOutputStream output;

    /**
     * Constructs this using an output stream to serialize into.
     * @param os The output stream to serialize into.
     */
    public StreamSerializer(OutputStream os){
        output = new DataOutputStream(os);
    }

    /**
     * Returns a 'StreamSerializer' {@link StreamSerializer} to serialize into a file.
     * @param file The file to serialize into.
     * @return The 'StreamSerializer' {@link StreamSerializer} associated with the file.
     * @throws FileNotFoundException If the given file is not found
     */
    public static StreamSerializer fileSerializer(File file) throws FileNotFoundException {
        return new StreamSerializer(new FileOutputStream(file));
    }

    /**
     * Returns a 'StreamSerializer' {@link StreamSerializer} to serialize into a file.
     * @param path The string path to a file to serialize into.
     * @return The 'StreamSerializer' {@link StreamSerializer} associated with the file.
     * @throws FileNotFoundException If the given file is not found
     */
    public static StreamSerializer fileSerializer(String path) throws FileNotFoundException {
        return new StreamSerializer(new FileOutputStream(path));
    }

    @Override
    public DataOutputStream getOutputStream() {
        return output;
    }
}
