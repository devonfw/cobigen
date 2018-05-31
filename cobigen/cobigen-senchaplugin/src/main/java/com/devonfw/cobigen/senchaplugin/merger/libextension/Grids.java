package com.capgemini.cobigen.senchaplugin.merger.libextension;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ObjectLiteral;

/**
 * Keeps all the necessary grid info of a view
 */
public class Grids {

    /**
     * List of grids in the view
     */
    private List<ObjectLiteral> gridsCollection;

    /**
     * Grid and his name mapped
     */
    private Map<String, AstNode> grids;

    /**
     * Constructor
     */
    public Grids() {
        gridsCollection = new LinkedList<>();
        grids = new HashMap<>();
    }

    /**
     * @return the list of the grids
     */
    public List<ObjectLiteral> getGridsCollection() {
        return gridsCollection;
    }

    /**
     * @param gridsCollection
     *            sets the list of the grids
     */
    public void setGridsCollection(List<ObjectLiteral> gridsCollection) {
        this.gridsCollection = gridsCollection;
    }

    /**
     * @return the map of name and grid
     */
    public Map<String, AstNode> getGrids() {
        return grids;
    }

    /**
     * @param hashMap
     *            sets the map of name and grid
     */
    public void setGrids(HashMap<String, AstNode> hashMap) {
        grids = hashMap;
    }

}
