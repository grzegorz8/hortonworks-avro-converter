FROM openjdk:8-jre

ARG VERSION=0.5.3

WORKDIR /opt/schema-registry

RUN set -ex && \
  apt-get update && \
  apt-get -y install procps && \
  rm -rf /var/lib/apt/lists/*

RUN set -ex && \
  wget https://github.com/hortonworks/registry/releases/download/$VERSION/hortonworks-registry-${VERSION}.tar.gz && \
  tar -xf hortonworks-registry-${VERSION}.tar.gz --strip-components=1 && \
  rm hortonworks-registry-${VERSION}.tar.gz

COPY registry.yaml /opt/schema-registry/conf/

CMD ["bin/registry-server-start.sh", "conf/registry.yaml"]

EXPOSE 9090