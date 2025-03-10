FROM amazoncorretto:17

# 한글 로케일 및 타임존 설정
ENV LANG=ko_KR.UTF-8
ENV LC_ALL=ko_KR.UTF-8
ENV TZ=Asia/Seoul

# JVM 기본 인코딩을 UTF-8로 설정
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Seoul"

ARG JAR_FILE=./build/libs/DoItTogether-0.0.1-SNAPSHOT.jar

WORKDIR /app

COPY ${JAR_FILE} /app/DoItTogether.jar

CMD ["java", "-Dfile.encoding=UTF-8", "-Duser.timezone=Asia/Seoul", "-jar", "DoItTogether.jar"]