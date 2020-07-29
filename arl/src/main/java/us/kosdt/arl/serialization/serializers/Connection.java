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

/**
 * A 'Serializer' {@link Serializer} which serializes across a Socket connection.
 */
public class Connection implements Serializer {

    private final Socket socket;
    private StreamSerializer ser;
    private final Object readMutex = new Object();
    private final Object writeMutex = new Object();

    private boolean closed = false;
    private final List<Runnable> onClose = new LinkedList<>();
    private final Map<Short, Runnable> handlerMap = new HashMap<>();

    /**
     * Creates a new 'Connection' {@link Connection} from a 'Socket' {@link Socket}.
     * @param socket The socket to create the connection from.
     */
    public Connection(Socket socket) {
        this.socket = socket;
        try{
            ser = new StreamSerializer(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException e) {
            close();
        }
    }

    /**
     * Creates a new 'Connection' {@link Connection} to the given address.
     * @param ip IP of the server to connect to.
     * @param port Port to use to connect to the server.
     * @return The new connection to the server. Null if an 'IOException' {@link IOException} is thrown by the
     * 'Socket' {@link Socket}.
     */
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

    /**
     * Creates a server thread.
     * @param onConnect Lambda to run when a new client connects.
     * @param port Port to start server on.
     * @return The thread which will run the server.
     */
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

    /**
     * Adds lambdas to run when the connection closes.
     * @param r lambda to run on when {@link Connection#close()} is called.
     */
    public void onClose(Runnable r) {
        onClose.add(r);
    }

    /**
     * Returns whether the connection is closed.
     * @return Whether the connection is closed.
     */
    public boolean isClosed(){ return closed; }

    /**
     * Opens the connection for receiving messages.
     */
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

    /**
     * Registers a handler to be called when receiving messages of the given id.
     * @param id Id of message to handle.
     * @param handler Handler to handle message with given id. Handler must make sure to deserialize all data associated
     *               with a message.
     */
    public void registerHandler(int id, Runnable handler) {
        handlerMap.put((short) id, handler);
    }

    /**
     * Sends a message with the given id.
     * @param id Id of the message to send.
     * @param data Data associated with the message.
     */
    public synchronized void sendMessage(int id, Object... data) {
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
        synchronized (readMutex) {
            if (!closed) {
                try {
                    return ser.read(c);
                } catch (IOException e) {
                    close();
                }
            }
            return null;
        }
    }

    @Override
    public Object readAlg(int alg) throws IOException {
        synchronized (readMutex) {
            return ser.readAlg(alg);
        }
    }

    @Override
    public void write(Object... oa) {
        synchronized(writeMutex){
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
    public <T> void writeAlg(int alg, T o) throws IOException {
        synchronized (writeMutex){
            ser.writeAlg(alg, o);
        }
    }

    @Override
    public DataInputStream getInputStream() {
        return ser.getInputStream();
    }

    @Override
    public DataOutputStream getOutputStream() {
        return ser.getOutputStream();
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
