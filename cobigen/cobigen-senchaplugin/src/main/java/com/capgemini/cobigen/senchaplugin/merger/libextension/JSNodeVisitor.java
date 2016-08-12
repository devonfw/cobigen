package com.capgemini.cobigen.senchaplugin.merger.libextension;

import java.util.LinkedList;
import java.util.List;

import sun.org.mozilla.javascript.internal.Token;
import sun.org.mozilla.javascript.internal.ast.AstNode;
import sun.org.mozilla.javascript.internal.ast.NodeVisitor;
import sun.org.mozilla.javascript.internal.ast.ObjectProperty;
import sun.org.mozilla.javascript.internal.ast.PropertyGet;
import sun.org.mozilla.javascript.internal.ast.StringLiteral;

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
    private PropertyGet functionCall;

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
        functionCall = new PropertyGet();
        firstArgument = new StringLiteral();

    }

    @Override
    public boolean visit(AstNode node) {
        if (node instanceof ObjectProperty && node.depth() == 4) {
            ObjectProperty obj = (ObjectProperty) node;
            propertyNodes.add(obj);
        } else if (node.getType() == Token.GETPROP) {
            functionCall = (PropertyGet) node;
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
    public PropertyGet getFunctionCall() {
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