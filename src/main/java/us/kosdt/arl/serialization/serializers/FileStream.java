package us.kosdt.arl.serialization.serializers;

import us.kosdt.arl.serialization.Serializer;

import java.io.*;

public class FileStream extends StreamSerializer {

    private final boolean inNull;
    private final boolean outNull;

    /**
     * Constructs this using an input and output file to draw from and into. Each can be null.
     *
     * @param input The file to deserialize from. May be null.
     * @param output The file to serialize into. May be null.
     */
    public FileStream(File input, File output) throws FileNotFoundException {
        super(input == null ? null : new FileInputStream(input),
                output == null ? null : new FileOutputStream(output));
        inNull = input == null;
        outNull = output == null;
    }

    @Override
    public <T> T read(Class<T> c) throws IOException {
        if(inNull){
            throw new UnsupportedOperationException("Input file was null, so reading is unsupported");
        }
        return super.read(c);
    }

    @Override
    public void write(Object... oa) throws IOException {
        if(outNull){
            throw new UnsupportedOperationException("Output file was null, so writing is unsupported");
        }
        super.write(oa);
    }

    @Override
    public Object readAlg(int alg) throws IOException {
        if(inNull){
            throw new UnsupportedOperationException("Input file was null, so reading is unsupported");
        }
        return super.readAlg(alg);
    }

    @Override
    public <T> void writeAlg(int alg, T o) throws IOException {
        if(outNull){
            throw new UnsupportedOperationException("Output file was null, so writing is unsupported");
        }
        super.writeAlg(alg, o);
    }

    @Override
    public void close() throws IOException {
        if(!inNull){
            getInputStream().close();
        }
        if(!outNull){
            getOutputStream().close();
        }
    }
}
