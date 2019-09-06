
// Run this with: 
// oc create secret generic --from-file=application.properties camel-secrets
// kamel run telegram.groovy --secret=camel-secrets

// To check autoscaling, try adding the delay and rerun telegram with this settings:
// kamel run telegram.groovy --secret=camel-secrets -t knative-service.autoscaling-target=1 

from('knative:channel/out')
  .convertBodyTo(String.class)
  .log('Received ${body}')
  //.delay(3000)
  .log('Sending ${body} to Telegram')
  .to('telegram:bots/{{token}}?chatId={{chatId}}')
