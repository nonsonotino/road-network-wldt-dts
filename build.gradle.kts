plugins {
    application
}

val ghPackagesUsername: String by project
val ghPackagesPwd: String by project

repositories {
    mavenCentral()
    maven {
        url = uri("https://git.informatik.uni-hamburg.de/api/v4/groups/sane-public/-/packages/maven")
    }
    maven {
        url = uri("https://maven.pkg.github.com/Web-of-Digital-Twins/wldt-wodt-adapter")
        credentials {
            username = project.findProperty("ghPackagesUsername")?.toString() ?: ghPackagesUsername
            password = project.findProperty("ghPackagesPwd")?.toString() ?: ghPackagesPwd
        }
    }
}

dependencies {
    implementation("io.github.webbasedwodt:wldt-wodt-adapter:5.2.1")
    implementation("io.github.wldt:mqtt-physical-adapter:0.1.2")
}

application {
    mainClass.set("io.github.wodt.Laucher")
}