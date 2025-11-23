package iuh.fit.se.client.network;

import iuh.fit.se.client.config.ServerConfig;
import iuh.fit.se.common.Request;
import iuh.fit.se.common.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSocket {
    private static ClientSocket instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isConnected;

    private ClientSocket() {
        this.isConnected = false;
    }

    public static synchronized ClientSocket getInstance() {
        if (instance == null) {
            instance = new ClientSocket();
        }
        return instance;
    }

    public boolean connect() {
        try {
            socket = new Socket(ServerConfig.getHost(), ServerConfig.getPort());
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            System.out.println("Connected to server: " + ServerConfig.getServerAddress());
            return true;
        } catch (IOException e) {
            System.err.println("Cannot connect to server: " + e.getMessage());
            isConnected = false;
            return false;
        }
    }

    public Response sendRequest(Request request) {
        if (!isConnected) {
            if (!connect()) {
                return Response.error("Không thể kết nối đến server");
            }
        }

        try {
            out.writeObject(request);
            out.flush();

            Response response = (Response) in.readObject();
            return response;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error sending request: " + e.getMessage());
            disconnect();
            return Response.error("Lỗi kết nối: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            isConnected = false;
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }

    public void reconnect() {
        disconnect();
        connect();
    }
}
