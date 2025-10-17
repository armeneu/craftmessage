# CraftMessage Minecraft Mod

A simple Minecraft Fabric mod to send messages via a GUI interface and store them in a PostgreSQL database using Hibernate with JPA Repository.

## Prerequisites

- Java 21
- Gradle 8.14
- PostgreSQL database
- Minecraft 1.21.8
- Fabric Loader

## Configuration

Update the database connection settings in `database.properties`:
- URL: `jdbc:postgresql://localhost:5433/minecraft`
- Username: `minecraft`
- Password: `password`

## Building

1. Clone or download this project
2. Navigate to the project directory
3. Run the build command:
```bash
./gradlew build
```

## Installation

1. Copy the generated JAR file from `build/libs/` to your Minecraft `mods/` folder
2. Start Minecraft with Fabric Loader

## Usage

1. In-game, press the `M` key to open the message screen
2. Type your message in the text field
3. Click "Send" to send the message
4. The message will be sent to the server
5. The server will save the message to the PostgreSQL database using Hibernate

## Dependencies

- Fabric API
- Hibernate ORM 6.6.7
- Jakarta Persistence API 3.1.0
- PostgreSQL JDBC Driver 42.7.3
- SmallRye Jandex 3.5.0 (https://mvnrepository.com/artifact/io.smallrye/jandex/3.5.0)

## License

MIT License