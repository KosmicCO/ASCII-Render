package us.kosdt.arl.testing_other_libraries;

import us.kosdt.arl.serialization.SerializationUtil;
import us.kosdt.arl.serialization.Serializer;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class ReflectionTypesTest {

    public static void main(String[] args) {
        nothing(IntMap.class);
        System.out.println(IntMap.class.getName());
    }

    public static <T extends Map> void nothing(Class<T> c){
        Class<T[]> arrayType = (Class<T[]>) Array.newInstance(c, 0).getClass();
        Class<? extends T>[] arr = (Class<? extends T>[]) Array.newInstance(Class.class, 0);
        SerializationUtil.Reader<Serializer, ? extends T>[] readers = new SerializationUtil.Reader[5];
    }

    private static class IntMap extends HashMap<Integer, Integer>{

    }
}
