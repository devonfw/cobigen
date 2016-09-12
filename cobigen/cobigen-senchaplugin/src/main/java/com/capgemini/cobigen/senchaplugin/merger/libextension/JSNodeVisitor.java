package com.capgemini.cobigen.senchaplugin.merger.libextension;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;

/**
 *
 * @author rudiazma (Jul 7, 2016)
 *
 */

public class JSNodeVisitor implements NodeVisitor {

    /**
     *
     */
    private List<ObjectProperty> propertyNodes;

    /**
     *
     */
    private List<PropertyGet> functionCall;

    /**
     *
     */
    private StringLiteral firstArgument;

    /**
     *
     * @author rudiazma (26 de jul. de 2016)
     */
    public JSNodeVisitor() {
        propertyNodes = new LinkedList<>();
        functionCall = new LinkedList<>();
        firstArgument = new StringLiteral();

    }

    @Override
    public boolean visit(AstNode node) {
        if (node instanceof ObjectProperty && node.depth() == 4) {
            ObjectProperty obj = (ObjectProperty) node;
            propertyNodes.add(obj);
        } else if (node.getType() == Token.GETPROP) {
            functionCall.add((PropertyGet) node);
        } else if (node instanceof StringLiteral && node.depth() == 3) {
            firstArgument = (StringLiteral) node;
        }

        return true;
    }

    /**
     * @return list of FunctionNode node type with depth 1
     * @author rudiazma (26 de jul. de 2016)
     */
    public List<ObjectProperty> getPropertyNodes() {
        return propertyNodes;
    }

    /**
     * @return the Ext.define
     * @author rudiazma (8 de ago. de 2016)
     */
    public List<PropertyGet> getFunctionCall() {
        return functionCall;
    }

    /**
     * @return the first argument of the Ext.define
     *
     * @author rudiazma (8 de ago. de 2016)
     */
    public StringLiteral getFirstArgument() {
        return firstArgument;
    }
}