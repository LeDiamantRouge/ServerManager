package fr.lediamantrouge.servermanager.bungeepubsub;

import redis.clients.jedis.JedisPubSub;

public abstract class BaseBungeePubSub extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {

    }
}
