

Role Bindings: 

gcloud projects add-iam-policy-binding dryruns --member="serviceAccount:551624959543-compute@developer.gserviceaccount.com" --role="roles/artifactregistry.reader"

gcloud projects add-iam-policy-binding dryruns --member="serviceAccount:551624959543-compute@developer.gserviceaccount.com" --role="roles/logging.logWriter"



--------


Chaos Testing setup: 

1. Install Chaos-Mesh Armory
```shell
dalquint@cloudshell:~ (dryruns)$ helm repo add chaos-mesh https://charts.chaos-mesh.org
helm repo update
kubectl create namespace chaos-testing
helm install chaos-mesh chaos-mesh/chaos-mesh \
  -n chaos-testing --create-namespace \
  --set dashboard.create=true \
  --set chaosDaemon.runtime=containerd \
  --set chaosDaemon.socketPath=/run/containerd/containerd.sock
NAME: chaos-mesh
LAST DEPLOYED: Thu Sep 26 14:21:29 2024
NAMESPACE: chaos-testing
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
1. Make sure chaos-mesh components are running
   kubectl get pods --namespace chaos-testing -l app.kubernetes.io/instance=chaos-mesh
```


2. Check that installation went fine: 

```shell
dalquint@cloudshell:~/DevOps/springboot-helloworld$ kubectl get pods --namespace chaos-testing -l app.kubernetes.io/instance=chaos-mesh
NAME                                        READY   STATUS    RESTARTS   AGE
chaos-controller-manager-5fdb5c6b57-674kx   1/1     Running   0          17m
chaos-controller-manager-5fdb5c6b57-nwzq7   1/1     Running   0          6m57s
chaos-controller-manager-5fdb5c6b57-x4nkc   1/1     Running   0          6m51s
chaos-daemon-5s79n                          1/1     Running   0          7m47s
chaos-daemon-66n9h                          1/1     Running   0          17m
chaos-daemon-6sqbr                          1/1     Running   0          5m3s
chaos-daemon-7hzfb                          1/1     Running   0          7m37s
chaos-daemon-7sr6p                          1/1     Running   0          4m59s
chaos-daemon-9g5sg                          1/1     Running   0          5m1s
chaos-daemon-b5ks6                          1/1     Running   0          17m
chaos-daemon-c9dfz                          1/1     Running   0          17m
chaos-daemon-cldnd                          1/1     Running   0          17m
chaos-daemon-gqqg4                          1/1     Running   0          5m1s
chaos-daemon-gxsbz                          1/1     Running   0          17m
chaos-daemon-kls75                          1/1     Running   0          7m45s
chaos-daemon-lwdxl                          1/1     Running   0          17m
chaos-daemon-n4l4m                          1/1     Running   0          14m
chaos-daemon-nm7bb                          1/1     Running   0          17m
chaos-daemon-nqgl9                          1/1     Running   0          4m57s
chaos-daemon-qkg6t                          1/1     Running   0          4m59s
chaos-daemon-rndsc                          1/1     Running   0          5m2s
chaos-daemon-tdjrx                          1/1     Running   0          17m
chaos-daemon-tnnmp                          1/1     Running   0          5m
chaos-daemon-twkgc                          1/1     Running   0          17m
chaos-daemon-vzv9d                          1/1     Running   0          5m
chaos-daemon-zbxbv                          1/1     Running   0          5m3s
chaos-daemon-zkc9l                          1/1     Running   0          7m44s
chaos-dashboard-7c66c9f685-rn25s            1/1     Running   0          17m
chaos-dns-server-69dd8595bf-wcvmh           1/1     Running   0          3m41s

```

3. Access dashboard: 

```shell
kubectl port-forward -n chaos-testing svc/chaos-dashboard 2333:2333
```

Access should be available in http://localhost:2333

4. Generate RBAC and apply it. See file under ./chaos-engineering/setup/01_rbac.yaml 


```shell
dralquinta-mac:setup dralquinta$ kubectl apply -f rbac.yaml 
serviceaccount/account-default-manager-monkey created
role.rbac.authorization.k8s.io/role-default-manager-monkey created
rolebinding.rbac.authorization.k8s.io/bind-default-manager-monkey created


dralquinta-mac:setup dralquinta$ kubectl create token account-default-manager-monkey
eyJhbGciOiJSUzI1NiIsImtpZCI6Il93S3p6bDN6Wnl3aEdjTUtMTjlpWF9XSVRPcjcwU0lYMVVGQ0pfdXNWZG8ifQ.eyJhdWQiOlsiaHR0cHM6Ly9jb250YWluZXIuZ29vZ2xlYXBpcy5jb20vdjEvcHJvamVjdHMvZHJ5cnVucy9sb2NhdGlvbnMvc291dGhhbWVyaWNhLXdlc3QxLWEvY2x1c3RlcnMvY2x1c3Rlci0xIl0sImV4cCI6MTcyNzM2NzM4OSwiaWF0IjoxNzI3MzYzNzg5LCJpc3MiOiJodHRwczovL2NvbnRhaW5lci5nb29nbGVhcGlzLmNvbS92MS9wcm9qZWN0cy9kcnlydW5zL2xvY2F0aW9ucy9zb3V0aGFtZXJpY2Etd2VzdDEtYS9jbHVzdGVycy9jbHVzdGVyLTEiLCJqdGkiOiI2MDhhMzM3Ny1mZTM4LTQ5MTQtOTQ0NS05OGFmZGNiMDA4YTIiLCJrdWJlcm5ldGVzLmlvIjp7Im5hbWVzcGFjZSI6ImRlZmF1bHQiLCJzZXJ2aWNlYWNjb3VudCI6eyJuYW1lIjoiYWNjb3VudC1kZWZhdWx0LW1hbmFnZXItbW9ua2V5IiwidWlkIjoiOWI0NjQ1M2EtNmM5MC00NjYyLTg5NmUtZmZjMGZiOGE2MjU4In19LCJuYmYiOjE3MjczNjM3ODksInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmFjY291bnQtZGVmYXVsdC1tYW5hZ2VyLW1vbmtleSJ9.A1wrSiDxHyaH6HO2ruWlEHguazV65wqnj1kesS-QJP5s7CTpYUvsYuQUN3PDg0XwHZdeNXWdHZDTEcqKUQFNQwk9rpk5sBBZurwsHQg1vteJ661P7VlLjyGa7wJB0nEwxTObkmVevyaOkD3jzAxTQnJQBzKkXxvrjSwQDB6QiuDtqYvJJ7WHiT8LdgHqU7MRnprXfcZ8mwlRZ5omfuikhu24aLOmi9QG3hDUB3PqGgz-00w8dr29a3g-EhFjED1Nclk4zbM19y4K9l0aJl3aW1FQjpG08MuO_ZeesavFX_5GcuJixDlu07sGdEFDNmDPiykg_mHojXjp17qVNuh0DQ
```



Now apply chaos mesh role under file ./chaos-engineering/setup/02_chaos-mesh-role.yaml

```shell
dralquinta-mac:setup dralquinta$ kubectl apply -f 02_chaos-mesh-role.yaml 
role.rbac.authorization.k8s.io/chaos-mesh-namespace-role created
rolebinding.rbac.authorization.k8s.io/chaos-mesh-rolebinding created
```


5. Create token and describe it: 

```shell
dralquinta-mac:setup dralquinta$ kubectl create token account-default-viewer-fvdyi
eyJhbGciOiJSUzI1NiIsImtpZCI6Il93S3p6bDN6Wnl3aEdjTUtMTjlpWF9XSVRPcjcwU0lYMVVGQ0pfdXNWZG8ifQ.eyJhdWQiOlsiaHR0cHM6Ly9jb250YWluZXIuZ29vZ2xlYXBpcy5jb20vdjEvcHJvamVjdHMvZHJ5cnVucy9sb2NhdGlvbnMvc291dGhhbWVyaWNhLXdlc3QxLWEvY2x1c3RlcnMvY2x1c3Rlci0xIl0sImV4cCI6MTcyNzM2NjY3NCwiaWF0IjoxNzI3MzYzMDc0LCJpc3MiOiJodHRwczovL2NvbnRhaW5lci5nb29nbGVhcGlzLmNvbS92MS9wcm9qZWN0cy9kcnlydW5zL2xvY2F0aW9ucy9zb3V0aGFtZXJpY2Etd2VzdDEtYS9jbHVzdGVycy9jbHVzdGVyLTEiLCJqdGkiOiIyM2ZjZWQwNy00Mjk0LTQ1ZjAtYTE1Yy0xN2Y1YWIzNWE0MWUiLCJrdWJlcm5ldGVzLmlvIjp7Im5hbWVzcGFjZSI6ImRlZmF1bHQiLCJzZXJ2aWNlYWNjb3VudCI6eyJuYW1lIjoiYWNjb3VudC1kZWZhdWx0LXZpZXdlci1mdmR5aSIsInVpZCI6IjBmNzE2YzYwLTFlODctNDRhYS1iNzk4LTc4YTRlMjZlMGQ0YiJ9fSwibmJmIjoxNzI3MzYzMDc0LCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6ZGVmYXVsdDphY2NvdW50LWRlZmF1bHQtdmlld2VyLWZ2ZHlpIn0.DEwhfHjD2R4Gmt8JCt-sqhrF1nMVo_xxG2bAEjCvj_yMGOM9rnfOaDenszW1sAIk474W8MUI6S3yJjDB_Wnlu-C6BxKDVP3Qou1HQwfMRVhfCiZp0RL2fyhJZF6ipth5G0TgxZkqj3zJJTZZfc04o0G77yWPgpurpLSOH6Ze8cKZ53ZA3K5nAViG94Lt-PpAe4h2SB-Lm6uVJikBvi0Z2rOEVHFCDlgujeqey9PGrs5ajG3HXxeGHDNiSoql3mB5NsrJO1MfejBSRJQGlWUghDhGMWndfMPaIZseig_gwvEC99oMCkwcbHWRJ2ciH6ccG9rc9CcS-W4WLQ9hgWPFtg
```

Describe it in case you lose it: 



5. Craft experiments in dashboard. 



----


Enable Istio

1. run: 

```shell
helm repo add istio https://istio-release.storage.googleapis.com/charts
"istio" has been added to your repositories

kubectl create namespace istio-system
namespace/istio-system created

helm install istio-base istio/base -n istio-system
NAME: istio-base
LAST DEPLOYED: Fri Sep 27 13:30:10 2024
NAMESPACE: istio-system
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
Istio base successfully installed!

To learn more about the release, try:
  $ helm status istio-base -n istio-system
  $ helm get all istio-base -n istio-system

helm install istiod istio/istiod -n istio-system --wait
NAME: istiod
LAST DEPLOYED: Fri Sep 27 13:30:34 2024
NAMESPACE: istio-system
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
"istiod" successfully installed!

To learn more about the release, try:
  $ helm status istiod -n istio-system
  $ helm get all istiod -n istio-system

Next steps:
  * Deploy a Gateway: https://istio.io/latest/docs/setup/additional-setup/gateway/
  * Try out our tasks to get started on common configurations:
    * https://istio.io/latest/docs/tasks/traffic-management
    * https://istio.io/latest/docs/tasks/security/
    * https://istio.io/latest/docs/tasks/policy-enforcement/
  * Review the list of actively supported releases, CVE publications and our hardening guide:
    * https://istio.io/latest/docs/releases/supported-releases/
    * https://istio.io/latest/news/security/
    * https://istio.io/latest/docs/ops/best-practices/security/

For further documentation see https://istio.io website
```

2. Label namespace to do sidecar injection: 

```shell
# Label the namespace to enable Istio sidecar injection
kubectl label namespace default istio-injection=enabled
namespace/default labeled
```

3. Install OpenTelemetry

```shell
kubectl apply -f https://github.com/open-telemetry/opentelemetry-operator/releases/download/v0.34.0/opentelemetry-operator.yaml
namespace/opentelemetry-operator-system created
customresourcedefinition.apiextensions.k8s.io/opentelemetrycollectors.opentelemetry.io created
serviceaccount/opentelemetry-operator-controller-manager created
role.rbac.authorization.k8s.io/opentelemetry-operator-leader-election-role created
clusterrole.rbac.authorization.k8s.io/opentelemetry-operator-manager-role created
clusterrole.rbac.authorization.k8s.io/opentelemetry-operator-metrics-reader created
clusterrole.rbac.authorization.k8s.io/opentelemetry-operator-proxy-role created
rolebinding.rbac.authorization.k8s.io/opentelemetry-operator-leader-election-rolebinding created
clusterrolebinding.rbac.authorization.k8s.io/opentelemetry-operator-manager-rolebinding created
clusterrolebinding.rbac.authorization.k8s.io/opentelemetry-operator-proxy-rolebinding created
service/opentelemetry-operator-controller-manager-metrics-service created
service/opentelemetry-operator-webhook-service created
deployment.apps/opentelemetry-operator-controller-manager created
mutatingwebhookconfiguration.admissionregistration.k8s.io/opentelemetry-operator-mutating-webhook-configuration created
validatingwebhookconfiguration.admissionregistration.k8s.io/opentelemetry-operator-validating-webhook-configuration created
resource mapping not found for name: "opentelemetry-operator-serving-cert" namespace: "opentelemetry-operator-system" from "https://github.com/open-telemetry/opentelemetry-operator/releases/download/v0.34.0/opentelemetry-operator.yaml": no matches for kind "Certificate" in version "cert-manager.io/v1alpha2"
ensure CRDs are installed first
resource mapping not found for name: "opentelemetry-operator-selfsigned-issuer" namespace: "opentelemetry-operator-system" from "https://github.com/open-telemetry/opentelemetry-operator/releases/download/v0.34.0/opentelemetry-operator.yaml": no matches for kind "Issuer" in version "cert-manager.io/v1alpha2"
ensure CRDs are installed first
```