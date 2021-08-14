#
# Copyright 2018-2021 Hazelcast, Inc
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
#  limitations under the License.package com.theyawns.controller.launcher;
#
#

# Run just the database, for initial data creation before starting up the whole demo
docker network create msfnet
docker run -p 127.0.0.1:3306:3306 --name invdb --network msfnet --cap-add sys_nice -e MYSQL_ROOT_PASSWORD=secret -d mysql:latest --log-bin --binlog_format=ROW