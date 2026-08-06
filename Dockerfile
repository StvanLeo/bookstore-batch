# Step 1: Use an official OpenJDK runtime image
FROM eclipse-temurin:21-jre
# Step 2: Set the working directory inside the container
WORKDIR /app
# Step 3: Copy your compiled JAR file into the container
COPY build/libs/*-SNAPSHOT.jar app.jar
# Step 4: Run the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]