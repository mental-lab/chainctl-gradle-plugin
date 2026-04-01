package com.chainguard.chainctl

import com.chainguard.chainctl.tasks.ChainctlVerifyDependenciesTask
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

class ChainctlGradlePluginTest {

    @Test
    fun `plugin registers chainctlVerifyDependencies task`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.chainguard.chainctl")

        assertNotNull(project.tasks.findByName(ChainctlVerifyDependenciesTask.TASK_NAME))
    }

    @Test
    fun `plugin registers chainctlVerify extension`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.chainguard.chainctl")

        assertNotNull(project.extensions.findByName("chainctlVerify"))
    }

    @Test
    fun `extension defaults are correct`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.chainguard.chainctl")

        val ext = project.extensions.getByType(
            com.chainguard.chainctl.extension.ChainctlExtension::class.java
        )
        assert(ext.failOnUnverified)
        assert(ext.skipConfigurations.isEmpty())
        assert(ext.chainctlPath == "chainctl")
    }
}
