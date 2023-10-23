package fr.lediamantrouge.servermanager.templatemanager;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Template {

    private String name;
    private int ram;
    private int minServerCount;
    private String format;
}
