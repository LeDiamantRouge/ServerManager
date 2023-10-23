package fr.lediamantrouge.servermanager.manager;

import lombok.Getter;
import redis.clients.jedis.*;

public class RedisManager {

    private static RedisManager instance;

    @Getter
    private JedisPooled connection;
    boolean isConnected = false;

    public void connectRedis(RedisCredentials credentials) {
        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .ssl(false)
                .clientName("ServerManager")
                .connectionTimeoutMillis(5000)
                .database(credentials.getDb())
                .password(credentials.getPassword())
                .build();

        JedisPooled pool = new JedisPooled(new HostAndPort(credentials.getHost(), credentials.getPort()), config);

        connection = pool;
        isConnected = true;
    }

    public void disconnect() {
        connection.close();
        isConnected = false;
    }

    public static RedisManager getInstance() {
        if (instance == null) {
            instance = new RedisManager();
        }
        return instance;
    }
}
