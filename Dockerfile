FROM java:7-jre
MAINTAINER Mesosphere support@mesosphere.io

ADD target/scala-*/oinker-bot-assembly-*.jar /opt/

CMD java -jar /opt/oinker-bot-assembly-*.jar
