package com.chainguard.chainctl.tasks

import com.chainguard.chainctl.extension.ChainctlExtension
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskAction

open class ChainctlVerifyDependenciesTask : DefaultTask() {

    companion object {
        const val TASK_NAME = "chainctlVerifyDependencies"
    }

    @TaskAction
    fun verify() {
        val ext = project.extensions.getByType(ChainctlExtension::class.java)

        val jars = collectJars(ext)
        if (jars.isEmpty()) {
            logger.lifecycle("[$TASK_NAME] No resolved JARs found; nothing to verify.")
            return
        }

        logger.lifecycle("[$TASK_NAME] Verifying ${jars.size} JAR(s) with chainctl...")

        val failures = mutableListOf<String>()
        for (jar in jars) {
            val result = runChainctl(ext.chainctlPath, jar)
            if (result != 0) {
                logger.warn("[$TASK_NAME] FAILED: $jar (exit code $result)")
                failures.add(jar)
            } else {
                logger.info("[$TASK_NAME] OK: $jar")
            }
        }

        if (failures.isNotEmpty()) {
            val msg = "chainctl verification failed for ${failures.size} JAR(s):\n" +
                    failures.joinToString("\n") { "  - $it" }
            if (ext.failOnUnverified) {
                throw GradleException(msg)
            } else {
                logger.warn(msg)
            }
        } else {
            logger.lifecycle("[$TASK_NAME] All JARs verified successfully.")
        }
    }

    private fun collectJars(ext: ChainctlExtension): List<String> {
        val seen = mutableSetOf<String>()
        val jars = mutableListOf<String>()

        project.configurations
            .filter { canBeResolved(it) && !shouldBeSkipped(it, ext) }
            .forEach { configuration ->
                try {
                    configuration.resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
                        val path = artifact.file.absolutePath
                        if (path.endsWith(".jar") && seen.add(path)) {
                            jars.add(path)
                        }
                    }
                } catch (e: Exception) {
                    logger.warn("[$TASK_NAME] Could not resolve configuration '${configuration.name}': ${e.message}")
                }
            }

        return jars
    }

    private fun canBeResolved(configuration: Configuration): Boolean =
        configuration.isCanBeResolved

    private fun shouldBeSkipped(configuration: Configuration, ext: ChainctlExtension): Boolean =
        ext.skipConfigurations.contains(configuration.name)

    private fun runChainctl(chainctlPath: String, jarPath: String): Int {
        return try {
            val process = ProcessBuilder(chainctlPath, "libraries", "verify", jarPath)
                .inheritIO()
                .start()
            process.waitFor()
        } catch (e: Exception) {
            logger.error("[$TASK_NAME] Failed to run chainctl for $jarPath: ${e.message}")
            1
        }
    }
}
