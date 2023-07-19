# Establece la imagen base de Java
FROM openjdk:11-jdk

# Establece el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo JAR del servicio Eureka al contenedor
COPY target/ms-client-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto del servicio Eureka (por ejemplo, 8761)
EXPOSE 8080

# Comando para ejecutar el servicio Eureka cuando se inicie el contenedor
CMD ["java", "-jar", "app.jar"]