#!/bin/bash

echo \"> creating queue catalog emit\"

aws sqs create-queue --endpoint-url=http://localhost:4566 \
   --region sa-east-1 \
   --queue-name catalog_emit

aws sqs create-queue --endpoint-url=http://localhost:4566 \
   --region sa-east-1 \
   --queue-name catalog_emit_dlq

aws sqs set-queue-attributes --endpoint-url=http://localhost:4566 \
   --profile localstack \
   --region sa-east-1 \
   --queue-url http://sqs.sa-east-1.localhost.localstack.cloud:4566/000000000000/catalog_emit \
   --attributes '{ "RedrivePolicy": "{\"deadLetterTargetArn\":\"arn:aws:sqs:sa-east-1:000000000000:catalog_emit_dlq\",\"maxReceiveCount\":\"1\"}"}'

aws sqs list-queues --endpoint-url=http://localhost:4566 --profile localstack --region sa-east-1
