package fr.lediamantrouge.servermanager.templatemanager;

import fr.lediamantrouge.servermanager.BungeeMain;
import fr.lediamantrouge.servermanager.CommonMain;
import fr.lediamantrouge.servermanager.servermanager.Server;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class YamlTemplateManager implements ITemplateManager {

    private final Path templatesConfigDir = Paths.get(CommonMain.getInstance().getTemplatesFile() + "/" + "CONFIG");

    private List<Template> templates;

    public YamlTemplateManager() {
        templates = new ArrayList<>();
        reloadTemplates();
        updateTemplates();
    }

    @Override
    public List<Template> getTemplates() {
        return templates;
    }

    @Override
    public Template getTemplateByName(String name) {
        for (Template template : templates) {
            if(template.getName().equals(name)) return template;
        }
        return null;
    }

    public int getTotalInTemplate(Template template) {
        int total = 0;
        for (Server server : CommonMain.getInstance().getServerManager().getServers()) {
            if (server.getTemplate().equals(template.getName())) total++;
        }
        return total;
    }

    @Override
    public void updateTemplates() {

        if (!CommonMain.getInstance().isAutoStart()) return;

        for (Template template : templates) {

            int total = getTotalInTemplate(template);
            BungeeMain.getInstance().getProxy().getScheduler().schedule(BungeeMain.getInstance(), () -> {
                if (total < template.getMinServerCount()) {
                    int want = template.getMinServerCount() - total;
                    for (int i = 0; i < want; i++) {
                        Server server = CommonMain.getInstance().getServerCreator().createServer(template.getName());
                        System.out.println("Le serveur " + server.getDisplayName() + " (" + server.getName() + ") est en cours de démarrage !");
                        BungeeMain.getInstance().getProxy().getPlayers().forEach(proxiedPlayer -> {
                            if(proxiedPlayer.hasPermission("servermanager.alert")) {
                                proxiedPlayer.sendMessage(new TextComponent("§7Le serveur §e" + server.getDisplayName() + " §7(" + server.getName() + ") est §6en cours de démarrage §7!"));
                            }
                        });
                    }
                }
            }, 1L, TimeUnit.SECONDS);
        }
    }

    @Override
    public void reloadTemplates() {
        File[] files = templatesConfigDir.toFile().listFiles();
        if(files == null) return;
        List<Template> getTemplates = new ArrayList<>();
        for(File file : files) {
            try {
                ConfigurationProvider yamlConfig = ConfigurationProvider.getProvider(YamlConfiguration.class);
                Configuration config = yamlConfig.load(file);

                String name = config.getString("name");
                int ram = config.getInt("ram");
                int minServerCount = config.getInt("minServerCount");
                String format = config.getString("format");

                getTemplates.add(new Template(name, ram, minServerCount, format));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        templates.clear();
        templates.addAll(getTemplates);
    }
}
