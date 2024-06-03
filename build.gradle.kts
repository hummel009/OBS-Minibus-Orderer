import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.jvm") version "2.0.0"
	id("application")
}

group = "com.github.hummel"
version = LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

val embed: Configuration by configurations.creating

dependencies {
	embed("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
	embed("com.google.code.gson:gson:2.11.0")
	embed("org.apache.httpcomponents.client5:httpclient5:5.3.1")
	embed("com.formdev:flatlaf:3.4.1")
	embed("com.formdev:flatlaf-intellij-themes:3.4.1")
	implementation("com.google.code.gson:gson:2.11.0")
	implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")
	implementation("com.formdev:flatlaf:3.4.1")
	implementation("com.formdev:flatlaf-intellij-themes:3.4.1")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(8)
	}
}

application {
	mainClass = "com.github.hummel.shuttle.MainKt"
}

tasks {
	jar {
		manifest {
			attributes(
				mapOf(
					"Main-Class" to "com.github.hummel.shuttle.MainKt"
				)
			)
		}
		from(embed.map {
			if (it.isDirectory) it else zipTree(it)
		})
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}
}
