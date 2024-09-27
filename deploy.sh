#!/bin/bash

# Project configuration
PROJECT_ID="dryruns"
IMAGE_NAME="springboot-helloworld"
GCR_REPO="southamerica-west1-docker.pkg.dev/${PROJECT_ID}/${IMAGE_NAME}"
GKE_CLUSTER_NAME="cluster-1"
REGION="southamerica-west1-a"


# Build the Spring Boot JAR
mvn clean package

# Build the Docker image
docker build -t ${GCR_REPO}/${IMAGE_NAME}:latest .

# Push the image to Google Container Registry
docker push ${GCR_REPO}/${IMAGE_NAME}:latest

# Configure kubectl to connect to your GKE cluster
gcloud container clusters get-credentials ${GKE_CLUSTER_NAME} --region ${REGION} --project ${PROJECT_ID}



# Deploy to GKE
kubectl apply -f ./kubernetes/degration-config-map.yaml
kubectl apply -f ./kubernetes/deploy.yaml