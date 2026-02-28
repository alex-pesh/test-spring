# Ubuntu as the base image
FROM ubuntu:latest

# Install OpenJDK 21 and other dependencies
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk default-jre wget tar

# Download and install Apache Zookeeper
RUN wget https://archive.apache.org/dist/zookeeper/zookeeper-3.6.2/apache-zookeeper-3.6.2-bin.tar.gz && \
    tar -xvf apache-zookeeper-3.6.2-bin.tar.gz && \
    mv apache-zookeeper-3.6.2-bin zookeeper

# Add Zookeeper bin directory to PATH
ENV PATH="/zookeeper/bin:${PATH}"

# Download and install Apache Kafka
RUN wget https://archive.apache.org/dist/kafka/2.6.3/kafka_2.12-2.6.3.tgz && \
    tar -xvzf kafka_2.12-2.6.3.tgz && \
    mv kafka_2.12-2.6.3 kafka

# Download and install Debezium PostgreSQL connector plugin
RUN cd kafka && \
    wget https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/2.5.1.Final/debezium-connector-postgres-2.5.1.Final-plugin.tar.gz && \
    tar -xvf debezium-connector-postgres-2.5.1.Final-plugin.tar.gz && \
    mv debezium-connector-postgres plugins && \
    cd plugins && \
    cp *.jar ../libs/ && \
    cd ..

# Modify connect-standalone.properties to include plugin path
RUN echo "plugin.path=/kafka/plugins" >> kafka/config/connect-standalone.properties

# Copy configuration files
COPY connect-debezium-postgresql.properties kafka/config/connect-debezium-postgresql.properties

# Copy start script
COPY startup.sh /startup.sh

# make startup.sh executable
RUN chmod +x /startup.sh

# Set start script as entrypoint
ENTRYPOINT ["/bin/bash", "/startup.sh"]

# Health check
HEALTHCHECK --interval=10s --timeout=5s \
  CMD netstat -tuln | grep -E '(2181|9092)' && \
      ps aux | grep -v grep | grep -q 'org.apache.zookeeper.server.quorum.QuorumPeerMain' && \
      ps aux | grep -v grep | grep -q 'kafka.Kafka' && \
      ps aux | grep -v grep | grep -q 'io.debezium.connector.postgresql.PostgresConnector'