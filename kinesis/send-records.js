var recordSender = require('./lib/record-sender');
var config = require('./config.json');

recordSender.init(config);

// Send records to kinesis
recordSender.send([
  {
    partitionKey: '1',
    data: 'test record foo'
  },
  {
    partitionKey: '1',
    data: 'test record bar'
  }
], function (err, sentSequenceNumber) {
    if (err) throw err;
    console.log('SEND: ', sentSequenceNumber);
});