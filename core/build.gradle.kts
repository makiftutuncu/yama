plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ktlint)
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(libs.bundles.kotest)
}
