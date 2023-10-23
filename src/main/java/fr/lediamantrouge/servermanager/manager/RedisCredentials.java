package fr.lediamantrouge.servermanager.manager;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RedisCredentials {

    private String host;
    private int port;
    private String password;
    private int db;
}
