package us.kosdt.arl.serialization;

import java.io.IOException;

/**
 * Simple interface for moving the writing portion of serialization into the class.
 */
public interface SerializeWritable {

    /**
     * Serializes this.
     * @param ser The 'Serializer' {@link Serializer}.
     * @throws IOException If there is a problem writing this.
     */
    void write(Serializer ser) throws IOException;
}
