package com.hendraanggrian.packr

import com.badlogicgames.packr.PackrConfig
import com.hendraanggrian.packr.dist.Distribution
import com.hendraanggrian.packr.dist.DistributionBuilder
import com.hendraanggrian.packr.dist.MacOSDistribution
import com.hendraanggrian.packr.dist.MacOSDistributionBuilder
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.invoke
import java.io.File

open class PackrExtension(private val project: Project) : VmArged {

    companion object {
        const val MINIMIZE_SOFT = "soft"
        const val MINIMIZE_HARD = "hard"
        const val MINIMIZE_ORACLEJRE8 = "oraclejre8"
    }

    private val distributions = mutableMapOf<PackrConfig.Platform, Distribution>()

    internal fun getDistributions() = distributions as Map<PackrConfig.Platform, Distribution>

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

    fun resources(vararg resources: String) {
        this.resources = resources.map { project.projectDir.resolve(it) }
    }

    /**
     * Minimize the JRE by removing directories and files as specified by an additional config file.
     * Comes with a few config files out of the box.
     * Default is `soft`.
     */
    var minimizeJre: String = MINIMIZE_SOFT

    /**
     * The output directory.
     * Default is `release` directory in project's build directory.
     */
    lateinit var outputDir: File

    var outputDirectory: String
        @Input get() = outputDir.absolutePath
        set(value) {
            outputDir = project.projectDir.resolve(value)
        }

    override var vmArgs: Iterable<String> = emptyList()

    /**
     * Print extra messages about JRE minimizeJre when set to `true`.
     * This is an optional property.
     */
    var verbose: Boolean = false

    /**
     * Open [outputDirectory] upon packing completion.
     * This is an optional property.
     */
    var openOnDone: Boolean = false

    /** Configure macOS distribution. Unlike other distributions, mac configuration have some OS-specific properties. */
    @JvmOverloads
    fun macOS(action: Action<MacOSDistributionBuilder>? = null) {
        distributions[PackrConfig.Platform.MacOS] = MacOSDistribution().also { action?.invoke(it) }
    }

    /** Configure Windows 32-bit distribution. */
    @JvmOverloads
    fun windows32(action: Action<DistributionBuilder>? = null) {
        distributions[PackrConfig.Platform.Windows32] = Distribution().also { action?.invoke(it) }
    }

    /** Configure Windows 64-bit distribution. */
    @JvmOverloads
    fun windows64(action: Action<DistributionBuilder>? = null) {
        distributions[PackrConfig.Platform.Windows64] = Distribution().also { action?.invoke(it) }
    }

    /** Configure Linux 32-bit distribution. */
    @JvmOverloads
    fun linux32(action: Action<DistributionBuilder>? = null) {
        distributions[PackrConfig.Platform.Linux32] = Distribution().also { action?.invoke(it) }
    }

    /** Configure Linux 64-bit distribution. */
    @JvmOverloads
    fun linux64(action: Action<DistributionBuilder>? = null) {
        distributions[PackrConfig.Platform.Linux64] = Distribution().also { action?.invoke(it) }
    }
}