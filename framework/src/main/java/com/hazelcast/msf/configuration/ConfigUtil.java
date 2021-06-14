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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.impl.spi.impl.discovery.HazelcastCloudDiscovery;
import com.hazelcast.client.properties.ClientProperty;

import java.net.URL;
import java.util.Map;
@Deprecated
public class ConfigUtil {

    // Configuration names (as found in properties.yaml configuration)
    public static final String EMBEDDED = "embedded";
    public static final String ON_PREM_CLUSTER = "on-premise";
    public static final String CLOUD_STARTER = "cloud-starter";
    public static final String CLOUD_ENTERPRISE = "cloud-enterprise";
    public static final String AWS = "aws";
    public static final String GCP = "gcp";
    public static final String AZURE = "azure";
    public static final String OPENSHIFT = "openshift";

    // Fields within the properties file
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String DISCOVERY_TOKEN = "discovery-token";
    public static final String URL_BASE = "url-base";
    // TODO: SSL-related config fields
    // TODO: any fields related to non-managed service cloud discovery

    private static Map<String, Map> configs;
    private static String defaultClusterName;

    static class ConfigInfo {
        String defaultConfig;
        Map<String, Map> clusterConfiguration;
        public ConfigInfo() {}
        public void setDefaultConfig(String config) { this.defaultConfig = config; }
        public void setClusterConfiguration(Map<String, Map> configs) {
            this.clusterConfiguration = configs;
        }
    }

    static {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        //Map<String, Map<String, Map>> properties = null;
        ConfigInfo configInfo;
        try {
            URL yamlFile = ConfigUtil.class.getClassLoader().getResource("properties.yaml");
            System.out.println("Reading config info from " + yamlFile.toExternalForm());
            configInfo = mapper.readValue(yamlFile, ConfigInfo.class);
            defaultClusterName = configInfo.defaultConfig;
            configs = configInfo.clusterConfiguration;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MSFConfig getEmbeddedConfig() {
        Map<String,String> onprem = configs.get(EMBEDDED);
        MSFConfig cc = new MSFConfig(onprem.get(NAME), onprem.get(PASSWORD),
                null,null);
        return cc;
    }

    public static MSFConfig getOnPremiseConfig() {
        Map<String,String> onprem = configs.get(ON_PREM_CLUSTER);
        MSFConfig cc = new MSFConfig(onprem.get(NAME), onprem.get(PASSWORD),
                null,null);
        return cc;
    }

    public static MSFConfig getHZCloudStarterConfig() {
        Map<String,String> personal = configs.get(CLOUD_STARTER);
        MSFConfig cc = new MSFConfig(personal.get(NAME), personal.get(PASSWORD),
                personal.get(DISCOVERY_TOKEN), personal.get(URL_BASE));
        return cc;
    }

    public static MSFConfig getHZCloudEnterpriseConfig() {
        Map<String,String> training = configs.get(CLOUD_ENTERPRISE);
        MSFConfig cc = new MSFConfig(training.get(NAME), training.get(PASSWORD),
                training.get(DISCOVERY_TOKEN), training.get(URL_BASE));
        return cc;
    }

    public static MSFConfig getAWSConfig() {
        Map<String,String> enterprise = configs.get(AWS);
        MSFConfig cc = new MSFConfig(enterprise.get(NAME), enterprise.get(PASSWORD),
        null, null);
        return cc;
    }

    public static String getDefaultConfigName() { return defaultClusterName; }
    public static ClientConfig getDefaultConfig() {
        return getClientConfigForCluster(defaultClusterName);
    }

    public static ClientConfig getClientConfigForCluster(String configname) {
        if (configname == null) {
            System.out.println("No command line argument for cluster, properties.yaml default is " + defaultClusterName);
//            switch (defaultClusterName) {
//                case "on-prem-cluster": configname = "onprem"; break;
//                case "personal-cluster": configname = "personal"; break;
//                case "shared-cluster": configname = "shared"; break;
//                case "enterprise-cluster": configname = "enterprise"; break;
//                default: configname = "onprem";
//            }
            configname = defaultClusterName;
        }
        System.out.println("Looking up config for " + configname);
        MSFConfig cloudConfig;
        switch (configname) {
            case "embedded": cloudConfig = getEmbeddedConfig(); break;
            case "onprem": cloudConfig = getOnPremiseConfig(); break;
            case "cloud-starter": cloudConfig = getHZCloudStarterConfig(); break;
            case "cloud-enterprise": cloudConfig = getHZCloudEnterpriseConfig(); break;
            case "aws" : cloudConfig = getAWSConfig(); break;
            // TODO: Azure, GCP, Openshift
            default:
                throw new IllegalArgumentException(("Bad cluster config name: " + configname));
        }

        // Note that for embedded we need a Config, not a ClientConfig.
        ClientConfig config = new ClientConfig();
        config.setClusterName(cloudConfig.name);

        config.setProperty("hazelcast.client.statistics.enabled", "true");
        if (cloudConfig.discoveryToken != null)
            config.setProperty(ClientProperty.HAZELCAST_CLOUD_DISCOVERY_TOKEN.getName(), cloudConfig.discoveryToken);
        if (cloudConfig.urlBase != null)
            config.setProperty(HazelcastCloudDiscovery.CLOUD_URL_BASE_PROPERTY.getName(), cloudConfig.urlBase);

        return config;
    }

    // TODO: MSF values will be different
    //  Minimum: embedded, onprem, hzc-starter, hzc-enterprise
    //  maybe some CSP-specific but autodiscovery might take care of us.
    public static String findConfigNameInArgs(String[] args) {
        for (String arg : args) {
            if (arg.equals("-embedded")) return "embedded";
            if (arg.equals("-onprem")) return "on-premise";
            if (arg.equals("-hzcs")) return "cloud-starter";
            if (arg.equals("-hzce")) return "cloud-enterprise";
            if (arg.equals("-aws")) return "aws";
            if (arg.equals("-gcp")) return "gcp";
            if (arg.equals("-azure")) return "azure";
            if (arg.equals("-openshift")) return "openshift";

        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println("Default config is " + defaultClusterName);
    }
}
