FROM adoptopenjdk/openjdk11
ARG DIST_FILE=imageService-distribution.tar.gz
ARG DIST=target/${DIST_FILE}
ARG TARGET_DIR=/

WORKDIR ${TARGET_DIR}
ADD ${DIST} .
RUN mv /imageService/*.jar /imageService/imageservice.jar
RUN mkdir images
ENTRYPOINT [ "java", "-jar", "/imageService/imageservice.jar", "/images","8080" ]

