#!/bin/bash

# Project configuration
PROJECT_ID="dryruns"
IMAGE_NAME="springboot-helloworld"
GCR_REPO="southamerica-west1-docker.pkg.dev/${PROJECT_ID}/${IMAGE_NAME}"
GKE_CLUSTER_NAME="autopilot-cluster-1"
REGION="southamerica-west1"


# Build the Spring Boot JAR
mvn clean package

# Navigate to the target directory
cd target

# Build the Docker image
docker build -t ${GCR_REPO}/${IMAGE_NAME}:latest .

# Push the image to Google Container Registry
docker push ${GCR_REPO}/${IMAGE_NAME}:latest

# Configure kubectl to connect to your GKE cluster
gcloud container clusters get-credentials ${GKE_CLUSTER_NAME} --region ${REGION} --project ${PROJECT}


# Deploy to GKE
kubectl apply -f ./kubernetes/deploy.yaml