package us.kosdt.arl.testing_other_libraries;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class ByteBufferTest1 {

    public static void main(String[] args) {
        ByteBuffer bb = ByteBuffer.allocate(100);
        bb.putFloat((float).33);
        bb.putInt(239);
        bb.position(0);
        System.out.println(bb.getFloat());
        System.out.println(bb.getInt());

        ByteBuffer b1 = ByteBuffer.allocate(12);
        FloatBuffer b2 = FloatBuffer.allocate(3);
        b1.putInt(0xFFFF_FFF0);
        b1.putFloat(3);
        b1.putInt(0x0000_FFFF);
        b1.flip();
        float d = b1.getFloat();
        b2.put(d);
        b2.put(b1.getFloat());
        b2.put(b1.getFloat());
        b2.flip();
        System.out.println(b2);
        System.out.println(b2.get());
        System.out.println(b2);
        System.out.println(b2.get());
        System.out.println(b2.get());


    }
}
