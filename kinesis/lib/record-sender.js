var _ = require('lodash');
var AWS = require('aws-sdk');

var kinesis, streamName;

module.exports.init = function (config) {
  kinesis = new AWS.Kinesis({ endpoint: config.kinesisEndpoint, accessKeyId: config.accessKeyId, secretAccessKey: config.secretAccessKey, region: config.region });
  streamName = config.StreamName;
};

module.exports.send = function (records, done) {
  var params = {
    Records: _.map(records, (record) => {
      return {
        Data: JSON.stringify(record.data),
        PartitionKey: record.partitionKey
      };
    }),
    StreamName: streamName
  };

  kinesis.putRecords(params, function (err, data) {
    if (err) { return done(err); }
    if (data.FailedRecordCount > 0) {
      return done(new Error('Failed to send all records to Kinesis'));
    }
    var lastSequenceNumber = data.Records[data.Records.length - 1].SequenceNumber;
    return done(undefined, lastSequenceNumber);
  });
};