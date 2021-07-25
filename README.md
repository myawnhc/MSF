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

# Next Steps

Proposing this as a candidate for the Platform 5.0 Hackathon; for purposes of that event here is a summary of work that could be done to help build out the demo.   Which of these tasks are done is primarily a function of who joins the team.

## TODO: Messaging Improvements

Currently, event notifications between services is done via MapListeners; doesn't work well because by the time a service registers as a listener, it may have already missed events it needs to see.  Proposal: Have a single MapListener created when the service starts, it will take events and publish them to one or more Reliable Topics.  Subscribers will subscribe to the topics of interest rather than register as Map listeners.  Each event type will get a dedicated Topic, but it's possible there may be aggregated topics as well (all Order events, for example).

## TODO: Polyglot

Prototypes have been written for all services in Java.  Propose to keep the Order service in Java, but reimplement Catalog, Account, and Inventory services in C#/.NET, Python, and Go (in any combination).  

The Inventory service is using Jet pipelines for business logic; when moving to other languages it's not clear what design to use - if possible to do SQL based Jet pipelines we should do that.  Other possibilities are a fairly generic Jet pipeline that calls out to gRPC implementations of Business Logic, Event Store Update, and View Update, or just implement natively in the chosen language. 

## TODO: Complete CDC implementation

Prototype Change Data Capture pipeline has been added to the Inventory project but it just logs changes.  Propose: Add a 'last changed by' field to all tables to allow services to filter out changes that originated from the service.  (Currently, only the Inventory service touches the database so all writes are by that service).  Implement a price change service in either the Catalog or Inventory service and update the in-memory view of the other service using the CDC events.

## TODO: Turbine demonstration

There are several potential failure points; most notably, inventory may not be available for an order, or customer payment may fail.  (The pipelines to handle these steps have not yet been written but will be very similar to the check credit and reserve inventory steps that are already done).  Sagas should be used to trigger a compensating transaction in the event of a failure of either of these steps.  This can be done without Turbine but if we are ready to have a public Turbine demos published, this could be one.

## TODO: User interface

There is no user interface other than watching log messages scroll by.  It would be nice to have a graphical interface suitable for demo at trade shows, etc.  Some ideas:
* Show a set of 10 or 20 orders, 1 order per line; each time an event is posted, update a 'current status' column for the order so you can see orders progress from 'created' to 'shipped'.  As orders ship, newly created orders can be added to the list,.

* Show CDC operation - have a GUI for the price change operation.
  ** User enters a item number, we query both the catalog and inventory service to show the item records.
  ** The GUI also subscribes to the item changed event in whichever service isn't doing the price change.
  ** User hits enter, we display a timestamp.
  ** When we get change event on the other service, show the change and timestamp / elapsed time. 
  
* GUI should use HIVE components to match Management Center look and feel.

* Would be nice if we build some reusable graphical components here - something that could be configured with IMap config info and a SQL query, for example, and display the query results as a table or list, possibly with option for continuous query / updates at some interval.

