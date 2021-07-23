###Microservices Framework Demo

This project is a demo showing a set of microservices using Hazelcast in-memory technology.

The demo includes 4 services:
* Account service (customer account balances)
* Catalog service (price lookups)
* Inventory service
* Order service

The technology stack used includes
* Hazelcast IMDG - In-memory storage
* Hazelcast Jet - Stream processing, computation
* gRPC - service-to-service communication 
* MySQL - database
* Debezium - Change Data Capture for database

The intent is to make this a polyglot demo, featuring Java, C#, Python, Go, and Node.js, but the original prototype is all in Java.  After initial prototype version of services are validated they can be reimplemented in the chosen target language.


