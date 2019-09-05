
from('knative:channel/in')
  .convertBodyTo(String.class)
  .log('Received: ${body}')
  .filter().simple('${body} startsWith \'exec: \'')
  .split().tokenize('\\s')
    .filter().simple('${body} != \'exec:\'')
    .log('Sending to channel out: ${body}')
    .to('knative:channel/out')
