# insight-paymo

A simple graph based fraud detector.

Reads in an initial "batch" csv in order to load historical transactions. 

Then reads in a "streaming" transactions csv that determines if 
each transaction is potentially fraudulent based on various features.

Writes the result of each feature application for every transaction to a corresponding txt file.

Adds the transaction after getting the results of each feature to the in memory graph.

## Requirements
- java 8
- maven 3

## Setup
- clone repo
- `cd insight-paymo`
- `mvn package`

## Run
- `./run.sh`

## Run Tests
- `cd insight_testsuite`
- `./run_tests.sh`
