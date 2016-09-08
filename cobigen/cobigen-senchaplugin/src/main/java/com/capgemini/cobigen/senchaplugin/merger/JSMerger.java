package com.capgemini.cobigen.senchaplugin.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;

import com.capgemini.cobigen.api.exception.MergeException;
import com.capgemini.cobigen.api.extension.Merger;
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
     * @author mbrunnli (19.03.2013)
     */
    public JSMerger(String type, boolean patchOverrides) {

        this.type = type;
        this.patchOverrides = patchOverrides;
    }

    /**
     * @author rudiazma (26 de jul. de 2016)
     */
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

        String result = null;
        AstRoot nodeBase = new AstRoot();
        AstRoot nodePatch = new AstRoot();
        AstRoot resultado = new AstRoot();

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
            System.out.println(propertyBase.getLeft().toSource());
            propsNames.add(propertyBase.getLeft().toSource());
        }
        System.out.println();
        // add all the patch properties that does not have any conflicts with the property already stored
        for (ObjectProperty propertyPatch : nodesPatch.getPropertyNodes()) {
            if (!propsNames.contains(propertyPatch.getLeft().toSource())) {
                System.out.println(propertyPatch.getLeft().toSource());
                listProps.add(propertyPatch);
            }
        }

        // Resolve the conflicted properties
        ArrayLiteral resultArray = new ArrayLiteral();
        for (ObjectProperty propertyBase : nodesBase.getPropertyNodes()) {
            for (ObjectProperty propertyPatch : nodesPatch.getPropertyNodes()) {
                // Do something only if there is a conflicted property
                if (propertyBase.getLeft().toSource().equals(propertyPatch.getLeft().toSource())) {
                    Object propertyRight = propertyBase.getRight();
                    // If the right part of the property is an array, add to the base the non existent
                    // elements
                    // of the patch file
                    if (propertyRight instanceof ArrayLiteral) {
                        List<String> arrayObjects = new LinkedList<>();
                        ArrayLiteral rightBase = (ArrayLiteral) propertyRight;
                        ArrayLiteral rightPatch = (ArrayLiteral) propertyPatch.getRight();
                        for (AstNode objArrayBase : rightBase.getElements()) {
                            /*
                             * int position = searchForNameField(obj); if (position <
                             * obj.getElements().size()) {
                             * arrayNames.add(obj.getElements().get(position).getRight().toSource()); }
                             * System.out.println(objArrayBase.toSource());
                             */
                            if (objArrayBase instanceof ObjectLiteral) {
                                ObjectLiteral objL = (ObjectLiteral) objArrayBase;
                                if (!objL.getElements().get(0).getRight().toSource().equals("grid")) {
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
                            }
                            /*
                             * int position = searchForNameField(obj); if (position <
                             * obj.getElements().size()) { if (!arrayNames
                             * .contains(obj.getElements().get(position).getRight().toSource())) {
                             * resultArray.addElement(objArrayPatch); } } else {
                             * resultArray.addElement(objArrayPatch); }
                             */
                        }
                        if (patchOverrides) {
                            for (AstNode objArrayBase : rightBase.getElements()) {
                                for (AstNode objArrayPatch : rightPatch.getElements()) {
                                    ObjectLiteral objBase = (ObjectLiteral) objArrayBase;
                                    ObjectLiteral objPatch = (ObjectLiteral) objArrayPatch;
                                    if (objBase.getElements().get(searchForNameField(objBase)).getRight()
                                        .toSource().equals(objPatch.getElements()
                                            .get(searchForNameField(objPatch)).getRight().toSource())) {
                                        resultArray.getElements().remove(objArrayBase);
                                        resultArray.addElement(objArrayPatch);
                                        break;
                                    }
                                }
                                ObjectProperty toAdd = new ObjectProperty();
                                toAdd.setLeft(propertyBase.getLeft());
                                toAdd.setRight(resultArray);
                                listProps.remove(propertyBase);
                                listProps.add(toAdd);
                                break;
                            }
                        } else {
                            ObjectProperty toAdd = new ObjectProperty();
                            toAdd.setLeft(propertyBase.getLeft());
                            toAdd.setRight(resultArray);
                            listProps.remove(propertyBase);
                            listProps.add(toAdd);
                        }
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
        arguments.add(0, firstArgument);
        newObj.setElements(listProps);
        arguments.add(1, newObj);
        newCall.setArguments(arguments);
        newExpr.setExpression(newCall);
        resultado.addChild(newExpr);

        // result = astToStringWithFormat(resultado, " ");

        return resultado.toSource();

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

    /**
     * Used to search the 'name' field position inside the {@link ObjectLiteral}
     * @param obj
     *            the {@link ObjectLiteral} to scan for
     * @return the position of the 'name' field
     * @author rudiazma (11 de ago. de 2016)
     */
    public int searchForNameField(ObjectLiteral obj) {
        int position = 0;
        for (ObjectProperty field : obj.getElements()) {
            String name = field.getLeft().toSource();
            if (name.equals("name")) {
                break;
            } else {
                position++;
            }
        }
        return position;
    }

    /**
     * Convert an AstRoot into a String with format
     * @param ast
     *            the AstRoot to parse to String
     * @param indent
     *            the indent spaces
     * @return result the source of the ast as String
     * @author rudiazma (28 de jul. de 2016)
     *
     */
    public String astToStringWithFormat(AstRoot ast, String indent) {
        String result = "";
        if (indent == null) {
            indent = "";
        }
        JSNodeVisitor resultAst = new JSNodeVisitor();

        ast.visitAll(resultAst);
        ExpressionStatement first = (ExpressionStatement) ast.getFirstChild();
        FunctionCall call = (FunctionCall) first.getExpression();
        result = result + call.getTarget().toSource() + '(' + call.getArguments().get(0).toSource() + ", {\n";

        int propertyPos = 0;
        for (ObjectProperty property : resultAst.getPropertyNodes()) {
            if (property.getRight() instanceof StringLiteral) {
                result = result + indent + property.getLeft().toSource() + ": ";
                if (propertyPos < resultAst.getPropertyNodes().size() - 1) {
                    result = result + property.getRight().toSource() + ',' + '\n';
                } else {
                    result = result + property.getRight().toSource() + '\n';
                }

            } else if (property.getRight() instanceof ArrayLiteral) {
                ArrayLiteral rightSide = (ArrayLiteral) property.getRight();
                result = result + indent + property.getLeft().toSource() + " : [\n";
                int position = 0;
                for (Iterator<AstNode> iterator = rightSide.getElements().iterator(); iterator.hasNext();) {
                    if (position < rightSide.getElements().size() - 1) {
                        result = result + indent + indent + rightSide.getElement(position).toSource() + ",\n";
                        position++;
                    } else {
                        result = result + indent + indent + rightSide.getElement(position).toSource() + '\n';
                        if (propertyPos < resultAst.getPropertyNodes().size() - 1) {
                            result = result + indent + "],\n";
                        } else {
                            result = result + indent + "]\n";
                        }
                    }
                }

            } else if (property.getRight() instanceof ObjectLiteral) {
                result = result + indent + property.getLeft().toSource() + " : {\n";
                ObjectLiteral obj = (ObjectLiteral) property.getRight();
                result = result + indent + objectLiteralIterate(obj, indent, 1);
                if (propertyPos < resultAst.getPropertyNodes().size() - 1) {
                    result = result + indent + "},\n";
                } else {
                    result = result + indent + "}\n";
                }

            } else if (property.getRight() instanceof FunctionNode && property.depth() <= 1) {
                FunctionNode func = (FunctionNode) property.getRight();
                result = result + indent + property.getLeft().toSource() + ": function() {\n";
                result = result + indent + indent + func.getBody().getFirstChild();
                if (propertyPos < resultAst.getPropertyNodes().size() - 1) {
                    result = result + property.getRight().toSource() + ',' + '\n';
                } else {
                    result = result + property.getRight().toSource() + '\n';
                }
            } else {
                result = result + indent + property.getLeft().toSource() + ": ";
                if (propertyPos < resultAst.getPropertyNodes().size() - 1) {
                    result = result + property.getRight().toSource() + ',' + '\n';
                } else {
                    result = result + property.getRight().toSource() + '\n';
                }
            }
            propertyPos++;
        }
        result = result + "});";
        return result;
    }

    /**
     *
     * @param obj
     *            the object to iterate
     * @param indent
     *            the indent spaces
     * @param tab
     *            the tabulations needed
     * @return result
     * @author rudiazma (Aug 11, 2016)
     */
    private String objectLiteralIterate(ObjectLiteral obj, String indent, int tab) {
        String result = "";
        int position = 0;
        for (ObjectProperty objProp : obj.getElements()) {
            result = result + StringUtils.repeat(indent, tab) + objProp.getLeft().toSource() + ": ";
            tab = tab + 1;
            if (objProp.getRight() instanceof ObjectLiteral) {
                result = result + "{\n";
                ObjectLiteral right = (ObjectLiteral) objProp.getRight();
                result = result + indent + objectLiteralIterate(right, indent, tab);
                if (position <= obj.getElements().size() - 1) {
                    result = result + StringUtils.repeat(indent, tab) + "}\n";
                } else {
                    result = result + StringUtils.repeat(indent, tab) + "},\n";
                }
            } else {
                result = result + objProp.getRight().toSource();
                if (position < obj.getElements().size() - 1) {
                    result = result + ",\n";
                } else {
                    result = result + "\n";
                }
            }
            position++;
        }
        return result;
    }
}
