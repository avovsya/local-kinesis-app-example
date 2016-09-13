var kinesalite = require('kinesalite');
var kinesaliteServer = kinesalite({ createStreamMs: 0 });
var AWS = require('aws-sdk')
var config = require('./config.json');

// Listen on port 4567
kinesaliteServer.listen(config.kinesisPort, function(err) {
    if (err) throw err;
    console.log('Kinesalite listens on port ' + config.kinesisPort);

    console.log('Creating stream: ', config.StreamName);

    var kinesis = new AWS.Kinesis({ endpoint: config.kinesisEndpoint, accessKeyId: config.accessKeyId, secretAccessKey: config.secretAccessKey, region: config.region });

    AWS.config.update({});

    kinesis.createStream({ StreamName: config.StreamName, ShardCount: config.ShardCount }, function (err) {
        if (err) throw err;

        kinesis.describeStream({ StreamName: config.StreamName }, function(err, data) {
         if (err) throw err;

         console.log('Stream created: ', data);
         console.dir(data)
         console.log('READY!');
        });
    });
})