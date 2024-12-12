FROM openjdk:21-jdk-slim

# Actualizar e instalar dependencias necesarias
RUN apt-get update && apt-get install -y curl unzip && apt-get clean

# Configurar directorio de trabajo
WORKDIR /app

# Copiar Gradle Wrapper
COPY gradlew /app/gradlew
COPY gradle /app/gradle

# Dar permisos de ejecución al Gradle Wrapper
RUN chmod +x /app/gradlew

# Copiar todo el proyecto al contenedor
COPY . /app

# Construir la aplicación con Gradle
RUN ./gradlew build -x test

# Verificar y mover el JAR generado
RUN find build/libs -name '*plain.jar' -delete && cp build/libs/*.jar booking.jar

# Exponer el puerto para el servidor Eureka
EXPOSE 8082

# Comando para iniciar el servidor
CMD ["java", "-jar", "booking.jar"]
