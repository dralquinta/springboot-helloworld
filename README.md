

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
helm install chaos-mesh chaos-mesh/chaos-mesh -n chaos-testing --set dashboard.create=true --set chaosDaemon.runtime=containerd --set chaosDaemon.socketPath=/run/containerd/containerd.sock
"chaos-mesh" has been added to your repositories
Hang tight while we grab the latest from your chart repositories...
...Successfully got an update from the "chaos-mesh" chart repository
...Successfully got an update from the "ingres-nginx" chart repository
...Successfully got an update from the "ingress-nginx" chart repository
Update Complete. ⎈Happy Helming!⎈
namespace/chaos-testing created
W0926 12:16:44.690369    3720 warnings.go:70] autopilot-default-resources-mutator:Autopilot updated DaemonSet chaos-testing/chaos-daemon: defaulted unspecified 'cpu' resource for containers [chaos-daemon] (see http://g.co/gke/autopilot-defaults).
W0926 12:16:44.901237    3720 warnings.go:70] autopilot-default-resources-mutator:Autopilot updated Deployment chaos-testing/chaos-controller-manager: adjusted 'cpu' resource to meet requirements for containers [chaos-mesh] (see http://g.co/gke/autopilot-defaults).
W0926 12:16:44.915881    3720 warnings.go:70] autopilot-default-resources-mutator:Autopilot updated Deployment chaos-testing/chaos-dashboard: adjusted 'cpu' resource to meet requirements for containers [chaos-dashboard] (see http://g.co/gke/autopilot-defaults).
Error: INSTALLATION FAILED: admission webhook "warden-validating.common-webhooks.networking.gke.io" denied the request: GKE Warden rejected the request because it violates one or more constraints.
Violations details: {"[denied by autogke-disallow-hostnamespaces]":["enabling hostPID is not allowed in Autopilot."],"[denied by autogke-disallow-privilege]":["container chaos-daemon is privileged; not allowed in Autopilot"],"[denied by autogke-no-write-mode-hostpath]":["hostPath volume socket-path in container chaos-daemon is accessed in write mode; disallowed in Autopilot.","hostPath volume sys-path in container chaos-daemon is accessed in write mode; disallowed in Autopilot.","hostPath volume lib-modules in container chaos-daemon is accessed in write mode; disallowed in Autopilot."]}
Requested by user: 'dalquint@dralquinta.altostrat.com', groups: 'system:authenticated'.
```


2. Check that installation went fine: 

```shell
dalquint@cloudshell:~ (dryruns)$ kubectl get pods -n chaos-testing
NAME                                        READY   STATUS    RESTARTS   AGE
chaos-controller-manager-76b9bf6d49-kf6gn   1/1     Running   0          101s
chaos-controller-manager-76b9bf6d49-pn59z   1/1     Running   0          101s
chaos-controller-manager-76b9bf6d49-r65z2   1/1     Running   0          101s
chaos-dashboard-7c4f46c965-lfjq4            1/1     Running   0          101s
chaos-dns-server-778bc979c7-zhns8           1/1     Running   0          101s

```

3. Access dashboard: 

```shell
kubectl port-forward -n chaos-testing svc/chaos-dashboard 2333:2333
```

Access should be available in http://localhost:2333

4. Generate RBAC and apply it: 

```yaml
kind: ServiceAccount
apiVersion: v1
metadata:
  namespace: default
  name: account-default-viewer-fvdyi

---
kind: Role
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  namespace: default
  name: role-default-viewer-fvdyi
rules:
- apiGroups: [""]
  resources: ["pods", "namespaces"]
  verbs: ["get", "watch", "list"]
- apiGroups: ["chaos-mesh.org"]
  resources: [ "*" ]
  verbs: ["get", "list", "watch"]

---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: bind-default-viewer-fvdyi
  namespace: default
subjects:
- kind: ServiceAccount
  name: account-default-viewer-fvdyi
  namespace: default
roleRef:
  kind: Role
  name: role-default-viewer-fvdyi
  apiGroup: rbac.authorization.k8s.io

```


```shell
dralquinta-mac:chaos-engineering dralquinta$ kubectl apply -f rbac.yaml 
serviceaccount/account-default-viewer-fvdyi created
role.rbac.authorization.k8s.io/role-default-viewer-fvdyi created
rolebinding.rbac.authorization.k8s.io/bind-default-viewer-fvdyi created
```

```shell
dralquinta-mac:chaos-engineering dralquinta$ kubectl create token account-default-viewer-fvdyi
eyJhbGciOiJSUzI1NiIsImtpZCI6IkZUZmJQRUIwdmlVUDl3UjlEak9sZ29VSjRXSnBFdk5vUDh6QlpXNmhvXzgifQ.eyJhdWQiOlsiaHR0cHM6Ly9jb250YWluZXIuZ29vZ2xlYXBpcy5jb20vdjEvcHJvamVjdHMvZHJ5cnVucy9sb2NhdGlvbnMvc291dGhhbWVyaWNhLXdlc3QxL2NsdXN0ZXJzL2F1dG9waWxvdC1jbHVzdGVyLTEiXSwiZXhwIjoxNzI3MzU3MjgxLCJpYXQiOjE3MjczNTM2ODEsImlzcyI6Imh0dHBzOi8vY29udGFpbmVyLmdvb2dsZWFwaXMuY29tL3YxL3Byb2plY3RzL2RyeXJ1bnMvbG9jYXRpb25zL3NvdXRoYW1lcmljYS13ZXN0MS9jbHVzdGVycy9hdXRvcGlsb3QtY2x1c3Rlci0xIiwianRpIjoiN2ZjNWE5OGItMzcyYS00YzNmLWFjMjEtOTI2YzMxYzJmYTQ2Iiwia3ViZXJuZXRlcy5pbyI6eyJuYW1lc3BhY2UiOiJkZWZhdWx0Iiwic2VydmljZWFjY291bnQiOnsibmFtZSI6ImFjY291bnQtZGVmYXVsdC12aWV3ZXItZnZkeWkiLCJ1aWQiOiJjNGUyN2UxMS03MGU0LTQzMjMtOWQ3Mi1hZWJlZjk2YTBjYzUifX0sIm5iZiI6MTcyNzM1MzY4MSwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OmRlZmF1bHQ6YWNjb3VudC1kZWZhdWx0LXZpZXdlci1mdmR5aSJ9.E344OlFL_VXDsqTXdjFJPONvIao9XlbEQSNoC-_oVmgnf6V0iyPJGK2Y0NEkl1ioFCTJ2-Jr8r87RpqhuGyOEt5dIqtaWPzdHDF1WlsRZZgy3QQy1XsWdk5z3KFPcfw4rmLWpVR-Iqtn-pI3i3UNOcyG_n0Ov2NSikJ0WUymjCFInOdko3Ct4WLA4gPV1W22uGhc_do-0C8FdtozkmEk1MK_k1_2ytama1CF9bJl9ms18UPBTLYAqpQuM4PzG43TEypp_o6CBXvG5nEyKEwd2m1cUd7h8yYMQdF8eXoK0OZxkK--lqLs2Yw2GCBsyrEmBy4IJuzodPgdRUe5PHgK3A
```

5. Apply role to inject JVM errors: 

```shell
dralquinta-mac:chaos-engineering dralquinta$ kubectl apply -f setup/02_chaos-mesh-role.yaml 
role.rbac.authorization.k8s.io/chaos-mesh-namespace-role created
rolebinding.rbac.authorization.k8s.io/chaos-mesh-rolebinding created
```