package fr.lediamantrouge.servermanager.templatemanager;

import fr.lediamantrouge.servermanager.CommonMain;
import fr.lediamantrouge.servermanager.servermanager.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BukkitTemplateManager implements ITemplateManager {

    private final Path templatesConfigDir = Paths.get(CommonMain.getInstance().getTemplatesFile() + "/" + "CONFIG");

    private List<Template> templates;

    public BukkitTemplateManager() {
        templates = new ArrayList<>();
        reloadTemplates();
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

    @Override
    public int getTotalInTemplate(Template template) {
        int total = 0;
        for (Server server : CommonMain.getInstance().getServerManager().getServers()) {
            if (server.getTemplate().equals(template.getName())) total++;
        }
        return total;
    }

    @Override
    public void updateTemplates() {
        return;
    }

    @Override
    public void reloadTemplates() {
        File[] files = templatesConfigDir.toFile().listFiles();
        if(files == null) return;
        List<Template> getTemplates = new ArrayList<>();
        for(File file : files) {
            try {
                YamlConfiguration yamlConfig = new YamlConfiguration();
                yamlConfig.load(file);

                String name = yamlConfig.getString("name");
                int ram = yamlConfig.getInt("ram");
                int minServerCount = yamlConfig.getInt("minServerCount");
                String format = yamlConfig.getString("format");

                getTemplates.add(new Template(name, ram, minServerCount, format));
            } catch (InvalidConfigurationException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        templates.clear();
        templates.addAll(getTemplates);
    }
}
