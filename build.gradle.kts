plugins {
    application
}

val ghPackageUsername: String by project
val ghPackagesPwd: String by project

repositories {
    mavenCentral()
    maven {
        url = uri("https://git.informatik.uni-hamburg.de/api/v4/groups/sane-public/-/packages/maven")
    }
    maven {
        url = uri("https://maven.pkg.github.com/Web-of-Digital-Twins/wldt-wodt-adapter")
        credentials {
            username = project.findProperty("ghPackagesUsername")?.toString() ?: ghPackageUsername
            password = project.findProperty("ghPackagesPwd")?.toString() ?: ghPackagesPwd
        }
    }
}

dependencies {
    implementation("io.github.webbasedwodt:wldt-wodt-adapter:5.2.1")
}