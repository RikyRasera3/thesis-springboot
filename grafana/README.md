# GRAFANA k6

The tool used for load testing in this project is [k6](https://k6.io/), an open-source load testing tool developed by 
Grafana Labs. 
k6 allows users to write test scripts using JavaScript and provides a usefull CLI for executing load tests and analyzing 
results.

## Test Environment

Tests have been executed using a  
[Compute Engine](https://cloud.google.com/products/compute?_gl=1*1mt07sm*_up*MQ..&gclid=CjwKCAjwzLHPBhBTEiwABaLsSnKN_mvr7If9AkAgTHfVeFSFXSuNmwhm30SYU3zVQobJGYhkHR6H4hoCgqEQAvD_BwE&gclsrc=aw.ds)
VM instance of
[Google Cloud Platform](https://cloud.google.com/free?utm_source=google&utm_medium=cpc&utm_campaign=Cloud-SS-DR-GCP-1713666-GCP-DR-EMEA-IT-it-Google-BKWS-MIX-na&utm_content=c-Hybrid+%7C+BKWS+-+MIX+%7C+Txt+-+Generic+Cloud-Cloud+Generic-Cloud+Generic-1815140985&utm_term=google+api+key&gclsrc=aw.ds&gad_source=1&gad_campaignid=731154719&gclid=CjwKCAjwzLHPBhBTEiwABaLsSlSRTKU7SJUYNY6CeexT8sOye1Q2soEJzO-dLPyt5bxk_kENWU6nuBoCG8MQAvD_BwE) 
with the following configurations:

### Configurations

- OS: Ubuntu 22.04 LTS
- 4 vCPU x86/64 Intel Broadwell
- 4 GB RAM
- 10 GB standard persistent disk

## Installation Guide
### System Update

```bash
sudo apt update
sudo apt upgrade -y
```

### Install Dependencies

```bash
sudo apt install -y gnupg software-properties-common curl
```

### Install GPG Key

```bash
curl -fsSL https://dl.k6.io/key.gpg | sudo gpg --dearmor -o /usr/share/keyrings/k6-archive-keyring.gpg
```

### Add Grafana k6 Official Repository
```bash
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
```


### Install k6

```bash
sudo apt update
sudo apt install k6 -y
```

## Usage Guide
### Average Scenario

Simulates realistic average traffic with 50 VUs, validating both p95 and p99 latency thresholds.

```bash
BASE_URL=http://127.0.0.1:3000 \
k6 run grafana/scenarios/average.js --out json=grafana/results/average.json
```

### P95 Scenario

Runs 100 VUs to validate that 95% of requests complete within the defined SLA (500ms).

```bash
BASE_URL=http://127.0.0.1:3000 \
k6 run grafana/scenarios/p95.js --out json=grafana/results/p95.json
```

### P99 Scenario

Runs 100 VUs to validate tail latency, ensuring 99% of requests complete within 1000ms.

```bash
BASE_URL=http://127.0.0.1:3000 \
k6 run grafana/scenarios/p99.js --out json=grafana/results/p99.json
```

### Stress Test

Ramps up to 1000 VUs to find the application breaking point.

```bash
BASE_URL=http://127.0.0.1:3000 \
k6 run grafana/scenarios/stress.js --out json=grafana/results/stress.json
```

### Spike Test

Simulates a sudden burst of 500 VUs to test resilience and recovery.

```bash
BASE_URL=http://127.0.0.1:3000 \
k6 run grafana/scenarios/spike.js --out json=grafana/results/spike.json
```

### Soak Test

Runs 50 VUs for 30 minutes to detect memory leaks and performance degradation over time.

```bash
BASE_URL=http://127.0.0.1:3000 \
k6 run grafana/scenarios/soak.js --out json=grafana/results/soak.json
```
