package us.kosdt.arl.serialization.serializers;

import us.kosdt.arl.serialization.Serializer;
import us.kosdt.arl.util.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Connection implements Serializer {

    private Socket socket;
    private StreamSerializer ser;

    private boolean closed = false;
    private final List<Runnable> onClose = new LinkedList();
    private final Map<Short, Runnable> handlerMap = new HashMap();

    public Connection(Socket socket) {
        this.socket = socket;
        try{
            ser = new StreamSerializer(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException e) {
            close();
        }
    }

    public static Connection connect(String ip, int port) {
        if (ip.equals("")) {
            ip = "localhost";
        }
        try {
            Connection c = new Connection(new Socket(ip, port));
            System.out.println("Connected to server");
            c.onClose(() -> System.out.println("Disconnected from server"));
            return c;
        } catch (IOException ex) {
            System.out.println("Could not connect to server: " + ip);
            return null;
        }
    }

    public static Thread server(Consumer<Connection> onConnect, int port) {
        return new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                Log.print("Server started on port " + port);
                while (true) {
                    Connection conn = new Connection(serverSocket.accept());
                    //Going too fast somtimes means the message handlers haven't registered
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                        Log.error(ex);
                    }
                    onConnect.accept(conn);
                }
            } catch (IOException ex) {
                Log.error(ex);
            }
        });
    }

    public void onClose(Runnable r) {
        onClose.add(r);
    }

    public boolean isClosed(){ return closed; }

    public void open() {
        new Thread(() -> {
            while (!closed) {
                try {
                    short id = ser.getInputStream().readShort();
                    processMessage(id);
                } catch (IOException ex) {
                    close();
                }
            }
        }).start();
    }

    private void processMessage(short id) {
        if (handlerMap.containsKey(id)) {
            handlerMap.get(id).run();
        } else {
            Log.error("Unknown message id: " + id + " is not one of known messages types " + handlerMap.keySet() + " of connection " + this);
            close();
        }
    }

    public void registerHandler(int id, Runnable handler) {
        handlerMap.put((short) id, handler);
    }

    public synchronized void sendMessage(int id, Object[] data) {
        sendMessage(id, () -> write(data));
    }

    private synchronized void sendMessage(int id, Runnable printer) {
        if (!closed) {
            try {
                ser.getOutputStream().writeShort(id);
                printer.run();
            } catch (IOException ex) {
                close();
            }
        }
    }

    @Override
    public <T> T read(Class<T> c) {
        if(!closed){
            try{
                return ser.read(c);
            }catch (IOException e){
                close();
            }
        }
        return null;
    }

    @Override
    public void write(Object... oa) {
        synchronized(ser){
            if(!closed){
                try{
                    ser.write(oa);
                } catch (IOException e) {
                    close();
                }
            }
        }
    }

    @Override
    public DataInputStream getInputStream() {
        return null;
    }

    @Override
    public DataOutputStream getOutputStream() {
        return null;
    }

    @Override
    public void close() {
        closed = true;
        try{
            socket.close();
        } catch (IOException e) {
        }
        handlerMap.clear();
        onClose.forEach(Runnable::run);
        onClose.clear();
    }
}
