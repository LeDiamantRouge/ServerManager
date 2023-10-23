package fr.lediamantrouge.servermanager.servercreator;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.UUID;

public class ServerCreatorUtils {

    public static String foundId() {
        return UUID.randomUUID().toString().substring(0, 6).toLowerCase();
    }

    public static int nextFreePort() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(0);
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int port = serverSocket.getLocalPort();
        if(port == 25565) port = serverSocket.getLocalPort() + 1;
        return port;
    }
}
