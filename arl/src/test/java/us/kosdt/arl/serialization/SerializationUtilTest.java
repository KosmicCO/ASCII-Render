package us.kosdt.arl.serialization;

import us.kosdt.arl.serialization.serializers.StreamSerializer;
import us.kosdt.arl.util.math.Vec2d;
import us.kosdt.arl.util.math.Vec4d;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;

public class SerializationUtilTest {

    public static void main(String[] args) throws IOException {
        ByteArrayOutputStream b1 = new ByteArrayOutputStream();
        Serializer s1 = new StreamSerializer(new ByteArrayInputStream(new byte[0]), b1);
        s1.write("Hello World!", new Vec4d(0, 2, 3, 1), 6);
        Serializer s2 = new StreamSerializer(new ByteArrayInputStream(b1.toByteArray()), new ByteArrayOutputStream());
        System.out.println(s2.read(String.class));
        System.out.println(s2.read(Vec4d.class));
        System.out.println(s2.read(Integer.class));
        s1.close();
        s2.close();
    }
}
