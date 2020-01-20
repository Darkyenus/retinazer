@file:Suppress("unused")
import wemi.Configurations
import wemi.KeyDefaults
import wemi.assembly.AssemblyOperation
import wemi.assembly.DefaultAssemblyMapFilter
import wemi.assembly.DefaultRenameFunction
import wemi.assembly.NoConflictStrategyChooser
import wemi.assembly.NoPrependData
import wemi.compile.JavaCompilerFlags
import wemi.compile.JavaSourceFileExtensions
import wemi.expiresWith
import wemi.util.LineReadingWriter
import wemi.util.LocatedPath
import wemi.util.constructLocatedFiles
import wemi.util.div
import wemi.util.ensureEmptyDirectory
import wemi.util.exists
import wemi.util.name
import wemi.util.toSafeFileName
import java.io.File
import java.nio.file.Path
import java.util.*
import javax.tools.DiagnosticListener
import javax.tools.DocumentationTool
import javax.tools.JavaFileObject
import javax.tools.StandardLocation
import javax.tools.ToolProvider
import kotlin.collections.ArrayList

val retinazer by project(Archetypes.JavaProject) {

	projectGroup set { "com.darkyen" }
	projectName set { "retinazer" }
	projectVersion set { "0.2.2" }

	compilerOptions[JavaCompilerFlags.customFlags] = { it + "-encoding" + "UTF-8" }

	val gdxVersion = "1.9.2"
	libraryDependencies add { dependency("com.badlogicgames.gdx" , "gdx", gdxVersion) }

	libraryDependencies add { Dependency(JUnitAPI, ScopeTest) }
	libraryDependencies add { Dependency(JUnitEngine, ScopeTest) }

	extend(archivingDocs) {
		archive set {
			using(Configurations.archiving) {
				val sourceFiles = wemi.Keys.sources.getLocatedPaths(*JavaSourceFileExtensions)

				if (sourceFiles.isEmpty()) {
					println("JavaDoc: No source files for Javadoc, creating dummy documentation instead")
					return@using KeyDefaults.ArchiveDummyDocumentation(this)
				}

				val diagnosticListener: DiagnosticListener<JavaFileObject> = DiagnosticListener { diagnostic ->
					println("JavaDoc: $diagnostic")
				}

				val documentationTool = ToolProvider.getSystemDocumentationTool()!!
				val fileManager = documentationTool.getStandardFileManager(diagnosticListener, Locale.ROOT, Charsets.UTF_8)
				val sourceRoots = HashSet<File>()
				sourceFiles.mapNotNullTo(sourceRoots) { it.root?.toFile() }
				fileManager.setLocation(StandardLocation.SOURCE_PATH, sourceRoots)
				val javadocOutput = cacheDirectory.get() / "javadoc-${projectName.get().toSafeFileName('_')}"
				javadocOutput.ensureEmptyDirectory()
				fileManager.setLocation(DocumentationTool.Location.DOCUMENTATION_OUTPUT, listOf(javadocOutput.toFile()))

				fun jdkToolsJar(javaHome: Path): Path? {
					return javaHome.resolve("lib/tools.jar").takeIf { it.exists() }
							?: (if (javaHome.name == "jre") javaHome.resolve("../lib/tools.jar").takeIf { it.exists() } else null)
				}
				// Try to specify doclet path explicitly
				val toolsJar = jdkToolsJar(Keys.javaHome.get())

				if (toolsJar != null) {
					fileManager.setLocation(DocumentationTool.Location.DOCLET_PATH, listOf(toolsJar.toFile()))
				}

				val options = archiveJavadocOptions.get()

				val docTask = documentationTool.getTask(LineReadingWriter { line ->
					println("JavaDoc: $line")
				}, fileManager,
						diagnosticListener,
						null,
						options,
						fileManager.getJavaFileObjectsFromFiles(sourceFiles.map { it.file.toFile() }))

				docTask.setLocale(Locale.ROOT)
				docTask.call()

				val locatedFiles = ArrayList<LocatedPath>()
				constructLocatedFiles(javadocOutput, locatedFiles)

				AssemblyOperation().use { assemblyOperation ->
					// Load data
					for (file in locatedFiles) {
						assemblyOperation.addSource(file, own = true, extractJarEntries = false)
					}

					val outputFile = archiveOutputFile.get()
					assemblyOperation.assembly(
							NoConflictStrategyChooser,
							DefaultRenameFunction,
							DefaultAssemblyMapFilter,
							outputFile,
							NoPrependData,
							compress = true)

					expiresWith(outputFile)
					outputFile
				}
			}
		}
	}
}
