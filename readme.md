# Prerequisites
[JDK 17](https://jdk.java.net/17/)

node 16.13.0 (LTS): [Win](https://nodejs.org/dist/v16.13.0/node-v16.13.0-x86.msi)
[Linux](https://nodejs.org/dist/v16.13.0/node-v16.13.0.tar.gz)
[Macos](https://nodejs.org/dist/v16.13.0/node-v16.13.0.pkg)

## Running the application
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## Deploy to production
```bash
 ./mvnw install -P production     
```

For connection to MongoDB locally just install it.
```bash
brew install mongodb-community
brew services start mongodb-community
```
Environment variables should be like:
```bash
MONGODB_URL_DEV mongodb://localhost:27017/?readPreference=primary&directConnection=true&ssl=false
MONGODB_URL mongodb://localhost:27017/?readPreference=primary&directConnection=true&ssl=false
```

**Note:** The application is running on port 8080. And could be accessed by http://localhost:8080/

It takes time to start the application as Vaadin initializes frontend and downloads necessary libs.

Links:
[Markdown support](https://github.com/rjeschke/txtmark)
[Vaadin](https://vaadin.com/)
[Heroku build pack](https://github.com/heroku/heroku-buildpack-java#customize-maven)
