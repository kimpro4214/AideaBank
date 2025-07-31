#!/bin/bash

BUILD_PATH="/home/ubuntu/spring-gift-0.0.1-SNAPSHOT.jar"
JAR_NAME=$(basename $BUILD_PATH)

echo "> 현재 실행 중인 애플리케이션 확인"
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 실행 중인 애플리케이션이 없습니다."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포: $JAR_NAME"
nohup java -jar $BUILD_PATH > /dev/null 2> /dev/null < /dev/null &
