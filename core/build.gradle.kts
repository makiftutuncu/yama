plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(libs.bundles.kotest)
}
