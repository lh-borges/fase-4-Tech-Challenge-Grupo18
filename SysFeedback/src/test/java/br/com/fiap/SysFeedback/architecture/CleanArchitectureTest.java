package br.com.fiap.SysFeedback.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Testes de arquitetura usando ArchUnit.
 *
 * <p>Garante as fronteiras principais da Clean Architecture no SysFeedback:
 * domain no centro, application dependendo de domain, e infrastructure como
 * camada externa responsável por web, persistência, segurança e configuração.</p>
 */
class CleanArchitectureTest {

    private static final String BASE_PACKAGE = "br.com.fiap.SysFeedback";

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setUp() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE_PACKAGE);

        if (!importedClasses.iterator().hasNext()) {
            Path targetClasses = Path.of("target", "classes");
            if (Files.exists(targetClasses)) {
                importedClasses = new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPath(targetClasses);
            }
        }
    }

    @Nested
    @DisplayName("Regras de dependência entre camadas")
    class RegrasDependenciaCamadas {

        @Test
        @DisplayName("Arquitetura em camadas deve ser respeitada")
        void devemRespeitarArquiteturaEmCamadas() {
            ArchRule regra = layeredArchitecture()
                    .consideringOnlyDependenciesInLayers()
                    .layer("Domain").definedBy(BASE_PACKAGE + ".domain..")
                    .layer("Application").definedBy(BASE_PACKAGE + ".application..")
                    .layer("Infrastructure").definedBy(BASE_PACKAGE + ".infrastructure..")
                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
                    .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
                    .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Application não deve depender de Infrastructure")
        void applicationNaoDeveDependerDeInfrastructure() {
            ArchRule regra = noClasses()
                    .that().resideInAPackage(BASE_PACKAGE + ".application..")
                    .should().dependOnClassesThat()
                    .resideInAPackage(BASE_PACKAGE + ".infrastructure..");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Domain não deve depender de outras camadas do projeto")
        void domainNaoDeveDependerDeOutrasCamadas() {
            ArchRule regra = noClasses()
                    .that().resideInAPackage(BASE_PACKAGE + ".domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            BASE_PACKAGE + ".application..",
                            BASE_PACKAGE + ".infrastructure..");

            regra.check(importedClasses);
        }
    }

    @Nested
    @DisplayName("Regras de independência de frameworks")
    class RegrasIndependenciaFrameworks {

        @Test
        @DisplayName("Domain não deve usar Spring")
        void domainNaoDeveUsarSpring() {
            ArchRule regra = noClasses()
                    .that().resideInAPackage(BASE_PACKAGE + ".domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("org.springframework..", "org.springframework.boot..");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Domain não deve usar JPA/Hibernate")
        void domainNaoDeveUsarJpa() {
            ArchRule regra = noClasses()
                    .that().resideInAPackage(BASE_PACKAGE + ".domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "javax.persistence..", "org.hibernate..");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Domain não deve usar Jackson")
        void domainNaoDeveUsarJackson() {
            ArchRule regra = noClasses()
                    .that().resideInAPackage(BASE_PACKAGE + ".domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("com.fasterxml.jackson..");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Domain não deve usar Bean Validation")
        void domainNaoDeveUsarBeanValidation() {
            ArchRule regra = noClasses()
                    .that().resideInAPackage(BASE_PACKAGE + ".domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.validation..", "javax.validation..");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Application não deve usar JPA/Hibernate")
        void applicationNaoDeveUsarJpa() {
            ArchRule regra = noClasses()
                    .that().resideInAPackage(BASE_PACKAGE + ".application..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("jakarta.persistence..", "javax.persistence..", "org.hibernate..");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Application não deve usar APIs web do Spring")
        void applicationNaoDeveUsarSpringWebOuBoot() {
            ArchRule regra = noClasses()
                    .that().resideInAPackage(BASE_PACKAGE + ".application..")
                    .and().haveSimpleNameNotEndingWith("MapperImpl")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "org.springframework.web..",
                            "org.springframework.boot..",
                            "org.springframework.stereotype..",
                            "org.springframework.context..");

            regra.check(importedClasses);
        }
    }

    @Nested
    @DisplayName("Regras de localização de anotações")
    class RegrasLocalizacaoAnotacoes {

        @Test
        @DisplayName("@Entity JPA deve residir apenas em infrastructure.persistence")
        void entidadesJpaDevemResidirEmInfrastructurePersistence() {
            ArchRule regra = classes()
                    .that().areAnnotatedWith(jakarta.persistence.Entity.class)
                    .should().resideInAPackage(BASE_PACKAGE + ".infrastructure.persistence..")
                    .allowEmptyShould(true);

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("@Configuration deve residir apenas em infrastructure")
        void configuracoesDevemResidirEmInfrastructure() {
            ArchRule regra = classes()
                    .that().areAnnotatedWith(org.springframework.context.annotation.Configuration.class)
                    .should().resideInAPackage(BASE_PACKAGE + ".infrastructure..");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("@RestController deve residir em infrastructure.web e terminar com Controller")
        void restControllersDevemSeguirPadrao() {
            ArchRule regra = classes()
                    .that().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                    .should().resideInAPackage(BASE_PACKAGE + ".infrastructure.web..")
                    .andShould().haveSimpleNameEndingWith("Controller");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Controllers não devem depender diretamente da persistência")
        void controllersNaoDevemDependerDiretamenteDePersistencia() {
            ArchRule regra = noClasses()
                    .that().resideInAPackage(BASE_PACKAGE + ".infrastructure.web..")
                    .should().dependOnClassesThat()
                    .resideInAPackage(BASE_PACKAGE + ".infrastructure.persistence..");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("@RestControllerAdvice deve residir em infrastructure.web.handler")
        void restControllerAdviceDeveResidirEmHandler() {
            ArchRule regra = classes()
                    .that().areAnnotatedWith(org.springframework.web.bind.annotation.RestControllerAdvice.class)
                    .should().resideInAPackage(BASE_PACKAGE + ".infrastructure.web.handler..")
                    .andShould().haveSimpleNameEndingWith("Handler");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("@Repository deve residir apenas em infrastructure.persistence")
        void repositoriesDevemResidirEmInfrastructurePersistence() {
            ArchRule regra = classes()
                    .that().areAnnotatedWith(org.springframework.stereotype.Repository.class)
                    .should().resideInAPackage(BASE_PACKAGE + ".infrastructure.persistence..")
                    .allowEmptyShould(true);

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("@Mapper deve residir apenas nos pacotes de mapper")
        void mapstructMappersDevemResidirEmPacotesDeMapper() {
            ArchRule regra = classes()
                    .that().areAnnotatedWith(org.mapstruct.Mapper.class)
                    .should().resideInAnyPackage(
                            BASE_PACKAGE + ".application.mapper..",
                            BASE_PACKAGE + ".infrastructure.mapper..");

            regra.check(importedClasses);
        }
    }

    @Nested
    @DisplayName("Convenções de nomenclatura")
    class ConvencoesNomenclatura {

        @Test
        @DisplayName("Use cases devem terminar com UseCase")
        void useCasesDevemTerminarComUseCase() {
            ArchRule regra = classes()
                    .that().resideInAPackage(BASE_PACKAGE + ".application.usecase..")
                    .and().areTopLevelClasses()
                    .and().areNotInterfaces()
                    .should().haveSimpleNameEndingWith("UseCase");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Controllers devem terminar com Controller")
        void controllersDevemTerminarComController() {
            ArchRule regra = classes()
                    .that().resideInAPackage(BASE_PACKAGE + ".infrastructure.web..")
                    .and().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                    .should().haveSimpleNameEndingWith("Controller");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Exceções de domínio devem terminar com Exception")
        void excecoesDeDominioDevemTerminarComException() {
            ArchRule regra = classes()
                    .that().resideInAPackage(BASE_PACKAGE + ".domain.exception..")
                    .should().haveSimpleNameEndingWith("Exception");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Entidades JPA devem terminar com JpaEntity")
        void entidadesJpaDevemTerminarComJpaEntity() {
            ArchRule regra = classes()
                    .that().areAnnotatedWith(jakarta.persistence.Entity.class)
                    .should().haveSimpleNameEndingWith("JpaEntity")
                    .allowEmptyShould(true);

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Portas de repositório devem ser interfaces e terminar com Port")
        void portasRepositorioDevemSerInterfacesETerminarComPort() {
            ArchRule regra = classes()
                    .that().resideInAPackage(BASE_PACKAGE + ".application.repository..")
                    .should().beInterfaces()
                    .andShould().haveSimpleNameEndingWith("Port");

            regra.check(importedClasses);
        }

        @Test
        @DisplayName("Mappers devem ser interfaces e terminar com Mapper")
        void mappersDevemSerInterfacesETerminarComMapper() {
            ArchRule regra = classes()
                    .that().resideInAnyPackage(
                            BASE_PACKAGE + ".application.mapper..",
                            BASE_PACKAGE + ".infrastructure.mapper..")
                    .and().areAnnotatedWith(org.mapstruct.Mapper.class)
                    .should().beInterfaces()
                    .andShould().haveSimpleNameEndingWith("Mapper");

            regra.check(importedClasses);
        }
    }
}
