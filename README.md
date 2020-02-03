# AKS setup

### Project structure
1. [aks](aks) folder store all related configurations and templates for bootstrapping AKS cluster with [aks-engine](https://github.com/Azure/aks-engine) tool
2. [app](app) folder store SpringBoot Rest Services which is fetch Bitcoin rate and make a calculation
3. [app-deploy-a](app-deploy-a) folder store K8S manifests for deploying ServiceA
4. [app-deploy-b](app-deploy-b) folder store K8S manifests for deploying ServiceB
5. [ingress](ingress) folder stores related K8S manifests for deploying K8S Nginx Ingress Controller
6. [network-policy](network-policy) folder stores K8S manifests for configuring network policies


### E2E setup 
1. Deploy AKS cluster
```bash
cd aks

# Create Resource Group
az group create --name test-group-4 --location westeurope

# Export your CLIENT_ID and SECRET
export CLIENT_ID=<YOUR-CLIENT-ID>
export SECRET=<YOUR-SECRET>

# Generate ARM templates 
aks-engine generate --set servicePrincipalProfile.clientId=$CLIENT_ID,servicePrincipalProfile.secret=$SECRET -m  kubernetes.json 

# Deploy the cluster 
az group deployment create \
    --name "test-group-4" \
    --resource-group "test-group-4" \
    --template-file "./_output/test-group-4/azuredeploy.json" \
    --parameters "./_output/test-group-4/azuredeploy.parameters.json"

# Once the cluster read, export KUBECONFIG
export KUBECONFIG=$(pwd)/_output/test-group-4/kubeconfig/kubeconfig.westeurope.json

```

2.  Deploy Ingress Controller
```bash
cd ingress
# Create namespace
kubectl apply -f ns.yaml
# Deploy the Nginx ingress controller 
kubectl apply -f ingress.yaml -n ingress-basic
```

3. Deploy ServiceA
```bash
cd app-deploy-a
kubectl create -f ns.yaml
kubectl create -f dep.yaml
kubectl create -f service.yaml
kubectl create -f route.yaml
```

5. Deploy ServiceB
```bash
cd app-deploy-b
kubectl create -f ns.yaml
kubectl create -f dep.yaml
kubectl create -f service.yaml
kubectl create -f route.yaml
```

6. Create Network Policies
```bash
# First deny all
kubectl create -f block-b.yaml
# Second allow access from ingress namespace 
kubectl create -f allow-ingress.yaml
```

### Verify setup
1. Access to ServiceA and ServiceB
```bash
# Get External IP of the ingress controller
LB_IP=$(kubectl get services -n ingress-basic | grep release-name-nginx-ingress-controller | awk '{print $4}')

# Access to ServiceA
curl http://${LB_IP}/servicea/rates

# Access to ServiceB
curl http://${LB_IP}/serviceb/rates

```
2. Check Network Policy 
```bash
# Connect to serviceA
SERVICE_A_POD=$(kubectl get pods -n a | grep Running | cut -f1 -d' ')
kubectl exec -it ${SERVICE_A_POD} bash -n a
# From serviceA try to access to serviceB, the request will fail 
curl http://bitcoin-app-b.b.svc.cluster.local/rates 

# Test vice versa, e.g serviceB do able to talk to service A
SERVICE_B_POD=$(kubectl get pods -n b | grep Running | cut -f1 -d' ')
kubectl exec -it ${SERVICE_B_POD} bash -n b
# From serviceB try to access to serviceA, the request will be done successfully 
curl http://bitcoin-app-a.a.svc.cluster.local/rates
```