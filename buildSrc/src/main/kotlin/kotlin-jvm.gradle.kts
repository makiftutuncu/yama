package buildsrc.convention

import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.time.LocalDate

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED
        )
    }
}

dokka {
    moduleName = "${rootProject.name}-${project.name}"
    dokkaPublications.html {
        failOnWarning = true
        suppressObviousFunctions = true
    }
    pluginsConfiguration.html {
        footerMessage = "&#169; ${LocalDate.now().year} Mehmet Akif Tütüncü"
        separateInheritedMembers = true
    }
}
