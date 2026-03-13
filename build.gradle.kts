plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.intellij.platform") version "2.10.2"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
}

group = "com.yichangyiwai"
version = "1.2.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        intellijIdea("2025.2.4")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add plugin dependencies for compilation here:

        composeUI()

        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.modules.json")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "252"
        }

        changeNotes = """
            <h3>v1.2.0</h3>
            <ul>
                <li>新增 TCP 发送面板，支持完整 Hex 发送与按偏移规则组包</li>
                <li>新增 TCP 接收解析面板，支持接收记录、实时规则解析和 Ping 探活</li>
                <li>抽取共享 Hex/字节解析组件，统一接收页空态、规则结果与交互体验</li>
            </ul>
        """.trimIndent()
    }

    pluginVerification {
        ides {
            create(org.jetbrains.intellij.platform.gradle.IntelliJPlatformType.IntellijIdea, "2025.2.4")
        }
    }

    buildSearchableOptions = false
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
