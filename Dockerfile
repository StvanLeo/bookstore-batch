# -----------------------------
# Stage 1: Build the application
# -----------------------------
FROM gradle:9.5-jdk21 AS builder
# Step 1: Set the working directory for gradle
WORKDIR /app

# Step 2: Copy Gradle configuration
COPY build.gradle settings.gradle ./

# Step 3: Copy Gradle wrapper/configuration if your project uses them
COPY gradle ./gradle

# Step 4: Copy and run gradlew with priviliges
COPY gradlew .
RUN chmod +x gradlew

# Step 5: Run gradle dependencies
RUN gradle dependencies --no-daemon

# Step 6: Copy source
COPY src ./src

# Step 7: Build the Spring Boot JAR
RUN gradle clean bootJar --no-daemon

# -----------------------------
# Stage 2: Run the application
# -----------------------------
# Step 1: Use an official OpenJDK runtime image
FROM eclipse-temurin:21-jre

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3; Set the directory to download files
RUN mkdir -p /app/downloaded

# Step 4: Copy your compiled JAR file into the container
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

# Step 5: Run the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]