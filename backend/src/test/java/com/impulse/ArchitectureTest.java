package com.impulse;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

/** Basic layering guard (extend as code grows). */
public class ArchitectureTest {

    @Test
    void domainShouldNotDependOnSpring() {
        var domain = new ClassFileImporter().importPackages("com.impulse.shared", "com.impulse.features");
        ArchRuleDefinition.noClasses().that().resideInAnyPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
                .check(domain);
    }
}
