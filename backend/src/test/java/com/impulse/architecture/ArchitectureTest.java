package com.impulse.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

@AnalyzeClasses(packages = "com.impulse")
public class ArchitectureTest {

  @ArchTest
  public static void controllers_should_not_depend_on_repositories(JavaClasses classes) {
    ArchRuleDefinition.noClasses()
      .that().resideInAPackage("..controller..")
      .should().dependOnClassesThat().resideInAPackage("..repository..")
      .check(classes);
  }

  @ArchTest
  public static void services_should_only_be_accessed_by_controllers_or_other_services(JavaClasses classes) {
    ArchRuleDefinition.classes()
      .that().resideInAPackage("..service..")
      .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..")
      .check(classes);
  }
}
