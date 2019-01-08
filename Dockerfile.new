FROM maven:3.3.3

# 移动 settings
ADD settings.xml /root/.m2/

# 把父项目pom.xml 移动到 /tmp/build
ADD pom.xml /tmp/build/
ADD CloudBot-Common/pom.xml /tmp/build/CloudBot-Common/
ADD Service-Manager/pom.xml /tmp/build/Service-Manager/

RUN cd /tmp/build && mvn clean install --projects 'CloudBot-Common'

ADD . /tmp/build
        #构建应用

RUN cd /tmp/build && mvn -q -DskipTests=true package -pl 'Service-Manager' --also-make \
        #拷贝编译结果到指定目录
        && cd ./Service-Manager && mv target/*.jar /app.jar \
        #清理编译痕迹
        && cd / && rm -rf /tmp/build

VOLUME /tmp
EXPOSE 8101
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar", "/app.jar"]
