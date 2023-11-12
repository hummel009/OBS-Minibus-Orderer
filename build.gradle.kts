import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
	id("org.jetbrains.kotlin.jvm") version "1.9.20"
	id("application")
}

group = "org.example"
version = "v" + LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd"))

repositories {
	mavenCentral()
}

val embed: Configuration by configurations.creating

dependencies {
	embed("org.jetbrains.kotlin:kotlin-stdlib:1.9.20")
	embed("com.google.code.gson:gson:2.10.1")
	embed("org.apache.httpcomponents:httpclient:4.5.14")
	embed("com.formdev:flatlaf:3.2.1")
	embed("com.formdev:flatlaf-intellij-themes:3.2.1")
	implementation("com.formdev:flatlaf:3.2.1")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("org.apache.httpcomponents:httpclient:4.5.14")
	implementation("com.formdev:flatlaf-intellij-themes:3.2.1")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
	jvmToolchain(17)
}

application {
	mainClass = "hummel.MainKt"
}

tasks {
	jar {
		manifest {
			attributes(
				mapOf(
					"Main-Class" to "hummel.MainKt"
				)
			)
		}
		from(embed.map {
			if (it.isDirectory()) it else zipTree(it)
		})
		duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	}
}