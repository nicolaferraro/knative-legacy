
// Run this with: 
// kamel run telegram.groovy -t knative-service.autoscaling-target=1 -p token=<the-token> -p chatId=<the-chat-id>

from('knative:channel/out')
  .convertBodyTo(String.class)
  .log('Received ${body}')
  .delay(3000)
  .log('Sending ${body} to Telegram')
  .to('telegram:bots/{{token}}?chatId={{chatId}}')
