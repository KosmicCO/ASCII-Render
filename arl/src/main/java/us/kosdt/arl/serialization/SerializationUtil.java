package us.kosdt.arl.serialization;

import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.util.math.Vec2d;
import us.kosdt.arl.util.math.Vec3d;
import us.kosdt.arl.util.math.Vec4d;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class SerializationUtil {

    private static final Map<Class, Reader<Serializer, Object>> READERS = new HashMap();
    private static final Map<Class, Writer<Serializer, Object>> WRITERS = new HashMap();
    private static final Map<Class, Class> ALIASES = new HashMap();

    static{
        registerBasicType(Boolean.class, DataInputStream::readBoolean, DataOutputStream::writeBoolean);
        registerBasicType(Byte.class, DataInputStream::readByte, (o, b) -> o.writeByte(b));
        registerBasicType(Float.class, DataInputStream::readFloat, DataOutputStream::writeFloat);
        registerBasicType(Double.class, DataInputStream::readDouble, DataOutputStream::writeDouble);
        registerBasicType(Integer.class, DataInputStream::readInt, DataOutputStream::writeInt);
        registerBasicType(Long.class, DataInputStream::readLong, DataOutputStream::writeLong);
        registerBasicType(String.class, i -> i.readUTF(), DataOutputStream::writeUTF);

        registerType(Color.class, ser -> new Color(ser.read(Double.class), ser.read(Double.class), ser.read(Double.class), ser.read(Double.class)),
                (ser, o) -> ser.write(o.r, o.g, o.b, o.a));
        registerType(Vec2d.class, ser -> new Vec2d(ser.read(Double.class), ser.read(Double.class)),
                (ser, v) -> ser.write(v.x, v.y));
        registerType(Vec3d.class, ser -> new Vec3d(ser.read(Double.class), ser.read(Double.class), ser.read(Double.class)),
                (ser, v) -> ser.write(v.x, v.y, v.z));
        registerType(Vec4d.class, ser -> new Vec4d(ser.read(Double.class), ser.read(Double.class), ser.read(Double.class), ser.read(Double.class)),
                (ser, v) -> ser.write(v.x, v.y, v.z, v.w));

        registerArrayType(Integer.class);
        registerArrayType(Double.class);
    }

    public static Reader<Serializer, Object> getReader(Class c){
        return READERS.get(c);
    }

    public static Writer<Serializer, Object> getWriter(Class c){
        return WRITERS.get(c);
    }

    public static boolean containsReader(Class c){
        return READERS.containsKey(c);
    }

    public static boolean containsWriter(Class c){
        return WRITERS.containsKey(c);
    }

    public static <T> void registerType(Class<T> c, Reader<Serializer, T> reader, Writer<Serializer, T> writer) {
        if (!READERS.containsKey(c)) {
            READERS.put(c, (Reader) reader);
            WRITERS.put(c, (Writer) writer);
        }
    }

    public static <T extends SerializeWritable> void registerType(Class<T> c, Reader<Serializer, T> reader){
        registerType(c, reader, (ser, o) -> ((SerializeWritable) o).write(ser));
    }

    private static <T> void registerBasicType(Class<T> c, Reader<DataInputStream, T> reader, Writer<DataOutputStream, T> writer) {
        registerType(c, ser -> reader.read(ser.getInputStream()), (ser, t) -> writer.write(ser.getOutputStream(), t));
    }

    public static <T> void registerArrayType(Class<T> c) {
        if (!READERS.containsKey(c)) {
            throw new RuntimeException("Cannot register an array type for a type which is not registered: " + c);
        }
        Class arrayClass = Array.newInstance(c, 0).getClass();
        if (!READERS.containsKey(arrayClass)) {
            READERS.put(arrayClass, ser -> {
                int len = ser.read(Integer.class);
                T[] array = (T[]) Array.newInstance(c, len);
                for (int i = 0; i < len; i++) {
                    array[i] = ser.read(c);
                }
                return array;
            });
            WRITERS.put(arrayClass, (ser, a) -> {
                Object[] array = (Object[]) a;
                ser.write(array.length);
                for (Object o : array) {
                    if (o == null) {
                        throw new RuntimeException("Null is not serializable.");
                    }
                    ser.write(o);
                }
            });
        }
    }

    public static <T, S> void registerMapType(Class<T> kc, Class<S> vc) {
        registerArrayType(kc);
        registerArrayType(vc);
        Map<T, S> s = new HashMap();
        Class mapClass = s.getClass();
        if (!READERS.containsKey(mapClass)) {
            READERS.put(mapClass, ser -> {
                T[] keys = (T[]) ser.read(Array.newInstance(kc, 0).getClass());
                S[] values = (S[]) ser.read(Array.newInstance(vc, 0).getClass());
                if (keys.length != values.length) {
                    throw new RuntimeException("Key and value lengths do not match.");
                }
                Map<T, S> map = new HashMap();
                for (int i = 0; i < keys.length; i++) {
                    map.put(keys[i], values[i]);
                }
                return map;
            });
            WRITERS.put(mapClass, (ser, m) -> {
                Map<T, S> map = (Map<T, S>) m;
                Object[] rawKeys = map.keySet().toArray();
                T[] keys = (T[]) Array.newInstance(kc, rawKeys.length);
                for (int i = 0; i < rawKeys.length; i++) {
                    keys[i] = (T) rawKeys[i];
                }
                S[] values = (S[]) Array.newInstance(vc, keys.length);
                for (int i = 0; i < keys.length; i++) {
                    if (keys[i] == null) {
                        throw new RuntimeException("Null in keys is not serializable.");
                    }
                    values[i] = map.get(keys[i]);
                    if (values[i] == null) {
                        throw new RuntimeException("Null in values is not serializable.");
                    }
                }
                ser.write(keys, values);
            });
        }
    }

    public static <T extends S, S> void registerAlias(Class<T> c, Class<S> alias){
        ALIASES.put(c, alias);
    }

    public static Class getAlias(Class c){
        return ALIASES.get(c);
    }

    @FunctionalInterface
    public static interface Reader<T, R> {

        public R read(T t) throws IOException;
    }

    @FunctionalInterface
    public static interface Writer<T, R> {

        public void write(T t, R r) throws IOException;
    }
}
