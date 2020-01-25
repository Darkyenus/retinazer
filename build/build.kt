@file:Suppress("unused")
import wemi.compile.JavaCompilerFlags
import wemi.dependency.Jitpack
import wemi.dependency.NoClassifier
import wemi.publish.artifacts

val retinazer by project(Archetypes.JavaProject) {

	projectGroup set { "com.darkyen" }
	projectName set { "retinazer" }
	projectVersion set { "0.2.5" }

	compilerOptions[JavaCompilerFlags.customFlags] = { it + "-encoding" + "UTF-8" }

	val gdxVersion = "1.9.10"
	libraryDependencies add { dependency("com.badlogicgames.gdx" , "gdx", gdxVersion) }
	libraryDependencies add { dependency("org.jetbrains", "annotations", "16.0.2", scope = ScopeProvided) }

	repositories add { Jitpack }
	libraryDependencies add { dependency("com.github.mp911de.microbenchmark-runner", "microbenchmark-runner-junit5", "0.2.0.RELEASE", scope = ScopeTest) }
	libraryDependencies add { dependency("org.openjdk.jmh:jmh-generator-annprocess:1.21") }

	libraryDependencies add { Dependency(JUnitAPI, ScopeTest) }
	libraryDependencies add { Dependency(JUnitEngine, ScopeTest) }

	// Workaround for Wemi 0.11 which broke this
	publishArtifacts set { artifacts(NoClassifier, includeSources = false, includeDocumentation = false) }
}
