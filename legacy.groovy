
from('file:/tmp/legacy')
  .convertBodyTo(String.class)
  .log('Forwarding ${body}')
  .to('knative:channel/in')
