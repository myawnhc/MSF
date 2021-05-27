FROM hazelcast/hazelcast-jet:4.5

# TODO: add config files
# hazelcast-jet.yaml - not needed
# hazelcast-yaml - needed for event store map journal config

# TODO: add jar files (/ext directory)
# framework jar
# service jar

# TODO: Start the service (should set main class atttribute in manifest and then it's just run the jar)

# Maven compiled target
ARG JAR_FILE
COPY target/${JAR_FILE} application.jar
ENV JAVA_ARGS ""
ENV JAVA_OPTS "--add-modules java.se --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED"

ENTRYPOINT exec java \
 -Dlocal.cluster.name=$LOCAL_CLUSTER_NAME \
 -Dhost.ip=$HOST_IP \
 -Ddockerfile.instance.name=account-svc \
 $JAVA_ARGS $JAVA_OPTS \
 -jar application.jar