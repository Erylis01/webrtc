from debian:jessie

RUN apt-get update
RUN apt-get install -y git maven openjdk-7-jdk
RUN echo "no cache" && git clone -b master "https://github.com/Erylis01/webrtc"

RUN apt-get install -y wget
RUN wget http://mirror.xivo.io/fai/xivo-migration/xivo_install.sh
RUN chmod +x xivo_install.sh
RUN ./xivo_install.sh

WORKDIR "webrtc/kurento-agent"
RUN echo "#!/bin/bash" > start.sh
RUN echo "git pull" >> start.sh
RUN echo "mvn clean package" >> start.sh
RUN echo "mvn exec:java" >> start.sh
RUN chmod +x start.sh
ENTRYPOINT ["./start.sh"]
EXPOSE 8080
