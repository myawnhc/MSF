#/bin/bash
export JET_HOME="/Users/myawn/Documents/Hazelcast/Releases/hazelcast-jet-4.5"
echo JET_HOME is $JET_HOME

# TODO: framework jar needs to be on the classpath or part of the jar we build

$JET_HOME/bin/jet submit -v -c com.hazelcast.msfdemo.acctsvc.business.OpenAccountPipeline target/AccountService-1.0-SNAPSHOT.jar
#$JET_HOME/bin/jet submit -v -c com.hazelcast.msfdemo.acctsvc.business.AdjustBalancePipeline target/AccountService-1.0-SNAPSHOT.jar