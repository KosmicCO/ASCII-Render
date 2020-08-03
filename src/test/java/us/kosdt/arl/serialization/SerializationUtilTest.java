package us.kosdt.arl.serialization;

import us.kosdt.arl.serialization.serializers.StreamDeserializer;
import us.kosdt.arl.serialization.serializers.StreamSerializer;
import us.kosdt.arl.util.math.Vec4d;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class SerializationUtilTest {

    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        Serializer s1 = new StreamSerializer(b1);
        s1.write("Hello World!", new Vec4d(0, 2, 3, 1), 6);
        Deserializer s2 = new StreamDeserializer(new ByteArrayInputStream(b1.toByteArray()));
        System.out.println(s2.read(String.class));
        System.out.println(s2.read(Vec4d.class));
        System.out.println(s2.read(Integer.class));
        s1.close();
        s2.close();

        System.out.println("\n---\n");

        SerializationUtil.registerType(A.class, A::new);
        SerializationUtil.registerType(B.class, B::new);
        SerializationUtil.registerAlias(C.class, A.class);
        SerializationUtil.registerDefaultArrayType(A.class);
        int algAArrayID = SerializationUtil.registerDynamicArrayAlg(A.class);

        A[] a1 = {new A(0), new C(1, -1), new B(2, -2), null, new A(3), new B(4, -4), null};
        A[] a2 = {new A(0), new C(1, -1), new C(2, -2), new A(3), new C(4, -4)};

        try {
            b1 = new ByteArrayOutputStream();
            s1 = new StreamSerializer(b1);
            s1.write((Object) a1); // should fail
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        b1 = new ByteArrayOutputStream();
        s1 = new StreamSerializer(b1);
        s1.write((Object) a2);
        s2 = new StreamDeserializer(new ByteArrayInputStream(b1.toByteArray()));
        System.out.println(Arrays.toString(s2.read(A[].class)));

        b1 = new ByteArrayOutputStream();
        s1 = new StreamSerializer(b1);
        s1.writeAlg(algAArrayID, a1);
        s2 = new StreamDeserializer(new ByteArrayInputStream(b1.toByteArray()));
        System.out.println(Arrays.toString((Object[]) s2.readAlg(algAArrayID)));
    }

    private static class A implements SerializeWritable {
        public final int a;

        public A(int a){
            this.a = a;
        }

        public A(Deserializer ser) throws IOException {
            this.a = ser.read(Integer.class);
        }

        @Override
        public void write(Serializer ser) throws IOException {
            ser.write(a);
        }

        @Override
        public String toString() {
            return "{A | a: " + a + "}";
        }
    }

    private static class B extends A {
        public final int b;

        public B(int a, int b){
            super(a);
            this.b = b;
        }

        public B(Deserializer ser) throws IOException {
            super(ser);
            this.b = ser.read(Integer.class);
        }

        @Override
        public void write(Serializer ser) throws IOException {
            super.write(ser);
            ser.write(b);
        }

        @Override
        public String toString() {
            return "{B | a: " + a + ", b: " + b + "}";
        }
    }

    private static class C extends A {
        public final int c;

        public C(int a, int c){
            super(a);
            this.c = c;
        }

        @Override
        public String toString() {
            return "{C | a: " + a + ", c: " + c + "}";
        }
    }
}
