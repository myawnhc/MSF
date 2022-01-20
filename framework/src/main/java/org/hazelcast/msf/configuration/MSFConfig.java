/*
 * Copyright 2018-2022 Hazelcast, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.hazelcast.msf.configuration;

// Most fields based on using Hazelcast Cloud
// Just cluster name is enough to connect to on-premise
// Embedded added but not yet implemented
@Deprecated
public class MSFConfig {
    public boolean embedded;
    public String name;
    public String password;
    public String discoveryToken;
    public String urlBase;  // May be null, and should not set property when that is the case

    public MSFConfig(String name, String password, String discoveryToken, String urlBase) {
        this.name = name;
        this.password = password;
        this.discoveryToken = discoveryToken;
        this.urlBase = urlBase;
    }

    public String toString() {
        return name + " " + password + " " + discoveryToken + " " + urlBase;
    }

}
