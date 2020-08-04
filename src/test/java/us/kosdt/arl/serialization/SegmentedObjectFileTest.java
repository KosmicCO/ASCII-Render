package us.kosdt.arl.serialization;

import us.kosdt.arl.serialization.serializers.SegmentedObjectFile;

import java.io.IOException;

public class SegmentedObjectFileTest {

    public static void main(String[] args) throws IOException {
        SegmentedObjectFile sof = new SegmentedObjectFile("test_files/SegmentedObjectFileTest.ob", 16);

        sof.write(0, "Alpha Beta Gamma Delta");
        sof.write(1, "Epsilon Zeta Eta Theta");
        System.out.println(sof.getReader(0).read(String.class));
        sof.remove(0);
        sof.save();
        sof.write(3, "Iota Kappa Lambda Mu");
        System.out.println(sof.getReader(1).read(String.class));
        System.out.println(sof.getReader(3).read(String.class));

        sof.close();

        System.out.println("\n--- Reopen \n");

        sof = new SegmentedObjectFile("test_files/SegmentedObjectFileTest.ob");

        System.out.println(sof.getReader(1).read(String.class));
        System.out.println(sof.getReader(3).read(String.class));
        sof.write(4, "Nu Xi Omicron Pi");
    }
}
