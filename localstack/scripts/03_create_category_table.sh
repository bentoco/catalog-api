#!/bin/bash

file=/opt/scripts/resources/category_table.json

echo \"> creating table $file\"

aws dynamodb create-table --endpoint-url=http://localhost:4566 \
   --profile localstack \
   --region sa-east-1 \
   --cli-input-json file://$file

aws dynamodb list-tables --endpoint-url=http://localhost:4566 --profile localstack --region sa-east-1
