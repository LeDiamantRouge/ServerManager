package fr.lediamantrouge.servermanager.servermanager;

import java.util.List;

public interface IServerManager {

    List<Server> getServers();

    Server getServerByName(String name);

    Server getServerByDisplayName(String displayName);

    void createServer(Server server);

    void stopServer(Server server);
}
