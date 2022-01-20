#!/bin/bash
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

export HZ_CLOUD_API_KEY=123
export HZ_CLOUD_API_SECRET=xyz
hzcloud enterprise-cluster create \
  --name=account \
  --cloud-provider=aws \
  --region=us-east-1 \
  --hazelcast-version=5.0 \
  --cidr-block=10.0.80.0/16 \
  --native-memory=7 \
  --instance-type=m5.large \
  --public-access-enabled \
  --zone-type=SINGLE \
  --wait

# TODO: would like capability to upload jar files
# TODO: would like capability to enable map journal
# TODO: would like capability to specify index fields for maps
# TODO: zone type is not documented, file issue for that
