@file:Suppress("unused")
import wemi.compile.JavaCompilerFlags
import wemi.dependency.NoClassifier
import wemi.publish.artifacts

val retinazer by project(Archetypes.JavaProject) {

	projectGroup set { "com.darkyen" }
	projectName set { "retinazer" }
	projectVersion set { "0.2.4" }

	compilerOptions[JavaCompilerFlags.customFlags] = { it + "-encoding" + "UTF-8" }

	val gdxVersion = "1.9.10"
	libraryDependencies add { dependency("com.badlogicgames.gdx" , "gdx", gdxVersion) }

	libraryDependencies add { Dependency(JUnitAPI, ScopeTest) }
	libraryDependencies add { Dependency(JUnitEngine, ScopeTest) }

	// Workaround for Wemi 0.11 which broke this
	publishArtifacts set { artifacts(NoClassifier, includeSources = false, includeDocumentation = false) }
}
