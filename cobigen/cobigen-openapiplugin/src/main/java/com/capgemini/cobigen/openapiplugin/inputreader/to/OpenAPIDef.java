package com.capgemini.cobigen.openapiplugin.inputreader.to;

import java.util.LinkedList;
import java.util.List;

import io.swagger.models.ModelImpl;

/**
 *
 */
public class OpenAPIDef {

    private ModelImpl model;

    private List<PathDef> paths;

    public OpenAPIDef() {
        paths = new LinkedList<>();
    }

    public void addPath(PathDef path) {
        paths.add(path);
    }

    public List<PathDef> getPaths() {
        return paths;
    }

    public void setPaths(List<PathDef> paths) {
        this.paths = paths;
    }

    public ModelImpl getModel() {
        return model;
    }

    public void setModel(ModelImpl model) {
        this.model = model;
    }

}
