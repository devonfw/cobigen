package com.capgemini.cobigen.senchaplugin.merger.libextension;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ObjectLiteral;

/**
 *
 */
public class Grids {

    private List<ObjectLiteral> gridsCollection;

    private Map<String, AstNode> grids;

    public Grids() {
        gridsCollection = new LinkedList<>();
        grids = new HashMap<>();
    }

    public List<ObjectLiteral> getGridsCollection() {
        return gridsCollection;
    }

    public void setGridsCollection(List<ObjectLiteral> gridsCollection) {
        this.gridsCollection = gridsCollection;
    }

    public Map<String, AstNode> getGrids() {
        return grids;
    }

    public void setGrids(HashMap<String, AstNode> hashMap) {
        grids = hashMap;
    }

}
