@file:Suppress("unused")
import wemi.compile.JavaCompilerFlags

val retinazer by project(Archetypes.JavaProject) {

	projectGroup set { "com.darkyen" }
	projectName set { "retinazer" }
	projectVersion set { "0.2.2" }

	compilerOptions[JavaCompilerFlags.customFlags] = { it + "-encoding" + "UTF-8" }

	val gdxVersion = "1.9.2"
	libraryDependencies add { dependency("com.badlogicgames.gdx" , "gdx", gdxVersion) }

	libraryDependencies add { Dependency(JUnitAPI, ScopeTest) }
	libraryDependencies add { Dependency(JUnitEngine, ScopeTest) }

}
