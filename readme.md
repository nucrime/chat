# Prerequisites
[JDK 17](https://jdk.java.net/17/)

node 16.13.0 (LTS): [Win](https://nodejs.org/dist/v16.13.0/node-v16.13.0-x86.msi)
[Linux](https://nodejs.org/dist/v16.13.0/node-v16.13.0.tar.gz)
[Macos](https://nodejs.org/dist/v16.13.0/node-v16.13.0.pkg)

## Running the application
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**Note:** The application is running on port 8080. And could be accessed by http://localhost:8080/

It takes time to start the application as Vaadin initializes frontend and downloads necessary libs.

Links:
[Markdown support](https://github.com/rjeschke/txtmark)
