plugins {
    alias(libs.plugins.moddev)
}

val id = project.property("mod_id") as String
group = project.property("maven_group") as String
version = project.property("mod_version") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

neoForge {
    version = libs.versions.neoforge.asProvider().get()
    parchment {
        mappingsVersion = libs.versions.parchment.get()
        minecraftVersion = libs.versions.minecraft.asProvider().get()
    }
    validateAccessTransformers = true

    runs {
        configureEach {
            systemProperty("forge.logging.console.level", "debug")
        }

        create("client") {
            client()
        }

        create("server") {
            server()
        }

        create("data") {
            data()
        }
    }

    mods {
        create(id) {
            sourceSet(sourceSets["main"])
        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.shedaniel.me")
    maven("https://cursemaven.com")
    maven("https://jitpack.io")
    flatDir {
        dir("libs")
    }
}

dependencies {
    // TACZ API (compile only - provided at runtime)
    compileOnly("com.tacz:tacz-neoforge-1.21.1:${project.property("tacz_version")}") {
        isTransitive = false
    }

    // Cloth Config API (optional, for in-game config GUI)
    compileOnly(libs.me.shedaniel.cloth.config.neoforge)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.processResources {
    val properties = mapOf(
        "id" to id,
        "version" to project.version,
        "name" to project.property("mod_name") as String,
        "minecraft_version" to libs.versions.minecraft.range.get(),
        "loader_version" to libs.versions.neoforge.range.get()
    )
    filteringCharset = "UTF-8"
    inputs.properties(properties)
    filesMatching("META-INF/neoforge.mods.toml") { expand(properties) }
}
