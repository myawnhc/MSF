/*
 * Copyright 2018-2021 Hazelcast, Inc
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
 *  limitations under the License.package com.theyawns.controller.launcher;
 *
 */

package com.hazelcast.msf.configuration;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ServiceConfig {

    static Map<String,ServiceProperties> configurations = new HashMap<>();

    public static class ServiceProperties {
        public String service_name;
        public String grpc_hostname;
        public String grpc_port; // actually numeric
        public String hz_mode;

        // Format used by gRPC ManagedChannelBuilder.forTarget()
        public String getTarget() {
            return grpc_hostname + ":" + grpc_port;
        }
        public String getGrpcHostname() { return grpc_hostname; }
        public int getGrpcPort() {
            return Integer.parseInt(grpc_port);
        }
        public boolean isEmbedded() {
            validateMode();
            return hz_mode.equalsIgnoreCase("embedded");
        }
        public boolean isClientServer() {
            validateMode();
            return hz_mode.equalsIgnoreCase("client-server");
        }
        private boolean validateMode() {
            if (hz_mode.equalsIgnoreCase("embedded") )
                return true;
            if (hz_mode.equalsIgnoreCase("client-server"))
                return true;
            if (hz_mode == null)
                throw new IllegalArgumentException(("HZ mode not set"));
            else
                throw new IllegalArgumentException(("Illegal hz_mode setting " + hz_mode));

        }
    }

    static {
        read();
    }

    public static ServiceProperties get(String serviceName) {
        return configurations.get(serviceName);
    }

    static private void read() {
        YAMLFactory yfactory = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(yfactory);
        MappingIterator<ServiceProperties> configInfo;
        try {
            URL yamlFile = ServiceConfig.class.getClassLoader().getResource("service.yaml");
            YAMLParser parser = yfactory.createParser(yamlFile);
            System.out.println("ServiceConfig reading config info from " + yamlFile.toExternalForm());
            configInfo = mapper.readValues(parser, ServiceProperties.class);
            while (configInfo.hasNext()) {
                ServiceProperties sp = configInfo.next();
                configurations.put(sp.service_name, sp);
                System.out.println(" -- " + sp.service_name);
            }
            System.out.println("ServiceConfig loaded " + configurations.size() + " service definitions");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // For testing use only.  Needs service-yaml.test renamed to drop '.test' in order to work.
    // (Renamed because otherwise it overwrote service-specific files when jar with dependencies built)
    public static void main(String[] args) {
        //ServiceConfig serviceConfig = new ServiceConfig();
        ServiceProperties props = get("test-service-1");
        System.out.println("Test service 1 can be found at " + props.grpc_hostname + ":" + props.grpc_port);
        if (! (props.isClientServer() || props.isEmbedded()) )
            throw new IllegalArgumentException(("HZ mode not set or set to illegal value"));
        props = get("test-service-2");
        System.out.println("Test service 2 can be found at " + props.grpc_hostname + ":" + props.grpc_port);
        if (! (props.isClientServer() || props.isEmbedded()) )
            throw new IllegalArgumentException(("HZ mode not set or set to illegal value"));
    }
}
