package us.kosdt.arl.testing_other_libraries;

import java.io.*;

public class DataStreamTest {

    public static void main(String[] args) throws IOException {
        File f = new File("resources/deprecated_fontsheets/Courier-12.yml");
        DataInputStream dos = new DataInputStream(new FileInputStream(f));
        System.out.println(dos.readChar());
        dos.close();
    }
}
