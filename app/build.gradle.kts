plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.misw.medisupply"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.misw.medisupply"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            
            // URLs para desarrollo local (10.0.2.2 = localhost en emulador Android)
            buildConfigField("String", "SALES_SERVICE_URL", "\"http://10.0.2.2:8000/\"")
            buildConfigField("String", "CATALOG_SERVICE_URL", "\"http://10.0.2.2:8001/\"")
            buildConfigField("String", "LOGISTICS_SERVICE_URL", "\"http://10.0.2.2:8002/\"")
            buildConfigField("String", "ENVIRONMENT", "\"LOCAL\"")
        }
        
        release {
            isMinifyEnabled = false
            isDebuggable = false
            
            // Usar firma de debug para testing (evita necesidad de keystore)
            // Para producción real (Play Store), crea un keystore y cambia esto
            signingConfig = signingConfigs.getByName("debug")
            
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // URLs para AWS (producción)
            buildConfigField("String", "SALES_SERVICE_URL", "\"http://lb-sales-service-570996197.us-east-1.elb.amazonaws.com/\"")
            buildConfigField("String", "CATALOG_SERVICE_URL", "\"http://lb-catalog-service-11171664.us-east-1.elb.amazonaws.com/\"")
            buildConfigField("String", "LOGISTICS_SERVICE_URL", "\"http://lb-logistics-service-1435144637.us-east-1.elb.amazonaws.com/\"")
            buildConfigField("String", "ENVIRONMENT", "\"AWS\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        buildConfig = true  // Habilitar BuildConfig
    }
    
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    // Compose Material Dialogs (Date/Time Picker 100% Compose)
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    
    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.5")
    
    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")
    
    // Hilt for Dependency Injection
    implementation("com.google.dagger:hilt-android:2.54")
    kapt("com.google.dagger:hilt-compiler:2.54")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    
    // Retrofit for Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.11.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    
    // Room Database (opcional para cache local)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Testing
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("com.google.truth:truth:1.4.4")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.0.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("app.cash.turbine:turbine:1.0.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    
    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}