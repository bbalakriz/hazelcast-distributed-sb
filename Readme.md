## Deploy this app to Openshift

```
oc new-project hazelcast-embedded

oc new-build https://github.com/bbalakriz/hazelcast-embedded-sb.git --dockerfile='FROM maven:3.6.3-openjdk-11 as builder
WORKDIR /project
COPY . /project/
RUN mvn package -DskipTests -B

FROM adoptopenjdk:11-jre-hotspot
COPY --from=builder /project/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]'

oc apply -f https://raw.githubusercontent.com/bbalakriz/hazelcast-embedded-sb/master/deploy/deployment.yaml

# Note that the service and namespace names referenced in resources/hazelcast.yaml should match the corresponding resource names defined in deploy/deployment.yaml.
```

## Test the app

The pod logs should have the following entries:

```
Members {size:2, ver:2} [
Member [10.131.0.49]:5701 - 56569450-8c15-4e93-a111-d1f57733c0a2
Member [10.128.2.105]:5701 - 359b8e34-8ecc-4a50-bbb9-b6d738cd748d this
]
```

Also, POST some data by the curl command. 
```
curl -v -X POST https://hazelcast-embedded-sb-hazelcast-embedded.apps.my-rosa-cluster.dzk8.p1.openshiftapps.com/put?key=7889&value=best 
```

Now log into each pod terminal and do a get. It should return the same value irrespective of the pod. 

```
curl -v  http://localhost:8080/get?key=7889
```

Reference: https://hazelcast.com/blog/how-to-use-embedded-hazelcast-on-kubernetes/
