plugins {
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    implementation(project(":core"))
    implementation(libs.bundles.spring)
    implementation(libs.springDoc)
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}
