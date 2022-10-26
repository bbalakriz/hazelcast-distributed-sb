## Deploy this app to Openshift

```
oc new-project hazelcast-distributed
```

## Install hazelcast
Install the Hazelcast Platform Operator (community version) in the namespace `hazelcast-distributed` from the openshift webconsole or by using `apply -f https://repository.hazelcast.com/operator/bundle-latest.yaml`

Install Hazelcast resource

```
cat << EOF | oc apply -f -
apiVersion: hazelcast.com/v1alpha1
kind: Hazelcast
metadata:
  name: hz-hazelcast
EOF
```

```
oc new-build https://github.com/bbalakriz/hazelcast-distributed-sb.git --dockerfile='FROM maven:3.6.3-openjdk-11 as builder
WORKDIR /project
COPY . /project/
RUN mvn package -DskipTests -B

FROM adoptopenjdk:11-jre-hotspot
COPY --from=builder /project/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]'

oc apply -f https://raw.githubusercontent.com/bbalakriz/hazelcast-distributed-sb/master/deploy/deployment.yaml

```

## Test the app

The pod logs should have the following entries:

```
Members [3] {
Member [10.128.2.17]:5701 - 2f707d6c-4003-4126-a01c-eb48527210c2
Member [10.128.2.18]:5701 - 3f2c3058-3935-4f70-8419-cfde7402a2d8
Member [10.131.0.42]:5701 - 1bee3c23-15fc-4013-8963-d276a1be47fb
}
```

Also, POST some data by the curl command. 
```
curl -v -X POST https://hazelcast-distributed-sb-hazelcast-distributed.apps.my-rosa-cluster.dzk8.p1.openshiftapps.com/put?key=7889&value=best 
```

Now log into each pod terminal and do a get. It should return the same value irrespective of the pod. 

```
curl -v  http://localhost:8080/get?key=7889
```

Reference: https://hazelcast.com/blog/how-to-use-distributed-hazelcast-on-kubernetes/
