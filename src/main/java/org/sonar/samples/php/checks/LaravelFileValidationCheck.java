/*
 * SonarQube PHP Custom Rules Example
 * Copyright (C) 2016-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.samples.php.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.declaration.FunctionDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.MethodDeclarationTree;
import org.sonar.plugins.php.api.tree.declaration.ParameterTree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;
import org.sonar.plugins.php.api.tree.expression.MemberAccessTree;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

import java.util.List;

@Rule(key = LaravelFileValidationCheck.KEY)
public class LaravelFileValidationCheck extends PHPVisitorCheck {

    public static final String KEY = "LaravelFileValidationCheck";

    boolean request = false;
    boolean mimeCheck = false;
    boolean fileStorage = false;
    String requestVarName = "";

    // If the method accepts parameter of type Request, we need to save the name of the variable
    // to check if it is accessed for validation later on
    @Override
    public void visitMethodDeclaration(MethodDeclarationTree tree) {
        tree.parameters().parameters().elementsAndSeparators(ParameterTree::type).forEachRemaining(type -> {
            request = "Request".equals(type.toString());
            requestVarName = tree.parameters().parameters().elementsAndSeparators(ParameterTree::variableIdentifier).next().toString();
        });
        super.visitMethodDeclaration(tree);
    }

    // We need to be in a request method to check if this request is being validated
    // And because we are checking for file validation, we check if the word mime at least appears once in the
    // validation
    @Override
    public void visitFunctionCall(FunctionCallTree tree) {
        if (request) {
            if ((requestVarName + "->validate").equals(tree.callee().toString())) {
                if (tree.arguments().elementsAndSeparators().next().toString().contains("mime"))
                    mimeCheck = true;
            }
            if ((requestVarName + "->file").equals(tree.callee().toString())) {
                if (!mimeCheck) {
                    context().newIssue(this, tree, "This file is parsed without any previous (mimetype) validation.");
                    System.out.println("This file is parsed without previous (mime) validation." + tree.toString());
                }
            }
        }
        super.visitFunctionCall(tree);
    }
}
