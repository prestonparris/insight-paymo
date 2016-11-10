#!/usr/bin/env bash

java -cp ./target/paymo-1.0-SNAPSHOT.jar com.prestonparris.paymo.AntiFraud ./paymo_input/batch_payment.txt ./paymo_input/stream_payment.txt ./paymo_output/output1.txt ./paymo_output/output2.txt ./paymo_output/output3.txt
