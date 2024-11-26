#!/bin/bash

for i in {1..10000}; do
  curl http://localhost:30081/quick_service/customers
  sleep 0.0001
done