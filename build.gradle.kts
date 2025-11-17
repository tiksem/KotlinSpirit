plugins {
    kotlin("multiplatform") version "1.9.10"
    id("maven-publish")
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(11)
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    // iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // macOS targets
    macosX64()
    macosArm64()

    // Optional: JS target
    js(IR) {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {

        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        // JVM source set
        val jvmMain by getting {
            dependencies {
                implementation("org.json:json:20220924")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("org.json:json:20180813")
                implementation("org.skyscreamer:jsonassert:1.5.1")
                runtimeOnly("org.junit.vintage:junit-vintage-engine:5.9.0")
            }
        }

        // Apple (iOS + macOS) shared source set
        val appleMain by creating {
            dependsOn(commonMain)

            dependencies {
                implementation("co.touchlab:stately-concurrent-collections:2.0.6")
            }
        }

        val appleTest by creating {
            dependsOn(commonTest)
        }

        // iOS shared source set
        val iosMain by creating {
            dependsOn(appleMain)
        }

        val iosTest by creating {
            dependsOn(appleTest)
        }

        // Link iOS targets to iosMain
        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }

        val iosX64Test by getting { dependsOn(iosTest) }
        val iosArm64Test by getting { dependsOn(iosTest) }
        val iosSimulatorArm64Test by getting { dependsOn(iosTest) }

        // macOS source sets
        val macosMain by creating {
            dependsOn(appleMain)
        }

        val macosTest by creating {
            dependsOn(appleTest)
        }

        val macosX64Main by getting { dependsOn(macosMain) }
        val macosArm64Main by getting { dependsOn(macosMain) }

        val macosX64Test by getting { dependsOn(macosTest) }
        val macosArm64Test by getting { dependsOn(macosTest) }
    }
}