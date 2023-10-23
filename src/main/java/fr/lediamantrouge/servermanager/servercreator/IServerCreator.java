package fr.lediamantrouge.servermanager.servercreator;

import fr.lediamantrouge.servermanager.servermanager.Server;

import java.io.File;

public interface IServerCreator {

    Server createServer(String template);

    void startServer(Server server);

    void stopServer(Server server);

    void removeServer(File file);
}
