# Этап 1: Сборка приложения с зависимостями
FROM mirror.gcr.io/maven:3.8.4-openjdk-17 AS build

# Установка рабочего каталога
WORKDIR /build

# Копирование файлов проекта Maven
COPY pom.xml pom.xml
COPY libs libs

RUN mvn install:install-file -Dfile=libs/resolver-1.0.0.jar -DgroupId=ru.sh1bari -DartifactId=resolver -Dversion=1.0.0 -Dpackaging=jar
RUN mvn install:install-file -Dfile=libs/validation-1.0.0.jar -DgroupId=ru.noxly -DartifactId=validation -Dversion=1.0.0 -Dpackaging=jar

# Скачивание зависимостей и сохранение их в отдельном слое
RUN mvn dependency:go-offline

# Копирование исходного кода
COPY src src

# Сборка приложения
RUN mvn clean verify -DskipTests=true

# Этап 2: Запуск приложения
FROM mirror.gcr.io/openjdk:17-jdk

# Установка рабочего каталога
WORKDIR /application

# Копирование JAR-файла приложения из сборочного этапа
COPY --from=build /build/target/*.jar application.jar
COPY --from=build /build/libs libs

# Запуск приложения
CMD ["java", "-jar", "application.jar"]

