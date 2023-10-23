package fr.lediamantrouge.servermanager.servermanager;

import fr.lediamantrouge.servermanager.manager.RedisManager;
import fr.lediamantrouge.servermanager.manager.SerialiserManager;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class RedisServerManager implements IServerManager {
    
    @Override
    public List<Server> getServers() {
        String cursor = "0";
        ScanParams scanParams = new ScanParams().count(999);

        List<String> keys = new ArrayList<>();
        do {
            ScanResult<String> scanResult = RedisManager.getInstance().getConnection().scan(cursor, scanParams);
            keys.addAll(scanResult.getResult());
            cursor = scanResult.getCursor();
        } while (!cursor.equals("0"));


        List<Server> servers = new ArrayList<>();
        for(String key : keys) {
            String json = RedisManager.getInstance().getConnection().get(key);
            servers.add(SerialiserManager.decode(json));
        }

        return servers;
    }

    @Override
    public Server getServerByName(String name) {
        for (Server server : getServers()) {
            if(server.getName().equals(name)) return server;
        }
        return null;
    }

    @Override
    public Server getServerByDisplayName(String displayName) {
        for (Server server : getServers()) {
            if(server.getDisplayName().equals(displayName)) return server;
        }
        return null;
    }

    @Override
    public void createServer(Server server) {
        RedisManager.getInstance().getConnection().set(server.getName(), SerialiserManager.encode(server));
    }

    @Override
    public void stopServer(Server server) {
        RedisManager.getInstance().getConnection().del(server.getName());
    }
}
