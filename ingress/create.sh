#!/usr/bin/env bash
kubectl apply -f ns.yaml
kubectl apply -f ingress.yaml -n ingress-basic