#!/usr/bin/env bash

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

NAMESPACE="msf"
REGISTRY=""

# Enable HZ Kubernetes Discovery Plugin to make needed calls to Kubernetes API
kubectl apply -f https://raw.githubusercontent.com/hazelcast/hazelcast-kubernetes/master/rbac.yaml


for FILE in ../templates/*.yaml
do
  echo Deploying $FILE
  cat $FILE | \
  sed 's/\$(REGISTRY)\/'"/${REGISTRY}/g" | \
  sed 's/\$(NAMESPACE)'"/${NAMESPACE}/g" | \
  sed 's/\$(IPP)'"/Never/g" | \
  kubectl apply -f -
done