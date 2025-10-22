# CraftMessage Minecraft Mod v1.1

A simple Minecraft mod that allows players to send messages via Protobuf to a PostgreSQL database.

## Features

- Simple GUI for sending messages
- Protobuf 3 message encoding
- PostgreSQL database storage
- Hibernate JPA for database operations

## Requirements

- Minecraft 1.21.8
- Fabric Loader
- Java 21
- PostgreSQL database running on localhost:5433

## Installation

1. Clone or download this mod
2. Build the mod using Gradle:
   ```bash
   ./build.sh
   ```
3. Copy the generated JAR file from `build/libs/` to your Minecraft mods folder

## Usage

1. Launch Minecraft with Fabric Loader
2. Press `M` key to open the message screen
3. Type your message in the text field
4. Click "Send Message" to send the message to the database
5. You'll see a confirmation message: "Message sent" or "Message not sent"

## Configuration

The database connection is configured in `src/main/resources/META-INF/persistence.xml`:
- Host: localhost:5433
- Database: minecraft
- Username: minecraft
- Password: password

## Development

This mod uses:
- Fabric API for Minecraft modding
- Hibernate 6.4.4 for JPA
- PostgreSQL JDBC driver
- Java 21
- SmallRye Jandex 3.5.0 (https://mvnrepository.com/artifact/io.smallrye/jandex/3.5.0)

## License

MIT License
