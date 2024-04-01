#!/usr/bin/env bash
PROJECT_ROOT="/home/ubuntu/dooingle"

JAR_FILE="$PROJECT_ROOT/build/libs/dooingle-1.0.0.jar"

DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# build 파일 복사
echo "$TIME_NOW > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/dooingle-0.1.jar $JAR_FILE

# jar 파일 실행
echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
# nohup java -jar $JAR_FILE --spring.profiles.active=dev 1> /dev/null 2>&1 & # 각 인스턴스에서 dev/prod로 변경

CURRENT_PID=$(pgrep -f "java")
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG
