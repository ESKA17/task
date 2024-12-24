# Установка базового образа
FROM bellsoft/liberica-openjdk-alpine:21

# Установка переменных окружения
ENV TZ="Asia/Aqtau" JAVA_FLAGS="-Xms300m -Xmx500m"

# Установка рабочей директории
WORKDIR /app

# Копирование готового .jar файла
ARG JAR_FILE=task-service/target/*.jar
COPY ${JAR_FILE} task-service.jar

# Команда для запуска приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_FLAGS -jar task-service.jar"]

# Открытие порта
EXPOSE 8080