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

kubectl scale --replicas=0 statefulset account-service
kubectl scale --replicas=0 statefulset inventory-service
kubectl scale --replicas=0 statefulset catalog-service
kubectl scale --replicas=0 statefulset order-service
kubectl scale --replicas=0 statefulset mysql-invdb

kubectl delete statefulset account-service
kubectl delete statefulset inventory-service
kubectl delete statefulset catalog-service
kubectl delete statefulset order-service

kubectl delete deployment management-center

kubectl delete service acctsvc
kubectl delete service invsvc
kubectl delete service catalogsvc
kubectl delete service ordersvc
kubectl delete service invdb
kubectl delete service management-center-service

#kubectl delete horizontalpodautoscaler bankinabox-imdg

kubectl delete job test-client
