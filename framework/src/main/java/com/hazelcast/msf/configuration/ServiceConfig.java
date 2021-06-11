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
        public String hostname;
        public String port; // actually numeric
        // additional fields will be needed for cloud deployments
        // List of API details can go here but not needed for first prototype

        // Format used by gRPC ManagedChannelBuilder.forTarget()
        public String getTarget() {
            return hostname + ":" + port;
        }

        public int getPort() {
            return Integer.parseInt(port);
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
            System.out.println("Reading config info from " + yamlFile.toExternalForm());
            configInfo = mapper.readValues(parser, ServiceProperties.class);
            while (configInfo.hasNext()) {
                ServiceProperties sp = configInfo.next();
                configurations.put(sp.service_name, sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // For testing use only
    public static void main(String[] args) {
        ServiceConfig serviceConfig = new ServiceConfig();
        ServiceProperties props = serviceConfig.get("test-service-1");
        System.out.println("Test service 1 can be found at " + props.hostname + ":" + props.port);
        props = serviceConfig.get("test-service-2");
        System.out.println("Test service 2 can be found at " + props.hostname + ":" + props.port);
    }
}