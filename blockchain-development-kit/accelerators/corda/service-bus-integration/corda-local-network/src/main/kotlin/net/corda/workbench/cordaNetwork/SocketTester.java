package net.corda.workbench.cordaNetwork;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketTester {

    public static boolean isAlive(String host, int port) {
        boolean isAlive = false;
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        Socket socket = new Socket();
        try {
            socket.connect(socketAddress, 1000);
            isAlive = true;
        } catch (IOException ignored) {
            // ignored
        }
        return isAlive;
    }
}
