FROM hazelcast/docker-grafana-graphite:latest

# Dashboards included in base image not relevant for us
RUN rm /src/dashboards/flight-telemetry.json
RUN rm /src/dashboards/Jet.json

COPY target/classes/order-dashboard.json      /src/dashboards

