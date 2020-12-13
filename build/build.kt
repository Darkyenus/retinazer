@file:Suppress("unused")
import wemi.compile.JavaCompilerFlags
import wemi.dependency.Jitpack

val retinazer by project(Archetypes.JavaProject, Archetypes.JUnitLayer) {

	projectGroup set { "com.darkyen" }
	projectName set { "retinazer" }
	projectVersion set { "0.3.1" }

	compilerOptions[JavaCompilerFlags.customFlags] = { it + "-encoding" + "UTF-8" }

	val gdxVersion = "1.9.12"
	libraryDependencies add { dependency("com.badlogicgames.gdx" , "gdx", gdxVersion) }
	libraryDependencies add { dependency("org.jetbrains", "annotations", "16.0.2", scope = ScopeProvided) }

	repositories add { Jitpack }
	libraryDependencies add { dependency("com.github.mp911de.microbenchmark-runner", "microbenchmark-runner-junit5", "0.2.0.RELEASE", scope = ScopeTest) }
	libraryDependencies add { dependency("org.openjdk.jmh", "jmh-generator-annprocess", "1.21", scope = ScopeTest) }

	libraryDependencies add { Dependency(JUnitAPI, ScopeTest) }
	libraryDependencies add { Dependency(JUnitEngine, ScopeTest) }
}
