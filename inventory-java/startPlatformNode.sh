
#
# Copyright 2018-2022 Hazelcast, Inc
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

export FRAMEWORK_JAR=../framework/target/framework-1.0-SNAPSHOT.jar
export SERVICE_PROTO_JAR=../inventory-protobuf/target/inventory.protobuf-1.0-SNAPSHOT.jar
export SERVICE_JAR=./target/inventory-java-1.0-SNAPSHOT.jar

# Notes: schema validation can be re-enabled on 5.1
# Uploading jars with JobConfig still results in not found, so adding explicitly here

echo Starting Hazelcast Platform
hz start -J-Dhazelcast.config.schema.validation.enabled=false \
         -J-Dhz.jet.resource-upload-enabled=true \
         --config=target/classes/hazelcast.yaml \
         --jar=$FRAMEWORK_JAR,$SERVICE_PROTO_JAR,$SERVICE_JAR