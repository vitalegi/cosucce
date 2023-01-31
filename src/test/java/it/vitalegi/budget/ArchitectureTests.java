package it.vitalegi.budget;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

//@AnalyzeClasses(packages = "it.vitalegi", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTests {
    static JavaClasses classes;
    Architectures.LayeredArchitecture architecture;

    @BeforeAll
    static void initClasses() {
        classes = new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests())
                                         .importPackages("it.vitalegi");
    }

    @BeforeEach
    void init() {
        architecture = layeredArchitecture().consideringAllDependencies();
        architecture.layer("Presentation").definedBy("..resource..");
        architecture.layer("Service").definedBy("..service..");
        architecture.layer("Repository").definedBy("..repository..");
        architecture.layer("Model").definedBy("..dto..");
        architecture.layer("Entity").definedBy("..entity..");
        architecture.layer("Mapper").definedBy("..mapper..");
    }

    @DisplayName("Entity should be called only by Repository, Service and Mapper")
    @Test
    void test_entity_shouldBeCalled() {
        architecture.whereLayer("Entity").mayOnlyBeAccessedByLayers("Repository", "Service", "Mapper");
        architecture.check(classes);
    }

    @DisplayName("Mapper should be called only by Service")
    @Test
    void test_mapper_shouldBeCalled() {
        architecture.whereLayer("Mapper").mayOnlyBeAccessedByLayers("Service");
        architecture.check(classes);
    }

    @DisplayName("Entity should be called only by Repository, Service and Mapper")
    @Test
    void test_model_shouldBeCalled() {
        architecture.whereLayer("Model").mayOnlyBeAccessedByLayers("Presentation", "Service", "Mapper");
        architecture.check(classes);
    }

    @DisplayName("Presentation should not be called")
    @Test
    void test_presentation_shouldNotBeCalled() {
        architecture.whereLayer("Presentation").mayNotBeAccessedByAnyLayer();
        architecture.check(classes);
    }

    @DisplayName("Repository should be called only by Service")
    @Test
    void test_repository_shouldBeCalled() {
        architecture.whereLayer("Repository").mayOnlyBeAccessedByLayers("Service");
        architecture.check(classes);
    }

    @DisplayName("Service should be called only by Presentation")
    @Test
    void test_service_shouldBeCalled() {
        architecture.whereLayer("Service").mayOnlyBeAccessedByLayers("Presentation");
        architecture.check(classes);
    }

    @DisplayName("javax.transaction.Transactional annotation is not used")
    @Test
    void test_transactionalAnnotation() {
        ArchRuleDefinition.noCodeUnits().that().areAnnotatedWith(javax.transaction.Transactional.class).should()
                          .beDeclaredIn("..");
    }

    @Disabled("implement")
    @DisplayName("a @Transactional code unit is called only by Presentation")
    @Test
    void test_transactional_isCalledOnlyByPresentation() {
    }
}
