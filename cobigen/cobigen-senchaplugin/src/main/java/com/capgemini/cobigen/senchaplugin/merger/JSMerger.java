package com.capgemini.cobigen.senchaplugin.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
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
import com.capgemini.cobigen.jsonplugin.JSONMerger;
import com.capgemini.cobigen.senchaplugin.exceptions.JSParseError;
import com.capgemini.cobigen.senchaplugin.merger.libextension.JSNodeVisitor;

/**
 * The {@link JSMerger} merges a patch and the base JS file of the same file. This merge is a structural merge
 * considering code blocks of functions, variables, function calls and expressions. There will be no merging
 * on statement level
 * @author rudiazma (26 de jul. de 2016)
 */

public class JSMerger implements Merger {

    /**
     * Path for the JS Beautifier script
     */
    public static final String BEAUTIFY_JS = "/beautify.js";

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
     * Creates a new {@link JSMerger}
     *
     * @param type
     *            merger type
     * @param patchOverrides
     *            if <code>true</code>, conflicts will be resolved by using the patch contents<br>
     *            if <code>false</code>, conflicts will be resolved by using the base contents
     * @author rudiazma (26 de jul. de 2016)
     */
    public JSMerger(String type, boolean patchOverrides) {

        this.type = type;
        this.patchOverrides = patchOverrides;
    }

    @Override
    public String getType() {

        return type;
    }

    /**
     * @author rudiazma (26 de jul. de 2016)
     */
    @Override
    public String merge(File base, String patch, String targetCharset) {

        // Configure the compiler enviroment for the parser
        CompilerEnvirons env = new CompilerEnvirons();
        env.setRecordingLocalJsDocComments(true);
        env.setAllowSharpComments(true);
        env.setRecordingComments(true);

        // String result = null;
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

        if (file.endsWith(".xds") || file.endsWith("Application")) {
            // JSONTokener tokensBase = new JSONTokener(baseString);
            // JSONObject jsonBase = new JSONObject(tokensBase);
            // JSONObject jsonNodeBase = jsonBase.getJSONObject("viewOrderMap");
            // JSONArray modelBase = jsonNodeBase.getJSONArray("model");
            // JSONArray controllerBase = jsonNodeBase.getJSONArray("controller");
            // JSONArray viewBase = jsonNodeBase.getJSONArray("view");
            // JSONArray storeBase = jsonNodeBase.getJSONArray("store");
            // jsonBase.remove("viewOrderMap");
            // JSONTokener tokensPatch = new JSONTokener(patch);
            // JSONObject jsonPatch = new JSONObject(tokensPatch);
            // JSONObject jsonNodePatch = jsonPatch.getJSONObject("viewOrderMap");
            // JSONArray modelPatch = jsonNodePatch.getJSONArray("model");
            // JSONArray controllerPatch = jsonNodePatch.getJSONArray("controller");
            // JSONArray viewPatch = jsonNodePatch.getJSONArray("view");
            // JSONArray storePatch = jsonNodePatch.getJSONArray("store");
            //
            // JSONObject newMap = new JSONObject();
            // newMap.put("model", modelBase.put(modelPatch.get(0)));
            // System.out.println(modelPatch.get(0));
            // newMap.put("controller", controllerBase.put(controllerPatch.get(0)));
            // System.out.println(controllerPatch.get(0));
            // newMap.put("view", viewBase.put(viewPatch.get(0)));
            // newMap.put("store", storeBase.put(storePatch.get(0)));
            // newMap.put("resource", jsonNodeBase.getJSONArray("resource"));
            // newMap.put("app", jsonNodeBase.getJSONArray("app"));
            // jsonBase.put("viewOrderMap", newMap);

            JSONMerger jsonMerger = new JSONMerger("jsonmerge", patchOverrides);
            return jsonMerger.merge(base, patch, targetCharset);

        } else {
            JSNodeVisitor nodesBase = new JSNodeVisitor();
            JSNodeVisitor nodesPatch = new JSNodeVisitor();

            // parsing the base
            nodesBase = parseAst(nodeBase, baseString, file, env);

            // parsing the patch string
            nodesPatch = parseAst(nodePatch, patch, patch, env);

            // Auxiliar structures to build the resultant ast at the end
            List<ObjectProperty> listProps = new LinkedList<>();

            // This list is used to store the field "name" of each property
            List<String> propsNames = new LinkedList<>();

            // add to the auxiliar list all the properties of the base
            for (ObjectProperty propertyBase : nodesBase.getPropertyNodes()) {
                listProps.add(propertyBase);
                propsNames.add(propertyBase.getLeft().toSource());
            }
            // add all the patch properties that does not have any conflicts with the property already stored
            for (ObjectProperty propertyPatch : nodesPatch.getPropertyNodes()) {
                if (!propsNames.contains(propertyPatch.getLeft().toSource())) {
                    listProps.add(propertyPatch);
                } else {
                    if (patchOverrides) {
                        int index = listProps.indexOf(propertyPatch.getLeft().toSource());
                        if (index >= 0) {
                            listProps.remove(index);
                            listProps.add(index, propertyPatch);
                        } else {
                            listProps.add(propertyPatch);
                        }

                    }
                }
            }

            // Resolve the conflicted properties

            for (ObjectProperty propertyBase : nodesBase.getPropertyNodes()) {
                for (ObjectProperty propertyPatch : nodesPatch.getPropertyNodes()) {
                    // Do something only if there is a conflicted property
                    if (propertyBase.getLeft().toSource().equals(propertyPatch.getLeft().toSource())) {
                        Object propertyRight = propertyBase.getRight();
                        // If the right part of the property is an array, add to the base the non existent
                        // elements
                        // of the patch file
                        if (propertyRight instanceof ArrayLiteral) {
                            ArrayLiteral resultArray = new ArrayLiteral();
                            List<String> arrayObjects = new LinkedList<>();
                            ArrayLiteral rightBase = (ArrayLiteral) propertyRight;
                            ArrayLiteral rightPatch = (ArrayLiteral) propertyPatch.getRight();

                            for (AstNode objArrayBase : rightBase.getElements()) {
                                if (objArrayBase instanceof ObjectLiteral) {
                                    ObjectLiteral objL = (ObjectLiteral) objArrayBase;
                                    boolean noGrid = true;

                                    for (ObjectProperty property : objL.getElements()) {
                                        if (property.getRight().toSource().equals("'grid'")
                                            && property.getLeft().toSource().equals("xtype")) {
                                            noGrid = false;
                                            break;
                                        }
                                    }
                                    if (noGrid) {
                                        arrayObjects.add(objArrayBase.toSource());
                                        resultArray.addElement(objArrayBase);
                                    }

                                } else {
                                    arrayObjects.add(objArrayBase.toSource());
                                    resultArray.addElement(objArrayBase);
                                }

                            }
                            for (AstNode objArrayPatch : rightPatch.getElements()) {
                                if (!arrayObjects.contains(objArrayPatch.toSource())) {
                                    resultArray.addElement(objArrayPatch);
                                } else {
                                    if (patchOverrides) {
                                        int index = arrayObjects.indexOf(objArrayPatch.toSource());
                                        resultArray.getElements().remove(index);
                                        resultArray.addElement(objArrayPatch);
                                    }
                                }
                            }
                            ObjectProperty toAdd = new ObjectProperty();
                            toAdd.setLeft(propertyBase.getLeft());
                            toAdd.setRight(resultArray);
                            listProps.remove(propertyBase);
                            listProps.add(toAdd);
                            resultArray = new ArrayLiteral();

                            break;

                            // At the case of non array, and if patchOverrides is true, just replace the
                            // entire
                            // property
                            // not doing nothing in case of patchOverrides is false, because the base property
                            // is
                            // already added

                        } else {
                            if (patchOverrides) {
                                int index = listProps.indexOf(propertyBase);
                                listProps.remove(propertyBase);
                                listProps.add(index, propertyPatch);
                            }
                        }
                    }
                }

            }

            // Building the resultant ast following the structure of a sencha file
            List<PropertyGet> prop = nodesBase.getFunctionCall();

            StringLiteral firstArgument = nodesBase.getFirstArgument();

            FunctionCall newCall = new FunctionCall();

            ExpressionStatement newExpr = new ExpressionStatement();

            ObjectLiteral newObj = new ObjectLiteral();

            List<AstNode> arguments = new LinkedList<>();
            newCall.setTarget(prop.get(0));

            if (firstArgument != null) {
                arguments.add(0, firstArgument);
                newObj.setElements(listProps);
                arguments.add(1, newObj);
                newCall.setArguments(arguments);
                newExpr.setExpression(newCall);

            } else {
                newObj.setElements(listProps);
                arguments.add(0, newObj);
                newCall.setArguments(arguments);
                newExpr.setExpression(newCall);
            }
            out.addChild(newExpr);

            return jsBeautify(out.toSource(), indent);
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
     * @author rudiazma (12 de sept. de 2016)
     */
    private String jsBeautify(String source, int indent) {

        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();

        InputStream resourceAsStream = JSMerger.class.getResourceAsStream(BEAUTIFY_JS);

        try {
            Reader reader = new InputStreamReader(resourceAsStream);
            cx.evaluateReader(scope, reader, "__beautify.js", 1, null);
            reader.close();
        } catch (IOException e) {
            throw new Error("Error reading " + "beautify.js");
        }
        scope.put("jsCode", scope, source);
        return (String) cx.evaluateString(scope, "js_beautify(jsCode, {indent_size:" + indent + "})",
            "inline", 1, null);
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
     * @return ast the ast parsed
     * @author rudiazma (8 de ago. de 2016)
     */
    private JSNodeVisitor parseAst(AstRoot ast, String reader, String file, CompilerEnvirons env) {

        try {
            ast = new Parser(env).parse(reader, file, 1);
        } catch (EvaluatorException e) {
            throw new JSParseError(e.getMessage() + " Line: " + e.lineNumber() + " -> " + e.lineSource());
        }

        JSNodeVisitor nodes = new JSNodeVisitor();

        ast.visitAll(nodes);
        return nodes;
    }

}
