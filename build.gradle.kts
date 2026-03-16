plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.intellij.platform") version "2.10.2"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
}

group = "com.yichangyiwai"
version = "1.2.6"

fun String.escapeHtml(): String =
    replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")

fun formatChangelogInline(text: String): String =
    Regex("\\*\\*(.+?)\\*\\*").replace(text.escapeHtml()) { matchResult ->
        "<strong>${matchResult.groupValues[1]}</strong>"
    }

fun buildPluginChangeNotes(changelogFile: java.io.File): String {
    val lines = changelogFile.readLines()
    val releaseHeaderIndexes = lines.withIndex()
        .filter { (_, line) -> line.startsWith("## [") && !line.startsWith("## [Unreleased]") }
        .map { it.index }

    return releaseHeaderIndexes.mapIndexed { index, start ->
        val end = releaseHeaderIndexes.getOrNull(index + 1) ?: lines.size
        val sectionLines = lines.subList(start, end)
        val header = sectionLines.first()
        val versionLabel = header.removePrefix("## [").substringBefore("]")
        val versionDate = header.substringAfter("] - ", "").takeIf { it.isNotBlank() }

        buildString {
            append("<h3>v")
            append(versionLabel.escapeHtml())
            if (versionDate != null) {
                append(" - ")
                append(versionDate.escapeHtml())
            }
            appendLine("</h3>")

            var inList = false

            fun closeList() {
                if (inList) {
                    appendLine("</ul>")
                    inList = false
                }
            }

            sectionLines.drop(1).forEach { rawLine ->
                when {
                    rawLine.isBlank() -> closeList()
                    rawLine.startsWith("### ") -> {
                        closeList()
                        appendLine("<h4>${formatChangelogInline(rawLine.removePrefix("### "))}</h4>")
                    }
                    rawLine.startsWith("- ") || rawLine.startsWith("  - ") -> {
                        if (!inList) {
                            appendLine("<ul>")
                            inList = true
                        }

                        val bulletPrefix = if (rawLine.startsWith("  - ")) "— " else ""
                        appendLine("<li>${bulletPrefix}${formatChangelogInline(rawLine.substringAfter("- ").trim())}</li>")
                    }
                    else -> {
                        closeList()
                        appendLine("<p>${formatChangelogInline(rawLine.trim())}</p>")
                    }
                }
            }

            closeList()
        }.trim()
    }.joinToString("\n")
}

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

        changeNotes = buildPluginChangeNotes(file("CHANGELOG.md"))
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
