// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins{
    id("com.android.application") version ("7.1.0") apply (false)
    id("com.android.library") version ("7.1.0") apply (false)
    id("org.jetbrains.kotlin.android") version ("1.5.21") apply (false)
    id("dagger.hilt.android.plugin") version ("2.40.5") apply (false)
    id("com.google.gms.google-services") version("4.3.10") apply(false)

}
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

buildscript {
    repositories {
        mavenCentral()
    }
}