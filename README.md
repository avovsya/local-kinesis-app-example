
Example that uses Kinesalite and Dynalite to allow local development of Kinesis/KCL consumer applications

## Prerequisites

1. SBT
2. Node.js + NPM

## How to run

1. Clone repo - `git clone https://github.com/avovsya/local-kinesis-app-example local-kinesis-app && cd local-kinesis-app`
2. Install Kinesalite & Dynolite - `(cd kinesis ; npm install)`
3. Run Kinesalite in background - `(cd kinesis ; npm start&)`
4. Run app - `sbt run`
5. Send some data to local Kinesis - `(cd kinesis ; node send-records.js)`

