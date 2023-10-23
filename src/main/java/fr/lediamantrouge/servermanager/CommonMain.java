package fr.lediamantrouge.servermanager;

import fr.lediamantrouge.servermanager.scheduler.JavaScheduler;
import fr.lediamantrouge.servermanager.servercreator.IServerCreator;
import fr.lediamantrouge.servermanager.servercreator.ScreenServerCreator;
import fr.lediamantrouge.servermanager.servermanager.IServerManager;
import fr.lediamantrouge.servermanager.servermanager.RedisServerManager;
import fr.lediamantrouge.servermanager.templatemanager.ITemplateManager;
import fr.lediamantrouge.servermanager.templatemanager.YamlTemplateManager;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public class CommonMain {

    private final Path mainFile = Paths.get("/home/serveur");

    private final Path templatesFile = Paths.get(this.mainFile + "/TEMPLATES");

    private final Path serversFile = Paths.get(this.mainFile + "/SERVERS");

    private final Path CoreFilePath = Paths.get(this.mainFile + "/Proxy/plugins/ServerManager.jar");

    @Getter
    private static CommonMain instance;
    private IServerCreator serverCreator;
    private IServerManager serverManager;
    private ITemplateManager templateManager;
    private JavaScheduler scheduler;

    @Setter
    private boolean autoStart;

    public CommonMain() {
        instance = this;
        autoStart = true;
        serverCreator = new ScreenServerCreator();
        serverManager = new RedisServerManager();
        templateManager = new YamlTemplateManager();
        scheduler = new JavaScheduler();
    }

    public static CommonMain getInstance() {
        if (instance == null) {
            instance = new CommonMain();
        }
        return instance;
    }
}
