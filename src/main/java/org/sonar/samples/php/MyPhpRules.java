package org.sonar.samples.php;

import java.util.List;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.php.api.visitors.PHPCustomRuleRepository;
import org.sonar.samples.php.checks.LaravelRawQueryCheck;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

public class MyPhpRules implements RulesDefinition, PHPCustomRuleRepository {

    public static final String REPOSITORY_KEY = "HerbertRepository";
    static final String RESOURCE_BASE_PATH = "org/sonar/l10n/php/rules/php";

    @Override
    public String repositoryKey() {
        return REPOSITORY_KEY;
    }

    @Override
    public List<Class> checkClasses() {
        return List.of(LaravelRawQueryCheck.class);
    }

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY_KEY, "php").setName("Herbert's Custom Repository");
        RuleMetadataLoader ruleMetadataLoader = new RuleMetadataLoader(RESOURCE_BASE_PATH);
        ruleMetadataLoader.addRulesByAnnotatedClass(repository, checkClasses());
        repository.done();
    }
}
