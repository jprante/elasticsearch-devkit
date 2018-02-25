package org.xbib.gradle.plugin.elasticsearch.build

import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.gradle.api.InvalidUserDataException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.XmlProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.xbib.gradle.plugin.elasticsearch.VersionCollection
import org.xbib.gradle.plugin.elasticsearch.VersionProperties
import org.xbib.gradle.plugin.randomizedtesting.RandomizedTestingTask
import org.xbib.gradle.task.elasticsearch.build.DependenciesInfoTask
import org.xbib.gradle.task.elasticsearch.qa.QualityAssuranceTasks

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Encapsulates build configuration for elasticsearch projects.
 */
class BuildPlugin implements Plugin<Project> {

    private static final Logger logger = LogManager.getLogger(BuildPlugin)

    static final JavaVersion minimumRuntimeVersion = JavaVersion.VERSION_1_8
    static final JavaVersion minimumCompilerVersion = JavaVersion.VERSION_1_9

    @Override
    void apply(Project project) {
        if (project.pluginManager.hasPlugin('org.xbib.gradle.plugin.elasticsearch.standalone-rest-test')) {
              throw new InvalidUserDataException('org.xbib.gradle.plugin.elasticsearch.standalone-test, ' +
                      'org.xbib.gradle.plugin.elasticearch.standalone-rest-test, and org.xbib.gradle.plugin.elasticsearch.build ' +
                      'are mutually exclusive')
        }
        project.pluginManager.apply('java')
        project.pluginManager.apply('org.xbib.gradle.plugin.randomizedtesting')
        configureJars(project)
        createProvidedConfiguration(project)
        globalBuildInfo(project)
        configureRepositories(project)
        project.ext.versions = VersionProperties.getAllVersions()
        configureCompile(project)
        configureJavadoc(project)
        configureSourcesJar(project)
        configurePomGeneration(project)
        configureTask(project)
        configureQualityAssurance(project)
        configureDependenciesInfo(project)
    }

    /** Performs checks on the build environment and prints information about the build environment. */
    static void globalBuildInfo(Project project) {
        if (project.rootProject.ext.has('buildChecksDone') == false) {
            String javaVendor = System.getProperty('java.vendor')
            String javaVersion = System.getProperty('java.version')
            String gradleJavaVersionDetails = "${javaVendor} ${javaVersion}" +
                " [${System.getProperty('java.vm.name')} ${System.getProperty('java.vm.version')}]"
            String compilerJavaVersionDetails = gradleJavaVersionDetails
            JavaVersion compilerJavaVersionEnum = JavaVersion.current()
            String runtimeJavaVersionDetails = gradleJavaVersionDetails
            JavaVersion runtimeJavaVersionEnum = JavaVersion.current()

            print """
Welcome to xbib's Elasticsearch dev kit gradle build plugin. Meouw.

    (\\___/)
    (='.'=)

"""
            println "Date: ${ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)}"
            println "Host: ${InetAddress.getLocalHost()}"
            println "OS: ${System.getProperty('os.name')} ${System.getProperty('os.version')} (${System.getProperty('os.arch')})"
            if (gradleJavaVersionDetails != compilerJavaVersionDetails || gradleJavaVersionDetails != runtimeJavaVersionDetails) {
                println "JDK Version (gradle): ${gradleJavaVersionDetails}"
                println "JDK Version (compile): ${compilerJavaVersionDetails}"
                println "JDK Version (runtime): ${runtimeJavaVersionDetails}"
            } else {
                println "JDK Version: ${gradleJavaVersionDetails}"
            }
            println "Gradle: ${project.gradle.gradleVersion}"
            println "Random Testing Seed: ${project.testSeed}"

            if (compilerJavaVersionEnum < minimumCompilerVersion) {
                logger.warn("Java ${minimumCompilerVersion} or above is required to build Elasticsearch")
            }
            if (runtimeJavaVersionEnum < minimumRuntimeVersion) {
                logger.warn("Java ${minimumRuntimeVersion} or above is required to run Elasticsearch")
            }

            project.rootProject.ext.compilerJavaVersion = compilerJavaVersionEnum
            project.rootProject.ext.runtimeJavaVersion = runtimeJavaVersionEnum
            project.rootProject.ext.buildChecksDone = true

            project.rootProject.ext.elasticsearchVersionCollection = new VersionCollection(project)
        }

        project.targetCompatibility = minimumRuntimeVersion
        project.sourceCompatibility = minimumRuntimeVersion

        project.ext.compilerJavaVersion = project.rootProject.ext.compilerJavaVersion
        project.ext.runtimeJavaVersion = project.rootProject.ext.runtimeJavaVersion
    }

    /**
     * Adds repositories used by ES dependencies.
     */
    static void configureRepositories(Project project) {
        RepositoryHandler repos = project.repositories
        if (System.getProperty("repos.mavenlocal") != null) {
            // with -Drepos.mavenlocal=true we can force checking the local .m2 repo which is
            // useful for development ie. bwc tests where we install stuff in the local repository
            // such that we don't have to pass hardcoded files to gradle
            repos.mavenLocal()
        }
        repos.mavenCentral()
        String luceneVersion = VersionProperties.getVersion('lucene')
        if (luceneVersion != null && luceneVersion.contains('-snapshot')) {
            // extract the revision number from the version with a regex matcher
            String revision = (luceneVersion =~ /\w+-snapshot-([a-z0-9]+)/)[0][1]
            repos.maven {
                name 'lucene-snapshots'
                url "http://s3.amazonaws.com/download.elasticsearch.org/lucenesnapshots/${revision}"
            }
        }
    }

    /**
     * Configuration generation of maven poms.
     */
    static void configurePomGeneration(Project project) {
        project.plugins.withType(MavenPublishPlugin.class).whenPluginAdded {
            project.publishing {
                publications {
                    all { MavenPublication publication ->
                        // add exclusions to the pom directly, for each of the transitive deps of this project's deps
                        publication.pom.withXml(fixupDependencies(project))
                    }
                }
            }

            project.tasks.withType(GenerateMavenPom.class) { GenerateMavenPom t ->
                // place the pom next to the jar it is for
                t.destination = new File(project.buildDir, "distributions/${project.archivesBaseName}-${project.version}.pom")
                // build poms with assemble (if the assemble task exists)
                Task assemble = project.tasks.findByName('assemble')
                if (assemble) {
                    assemble.dependsOn(t)
                }
            }
        }
    }

    /**
     * Adds compiler settings to the project.
     */
    static void configureCompile(Project project) {
        project.afterEvaluate {
            project.tasks.withType(JavaCompile) {
                final JavaVersion targetCompatibilityVersion = JavaVersion.toVersion(it.targetCompatibility)
                options.fork = true
                options.forkOptions.memoryMaximumSize = "1g"
                options.compilerArgs << '-Xlint:all,-deprecation,-serial'

                // either disable annotation processor completely (default) or allow to enable them if an annotation processor is explicitly defined
                if (options.compilerArgs.contains("-processor") == false) {
                    options.compilerArgs << '-proc:none'
                }

                options.encoding = 'UTF-8'
                options.incremental = true

                if (JavaVersion.current().java9Compatible) {
                    // TODO: use native Gradle support for --release when available (cf. https://github.com/gradle/gradle/issues/2510)
                    options.compilerArgs << '--release' << targetCompatibilityVersion.majorVersion
                }
            }
            // also apply release flag to groovy, which is used in build-tools
            project.tasks.withType(GroovyCompile) {
                final JavaVersion targetCompatibilityVersion = JavaVersion.toVersion(it.targetCompatibility)
                options.fork = true
                if (JavaVersion.current().java9Compatible) {
                    options.compilerArgs << '--release' << targetCompatibilityVersion.majorVersion
                }
            }
        }
    }

    static void configureJavadoc(Project project) {
        configureJavadocJar(project)
        if (project.compilerJavaVersion == JavaVersion.VERSION_1_10) {
            project.tasks.withType(Javadoc) { it.enabled = false }
            project.tasks.getByName('javadocJar').each { it.enabled = false }
        }
    }

    /**
     * Adds a javadocJar task to generate a jar containing javadocs.
     */
    static void configureJavadocJar(Project project) {
        Task javadocJarTask = project.task('javadocJar', type: Jar)
        javadocJarTask.classifier = 'javadoc'
        javadocJarTask.group = 'build'
        javadocJarTask.description = 'Assembles a jar containing javadocs.'
        javadocJarTask.from(project.tasks.getByName(JavaPlugin.JAVADOC_TASK_NAME))
        project.assemble.dependsOn(javadocJarTask)
    }

    static void configureSourcesJar(Project project) {
        Task sourcesJarTask = project.task('sourcesJar', type: Jar)
        sourcesJarTask.classifier = 'sources'
        sourcesJarTask.group = 'build'
        sourcesJarTask.description = 'Assembles a jar containing source files.'
        sourcesJarTask.from(project.sourceSets.main.allSource)
        project.assemble.dependsOn(sourcesJarTask)
    }

    /**
     * Adds additional manifest info to jars.
     */
    static void configureJars(Project project) {
        project.ext.licenseFile = null
        project.ext.noticeFile = null
        project.tasks.withType(Jar) { Jar jarTask ->
            // we put all our distributable files under distributions
            jarTask.destinationDir = new File(project.buildDir, 'distributions')
            // fixup the jar manifest
            jarTask.doFirst {
                boolean isSnapshot = VersionProperties.getVersion('elasticsearch').endsWith("-SNAPSHOT")
                String version = VersionProperties.getVersion('elasticsearch')
                if (isSnapshot) {
                    version = version.substring(0, version.length() - 9)
                }
                // this doFirst is added before the info plugin, therefore it will run
                // after the doFirst added by the info plugin, and we can override attributes
                jarTask.manifest.attributes(
                        'X-Compile-Elasticsearch-Version': version,
                        'X-Compile-Lucene-Version': VersionProperties.getVersion('lucene'),
                        'X-Compile-Elasticsearch-Snapshot': isSnapshot,
                        'Build-Date': ZonedDateTime.now(ZoneOffset.UTC),
                        'Build-Java-Version': project.compilerJavaVersion)
            }
            // add license/notice files if they are present
            project.afterEvaluate {
                if (project.licenseFile) {
                    jarTask.into('META-INF') {
                        from(project.licenseFile.parent) {
                            include project.licenseFile.name
                        }
                    }
                }
                if (project.noticeFile) {
                    jarTask.into('META-INF') {
                        from(project.noticeFile.parent) {
                            include project.noticeFile.name
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns a closure of common configuration shared by unit and integration tests.
     */
    static Closure commonTestConfig(Project project) {
        return {
            //jvm "${project.runtimeJavaHome}/bin/java"
            parallelism System.getProperty('tests.jvms', 'auto')
            ifNoTests 'fail'
            onNonEmptyWorkDirectory 'wipe'
            leaveTemporary true
            jvmArg '-Xmx' + System.getProperty('tests.heap.size', '512m')
            jvmArg '-Xms' + System.getProperty('tests.heap.size', '512m')
            // Do we always want to auto-create heapdump dir?
            // Do we want to trash our file system with heap dumps by default? Not really.
            if (System.getProperty('tests.heapdump')) {
                jvmArg '-XX:+HeapDumpOnOutOfMemoryError'
                File heapdumpDir = new File(project.buildDir, 'heapdump')
                heapdumpDir.mkdirs()
                jvmArg '-XX:HeapDumpPath=' + heapdumpDir
            }
            if (project.runtimeJavaVersion >= JavaVersion.VERSION_1_9) {
                jvmArg '--illegal-access=warn'
            }
            argLine System.getProperty('tests.jvm.argline')

            // we use './temp' since this is per JVM and tests are forbidden from writing to CWD
            systemProperty 'java.io.tmpdir', './temp'
            systemProperty 'java.awt.headless', 'true'
            systemProperty 'tests.gradle', 'true'
            systemProperty 'tests.artifact', project.name
            systemProperty 'tests.task', path
            systemProperty 'tests.security.manager', 'true'
            systemProperty 'jna.nosys', 'true'
            // default test sysprop values
            systemProperty 'tests.ifNoTests', 'fail'
            systemProperty 'tests.logger.level', System.getProperty('tests.logger.level', 'WARN')
            for (Map.Entry<Object, Object> property : System.properties.entrySet()) {
                String key = property.getKey().toString()
                if (key.startsWith('tests.') || key.startsWith('es.')) {
                    if (key.equals('tests.seed')) {
                        /* The seed is already set on the project so we
                         * shouldn't attempt to override it. */
                        continue
                    }
                    systemProperty property.getKey(), property.getValue()
                }
            }

            // Only set to 'true' because of randomizedtesting plugin.
            // Enabled assertions in test code are against fail-fast philosophy, they hide bugs in production mode
            // while they may expose bugs only in test mode.
            boolean assertionsEnabled = Boolean.parseBoolean(System.getProperty('tests.asserts', 'true'))
            enableSystemAssertions assertionsEnabled
            enableAssertions assertionsEnabled

            testLogging {
                showNumFailuresAtEnd 25
                slowTests {
                    heartbeat 10
                    summarySize 5
                }
                stackTraceFilters {
                    // custom filters: we carefully only omit test infra noise here
                    contains '.SlaveMain.'
                    regex(/^(\s+at )(org\.junit\.)/)
                    // also includes anonymous classes inside these two:
                    regex(/^(\s+at )(com\.carrotsearch\.randomizedtesting\.RandomizedRunner)/)
                    regex(/^(\s+at )(com\.carrotsearch\.randomizedtesting\.ThreadLeakControl)/)
                    regex(/^(\s+at )(com\.carrotsearch\.randomizedtesting\.rules\.)/)
                    regex(/^(\s+at )(org\.apache\.lucene\.util\.TestRule)/)
                    regex(/^(\s+at )(org\.apache\.lucene\.util\.AbstractBeforeAfterRule)/)
                }
                if (System.getProperty('tests.class') != null && System.getProperty('tests.output') == null) {
                    // if you are debugging, you want to see the output!
                    outputMode 'always'
                } else {
                    outputMode System.getProperty('tests.output', 'onerror')
                }
            }

            balancers {
                executionTime cacheFilename: ".local-${project.version}-${name}-execution-times.log"
            }

            listeners {
                junitXmlReport()
                junitHtmlReport()
            }
        }
    }

    /**
     * Configures the esTest task and let 'check' depends on it.
     * */
    static void configureTask(Project project) {
        Task testTask = project.tasks.findByPath('test')
        if (testTask == null) {
            // no test task, ok, user will use testing task on their own
            return
        }
        Task esTest = project.tasks.create([name: 'esTest',
                                            type: RandomizedTestingTask,
                                            dependsOn: ['testClasses'],
                                            group: JavaBasePlugin.VERIFICATION_GROUP,
                                            description: 'Runs Elasticsearch tests'
        ])
        esTest.configure(commonTestConfig(project))
        esTest.configure {
            classpath = testTask.classpath
            testClassesDirs = testTask.testClassesDirs
            exclude '**/*$*.class'
            include '**/*Tests.class'
        }
        // Add a method to create additional unit tests for a project, which will share the same
        // randomized testing setup, but by default run no tests.
        project.extensions.add('additionalTest', { String name, Closure config ->
            RandomizedTestingTask additionalTest = project.tasks.create(name, RandomizedTestingTask)
            additionalTest.classpath = esTest.classpath
            additionalTest.testClassesDirs = esTest.testClassesDirs
            additionalTest.configure(commonTestConfig(project))
            additionalTest.configure(config)
            esTest.dependsOn(additionalTest)
        })
        Task checkTask = project.tasks.findByPath('check')
        checkTask.dependsOn.add(esTest)
    }

    private static Configuration createProvidedConfiguration(Project project) {
        Configuration providedConf = project.configurations.create('provided')
                .setVisible(true)
                .setTransitive(true)
        providedConf
    }

    private static configureQualityAssurance(Project project) {
        Task quality = QualityAssuranceTasks.create(project, true)
        project.tasks.check.dependsOn(quality)
        // only require dependency licenses for non-elasticsearch deps
        project.dependencyLicenses.dependencies = project.configurations.runtime.fileCollection {
            (!it.group.startsWith('org.elasticsearch'))
        } - project.configurations.provided
    }

    private static configureDependenciesInfo(Project project) {
        Task deps = project.tasks.create("dependenciesInfo", DependenciesInfoTask.class)
        deps.dependencies = project.configurations.compile.allDependencies
    }

    /**
     * Return the configuration name used for finding transitive deps of the given dependency.
     * */
    private static String transitiveDepConfigName(String groupId, String artifactId, String version) {
        return "_transitive_${groupId}_${artifactId}_${version}"
    }

    /**
     * Returns a closure which can be used with a MavenPom for fixing problems with gradle generated poms.
     *
     * <ul>
     *     <li>Remove transitive dependencies. We currently exclude all artifacts explicitly instead of using wildcards
     *         as Ivy incorrectly translates POMs with * excludes to Ivy XML with * excludes which results in the main artifact
     *         being excluded as well (see https://issues.apache.org/jira/browse/IVY-1531). Note that Gradle 2.14+ automatically
     *         translates non-transitive dependencies to * excludes. We should revisit this when upgrading Gradle.</li>
     *     <li>Set compile time deps back to compile from runtime (known issue with maven-publish plugin)</li>
     * </ul>
     */
    private static Closure fixupDependencies(Project project) {
        return { XmlProvider xml ->
            // first find if we have dependencies at all, and grab the node
            NodeList depsNodes = xml.asNode().get('dependencies') as NodeList
            if (depsNodes.isEmpty()) {
                return
            }

            // check each dependency for any transitive deps
            for (Node depNode : depsNodes.get(0).children()) {
                String groupId = depNode.get('groupId').get(0).text()
                String artifactId = depNode.get('artifactId').get(0).text()
                String version = depNode.get('version').get(0).text()

                // fix deps incorrectly marked as runtime back to compile time deps
                // see https://discuss.gradle.org/t/maven-publish-plugin-generated-pom-making-dependency-scope-runtime/7494/4
                boolean isCompileDep = project.configurations.compile.allDependencies.find { dep ->
                    dep.name == depNode.artifactId.text()
                }
                if (depNode.scope.text() == 'runtime' && isCompileDep) {
                    depNode.scope*.value = 'compile'
                }

                // remove any exclusions added by gradle, they contain wildcards and systems like ivy have bugs with wildcards
                // see https://github.com/elastic/elasticsearch/issues/24490
                NodeList exclusionsNode = depNode.get('exclusions')
                if (exclusionsNode.size() > 0) {
                    depNode.remove(exclusionsNode.get(0))
                }

                // collect the transitive deps now that we know what this dependency is
                String depConfig = transitiveDepConfigName(groupId, artifactId, version)
                Configuration configuration = project.configurations.findByName(depConfig)
                if (configuration == null) {
                    continue // we did not make this dep non-transitive
                }
                Set<ResolvedArtifact> artifacts = configuration.resolvedConfiguration.resolvedArtifacts
                if (artifacts.size() <= 1) {
                    // this dep has no transitive deps (or the only artifact is itself)
                    continue
                }

                // we now know we have something to exclude, so add exclusions for all artifacts except the main one
                Node exclusions = depNode.appendNode('exclusions')
                for (ResolvedArtifact artifact : artifacts) {
                    ModuleVersionIdentifier moduleVersionIdentifier = artifact.moduleVersion.id
                    String depGroupId = moduleVersionIdentifier.group
                    String depArtifactId = moduleVersionIdentifier.name
                    // add exclusions for all artifacts except the main one
                    if (depGroupId != groupId || depArtifactId != artifactId) {
                        Node exclusion = exclusions.appendNode('exclusion')
                        exclusion.appendNode('groupId', depGroupId)
                        exclusion.appendNode('artifactId', depArtifactId)
                    }
                }
            }
        }
    }
}


