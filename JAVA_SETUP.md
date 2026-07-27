# Java Setup for Building

## The Problem

You are running **Java 21/24**, but **Loom 1.0** (required for Minecraft 1.16.5 modding) only works reliably with **Gradle 7.6**, which requires **Java 17** (or 8–19).

## Quick Fix (One-time setup)

### Step 1: Install Java 17 via SDKMAN

```bash
sdk install java 17.0.13-tem
sdk use java 17.0.13-tem
```

### Step 2: Find the Java 17 path

```bash
sdk home java 17.0.13-tem
```

This prints something like:
```
/home/sz/.sdkman/candidates/java/17.0.13-tem
```

### Step 3: Set it in gradle.properties

Open `gradle.properties` and uncomment + set:
```properties
org.gradle.java.home=/home/sz/.sdkman/candidates/java/17.0.13-tem
```

(Use the exact path from Step 2.)

### Step 4: Build

```bash
./gradlew build
```

The build should now complete in **under 2 minutes** instead of 30+ minutes or crashing.

## Why this works

| Component | Version | Reason |
|-----------|---------|--------|
| Gradle | 7.6 | Native version for Loom 1.0 |
| Java (build) | 17 | Gradle 7.6 supports up to Java 19 |
| Java (mod target) | 8 | Minecraft 1.16.5 requirement |

## After Building

The mod itself runs on **Java 8** (Minecraft 1.16.5). You only need Java 17 for the *build process*.

## Troubleshooting

**"Could not open init generic class cache"**
→ Run `./gradlew clean` first to clear corrupted caches.

**"Unsupported class file major version"**
→ You forgot to set `org.gradle.java.home` in `gradle.properties`.

**Build still slow?**
→ Make sure `org.gradle.jvmargs=-Xmx2G` is set in `gradle.properties`.
