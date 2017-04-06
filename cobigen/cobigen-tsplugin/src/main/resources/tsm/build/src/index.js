"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var fs_1 = require("fs");
var ts = require("typescript");
//merge(false, 'src/test.ts', 'src/test_patch.ts');
function merge(patchOverrides, fileBase, filePatch) {
    var sourceFile = ts.createSourceFile(fileBase, fs_1.readFileSync(fileBase).toString(), ts.ScriptTarget.ES2016, false);
    var sourceFilePatch = ts.createSourceFile(filePatch, fs_1.readFileSync(filePatch).toString(), ts.ScriptTarget.ES2016, true, ts.SyntaxKind[256]);
    var result = [];
    var columnsInfo = String();
    sourceFile.getChildAt(0).getChildren().forEach(function (child) {
        switch (child.kind) {
            case ts.SyntaxKind.ImportDeclaration:
                result.push(child.getFullText(sourceFile));
                break;
        }
    });
    sourceFilePatch.getChildAt(0).getChildren().forEach(function (childPatch) {
        switch (childPatch.kind) {
            case ts.SyntaxKind.ImportDeclaration:
                if (result.indexOf(childPatch.getFullText(sourceFilePatch)) < 0) {
                    result.push(childPatch.getFullText(sourceFilePatch));
                }
                break;
        }
    });
    sourceFile.getChildAt(0).getChildren().forEach(function (child) {
        if (child.kind == ts.SyntaxKind.ClassDeclaration) {
            var classDecl = child;
            var classDeclPatch_1;
            sourceFilePatch.getChildAt(0).getChildren().forEach(function (childPatch) {
                if (childPatch.kind == ts.SyntaxKind.ClassDeclaration) {
                    classDeclPatch_1 = childPatch;
                }
            });
            console.log(classDecl.name);
            if (classDecl.name.text == classDeclPatch_1.name.text) {
                if (patchOverrides) {
                    if (classDeclPatch_1.decorators) {
                        classDeclPatch_1.decorators.forEach(function (decorator) {
                            result.push(decorator.getFullText(sourceFilePatch));
                        });
                    }
                    if (classDeclPatch_1.modifiers) {
                        classDeclPatch_1.modifiers.forEach(function (modifier) {
                            result.push(modifier.getFullText(sourceFilePatch));
                        });
                    }
                    result.push(" class ", classDeclPatch_1.name.text);
                    if (classDeclPatch_1.heritageClauses) {
                        classDeclPatch_1.heritageClauses.forEach(function (heritage) {
                            result.push(heritage.getFullText(sourceFilePatch));
                        });
                    }
                    result.push(" {\n");
                }
                else {
                    if (classDecl.decorators) {
                        classDecl.decorators.forEach(function (decorator) {
                            if (decorator.getFullText(sourceFile).indexOf("NgModule") >= 0) {
                                result.push("\n@NgModule({\n");
                                ;
                                var arrayProperties_1 = [];
                                if (classDeclPatch_1.decorators) {
                                    for (var _i = 0, _a = classDeclPatch_1.decorators; _i < _a.length; _i++) {
                                        var decoratorPatch = _a[_i];
                                        if (decoratorPatch.getFullText(sourceFilePatch).indexOf("NgModule") >= 0) {
                                            if (decoratorPatch.expression.arguments) {
                                                if (decoratorPatch.expression.arguments[0].properties) {
                                                    // properties = ["", []];
                                                    decoratorPatch.expression.arguments[0].properties.forEach(function (propertyPatch) {
                                                        var elements = [];
                                                        if (propertyPatch.initializer.kind == ts.SyntaxKind.ArrayLiteralExpression) {
                                                            var array = propertyPatch.initializer;
                                                            array.elements.forEach(function (element) {
                                                                elements.push(element.getFullText(sourceFilePatch));
                                                            });
                                                            arrayProperties_1.push({ key: propertyPatch.name.text, values: elements });
                                                        }
                                                    });
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                                if (decorator.expression.arguments) {
                                    if (decorator.expression.arguments[0].properties) {
                                        decorator.expression.arguments[0].properties.forEach(function (property) {
                                            result.push(property.name.text + ": [");
                                            var elements = [];
                                            if (property.initializer.kind == ts.SyntaxKind.ArrayLiteralExpression) {
                                                var arrayBase_1 = [];
                                                property.initializer.elements.forEach(function (elem) {
                                                    arrayBase_1.push(elem.getFullText(sourceFile));
                                                    result.push(elem.getFullText(sourceFile) + ",");
                                                });
                                                arrayProperties_1.forEach(function (prop) {
                                                    if (prop.key == property.name.text) {
                                                        prop.values.forEach(function (proPatch) {
                                                            if (arrayBase_1.indexOf(proPatch) < 0) {
                                                                result.push(proPatch + ",");
                                                            }
                                                        });
                                                    }
                                                });
                                                result.push("\n],\n");
                                            }
                                        });
                                    }
                                }
                                result.push("})");
                            }
                            else {
                                result.push(decorator.getFullText(sourceFile));
                            }
                        });
                    }
                    if (classDecl.modifiers) {
                        classDecl.modifiers.forEach(function (modifier) {
                            result.push(modifier.getFullText(sourceFile));
                        });
                    }
                    result.push(" class ", classDecl.name.text);
                    if (classDecl.heritageClauses) {
                        classDecl.heritageClauses.forEach(function (heritage) {
                            result.push(heritage.getFullText(sourceFile));
                        });
                    }
                    result.push(" {\n");
                    var methodsBase_1 = [];
                    var propertiesBase = [];
                    var methodsToAdd_1 = [];
                    if (classDecl.members) {
                        classDecl.members.forEach(function (member) {
                            if (member.kind == ts.SyntaxKind.MethodDeclaration) {
                                methodsBase_1.push(member.name.text);
                            }
                        });
                        if (classDeclPatch_1.members) {
                            classDeclPatch_1.members.forEach(function (memberPatch) {
                                if (memberPatch.kind == ts.SyntaxKind.MethodDeclaration) {
                                    if (methodsBase_1.indexOf(memberPatch.name.text) < 0) {
                                        methodsToAdd_1.push(memberPatch.getFullText(sourceFilePatch));
                                    }
                                    else if (patchOverrides) {
                                        methodsToAdd_1.push(memberPatch.getFullText(sourceFilePatch));
                                    }
                                }
                            });
                        }
                    }
                    if (classDecl.members) {
                        classDecl.members.forEach(function (member) {
                            if (member.kind == ts.SyntaxKind.PropertyDeclaration) {
                                var propIdentifier = member.name.text;
                                switch (propIdentifier) {
                                    case "columns":
                                        var columnsPatch = void 0;
                                        for (var _i = 0, _a = classDeclPatch_1.members; _i < _a.length; _i++) {
                                            var memberPatch = _a[_i];
                                            if (memberPatch.kind == ts.SyntaxKind.PropertyDeclaration) {
                                                if (memberPatch.name.text == "columns") {
                                                    columnsPatch = memberPatch;
                                                    break;
                                                }
                                            }
                                        }
                                        if (columnsPatch) {
                                            if (patchOverrides) {
                                                columnsInfo = columnsPatch.initializer.getFullText(sourceFilePatch) + ";";
                                                result.push(columnsPatch.getFullText(sourceFilePatch));
                                            }
                                            else {
                                                var resultArray_1 = "";
                                                var arrayPatch = columnsPatch.initializer;
                                                var arrayBase_2 = member.initializer;
                                                if (member.type) {
                                                    result.push("\n  ", propIdentifier, ":", member.type.getFullText(sourceFile), " = [");
                                                }
                                                else {
                                                    result.push("\n  ", propIdentifier, " = [");
                                                }
                                                arrayBase_2.elements.forEach(function (element) {
                                                    resultArray_1 = resultArray_1 + "    " + element.getText(sourceFile);
                                                    if (arrayBase_2.elements.indexOf(element) != arrayBase_2.elements.length - 1) {
                                                        resultArray_1 = resultArray_1 + ",";
                                                    }
                                                });
                                                arrayPatch.elements.forEach(function (element) {
                                                    if (resultArray_1.indexOf(element.getText(sourceFilePatch)) == -1) {
                                                        resultArray_1 = resultArray_1 + ",    " + element.getText(sourceFilePatch);
                                                    }
                                                });
                                                columnsInfo = columnsInfo + "[" + resultArray_1 + "\n];\n";
                                                result.push(resultArray_1, "\n  ];\n");
                                            }
                                        }
                                        else {
                                            result.push(member.getText(sourceFile));
                                        }
                                        break;
                                    case "searchTerms":
                                        var itemTermPatch = void 0;
                                        for (var _b = 0, _c = classDeclPatch_1.members; _b < _c.length; _b++) {
                                            var memberPatch = _c[_b];
                                            if (memberPatch.kind == ts.SyntaxKind.PropertyDeclaration) {
                                                if (memberPatch.name.text == "searchTerms") {
                                                    itemTermPatch = memberPatch;
                                                    break;
                                                }
                                            }
                                        }
                                        if (patchOverrides) {
                                            result.push(itemTermPatch.getText(sourceFilePatch));
                                        }
                                        else {
                                            var resultObject_1 = "";
                                            var objectPatch = itemTermPatch.initializer;
                                            var objectBase_1 = member.initializer;
                                            if (member.type) {
                                                result.push("\n  ", propIdentifier, ":", member.type.getFullText(sourceFile), " = {");
                                            }
                                            else {
                                                result.push("\n  ", propIdentifier, " = {");
                                            }
                                            objectBase_1.properties.forEach(function (property) {
                                                resultObject_1 = resultObject_1 + property.getText(sourceFile);
                                                if (objectBase_1.properties.indexOf(property) != objectBase_1.properties.length - 1) {
                                                    resultObject_1 = resultObject_1 + ",";
                                                }
                                            });
                                            objectPatch.properties.forEach(function (propertyPatch) {
                                                if (resultObject_1.indexOf(propertyPatch.getText(sourceFilePatch)) == -1) {
                                                    resultObject_1 = resultObject_1 + "," + propertyPatch.getText(sourceFilePatch);
                                                }
                                            });
                                            result.push(resultObject_1, "\n  };\n");
                                        }
                                        break;
                                    case "item":
                                        var itemObjectPatch = void 0;
                                        for (var _d = 0, _e = classDeclPatch_1.members; _d < _e.length; _d++) {
                                            var memberPatch = _e[_d];
                                            if (memberPatch.kind == ts.SyntaxKind.PropertyDeclaration) {
                                                if (memberPatch.name.text == "item") {
                                                    itemObjectPatch = memberPatch;
                                                    break;
                                                }
                                            }
                                        }
                                        if (patchOverrides) {
                                            result.push(itemObjectPatch.getFullText(sourceFilePatch));
                                        }
                                        else {
                                            var resultObject_2 = "";
                                            var objectPatch = itemObjectPatch.initializer;
                                            var objectBase_2 = member.initializer;
                                            if (member.type) {
                                                result.push("\n  ", propIdentifier, ":", member.type.getText(sourceFile), " = {");
                                            }
                                            else {
                                                result.push("\n  ", propIdentifier, " = {");
                                            }
                                            objectBase_2.properties.forEach(function (property) {
                                                resultObject_2 = resultObject_2 + property.getText(sourceFile);
                                                if (objectBase_2.properties.indexOf(property) != objectBase_2.properties.length - 1) {
                                                    resultObject_2 = resultObject_2 + ",";
                                                }
                                            });
                                            objectPatch.properties.forEach(function (propertyPatch) {
                                                if (resultObject_2.indexOf(propertyPatch.getText(sourceFilePatch)) == -1) {
                                                    resultObject_2 = resultObject_2 + "," + propertyPatch.getText(sourceFilePatch);
                                                }
                                            });
                                            result.push(resultObject_2, "\n  };\n");
                                        }
                                        break;
                                    default:
                                        var identifier = member.name.text;
                                        var exists = true;
                                        for (var _f = 0, _g = classDeclPatch_1.members; _f < _g.length; _f++) {
                                            var propertyPatch = _g[_f];
                                            if (propertyPatch.kind == ts.SyntaxKind.PropertyDeclaration) {
                                                if (identifier == propertyPatch.name.text && patchOverrides) {
                                                    result.push(propertyPatch.getText(sourceFilePatch));
                                                    exists = true;
                                                    break;
                                                }
                                                else if (identifier == propertyPatch.name.text && !patchOverrides) {
                                                    result.push(member.getText(sourceFile));
                                                    exists = true;
                                                    break;
                                                }
                                                else {
                                                    exists = false;
                                                }
                                            }
                                        }
                                        if (!exists) {
                                            result.push(member.getText(sourceFile));
                                            exists = true;
                                        }
                                }
                            }
                            else if (member.kind == ts.SyntaxKind.Constructor) {
                                if (patchOverrides) {
                                    var hasConstructor = false;
                                    for (var _h = 0, _j = classDeclPatch_1.members; _h < _j.length; _h++) {
                                        var memberPatch = _j[_h];
                                        if (memberPatch.kind == ts.SyntaxKind.Constructor) {
                                            result.push(memberPatch.getText(sourceFilePatch));
                                            hasConstructor = true;
                                            break;
                                        }
                                    }
                                    if (!hasConstructor) {
                                        result.push(member.getText(sourceFile));
                                    }
                                }
                                else {
                                    result.push(member.getText(sourceFile));
                                }
                            }
                            else if (member.kind == ts.SyntaxKind.MethodDeclaration) {
                                //get patch methods unexistent at base fileBase
                                var identifier = member.name.text;
                                if (patchOverrides) {
                                    var hasMethod = false;
                                    for (var _k = 0, _l = classDeclPatch_1.members; _k < _l.length; _k++) {
                                        var memberPatch = _l[_k];
                                        if (memberPatch.kind == ts.SyntaxKind.MethodDeclaration) {
                                            if (memberPatch.name.text == identifier) {
                                                result.push(memberPatch.getFullText(sourceFilePatch));
                                                hasMethod = true;
                                            }
                                        }
                                    }
                                    if (!hasMethod) {
                                        result.push(member.getText(sourceFile));
                                    }
                                }
                                else {
                                    var exists = false;
                                    var _loop_1 = function (memberPatch) {
                                        if (memberPatch.kind == ts.SyntaxKind.MethodDeclaration) {
                                            var identifierPatch = memberPatch.name.text;
                                            if (identifier == memberPatch.name.text) {
                                                var methodPatch_1 = memberPatch;
                                                var methodBase = member;
                                                exists = true;
                                                var properties_1 = [];
                                                switch (identifier) {
                                                    case "getData":
                                                        result.push("\n\n");
                                                        if (methodBase.decorators) {
                                                            methodBase.decorators.forEach(function (decorator) {
                                                                result.push(decorator.getText(sourceFile));
                                                            });
                                                        }
                                                        if (methodBase.modifiers) {
                                                            methodBase.modifiers.forEach(function (modifier) {
                                                                result.push(modifier.getText(sourceFile));
                                                            });
                                                        }
                                                        result.push(identifier, "(");
                                                        if (methodBase.parameters) {
                                                            methodBase.parameters.forEach(function (parameter) {
                                                                result.push(parameter.getText(sourceFile));
                                                            });
                                                        }
                                                        result.push(")");
                                                        if (methodBase.type) {
                                                            result.push(":", methodBase.type.getFullText(sourceFile));
                                                        }
                                                        result.push("{\n");
                                                        if (methodBase.body) {
                                                            if (methodBase.body.statements) {
                                                                methodBase.body.statements.forEach(function (statement) {
                                                                    if (statement.kind == ts.SyntaxKind.VariableStatement) {
                                                                        var identifier_1 = statement.declarationList.declarations[0].name.text;
                                                                        if (identifier_1 == "pageData") {
                                                                            if (statement.declarationList.declarations[0].initializer) {
                                                                                var initializer_1 = statement.declarationList.declarations[0].initializer;
                                                                                if (initializer_1.kind == ts.SyntaxKind.ObjectLiteralExpression) {
                                                                                    if (initializer_1.properties) {
                                                                                        result.push("let ", identifier_1, " = {");
                                                                                        initializer_1.properties.forEach(function (prop) {
                                                                                            properties_1.push(prop.getText(sourceFile));
                                                                                            result.push(prop.getText(sourceFile));
                                                                                            if (!(initializer_1.properties.indexOf(prop) == initializer_1.properties.length - 1)) {
                                                                                                result.push(",");
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                    if (methodPatch_1.body.statements) {
                                                                                        methodPatch_1.body.statements.forEach(function (stmntPatch) {
                                                                                            if (stmntPatch.kind == ts.SyntaxKind.VariableStatement) {
                                                                                                var identifierPatch_1 = stmntPatch.declarationList.declarations[0].name.text;
                                                                                                if (identifierPatch_1 == "pageData") {
                                                                                                    if (statement.declarationList.declarations[0].initializer) {
                                                                                                        var initializerPatch = stmntPatch.declarationList.declarations[0].initializer;
                                                                                                        if (initializerPatch.kind == ts.SyntaxKind.ObjectLiteralExpression) {
                                                                                                            if (initializerPatch.properties) {
                                                                                                                initializerPatch.properties.forEach(function (propPatch) {
                                                                                                                    if (propPatch.getText(sourceFilePatch).indexOf("pagination") < 0) {
                                                                                                                        if (properties_1.indexOf(propPatch.getText(sourceFilePatch)) < 0) {
                                                                                                                            result.push("," + propPatch.getText(sourceFilePatch));
                                                                                                                        }
                                                                                                                    }
                                                                                                                });
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                    result.push("\n};");
                                                                                }
                                                                            }
                                                                        }
                                                                        else {
                                                                            result.push(statement.getText(sourceFile));
                                                                        }
                                                                    }
                                                                    else {
                                                                        result.push(statement.getText(sourceFile));
                                                                    }
                                                                });
                                                            }
                                                        }
                                                        result.push("\n}");
                                                        break;
                                                    case "saveData":
                                                        result.push("\n\n");
                                                        if (methodBase.decorators) {
                                                            methodBase.decorators.forEach(function (decorator) {
                                                                result.push(decorator.getText(sourceFile));
                                                            });
                                                        }
                                                        if (methodBase.modifiers) {
                                                            methodBase.modifiers.forEach(function (modifier) {
                                                                result.push(modifier.getText(sourceFile));
                                                            });
                                                        }
                                                        result.push(identifier, "(");
                                                        if (methodBase.parameters) {
                                                            methodBase.parameters.forEach(function (parameter) {
                                                                result.push(parameter.getText(sourceFile));
                                                            });
                                                        }
                                                        result.push(")");
                                                        if (methodBase.type) {
                                                            result.push(":", methodBase.type.getText(sourceFile));
                                                        }
                                                        result.push("{\n");
                                                        if (methodBase.body) {
                                                            if (methodBase.body.statements) {
                                                                methodBase.body.statements.forEach(function (statement) {
                                                                    if (statement.kind == ts.SyntaxKind.VariableStatement) {
                                                                        var identifier_2 = statement.declarationList.declarations[0].name.text;
                                                                        if (identifier_2 == "obj") {
                                                                            if (statement.declarationList.declarations[0].initializer) {
                                                                                var initializer_2 = statement.declarationList.declarations[0].initializer;
                                                                                if (initializer_2.kind == ts.SyntaxKind.ObjectLiteralExpression) {
                                                                                    if (initializer_2.properties) {
                                                                                        result.push("let ", identifier_2, " = {");
                                                                                        initializer_2.properties.forEach(function (prop) {
                                                                                            properties_1.push(prop.getText(sourceFile));
                                                                                            result.push(prop.getText(sourceFile));
                                                                                            if (!(initializer_2.properties.indexOf(prop) == initializer_2.properties.length - 1)) {
                                                                                                result.push(",");
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                    if (methodPatch_1.body.statements) {
                                                                                        methodPatch_1.body.statements.forEach(function (stmntPatch) {
                                                                                            if (stmntPatch.kind == ts.SyntaxKind.VariableStatement) {
                                                                                                var identifierPatch_2 = stmntPatch.declarationList.declarations[0].name.text;
                                                                                                if (identifierPatch_2 == "obj") {
                                                                                                    if (statement.declarationList.declarations[0].initializer) {
                                                                                                        var initializerPatch = stmntPatch.declarationList.declarations[0].initializer;
                                                                                                        if (initializerPatch.kind == ts.SyntaxKind.ObjectLiteralExpression) {
                                                                                                            if (initializerPatch.properties) {
                                                                                                                initializerPatch.properties.forEach(function (propPatch) {
                                                                                                                    if (properties_1.indexOf(propPatch.getText(sourceFilePatch)) < 0) {
                                                                                                                        result.push("," + propPatch.getText(sourceFilePatch));
                                                                                                                    }
                                                                                                                });
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                    result.push("\n}");
                                                                                }
                                                                            }
                                                                        }
                                                                        else {
                                                                            result.push(statement.getText(sourceFile));
                                                                        }
                                                                    }
                                                                    else {
                                                                        result.push(statement.getText(sourceFile));
                                                                    }
                                                                });
                                                            }
                                                        }
                                                        result.push("\n}");
                                                        break;
                                                    case "ngDoCheck":
                                                        result.push("\n\n");
                                                        if (methodBase.decorators) {
                                                            methodBase.decorators.forEach(function (decorator) {
                                                                result.push(decorator.getFullText(sourceFile));
                                                            });
                                                        }
                                                        if (methodBase.modifiers) {
                                                            methodBase.modifiers.forEach(function (modifier) {
                                                                result.push(modifier.getFullText(sourceFile));
                                                            });
                                                        }
                                                        result.push(identifier, "(");
                                                        if (methodBase.parameters) {
                                                            methodBase.parameters.forEach(function (parameter) {
                                                                result.push(parameter.getFullText(sourceFile));
                                                            });
                                                        }
                                                        result.push(")");
                                                        if (methodBase.type) {
                                                            result.push(":", methodBase.type.getFullText(sourceFile));
                                                        }
                                                        result.push("{\n");
                                                        if (methodBase.body) {
                                                            if (methodBase.body.statements) {
                                                                methodBase.body.statements.forEach(function (statement) {
                                                                    if (statement.kind == ts.SyntaxKind.IfStatement) {
                                                                        var ifStmnt = statement;
                                                                        if (ifStmnt.expression.getFullText(sourceFile).indexOf("this.language !== this.translate.currentLang") >= 0) {
                                                                            result.push("    if(", ifStmnt.expression.getFullText(sourceFile), ") {\n");
                                                                            var stmnt = statement.thenStatement;
                                                                            if (stmnt.statements) {
                                                                                stmnt.statements.forEach(function (stat) {
                                                                                    if (stat.kind == ts.SyntaxKind.ExpressionStatement) {
                                                                                        var exprStmnt = stat;
                                                                                        if (exprStmnt.expression.kind == ts.SyntaxKind.BinaryExpression) {
                                                                                            var binaryExpr = exprStmnt.expression;
                                                                                            if (binaryExpr.left.kind == ts.SyntaxKind.PropertyAccessExpression) {
                                                                                                var propExpr = binaryExpr.left;
                                                                                                if (propExpr.name.text == "columns") {
                                                                                                    result.push(binaryExpr.left.getFullText(sourceFile), binaryExpr.operatorToken.getFullText(sourceFile), " ", columnsInfo);
                                                                                                }
                                                                                                else {
                                                                                                    result.push(binaryExpr.getFullText(sourceFile), ";");
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                        else {
                                                                                            result.push(exprStmnt.getFullText(sourceFile), ";");
                                                                                        }
                                                                                    }
                                                                                    else {
                                                                                        result.push(stat.getFullText(sourceFile), ";");
                                                                                    }
                                                                                });
                                                                            }
                                                                            result.push("    }");
                                                                        }
                                                                        else {
                                                                            result.push(ifStmnt.getFullText(sourceFile));
                                                                        }
                                                                    }
                                                                    else {
                                                                        result.push(statement.getFullText(sourceFile));
                                                                    }
                                                                });
                                                            }
                                                        }
                                                        result.push("\n}");
                                                        break;
                                                    default:
                                                        result.push(member.getFullText(sourceFile));
                                                        exists = true;
                                                        break;
                                                }
                                            }
                                        }
                                    };
                                    for (var _m = 0, _o = classDeclPatch_1.members; _m < _o.length; _m++) {
                                        var memberPatch = _o[_m];
                                        _loop_1(memberPatch);
                                    }
                                    if (!exists) {
                                        result.push(member.getText(sourceFile));
                                        exists = false;
                                    }
                                }
                            }
                        });
                        result.push(methodsToAdd_1.join(""));
                    }
                }
                result.push("\n}");
            }
            else {
                result.push(classDecl.getFullText(sourceFile));
            }
        }
        else if (child.kind == ts.SyntaxKind.VariableStatement) {
            var variableBase = child;
            if (variableBase.declarationList.declarations[0].name.text == "appRoutes") {
                var variablePatch = void 0;
                var routesInitPatch = void 0;
                for (var _i = 0, _a = sourceFilePatch.getChildAt(0).getChildren(); _i < _a.length; _i++) {
                    var childPatch = _a[_i];
                    if (childPatch.kind == ts.SyntaxKind.VariableStatement) {
                        variablePatch = childPatch;
                        var identifierPatch = variablePatch.declarationList.declarations[0].name.text;
                        if (identifierPatch == "appRoutes") {
                            routesInitPatch = variablePatch.declarationList.declarations[0].initializer;
                            break;
                        }
                    }
                }
                if (routesInitPatch) {
                    if (patchOverrides) {
                        result.push(variablePatch.getFullText(sourceFilePatch));
                    }
                    else {
                        var routes_1 = variableBase.declarationList.declarations[0].initializer;
                        if (variableBase.decorators) {
                            variableBase.decorators.forEach(function (decorator) {
                                result.push(decorator.getFullText(sourceFile));
                            });
                        }
                        if (variableBase.modifiers) {
                            variableBase.modifiers.forEach(function (modifier) {
                                result.push(modifier.getFullText(sourceFile));
                            });
                        }
                        result.push("\n\nconst appRoutes: Routes = [");
                        var components_1 = [];
                        routes_1.elements.forEach(function (element) {
                            var object = element;
                            object.properties.forEach(function (property) {
                                var assigment = property;
                                components_1.push(assigment.initializer.text);
                            });
                            result.push(element.getFullText(sourceFile));
                            if (routes_1.elements.indexOf(element) != routes_1.elements.length - 1) {
                                result.push(",");
                            }
                        });
                        routesInitPatch.elements.forEach(function (elementPatch) {
                            var object = elementPatch;
                            object.properties.forEach(function (property) {
                                var assigment = property;
                                if (components_1.indexOf(assigment.initializer.text) < 0) {
                                    result.push("," + elementPatch.getFullText(sourceFilePatch));
                                }
                            });
                        });
                        result.push("\n]");
                    }
                }
            }
            else {
                result.push(variableBase.getFullText(sourceFile));
            }
        }
        else if (child.kind != ts.SyntaxKind.ImportDeclaration) {
            result.push(child.getFullText(sourceFile));
        }
    });
    console.log(result.join(""));
    return result.join("");
}
exports.merge = merge;
// function syntaxKindToName(kind: ts.SyntaxKind) {
//     return (<any>ts).SyntaxKind[kind];
// }
exports.default = merge;
//# sourceMappingURL=index.js.map