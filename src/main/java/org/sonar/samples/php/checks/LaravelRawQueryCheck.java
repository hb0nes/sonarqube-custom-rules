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

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;
import org.sonar.plugins.php.api.visitors.PHPVisitorCheck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Rule(key = LaravelRawQueryCheck.KEY)
public class LaravelRawQueryCheck extends PHPVisitorCheck {

    public static final String KEY = "LaravelRawQueryCheck";

    @Override
    public void visitFunctionCall(FunctionCallTree tree) {
        String functionName = tree.callee().toString();
        if ("DB::raw".equals(functionName)) {
            tree.arguments().elementsAndSeparators().forEachRemaining(subTree -> {
                if (Tree.Kind.EXPANDABLE_STRING_LITERAL.equals(subTree.getKind())) {
                    String query = subTree.toString();
                    Pattern pattern = Pattern.compile(".*(\\$\\b\\w+\\b).*");
                    Matcher matcher = pattern.matcher(query);
                    if (matcher.find()) {
                        String variable = matcher.group(1);
                        String err = "Usage of variable " + variable + " when using " + functionName + " is potentially unsafe and could lead to SQL Injection.";
                        context().newIssue(this, subTree, err);
                    }
                }
            });
        }
        super.visitFunctionCall(tree);
    }
}
