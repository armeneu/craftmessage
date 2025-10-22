#!/bin/bash

# Build script for CraftMessage Minecraft mod
echo "Building CraftMessage mod..."

# Clean previous builds
echo "Cleaning previous builds..."
gradle clean

# Build the mod
echo "Building mod..."
gradle build

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo "Mod JAR location: build/libs/craftmessage-1.0.0.jar"

    # Copy to Minecraft mods folder if it exists
    MINECRAFT_MODS_DIR="$HOME/.minecraft/mods"
    if [ -d "$MINECRAFT_MODS_DIR" ]; then
        echo "Copying mod to Minecraft mods folder..."
        cp build/libs/craftmessage-1.0.0.jar "$MINECRAFT_MODS_DIR/"
        echo "Mod copied to Minecraft mods folder!"
    else
        echo "Minecraft mods folder not found at $MINECRAFT_MODS_DIR"
        echo "Please copy the mod JAR manually to your Minecraft mods folder"
    fi
else
    echo "Build failed!"
    exit 1
fi
