#!/usr/bin/env bash

java -cp ./target/paymo-1.0-SNAPSHOT.jar com.prestonparris.paymo.AntiFraud ./paymo_input/batch_payment.csv ./paymo_input/stream_payment.csv ./paymo_output/output1.txt ./paymo_output/output2.txt ./paymo_output/output3.txt

#java -cp ./target/paymo-1.0-SNAPSHOT.jar com.prestonparris.paymo.AntiFraud ./paymo_input/degrees-of-seperation-test.csv ./paymo_input/degrees-of-seperation-stream.csv ./paymo_output/output1.txt ./paymo_output/output2.txt ./paymo_output/output3.txt
