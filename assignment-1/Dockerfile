# Use the official OpenJDK image for Java 21
FROM openjdk:21-jdk-slim

# Install necessary packages for AWT/Swing and other dependencies
RUN apt-get update && \
    apt-get install -y libx11-6 libxext6 libxrender1 libxtst6 libxi6 libxt6 libfreetype6 libfontconfig1 && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled application JAR to the container
COPY target/jackpot-1.jar /app/jackpot-1.jar

# Set the DISPLAY environment variable
ENV DISPLAY=host.docker.internal:0.0

# Specify the command to run your application
CMD ["java", "-jar", "/app/jackpot-1.jar"]
