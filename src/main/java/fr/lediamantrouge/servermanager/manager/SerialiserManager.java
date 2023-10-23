package fr.lediamantrouge.servermanager.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.lediamantrouge.servermanager.servermanager.Server;

public class SerialiserManager {

    public static String encode(Server server) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(server);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Server decode(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(json, Server.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
