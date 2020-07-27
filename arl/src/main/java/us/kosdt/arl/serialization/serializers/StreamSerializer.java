package us.kosdt.arl.serialization.serializers;

import us.kosdt.arl.serialization.SerializationUtil;
import us.kosdt.arl.serialization.Serializer;

import java.io.*;

public class StreamSerializer implements Serializer {

    private DataInputStream input;
    private DataOutputStream output;

    public StreamSerializer(InputStream is, OutputStream os){
        input = new DataInputStream(is);
        output = new DataOutputStream(os);
    }

    @Override
    public <T> T read(Class<T> c) throws IOException {
        if(!SerializationUtil.containsReader(c)){
            throw new RuntimeException("Reader not found for data type: " + c.toString());
        }
        return (T) SerializationUtil.getReader(c).read(this);
    }

    @Override
    public void write(Object... oa) throws IOException {
        for (Object o : oa) {
            if (!SerializationUtil.containsWriter(o.getClass())) {
                Class alias = SerializationUtil.getAlias(o.getClass());
                if (SerializationUtil.containsWriter(alias)) {
                    SerializationUtil.getWriter(alias).write(this, o);
                } else {
                    throw new RuntimeException("Writer not found for data type: " + o.getClass());
                }
            } else {
                SerializationUtil.getWriter(o.getClass()).write(this, o);
            }
        }
    }

    @Override
    public DataInputStream getInputStream() {
        return input;
    }

    @Override
    public DataOutputStream getOutputStream() {
        return output;
    }

    @Override
    public void close() throws IOException {
        input.close();
        output.close();
    }
}
