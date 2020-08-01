package us.kosdt.arl.serialization.serializers;

import us.kosdt.arl.serialization.Serializer;

import java.io.*;

/**
 * Simple 'Serializer' {@link Serializer} which uses the bare minimum to set up serialization.
 */
public class StreamSerializer implements Serializer {

    private final DataInputStream input;
    private final DataOutputStream output;

    /**
     * Constructs this using an input and output stream to draw from and into.
     * @param is The input stream to deserialize from.
     * @param os The output stream to serialize into.
     */
    public StreamSerializer(InputStream is, OutputStream os){
        input = new DataInputStream(is);
        output = new DataOutputStream(os);
    }

    @Override
    public DataInputStream getInputStream() {
        return input;
    }

    @Override
    public DataOutputStream getOutputStream() {
        return output;
    }
}
