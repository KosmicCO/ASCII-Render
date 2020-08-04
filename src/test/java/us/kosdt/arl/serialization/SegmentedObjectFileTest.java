package us.kosdt.arl.serialization;

import us.kosdt.arl.serialization.serializers.SegmentedObjectFile;

import java.io.IOException;

public class SegmentedObjectFileTest {

    public static void main(String[] args) throws IOException {
        SegmentedObjectFile sof = new SegmentedObjectFile("test_files/SegmentedObjectFileTest.ob", 32);

        sof.getWriter().write("Alpha Beta Gamma Delta");
        sof.write(0, 30);
        sof.getWriter().write("Epsilon Zeta Eta Theta");
        sof.write(1);
        System.out.println(sof.getReader(0).read(String.class));
        sof.remove(0);
        sof.save();
        sof.getWriter().write("Iota Kappa Lambda Mu");
        sof.write(3);
        System.out.println(sof.getReader(1).read(String.class));
        System.out.println(sof.getReader(3).read(String.class));

        sof.close();

        System.out.println("\n--- Reopen \n");

        sof = new SegmentedObjectFile("test_files/SegmentedObjectFileTest.ob");

        System.out.println(sof.getReader(1).read(String.class));
        System.out.println(sof.getReader(3).read(String.class));
        sof.getWriter().write("Nu Xi Omicron Pi");
        sof.write(4);
    }
}
