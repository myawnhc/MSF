
export FRAMEWORK_JAR=../framework/target/framework-1.0-SNAPSHOT.jar
export SERVICE_PROTO_JAR=../account-protobuf/target/account-proto-1.0-SNAPSHOT.jar
export SERVICE_JAR=./target/account.service-1.0-SNAPSHOT.jar

#ls $FRAMEWORK_JAR
#ls $SERVICE_PROTO_JAR
#ls $SERVICE_JAR

# CNFE on this ... confirming that it's there.
#jar -tvf $FRAMEWORK_JAR | grep MapJournalEnabler

echo Starting Hazelcast Platform
hz start --config=target/classes/hazelcast.yaml --jar=$FRAMEWORK_JAR,$SERVICE_PROTO_JAR,$SERVICE_JAR