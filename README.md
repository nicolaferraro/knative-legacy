# Camel K and Knative: connecting your legacy system

This demo shows how to connect a legacy system to a Knative serverless environment using Camel K.

## Scenario

The first integration **legacy.groovy** will connect a simulated legacy system that publishes files into the `/tmp/legacy` directory.
This integration can be really connected to an existing system using a shared volume, or you can replace the file endpoint with a FTP endpoint easily to make it more realistic.

The *legacy.groovy* integration will push the content of each file into a Knative channel named `in`.

An integration named *process.groovy* will be subscribed to the `in` channel and do content-based routing:
- Messages starting with "exec: " will be split and each word will be sent to the `out` channel
- All other messages will be sent without change to the `dev-null` channel

A *telegram.groovy* integration will be subscribed to the `out` channel and will send all messages to a specific Telegram chat.

A quarkus-native integration *quarkus.yaml* will listen from the `dev-null` channel and simply print the message.
What it's worth noting from this integration is how long it takes to start (**hint: few milliseconds!**), making it suitable for
reactive serverless applications.

## Running the integration

Requirements:
- Kubernetes or Openshift (`oc` or `kubectl` tool needed locally)
- Camel K 1.0.0-M1 operator installed (Camel K client tools `kamel` needed locally)
- Knative Serving 0.7.1 controller installed
- Knative Eventing 0.7.1 controller

First create the channels:

```
kubectl apply -f channels.yaml
```

Start the legacy integration:
```
kamel run legacy.groovy
```

Start the process integration:
```
kamel run process.groovy
```

To start the quarkus integration, first create an integration kit from a pre-existing image with some binary compiled libs:
```
kamel kit create quarkus --image=lburgazzoli/camel-k-quarkus:1.0.2-SNAPSHOT
```

Then start the quarkus integration using the quarkus kit:
```
kamel run quarkus.yaml --kit quarkus
```

To start the telegram integration, first create the secret:

```
cp application.properties.example application.properties
# edit the application.properties to provide a Telegram token and a recipient chat id (ask the @botfather bot on Telegram to provide a bot)
kubectl create secret generic --from-file=application.properties camel-secrets
```

Then start the telegram integration with the given secret:
```
kamel run telegram.groovy --secret=camel-secrets
```

## Playing with the demo

To play with the demo, open a terminal on the legacy pod and start writing files into the polling directory, simulating what a legacy system wil do:

```
# inside the legacy pod
echo "exec: message" > /tmp/legacy/file
```

The file will be sent for processing into the first channel and then into the others (file will be automatically deleted when processed).

Pods that will need to process the message will be scaled up (also from zero) automatically.
