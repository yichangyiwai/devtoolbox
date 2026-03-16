plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.intellij.platform") version "2.10.2"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
}

group = "com.yichangyiwai"
version = "1.2.6"

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
            <h3>v1.2.6</h3>
            <ul>
                <li>工具窗口新增独立窗口放大查看入口，并统一调整部分工具页签命名</li>
                <li>JSON 格式化与进制解码器补充成功/错误状态反馈，并优化输入光标显示</li>
                <li>TCP 报文发送与解析规则编辑体验优化，新增规则时可自动衔接偏移</li>
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
