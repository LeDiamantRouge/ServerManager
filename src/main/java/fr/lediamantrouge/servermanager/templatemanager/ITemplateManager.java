package fr.lediamantrouge.servermanager.templatemanager;

import java.util.List;

public interface ITemplateManager {

    List<Template> getTemplates();

    Template getTemplateByName(String name);

    int getTotalInTemplate(Template template);

    void updateTemplates();

    void reloadTemplates();
}
