
export JET_HOME=/Users/myawn/Documents/Hazelcast/Releases/hazelcast-jet-4.5/

export CLASSPATH=$CLASSPATH:../framework/target/framework-1.0-SNAPSHOT.jar
export CLASSPATH=$CLASSPATH:/target/AccountService-1.0-SNAPSHOT.jar

# May not need to specify these now that they've moved into local resources dir
JET_CONFIG="-Dhazelcast.jet-config=.target/classes/hazelcast-jet.yaml"
HZ_CONFIG="-Dhazelcast.config=target/classes/hazelcast.yaml"

# We don't pass client config to member nodes, this needs to be somewhere else
HZ_CLIENT_CONFIG="-Dhazelcast.client.config=target/classes/hazelcast-client.yaml"

export JAVA_OPTS="$JET_CONFIG $HZ_CONFIG"
echo Starting jet with $JAVA_OPTS
$JET_HOME/bin/jet-start
