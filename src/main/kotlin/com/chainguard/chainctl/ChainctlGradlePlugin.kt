package com.chainguard.chainctl

import com.chainguard.chainctl.extension.ChainctlExtension
import com.chainguard.chainctl.extension.ChainctlExtension.Companion.EXTENSION_NAME
import com.chainguard.chainctl.tasks.ChainctlVerifyDependenciesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ChainctlGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_NAME, ChainctlExtension::class.java)

        val verifyTask = project.tasks.register(
            ChainctlVerifyDependenciesTask.TASK_NAME,
            ChainctlVerifyDependenciesTask::class.java
        ) {
            it.group = "verification"
            it.description = "Verifies resolved JAR dependencies against Chainguard Libraries using chainctl"
        }

        // Hook before shadowJar/bootJar if those tasks exist in the project
        project.tasks.configureEach { task ->
            if (task.name == "shadowJar" || task.name == "bootJar") {
                task.dependsOn(verifyTask)
            }
        }
    }
}
