mvn clean package

cd target
gcloud builds submit . --tag southamerica-west1-docker.pkg.dev/springboot-helloworld/springboot-helloworld/springboot-helloworld

gcloud run deploy springboot-helloworld \
  --image southamerica-west1-docker.pkg.dev/springboot-helloworld/springboot-helloworld/springboot-helloworld \
  --platform managed \
  --region southamerica-west1 \
  --allow-unauthenticated \