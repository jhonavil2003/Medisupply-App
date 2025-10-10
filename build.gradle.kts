plugins {
    // No aplicar plugins globales aquí, solo configuración común
    kotlin("jvm") version "2.0.0" apply false
    id("org.springframework.boot") version "3.3.4" apply false
    id("io.spring.dependency-management") version "1.1.5" apply false
    id("com.android.application") version "8.5.2" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("runAll") {
    group = "application"
    description = "Ejecuta el backend y prepara la app Android"
    dependsOn(":app:backend:bootRun")
}
