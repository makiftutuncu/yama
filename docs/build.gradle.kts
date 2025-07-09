plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    dokka(project(":core"))
    dokka(project(":spring"))
}

dokka {
    dokkaSourceSets.main {
        enableJdkDocumentationLink = true
        enableKotlinStdLibDocumentationLink = true
        reportUndocumented = true
        skipDeprecated = false
        skipEmptyPackages = true
        suppressGeneratedFiles = true
    }
}
