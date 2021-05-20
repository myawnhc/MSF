package com.hazelcast.msf.configuration;

// Most fields based on using Hazelcast Cloud
// Just cluster name is enough to connect to on-premise
// Embedded added but not yet implemented
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
