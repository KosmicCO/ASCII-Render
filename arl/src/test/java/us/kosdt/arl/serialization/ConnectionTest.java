package us.kosdt.arl.serialization;

import us.kosdt.arl.serialization.serializers.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class ConnectionTest {

    private static final List<Connection> conns = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {

        Thread c = Connection.server(conn -> {
            conns.add(conn);
            initServerConnection(conn);
        }, 25565);

        Connection c1 = Connection.connect("", 25565); // Should Fail

        c.start();

        sleep(5);

        Connection c2 = Connection.connect("", 25565);
        initClientConnection(c2, 2);
        Connection c3 = Connection.connect("", 25565);
        initClientConnection(c3, 3);

        sleep(50);

        sendPing1(c2);
        sendPing2(c3);
    }

    public static void sendPing1(Connection c){
        int i = (int) (Math.random() * 100000);
        System.out.println("Sending: " + i);
        c.sendMessage(0, i);
    }

    public static void sendPing2(Connection c){
        Integer[] ia = new Integer[(int) (Math.random() * 10)];
        for (int i = 0; i < ia.length; i++){
            ia[i] = (int) (Math.random() * 100000);
        }
        System.out.println("Sending: " + Arrays.toString(ia));
        c.sendMessage(1, (Object) ia);
    }

    public static void initServerConnection(Connection conn){
        conn.registerHandler(0, () -> { // ping value
            Integer i = conn.read(Integer.class);
            System.out.println("Server: " + i);
            conns.forEach(c -> c.sendMessage(0, i));
        });

        conn.registerHandler(1, () -> { // ping array
            Integer[] ia = conn.read(Integer[].class);
            System.out.println("Server: " + Arrays.toString(ia));
            conns.forEach(c -> c.sendMessage(1, (Object) ia));
        });

        conn.open();
    }

    public static void initClientConnection(Connection conn, int id){
        conn.registerHandler(0, () -> { // ping value
            System.out.println("" + id + ": From Server: " + conn.read(Integer.class));
        });

        conn.registerHandler(1, () -> { // ping array
            System.out.println("" + id + ": From Server: " + Arrays.toString(conn.read(Integer[].class)));
        });

        conn.open();
    }
}
