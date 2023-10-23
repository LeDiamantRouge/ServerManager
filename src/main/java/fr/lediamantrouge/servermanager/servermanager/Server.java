package fr.lediamantrouge.servermanager.servermanager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Server {

    private String name;
    private String displayName;
    private int port;
    private String template;
    private int ram;
    private boolean open;
}
