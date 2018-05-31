package com.capgemini.cobigen.senchaplugin.merger.libextension;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;

/**
 * The {@link NodeVisitor} that retrieve the needed elements of the JS file
 */

public class SenchaNodeVisitor implements NodeVisitor {

    /**
     * List of {@link ObjectProperty}'s of the first level
     */
    private List<ObjectProperty> propertyNodes;

    /**
     * The info about grids of the view
     */
    private Grids grids;

    /**
     * The Sencha funtion called
     */
    private List<PropertyGet> functionCall;

    /**
     * The first argument of the function
     */
    private StringLiteral firstArgument;

    /**
     * The second argument of the function
     */
    private ObjectLiteral secondArgument;

    @SuppressWarnings("javadoc")
    public SenchaNodeVisitor() {
        propertyNodes = new LinkedList<>();
        functionCall = new LinkedList<>();
        firstArgument = null;
        secondArgument = new ObjectLiteral();
        grids = new Grids();

    }

    @Override
    public boolean visit(AstNode node) {
        if (node instanceof ObjectProperty && node.depth() == 4) {
            ObjectProperty obj = (ObjectProperty) node;
            propertyNodes.add(obj);
        } else if (node.getType() == Token.GETPROP) {
            functionCall.add((PropertyGet) node);
        } else if (node instanceof StringLiteral && node.depth() == 3) {
            firstArgument = new StringLiteral();
            firstArgument = (StringLiteral) node;
        } else if (node.depth() == 3) {
            secondArgument = (ObjectLiteral) node;
        } else if (node instanceof ArrayLiteral) {
            for (AstNode contains : ((ArrayLiteral) node).getElements()) {
                if ((contains instanceof ObjectLiteral)) {
                    ObjectLiteral objL = (ObjectLiteral) contains;
                    for (ObjectProperty prop : objL.getElements()) {
                        if (prop.getLeft().toSource().equals(Constants.REFERENCE)
                            && prop.getRight().toSource().contains(Constants.GRID)) {
                            grids.getGrids().put(prop.getRight().toSource(), objL);
                            grids.getGridsCollection().add(objL);
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * @return list of FunctionNode node type with depth 1
     */
    public List<ObjectProperty> getPropertyNodes() {
        return propertyNodes;
    }

    /**
     * @return Ext.define
     */
    public List<PropertyGet> getFunctionCall() {
        return functionCall;
    }

    /**
     * @return the first argument of the function
     */
    public StringLiteral getFirstArgument() {
        return firstArgument;
    }

    /**
     * @return the {@link ObjectLiteral} of the function argument
     */
    public ObjectLiteral getSecondArgument() {
        return secondArgument;
    }

    /**
     * @return the {@link Grids} object
     */
    public Grids getGrids() {
        return grids;
    }

    /**
     * @param grids
     *            sets the {@link Grids} object
     */
    public void setGrids(Grids grids) {
        this.grids = grids;
    }
}