mvn clean package
ls -l target/
pwd

gcloud builds submit --no-cache --tag southamerica-west1-docker.pkg.dev/springboot-helloworld/springboot-helloworld/springboot-helloworld

gcloud run deploy springboot-helloworld \
  --image southamerica-west1-docker.pkg.dev/springboot-helloworld/springboot-helloworld/springboot-helloworld \
  --platform managed \
  --region southamerica-west1 \
  --allow-unauthenticated \