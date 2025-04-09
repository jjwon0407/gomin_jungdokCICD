#빌드 시 OpenJDK 17 기반 이미지 사용
FROM openjdk:17

COPY backend/build/libs/gdgoc-0.0.1-SNAPSHOT.jar gomin_jungdok.jar

EXPOSE 3030

#실행 명령
ENTRYPOINT ["java", "-jar", "/gomin_jungdok.jar", ">", "app.log"]