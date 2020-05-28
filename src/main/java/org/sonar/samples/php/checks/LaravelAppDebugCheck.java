package org.sonar.samples.php.checks;


import com.google.common.collect.ImmutableSet;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.php.checks.utils.FunctionUsageCheck;
import org.sonar.php.ini.BasePhpIniIssue;
import org.sonar.php.ini.PhpIniCheck;
import org.sonar.php.ini.PhpIniIssue;
import org.sonar.php.ini.tree.Directive;
import org.sonar.plugins.php.api.tree.SeparatedList;
import org.sonar.plugins.php.api.tree.Tree;
import org.sonar.plugins.php.api.tree.expression.ExpressionTree;
import org.sonar.plugins.php.api.tree.expression.FunctionCallTree;
import org.sonar.plugins.php.api.tree.expression.LiteralTree;
import org.sonar.php.ini.tree.PhpIniFile;

import java.util.ArrayList;
import java.util.List;

@Rule(key = LaravelAppDebugCheck.KEY)
public class LaravelAppDebugCheck extends FunctionUsageCheck implements PhpIniCheck {

    public static final String KEY = "LaravelAppDebugCheck";
    private static final String MESSAGE = "Configure \"APP_DEBUG\" to false.";
    private static final String ACTION = "Pass \"false\" as argument.";

    @Override
    public List<PhpIniIssue> analyze(PhpIniFile phpIniFile) {
        List<PhpIniIssue> issues = new ArrayList<>();
        for (Directive directive : phpIniFile.directivesForName("APP_DEBUG")) {
            String value = directive.value().text();
            if (!"false".equals(value) && !"\"false\"".equals(value)) {
                issues.add(BasePhpIniIssue.newIssue(MESSAGE).line(directive.name().line()));
            }
        }
        return issues;
    }

    @Override
    protected ImmutableSet<String> functionNames() {
        return ImmutableSet.of("APP_DEBUG");
    }

    @Override
    protected void createIssue(FunctionCallTree functionCall) {
        SeparatedList<ExpressionTree> arguments = functionCall.arguments();
        if (!arguments.isEmpty()) {
            ExpressionTree firstArgument = arguments.get(0);
            if (firstArgument.is(Tree.Kind.NUMERIC_LITERAL)) {
                LiteralTree literal = (LiteralTree) firstArgument;
                if (!"0".equals(literal.value())) {
                    context().newIssue(this, firstArgument, ACTION);
                }
            }
        }
    }

}
