#!/bin/bash
# find-java.sh - Searches your system for Java 17 installations

echo "Searching for suitable Java versions (17)..."
echo ""

paths=(
    "$HOME/.sdkman/candidates/java/17.0.13-tem"
    "$HOME/.sdkman/candidates/java/17"
    "/usr/lib/jvm/java-17-openjdk"
    "/usr/lib/jvm/java-17-openjdk-amd64"
    "/usr/lib/jvm/temurin-17-jdk-amd64"
    "/usr/lib/jvm/jdk-17"
    "/opt/jdk-17"
)

found=0
for p in "${paths[@]}"; do
    if [ -d "$p" ]; then
        if [ -x "$p/bin/java" ]; then
            version=$("$p/bin/java" -version 2>&1 | head -n1)
            echo "FOUND: $p"
            echo "  $version"
            echo ""
            echo "Add this line to gradle.properties:"
            echo "  org.gradle.java.home=$p"
            echo ""
            found=1
        fi
    fi
done

if [ $found -eq 0 ]; then
    echo "No Java 17 found."
    echo ""
    echo "Install it with SDKMAN:"
    echo "  sdk install java 17.0.13-tem"
    echo "  sdk use java 17.0.13-tem"
    echo ""
    echo "Then run this script again."
fi
