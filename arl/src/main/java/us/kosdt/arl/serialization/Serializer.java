package us.kosdt.arl.serialization;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Serializer {

    <T> T read(Class<T> c) throws IOException;

    void write(Object ... oa) throws IOException;

    DataInputStream getInputStream();

    DataOutputStream getOutputStream();

    void close() throws IOException;
}
