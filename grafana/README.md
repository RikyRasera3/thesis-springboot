# Grafana & k6 Load Testing

This guide covers running [k6](https://k6.io/) load tests and visualizing results in [Grafana](https://grafana.com/oss/grafana/) with [InfluxDB](https://www.influxdata.com/)

## Table of Contents

1. [Running Load Tests](#running-load-tests)
2. [Grafana & InfluxDB Setup](#grafana--influxdb-setup)

---

## Running Load Tests

### Prerequisites

#### Local Machine

- **k6** installed ([Installation guide](#local-installation))
- Application running on `http://127.0.0.1:3000`

#### Google Cloud Platform Compute Engine VM

- **VM Configuration:**
  - **OS:** Ubuntu 22.04 LTS
  - **CPU:** 4 vCPU (Intel Broadwell x86/64)
  - **Memory:** 4 GB RAM
  - **Disk:** 10 GB standard persistent disk
  - **Network:** Firewall rule allowing traffic to the application

### Local Installation

Install k6 on your local machine (Ubuntu/Linux):

```bash
# Add GPG Key
curl -fsSL https://dl.k6.io/key.gpg | sudo gpg --dearmor -o /usr/share/keyrings/k6-archive-keyring.gpg

# Add Official Repository
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list

# Install k6
sudo apt update
sudo apt install k6 -y
```

### VM Configuration (Google Cloud Compute Engine)

1. **Create a VM instance:**
   - Machine type: 4 vCPUs, 4 GB RAM (e.g., `n1-standard-4`)
   - OS: Ubuntu 22.04 LTS
   - Boot disk: 10 GB standard persistent disk

2. **SSH into the VM:**
   ```bash
   gcloud compute ssh <instance-name> --zone=<zone>
   ```

3. **Update and install k6:**
   ```bash
   sudo apt update && sudo apt upgrade -y
   sudo apt install -y gnupg software-properties-common curl
   
   curl -fsSL https://dl.k6.io/key.gpg | sudo gpg --dearmor -o /usr/share/keyrings/k6-archive-keyring.gpg
   echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
   sudo apt update && sudo apt install k6 -y
   ```

4. **Copy test files to VM:**
   ```bash
   gcloud compute scp --recurse ./grafana/k6 <instance-name>:~/k6 --zone=<zone>
   ```

### Running Tests

#### On Local Machine

From the project root, run any scenario:

```bash
BASE_URL=http://127.0.0.1:3000 \
k6 run grafana/k6/scenarios/<scenario>.js \
  --out json=grafana/k6/results/<scenario>.json
```

**Available scenarios:**
- `average.js` - 50 VUs, realistic average traffic
- `p95.js` - 100 VUs, validates p95 latency SLA
- `p99.js` - 100 VUs, validates p99 tail latency
- `stress.js` - Ramps to 1000 VUs, find breaking point
- `spike.js` - 500 VUs spike, test resilience
- `soak.js` - 50 VUs for 30 minutes, detect memory leaks

#### On Google Cloud Compute Engine VM

SSH into the VM and run tests pointing to your deployed application:

```bash
# Replace <APP_URL> with your application's external IP
BASE_URL=http://<APP_EXTERNAL_IP>:3000 \
k6 run ~/k6/scenarios/<scenario>.js \
  --out json=~/k6/results/<scenario>.json
```

**Download results locally:**

```bash
gcloud compute scp --recurse <instance-name>:~/k6/results ./grafana/k6/ --zone=<zone>
```

---

## Grafana & InfluxDB Setup


### Prerequisites

- **Docker & Docker Compose** installed
- Ports **8086** (InfluxDB) and **3001** (Grafana) available
- k6 test results JSON files in `grafana/k6/results/` directory

### Start Services

Navigate to the influxdb directory and start containers:

```bash
cd grafana/influxdb
docker-compose up -d
```

Services will be available at:
- **Grafana:** `http://localhost:3001`
- **InfluxDB:** `http://localhost:8086`

### Load Test Results to InfluxDB

After collecting test results, load them into InfluxDB:

```bash
cd grafana/influxdb
node loadK6ToInflux.js
```

This script will:
- ✅ Auto-discover all `.json` files in `k6/results/`
- ✅ Parse k6 NDJSON format and extract metrics
- ✅ Convert to InfluxDB Line Protocol
- ✅ Load data in batches for optimal performance
- ✅ Store in the `thesis` database (tagged by `project: springboot` and `scenario: <name>`)
- ✅ Display real-time progress

### Access Grafana

Open your browser and navigate to:
```
http://localhost:3001
```

**Default credentials:** Admin / Admin (anonymous access enabled)

**Pre-configured Dashboards:**

- **k6 Load Testing Overview** - Overview of all scenarios
- **k6 Endpoint Analysis** - Performance per API endpoint
- **Scenario Comparison Dashboards:**
  - Average Load Test Scenario - Comparison
  - P95 Load Test Scenario - Comparison
  - P99 Load Test Scenario - Comparison
  - Soak Load Test Scenario - Comparison
  - Spike Load Test Scenario - Comparison
  - Stress Load Test Scenario - Comparison

Each scenario dashboard shows both **Node.js (Red)** and **Spring Boot (Blue)** results side-by-side for easy comparison.
