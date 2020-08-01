package us.kosdt.arl.serialization;

import us.kosdt.arl.graphics.Color;
import us.kosdt.arl.util.math.Vec2d;
import us.kosdt.arl.util.math.Vec3d;
import us.kosdt.arl.util.math.Vec4d;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Utility for registering serialization and deserialization methods for given types.
 */
public final class SerializationUtil {

    private static final Map<Class, Integer> ALG_ID = new HashMap<>();
    private static final Map<Class, Class> ALIASES = new HashMap<>();

    private static final List<Reader<Serializer, Object>> READERS = new ArrayList<>();
    private static final List<Writer<Serializer, Object>> WRITERS = new ArrayList<>();

    static{
        registerBasicType(Boolean.class, DataInputStream::readBoolean, DataOutputStream::writeBoolean);
        registerBasicType(Byte.class, DataInputStream::readByte, (Writer<DataOutputStream, Byte>) DataOutputStream::writeByte);
        registerBasicType(Float.class, DataInputStream::readFloat, DataOutputStream::writeFloat);
        registerBasicType(Double.class, DataInputStream::readDouble, DataOutputStream::writeDouble);
        registerBasicType(Integer.class, DataInputStream::readInt, DataOutputStream::writeInt);
        registerBasicType(Long.class, DataInputStream::readLong, DataOutputStream::writeLong);
        registerBasicType(String.class, DataInput::readUTF, DataOutputStream::writeUTF);

        registerType(Color.class, ser -> new Color(ser.read(Double.class), ser.read(Double.class), ser.read(Double.class), ser.read(Double.class)),
                (ser, o) -> ser.write(o.r, o.g, o.b, o.a));
        registerType(Vec2d.class, ser -> new Vec2d(ser.read(Double.class), ser.read(Double.class)),
                (ser, v) -> ser.write(v.x, v.y));
        registerType(Vec3d.class, ser -> new Vec3d(ser.read(Double.class), ser.read(Double.class), ser.read(Double.class)),
                (ser, v) -> ser.write(v.x, v.y, v.z));
        registerType(Vec4d.class, ser -> new Vec4d(ser.read(Double.class), ser.read(Double.class), ser.read(Double.class), ser.read(Double.class)),
                (ser, v) -> ser.write(v.x, v.y, v.z, v.w));

        registerDefaultArrayType(Boolean.class);
        registerDefaultArrayType(Byte.class);
        registerDefaultArrayType(Float.class);
        registerDefaultArrayType(Double.class);
        registerDefaultArrayType(Integer.class);
        registerDefaultArrayType(Long.class);
        registerDefaultArrayType(String.class);
    }

    /**
     * Returns whether the given type has an alias type that is used to serialize it.
     * @param c The type to check.
     * @return Whether the type is aliased.
     */
    public static boolean isAliased(Class c){
        return ALIASES.containsKey(c);
    }

    /**
     * Registers a serialization alias for a given type.
     * @param c The class to register an alias for.
     * @param alias The class to set as an alias for serialization.
     * @param <T> The type to register an alias for.
     * @param <S> The type to set as an alias for serialization.
     */
    public static <T extends S, S> void registerAlias(Class<T> c, Class<S> alias) {
        if (ALG_ID.containsKey(alias)){
            if (!isSerializable(c)) {
                ALIASES.put(c, alias);
            }else {
                throw new IllegalArgumentException("Types cannot be registered more than once");
            }
        }else{
            throw new IllegalArgumentException("Alias must be directly registered");
        }
    }

    /**
     * Returns whether the given type can be serialized using the registered types and aliased types.
     * @param c The type to check for registration.
     * @return Whether the type could be serialized.
     */
    public static boolean isSerializable(Class c){
        return ALG_ID.containsKey(c) || ALIASES.containsKey(c);
    }

    /**
     * Returns the type that the given type will serialize to. If the given type is registered directly, it will be
     * returned. If the given type has an alias, the alias type will be returned. If the type is unregistered, null will
     * be returned.
     * @param c The class to check for containment. May be null.
     * @return Type that given type will serialize to.
     */
    public static Class getSerializableClass(Class c){
        if(ALG_ID.containsKey(c)){
            return c;
        }
        return ALIASES.get(c);
    }

    /**
     * Returns the id of the serialization method for the given type.
     * @param c The type to find the serialization method for.
     * @return The given type's serialization method.
     */
    public static int getAlgID(Class c){
        Class serializable = getSerializableClass(c);
        if(serializable == null){
            return -1;
        }
        Integer index = ALG_ID.get(serializable);
        if(index == null){
            return -1;
        }
        return index;
    }

    /**
     * Returns the deserializer method by id.
     * @param alg The id of the method.
     * @return The deserializer method.
     */
    public static Reader<Serializer, ?> getReader(int alg){
        return READERS.get(alg);
    }

    /**
     * Returns the serializer method by id.
     * @param alg The id of the method.
     * @return The serializer method.
     */
    public static Writer<Serializer, ?> getWriter(int alg){
        return WRITERS.get(alg);
    }

    /**
     * Returns the deserializer method associated with the type given. The method will specifically give the
     * deserializer for the type given by 'getSerializableClass' {@link SerializationUtil#getSerializableClass(Class) }.
     * @param c The class to find the deserializer for.
     * @param <T> The type to deserialize.
     * @param <S> The type that will be output by deserialization.
     * @return The method for deserializing the given type.
     */
    public static <T> Reader<Serializer, T> getReader(Class<T> c){
        if(isAliased(c)){
            throw new IllegalArgumentException("Cannot read in type registered with an aliased reader");
        }
        int index = getAlgID(c);
        return index == -1 ? null : (Reader<Serializer, T>) READERS.get(index);
    }

    /**
     * Returns the serializer method associated with the type given. The method will specifically give the
     * serializer for the type given by 'getSerializableClass' {@link SerializationUtil#getSerializableClass(Class) }.
     * @param c The class to find the serializer for.
     * @param <T> The type to serialize.
     * @param <S> The type that will be output by serialization.
     * @return The method for serializing the given type.
     */
    public static <T extends S, S> Writer<Serializer, S> getWriter(Class<T> c){
        int index = getAlgID(c);
        return index == -1 ? null : (Writer<Serializer, S>) WRITERS.get(index);
    }

    private static int numAlgs(){
        return READERS.size();
    }

    /**
     * Registers a method for serialization with no associated type.
     * @param reader The deserializer.
     * @param writer The serializer.
     * @param <T> The type to register serialization and deserialization methods for.
     * @return The method id.
     */
    public static <T> int registerAlg(Reader<Serializer, T> reader, Writer<Serializer, T> writer){
        READERS.add((Reader) reader);
        WRITERS.add((Writer) writer);
        return numAlgs() - 1;
    }

    /**
     * Registers a method for serialization with an associated type.
     * @param c The class to register serialization and deserialization methods for.
     * @param reader The deserializer.
     * @param writer The serializer.
     * @param <T> The type to register serialization and deserialization methods for.
     * @return The method id.
     */
    public static <T> int registerType(Class<T> c, Reader<Serializer, T> reader, Writer<Serializer, T> writer) {
        if(isSerializable(c)){
            return getAlgID(c);
        }
        int algID = registerAlg(reader, writer);
        ALG_ID.put(c, algID);
        return algID;
    }

    /**
     * Registers a method for serialization with an associated type.
     * @param c The class to register serialization and deserialization methods for.
     * @param reader The deserializer, usually the constructor given as a lambda.
     * @param <T> The type to register serialization and deserialization methods for.
     * @return The method id.
     */
    public static <T extends SerializeWritable> int registerType(Class<T> c, Reader<Serializer, T> reader){
        return registerType(c, reader, (ser, o) -> o.write(ser));
    }

    private static <T> int registerBasicType(Class<T> c, Reader<DataInputStream, T> reader, Writer<DataOutputStream, T> writer) {
        return registerType(c, ser -> reader.read(ser.getInputStream()), (ser, t) -> writer.write(ser.getOutputStream(), t));
    }

    /**
     * Checks whether the given object uses the method associated with the given type to serialize.
     * @param o The object to check. Null returns false.
     * @param c The type to check.
     * @return Whether the method associated with the given type is that used by the given object.
     */
    public static boolean usesSameSerializerTypeNoAliasCheck(Object o, Class c){
        if(o == null){
            return false;
        }
        return c.equals(getSerializableClass(o.getClass()));
    }

    /**
     * Checks whether the given object uses the method associated with the given type's serializable class type to
     * serialize. Specifically, this method is the same as
     * 'usesSameSerializerTypeNoAliasCheck' {@link SerializationUtil#usesSameSerializerTypeNoAliasCheck(Object, Class)}
     * with the key difference that it checked against the class given by running the given type through
     * 'getSerializableClass' {@link SerializationUtil#getSerializableClass(Class)}.
     * @param o The object to check. Null returns false.
     * @param c The type to check.
     * @return Whether the method associated with the given type's serializable class type is that used by the given object.
     */
    public static boolean usesSameSerializerType(Object o, Class c){
        return usesSameSerializerTypeNoAliasCheck(o, getSerializableClass(c));
    }

    /**
     * Checks whether the given objects use the method associated with the given type's serializable class type to
     * serialize. Specifically, this method is a vectorization of the
     * 'usesSameSerializerType' {@link SerializationUtil#usesSameSerializerType(Object, Class)} method.
     * @param oa The objects to check. Not null.
     * @param c The type to check.
     * @return Whether the method associated with the given type's serializable class type is that used by the given object.
     */
    public static boolean isUniformSerializerType(Object[] oa, Class c){
        c = getSerializableClass(c);
        for (Object o : oa){
            if(!usesSameSerializerTypeNoAliasCheck(o, c)){
                return false;
            }
        }
        return true;
    }

    /**
     * Registers a simple array algorithm which only serializes objects with are of the given type or have the given
     * type as an alias. Each class will be deserialized to the given type directly.
     * @param c The class to register an array type for.
     * @param <T> The type to register an array type for.
     * @return The method id.
     */
    public static <T> int registerDefaultArrayType(Class<T> c){
        if(isAliased(c)){
            throw new IllegalArgumentException("Cannot register an array for an aliased type");
        }
        if(!isSerializable(c)){
            throw new IllegalArgumentException("Cannot register an array for an unregistered type");
        }
        return registerType((Class<T[]>) Array.newInstance(c, 0).getClass(), ser -> {
                    int len = ser.read(Integer.class);
                    T[] array = (T[]) Array.newInstance(c, len);
                    for(int i = 0; i < len; i++){
                        array[i] = ser.read(c);
                    }
                    return array;
                },
                (ser, a) -> {
                    if(!isUniformSerializerType(a, c)){
                        throw new IOException("Array not of uniform writable type");
                    }
                    ser.write(a.length);
                    for (T o : a){
                        ser.write(o);
                    }
                });
    }

    /**
     * Registers a dynamic array algorithm which serializes objects that have the given type as a superclass. Each class
     * will be deserialized to the type given by inputting its type into
     * 'getSerializableClass' {@link SerializationUtil#getSerializableClass(Class)}.
     * @param c The class to register an array type for.
     * @param <T> The type to register an array type for.
     * @return The method id.
     */
    public static <T> int registerDynamicArrayAlg(Class<T> c){

        Reader<Serializer, T[]> read = ser -> {
            int len = ser.read(Integer.class);
            T[] array = (T[]) Array.newInstance(c, len);
            String[] classes = ser.read(String[].class);
            Class<? extends T>[] initedClasses = (Class<? extends T>[]) Array.newInstance(Class.class, classes.length);
            try {
                for (int i = 0; i < classes.length; i++) {
                    Class<? extends T> subC = Class.forName(classes[i]).asSubclass(c);
                    if (!isSerializable(subC)) {
                        throw new IOException("Class " + subC + " not registered");
                    }
                    initedClasses[i] = subC;
                }
                for (int i = 0; i < len; i++){
                    int id = ser.read(Integer.class);
                    if(id == -1){
                        array[i] = null;
                        continue;
                    }
                    if(id < 0 || id >= initedClasses.length){
                        throw new IOException("Unrecognized array class id read");
                    }
                    array[i] = ser.read(initedClasses[id]);
                }
                return array;
            } catch (ClassNotFoundException | ClassCastException e) {
                throw new IOException(e);
            }
        };

        Writer<Serializer, T[]> write = (ser, a) -> {
            Set<Class<? extends T>> used = new HashSet();
            try {
                for (T o : a) {
                    if(o == null){
                        continue;
                    }
                    Class oClass = getSerializableClass(o.getClass());
                    if (oClass != null) {
                        used.add(oClass.asSubclass(c));
                    }else{
                        throw new IOException("Object type not registered");
                    }
                }

                Class<? extends T>[] usedArray = used.toArray((Class<? extends T>[]) Array.newInstance(Class.class, 0));
                String[] serClasses = new String[usedArray.length];
                Map<Class<? extends T>, Integer> typeMap = new HashMap();
                for(int i = 0; i < usedArray.length; i++){
                    serClasses[i] = usedArray[i].getName();
                    typeMap.put(usedArray[i], i);
                }

                ser.write(a.length, serClasses);
                for (T o : a){
                    if(o == null){
                        ser.write(-1);
                    } else {
                        ser.write(typeMap.get(getSerializableClass(o.getClass())), o);
                    }
                }
            } catch (ClassCastException e){
                throw new IOException(e);
            }
        };

        return registerAlg(read, write);
    }

    /**
     * Functional interface for deserializers.
     * @param <T> Type for object to deserialize from.
     * @param <R> Type of object to deserialize.
     */
    @FunctionalInterface
    public interface Reader<T, R> {

        /**
         * Deserializes object from given deserialization object.
         * @param t Object to deserialize from.
         * @return The deserialized object.
         * @throws IOException If there is a problem deserializing.
         */
        R read(T t) throws IOException;
    }

    /**
     *  Functional interface for serializers.
     * @param <T> Type for object to serialize into.
     * @param <R> Type of object to serialize.
     */
    @FunctionalInterface
    public interface Writer<T, R> {

        /**
         * Serializes object into given serialization object.
         * @param t Object to serialize into.
         * @param r Object to serialize.
         * @throws IOException If there is a problem serializing.
         */
        void write(T t, R r) throws IOException;
    }
}
