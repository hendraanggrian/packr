package com.hendraanggrian.packr

import com.badlogicgames.packr.PackrConfig
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.invoke
import java.io.File

open class PackrExtension(private val project: Project) : VmArged {

    private val distributions = mutableSetOf<Distribution>()

    internal fun getDistribution(platform: PackrConfig.Platform) =
        distributions.singleOrNull { it.platform == platform }

    /**
     * Name of the native executable, without extension such as `.exe`.
     * Default is project's name.
     */
    var executable: String? = null

    /**
     * File locations of the JAR files to package.
     * Default is empty.
     */
    var classpath: Iterable<File> = emptyList()

    /** Convenient method to add classpath from file path, relative to project directory. */
    fun classpath(vararg classpath: String) {
        this.classpath = classpath.map { project.projectDir.resolve(it) }
    }

    /**
     * The fully qualified name of the main class, using dots to delimit package names.
     * Must be defined or will throw an exception.
     */
    var mainClass: String? = null

    /**
     * List of files and directories to be packaged next to the native executable.
     * Default is empty.
     */
    var resources: Iterable<File> = emptyList()

    /** Convenient method to add resources directory from file path, relative to project directory. */
    fun resources(vararg resources: String) {
        this.resources = resources.map { project.projectDir.resolve(it) }
    }

    /**
     * Minimize the JRE by removing directories and files as specified by an additional config file.
     * Comes with a few config files out of the box.
     * Default is `soft`.
     */
    var minimizeJre: String = "soft"

    /**
     * The output directory.
     * Default is `release` directory in project's build directory.
     */
    lateinit var outputDir: File

    /** Convenient method to set output directory from file path, relative to project directory. */
    var outputDirectory: String
        @Input get() = outputDir.absolutePath
        set(value) {
            outputDir = project.projectDir.resolve(value)
        }

    override var vmArgs: Iterable<String> = emptyList()

    /** An optional property which, when enabled, prints extra messages about JRE minimizeJre. */
    var verbose: Boolean = false

    /** An optional property which, when enabled, opens [outputDirectory] upon packing completion. */
    var openOnDone: Boolean = false

    /** Configure macOS distribution. Unlike other distributions, mac configuration have some OS-specific properties. */
    @JvmOverloads
    fun macOS(action: Action<MacOSDistributionBuilder>? = null) {
        distributions += MacOSDistribution(project).also { action?.invoke(it) }
    }

    /** Configure Windows 32-bit distribution. */
    @JvmOverloads
    fun windows32(action: Action<DistributionBuilder>? = null) {
        distributions += Distribution(project, PackrConfig.Platform.Windows32).also { action?.invoke(it) }
    }

    /** Configure Windows 64-bit distribution. */
    @JvmOverloads
    fun windows64(action: Action<DistributionBuilder>? = null) {
        distributions += Distribution(project, PackrConfig.Platform.Windows64).also { action?.invoke(it) }
    }

    /** Configure Linux 32-bit distribution. */
    @JvmOverloads
    fun linux32(action: Action<DistributionBuilder>? = null) {
        distributions += Distribution(project, PackrConfig.Platform.Linux32).also { action?.invoke(it) }
    }

    /** Configure Linux 64-bit distribution. */
    @JvmOverloads
    fun linux64(action: Action<DistributionBuilder>? = null) {
        distributions += Distribution(project, PackrConfig.Platform.Linux64).also { action?.invoke(it) }
    }
}