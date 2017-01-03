package com.capgemini.cobigen.senchaplugin.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;
import com.capgemini.cobigen.senchaplugin.merger.libextension.Constants;
import com.capgemini.cobigen.senchaplugin.merger.libextension.SenchaNodeVisitor;

/**
 * The {@link SenchaMerger} merges a patch and the base Sencha file of the same file. This merge is a
 * recursive merge that goes through all nodes and children merginf them if necessary
 */

public class SenchaMerger implements Merger {

    /**
     * Establish the indentation index for the code beautifier
     *
     */
    public int indent = 4;

    /**
     * Merger Type to be registered
     */
    private String type;

    /**
     * The conflict resolving mode
     */
    private boolean patchOverrides;

    /**
     * Creates a new {@link SenchaMerger}
     *
     * @param type
     *            merger type
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     */
    public SenchaMerger(String type, boolean patchOverrides) {

        this.type = type;
        this.patchOverrides = patchOverrides;
    }

    @Override
    public String getType() {

        return type;
    }

    @Override
    public String merge(File base, String patch, String targetCharset) {

        CompilerEnvirons env = new CompilerEnvirons();

        AstRoot nodeBase = new AstRoot();
        AstRoot nodePatch = new AstRoot();
        AstRoot out = new AstRoot();

        String file = base.getAbsolutePath();
        Reader reader = null;
        String baseString;

        try {
            reader = new FileReader(file);
            baseString = IOUtils.toString(reader);
            reader.close();
        } catch (FileNotFoundException e) {
            throw new MergeException(base, "Can not read the base file " + base.getAbsolutePath());
        } catch (IOException e) {
            throw new MergeException(base, "Can not read the base file " + base.getAbsolutePath());
        }

        SenchaNodeVisitor nodesBase = new SenchaNodeVisitor();
        SenchaNodeVisitor nodesPatch = new SenchaNodeVisitor();

        // parsing the base
        try {
            nodesBase = parseAst(nodeBase, baseString, file, env);
        } catch (EvaluatorException e) {
            throw new MergeException(base, "Syntax error at [" + e.lineNumber() + ", " + e.columnNumber() + "]");
        }
        // parsing the patch
        try {
            nodesPatch = parseAst(nodePatch, patch, patch, env);
        } catch (EvaluatorException e) {
            throw new MergeException(base, "Patch syntax error at [" + e.lineNumber() + ", " + e.columnNumber() + "]");
        }

        switch (type) {
        case "senchamerge":
            // Merge process
            SenchaMerge(nodesBase.getSecondArgument(), nodesPatch.getSecondArgument(), nodesBase, nodesPatch,
                patchOverrides);
            break;
        case "senchamerge_override":
            // Merge process
            SenchaMerge(nodesBase.getSecondArgument(), nodesPatch.getSecondArgument(), nodesBase, nodesPatch,
                patchOverrides);
            break;
        default:
            throw new MergeException(base, "Merge strategy not yet supported!");
        }

        // Build the resultant AST
        List<PropertyGet> prop = nodesBase.getFunctionCall();

        StringLiteral firstArgument = null;
        if (patchOverrides) {
            firstArgument = nodesPatch.getFirstArgument();
        } else {
            firstArgument = nodesBase.getFirstArgument();
        }

        FunctionCall newCall = new FunctionCall();

        ExpressionStatement newExpr = new ExpressionStatement();

        ObjectLiteral newObj = new ObjectLiteral();

        List<AstNode> arguments = new LinkedList<>();
        newCall.setTarget(prop.get(0));

        if (firstArgument != null) {
            arguments.add(0, firstArgument);
            newObj = nodesBase.getSecondArgument();
            arguments.add(1, newObj);
            newCall.setArguments(arguments);
            newExpr.setExpression(newCall);
        } else {
            newObj = nodesBase.getSecondArgument();
            arguments.add(0, newObj);
            newCall.setArguments(arguments);
            newExpr.setExpression(newCall);
        }
        out.addChild(newExpr);

        return jsBeautify(out.toSource(4), indent);

    }

    /**
     * Merges {@link ObjectLiteral}'s
     *
     * @param nodesBase
     *            the destination of the merge result
     * @param nodesPatch
     *            nodes to patch
     * @param visitorBase
     *            The relation of nodes of the base file
     * @param visitorPatch
     *            the relation of nodes of the patch file
     * @param patchOverrides
     *            merge strategy
     */
    private void SenchaMerge(ObjectLiteral nodesBase, ObjectLiteral nodesPatch, SenchaNodeVisitor visitorBase,
        SenchaNodeVisitor visitorPatch, boolean patchOverrides) {
        Map<String, AstNode> entryPatch = new HashMap<>();
        Map<String, AstNode> entryBase = new HashMap<>();

        for (ObjectProperty node : nodesPatch.getElements()) {
            entryPatch.put(node.getLeft().toSource(), node.getRight());
        }
        for (ObjectProperty node : nodesBase.getElements()) {
            entryBase.put(node.getLeft().toSource(), node.getRight());
        }

        for (Map.Entry<String, AstNode> obj : entryPatch.entrySet()) {
            String patchKey = obj.getKey();
            AstNode patchValue = obj.getValue();
            if (entryBase.containsKey(patchKey)) { // conflict

                if (entryPatch.get(patchKey) instanceof ObjectLiteral
                    && entryBase.get(patchKey) instanceof ObjectLiteral) { // If is another ObjectLiteral -->
                                                                           // Recursion

                    SenchaMerge((ObjectLiteral) entryBase.get(patchKey), (ObjectLiteral) entryPatch.get(patchKey),
                        visitorBase, visitorPatch, patchOverrides);

                } else if (entryPatch.get(patchKey) instanceof ArrayLiteral
                    && entryBase.get(patchKey) instanceof ArrayLiteral) {

                    if (patchOverrides) { // IF patchOverrides, just replace Value

                        handleConflictOverride(nodesBase, patchValue, patchKey);

                    } else { // If not patchOverride, add non existent properties

                        ArrayLiteral arrayPatch = (ArrayLiteral) entryPatch.get(patchKey);
                        ArrayLiteral arrayBase = (ArrayLiteral) entryBase.get(patchKey);

                        for (AstNode node : arrayPatch.getElements()) {
                            boolean exists = false;
                            boolean mergeGrid = false;
                            ObjectLiteral gridBase = null;
                            ObjectLiteral gridPatch = null;
                            int index = 0;
                            for (AstNode contains : arrayBase.getElements()) {
                                if (contains.toSource().equals(node.toSource())) {
                                    exists = true;
                                    break;
                                }
                                if (contains instanceof ObjectLiteral) {
                                    ObjectLiteral objLB = (ObjectLiteral) contains;
                                    for (ObjectProperty prop : objLB.getElements()) {
                                        if (prop.getLeft().toSource().equals(Constants.REFERENCE)
                                            && prop.getRight().toSource().contains(Constants.GRID)) {
                                            if (!visitorPatch.getGrids().getGridsCollection().isEmpty()) {
                                                if (visitorPatch.getGrids().getGridsCollection().contains(visitorPatch
                                                    .getGrids().getGrids().get(prop.getRight().toSource()))) {
                                                    gridPatch = (ObjectLiteral) visitorPatch.getGrids().getGrids()
                                                        .get(prop.getRight().toSource());
                                                    exists = true;
                                                    mergeGrid = true;
                                                    index = arrayBase.getElements().indexOf(objLB);
                                                    gridBase = (ObjectLiteral) arrayBase.getElements().get(index);
                                                    break;
                                                } else if (contains.toSource().equals(node.toSource())) {
                                                    exists = true;
                                                    break;
                                                }
                                            } else if (contains.toSource().equals(node.toSource())) {
                                                exists = true;
                                                break;
                                            }
                                        }
                                    }
                                } else if (contains.toSource().equals(node.toSource())) {
                                    exists = true;
                                    break;
                                }

                            }
                            if (!exists) {
                                arrayBase.getElements().add(node);
                            }
                            if (mergeGrid) {
                                SenchaMerge(gridBase, gridPatch, visitorBase, visitorPatch, patchOverrides);
                            }
                        }

                    }

                } else if (entryPatch.get(patchKey) instanceof StringLiteral
                    && entryBase.get(patchKey) instanceof StringLiteral) {
                    if (patchOverrides) {
                        handleConflictOverride(nodesBase, patchValue, patchKey);
                    }

                } else { // IF not ObjectLiteral, not StringLiteral neither ArrayLiteral, and patchOverrides =
                         // true, merge
                    if (patchOverrides) {
                        handleConflictOverride(nodesBase, patchValue, patchKey);
                    }
                }
            } else { // If no conflict, add the property
                ObjectProperty toAdd = new ObjectProperty();
                toAdd.setLeftAndRight((AstNode) Node.newString(patchKey), patchValue);
                nodesBase.getElements().add(toAdd);
            }
        }
    }

    /**
     *
     * Handle conflict of existent elements at both files
     *
     * @param nodesBase
     *            the destination of the merge result
     * @param patchValue
     *            the value to patch
     * @param patchKey
     *            the key of the value to patch
     */
    private void handleConflictOverride(ObjectLiteral nodesBase, AstNode patchValue, String patchKey) {
        for (AstNode node : nodesBase.getElements()) {
            ObjectProperty ob = (ObjectProperty) node;
            if (ob.getLeft().toSource().equals(patchKey)) {
                ob.setRight(patchValue);
            }
        }
    }

    /**
     * Uses the JSBeautifier script to format the source code
     *
     * @param source
     *            to format
     * @param indent
     *            the indentation for tabulations
     * @return the formated source code
     */
    private String jsBeautify(String source, int indent) {

        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();

        InputStream resourceAsStream = SenchaMerger.class.getResourceAsStream(Constants.BEAUTIFY_JS);

        try {
            Reader reader = new InputStreamReader(resourceAsStream);
            cx.evaluateReader(scope, reader, "__beautify.js", 1, null);
            reader.close();
        } catch (IOException e) {
            throw new MergeException(new File(""), "Error reading " + "beautify.js");
        }
        scope.put("jsCode", scope, source);
        return (String) cx.evaluateString(scope, "js_beautify(jsCode, {indent_size:" + indent + "})", "inline", 1,
            null);
    }

    /**
     * Ast parser and visitor
     *
     * @param reader
     *            FileReader of the file to parse
     * @param file
     *            the file to parse
     * @param env
     *            the enviroment config
     * @param ast
     *            the ast to store the parse result
     * @throws EvaluatorException
     *             if there are any syntax error
     * @return nodes the node visitor
     *
     */
    private SenchaNodeVisitor parseAst(AstRoot ast, String reader, String file, CompilerEnvirons env)
        throws EvaluatorException {

        ast = new Parser(env).parse(reader, file, 1);

        SenchaNodeVisitor nodes = new SenchaNodeVisitor();

        ast.visitAll(nodes);
        return nodes;
    }

}
