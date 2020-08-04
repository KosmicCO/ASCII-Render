package us.kosdt.arl.serialization.serializers;

import us.kosdt.arl.serialization.Deserializer;
import us.kosdt.arl.serialization.SerializationUtil;
import us.kosdt.arl.serialization.Serializer;

import java.io.*;
import java.util.*;

public class SegmentedObjectFile {

    public static final long DEFAULT_SEGMENT_SIZE = 2^12;
    public static final int BITSET_SERIALIZATION_ALG;

    private static final long HEADER_LENGTH = 5;

    private final RandomAccessFile objectFile;
    private final int segmentSize;
    private final BitSet openSegments;
    private final List<Integer> objectIndices;
    private final boolean fixedIndices;
    private Deserializer currentReader;
    private ByteArrayOutputStream currentWriterStream;
    private Serializer currentWriter;
    private boolean closed = false;

    static{
        SerializationUtil.Reader<Deserializer, BitSet> reader = ser -> {
            Byte[] raw = ser.read(Byte[].class);
            byte[] bits = new byte[raw.length];
            for (int i = 0; i < raw.length; i++){
                bits[i] = raw[i];
            }
            return BitSet.valueOf(bits);
        };

        SerializationUtil.Writer<Serializer, BitSet> writer = (ser, bs) -> {
            byte[] raw = bs.toByteArray();
            Byte[] bits = new Byte[raw.length];
            for (int i = 0; i < raw.length; i++){
                bits[i] = raw[i];
            }
            ser.write((Object) bits);
        };

        BITSET_SERIALIZATION_ALG = SerializationUtil.registerAlg(reader, writer);
    }

    public SegmentedObjectFile(File file, int segmentSize, int numObjects) throws IOException {
        if(segmentSize <= 0){
            throw new IllegalArgumentException("Segment size cannot be less than or equal to 0");
        }
        if(file.exists()){
            if(!file.delete()){
                throw new IOException("File not able to be deleted");
            }
        }
        objectFile = new RandomAccessFile(file, "rw");
        this.segmentSize = segmentSize;
        fixedIndices = numObjects != 0;
        objectFile.writeInt(segmentSize);
        objectFile.writeBoolean(fixedIndices);
        openSegments = new BitSet();
        objectIndices = new ArrayList<>(numObjects);
        currentReader = null;
        currentWriterStream = null;
        currentWriter = null;
    }

    public SegmentedObjectFile(String path, int segmentSize) throws IOException {
        this(new File(path), segmentSize, 0);
    }

    public SegmentedObjectFile(File file) throws IOException {
        if(!file.exists()){
            throw new FileNotFoundException("File was not found");
        }
        objectFile = new RandomAccessFile(file, "rw");
        segmentSize = objectFile.readInt();
        fixedIndices = objectFile.readBoolean();
        currentReader = new StreamDeserializer(new SpacedReader(0));
        openSegments = (BitSet) currentReader.readAlg(BITSET_SERIALIZATION_ALG);
        objectIndices = new ArrayList<>(Arrays.asList(currentReader.read(Integer[].class)));
        currentReader.close();
        currentWriterStream = null;
        currentWriter = null;
    }

    public SegmentedObjectFile(String path) throws IOException {
        this(new File(path));
    }

    private void seekSegment(int pos) throws IOException {
        objectFile.seek(HEADER_LENGTH + ((long) pos) * (((long) segmentSize) + 4));
    }

    private void seekSegmentTail(int pos) throws IOException {
        objectFile.seek(HEADER_LENGTH + ((long) pos) * (((long) segmentSize) + 4) + ((long) segmentSize));
    }

    private void removeIndex(int index) throws IOException {
        int curSegment = index;
        while(curSegment > 0){
            openSegments.set(curSegment, false);
            seekSegmentTail(curSegment);
            curSegment = objectFile.readInt();
        }
    }

    public void remove(int objectIndex) throws IOException {
        if(closed){
            throw new IOException("Cannot do IO on closed file");
        }
        int index = objectIndices.get(objectIndex);
        if(index == 0){
            return;
        }
        removeIndex(index);
        objectIndices.set(objectIndex, 0);
    }

    public Deserializer getReader(int objectIndex) throws IOException {
        if(closed){
            throw new IOException("Cannot do IO on closed file");
        }
        if(currentReader != null) {
            currentReader.close();
        }
        int index = objectIndices.get(objectIndex);
        if(index == 0){
            currentReader = new StreamDeserializer(InputStream.nullInputStream());
        }else {
            currentReader = new StreamDeserializer(new SpacedReader(index));
        }
        return currentReader;
    }

    private boolean writeFromSerializer(int index, int maxSegments) throws IOException {
        if(currentWriter == null){
            throw new IOException("No current writer to write from");
        }
        currentWriter.close();
        ByteArrayInputStream toWrite = new ByteArrayInputStream(currentWriterStream.toByteArray());
        int len = currentWriterStream.size();
        int numSegments = (int) Math.ceil(((double) len) / segmentSize);

        if(maxSegments != -1){
            if(maxSegments < numSegments){
                return false;
            }
        }

        int[] writeIndices = new int[numSegments];
        writeIndices[0] = index;
        openSegments.set(index);
        for (int i = 1; i < numSegments; i++){
            writeIndices[i] = openSegments.nextClearBit(writeIndices[i - 1] + 1);
            openSegments.set(writeIndices[i]);
        }

        for (int i = 0; i < numSegments; i++){
            seekSegment(writeIndices[i]);
            byte[] segmentWrite = toWrite.readNBytes(segmentSize);
            objectFile.write(segmentWrite);
            objectFile.write(new byte[segmentSize - segmentWrite.length]);
            if(i == numSegments - 1){
                objectFile.writeInt(0);
            }else{
                objectFile.writeInt(writeIndices[i + 1]);
            }
        }
        return true;
    }

    public boolean write(int objectIndex, int maxSegments) throws IOException {
        if(closed){
            throw new IOException("Cannot do IO on closed file");
        }

        int index = 0;

        if (objectIndex >= objectIndices.size()) {
            if (fixedIndices) {
                throw new IndexOutOfBoundsException("Object index out of bounds");
            } else {
                index = openSegments.nextClearBit(1);
                while(objectIndices.size() <= objectIndex){
                    objectIndices.add(0);
                }
                objectIndices.set(objectIndex, index);
            }
        } else {
            index = objectIndices.get(objectIndex);
            if (index == 0) {
                index = openSegments.nextClearBit(1);
                objectIndices.set(objectIndex, index);
            } else {
                removeIndex(index);
            }
        }
        return writeFromSerializer(index, maxSegments);
    }

    public boolean write(int objectIndex) throws IOException {
        return write(objectIndex, -1);
    }

    public Serializer getWriter() throws IOException {
        if(currentWriter != null) {
            currentWriter.close();
        }
        currentWriterStream = new ByteArrayOutputStream();
        currentWriter = new StreamSerializer(currentWriterStream);
        return currentWriter;
    }

    public void save() throws IOException {
        if(closed){
            throw new IOException("Cannot do IO on closed file");
        }
        remove(0);
        Serializer ser = getWriter();
        ser.writeAlg(BITSET_SERIALIZATION_ALG, openSegments);
        ser.write((Object) objectIndices.toArray(new Integer[0]));
        writeFromSerializer(0, -1);
    }

    public void close() throws IOException {
        if(closed){
            return;
        }
        save();
        currentReader.close();
        objectFile.close();
        closed = true;
    }

    public boolean isClosed(){
        return closed;
    }

    private class SpacedReader extends InputStream{

        private InputStream segment;
        private int nextSegment;

        public SpacedReader(int start) throws IOException {
            loadNextSegment(start);
        }

        private void loadNextSegment(int next) throws IOException {
            seekSegment(next);
            byte[] seg = new byte[segmentSize];
            objectFile.read(seg);
            segment = new ByteArrayInputStream(seg, 0, segmentSize);
            nextSegment = objectFile.readInt();
        }

        @Override
        public int read() throws IOException {
            int nextByte = segment.read();
            if(nextByte == -1){
                if(nextSegment == 0){
                    return -1;
                }else{
                    loadNextSegment(nextSegment);
                    return read();
                }
            }
            return nextByte;
        }

        @Override
        public void close() throws IOException {
            super.close();
            segment = InputStream.nullInputStream();
            nextSegment = 0;
        }
    }
}
