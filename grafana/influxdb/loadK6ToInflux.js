const fs = require("fs");
const path = require("path");
const http = require("http");

const INFLUX_URL = "http://localhost:8086";
const INFLUX_DB = "thesis";
const RESULTS_DIR = path.join(__dirname, "../k6/results");

// All test data is normalized to this base epoch so that tests run at different
// times (e.g. Node.js on Monday, Spring Boot on Tuesday) always start at the
// same virtual timestamp and their series overlap correctly in Grafana.
const BASE_EPOCH_MS = new Date("2024-01-01T00:00:00.000Z").getTime();

function convertToLineProtocol(point, fileStartMs, testScenario) {
  const {metric, data} = point;
  const originalMs = new Date(data.time).getTime();
  const normalizedMs = BASE_EPOCH_MS + (originalMs - fileStartMs);
  const timestamp = normalizedMs * 1_000_000;  // nanoseconds
  const tags = { ...data.tags, test_scenario: testScenario };

  const tagString = Object.entries(tags)
    .filter(([_key, value]) => value !== "" && value !== null && value !== undefined)
    .map(([key, value]) => `${escapeTag(key)}=${escapeTag(value)}`)
    .join(",");

  const fieldString = `value=${data.value}`;
  const measurement = escapeMeasurement(metric);

  return tagString.length > 0 ?
      `${measurement},${tagString} ${fieldString} ${timestamp}` :
      `${measurement} ${fieldString} ${timestamp}`;
}

function escapeMeasurement(name) {
  return name.replace(/[\s,]/g, "\\ ");
}

function escapeTag(value) {
  return String(value).replace(/[\s,=]/g, "\\ ");
}

function readK6File(filePath) {
  const content = fs.readFileSync(filePath, "utf-8");
  const lines = content.split("\n").filter((line) => line.trim());
  const points = [];

  for (const line of lines) {
    try {
      const entry = JSON.parse(line);

      if (entry.type === "Metric") {
        continue;
      }

      if (entry.type === "Point" && entry.data) {
        points.push(entry);
      }
    } catch (error) {
      // Ignoring parsing errors
    }
  }

  return points;
}

function sendToInflux(lineProtocolData) {
  return new Promise((resolve, reject) => {
    const url = new URL(`${INFLUX_URL}/write?db=${INFLUX_DB}`);

    const options = {
      hostname: url.hostname,
      port: url.port || 8086,
      path: url.pathname + url.search,
      method: "POST",
      headers: {
        "Content-Type": "text/plain",
        "Content-Length": Buffer.byteLength(lineProtocolData),
      },
    };

    const req = http.request(options, (res) => {
      let data = "";

      res.on("data", (chunk) => {
        data += chunk;
      });

      res.on("end", () => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve();
        } else {
          reject(
            new Error(
              `Errore InfluxDB (${res.statusCode}): ${data}`
            )
          );
        }
      });
    });

    req.on("error", (error) => {
      reject(error);
    });

    req.write(lineProtocolData);
    req.end();
  });
}

async function main() {
  console.log("Loading k6 data to InfluxDB");
  console.log(`Directory: ${RESULTS_DIR}`);
  console.log(`InfluxDB URL: ${INFLUX_URL}`);
  console.log(`Database: ${INFLUX_DB}\n`);

  try {
    await new Promise((resolve, reject) => {
      const options = {
        hostname: "localhost",
        port: 8086,
        path: "/ping",
        method: "GET",
      };

      const req = http.request(options, (res) => {
        if(res.statusCode === 204) {
          resolve();
        } else {
          reject(new Error("InfluxDB not available"));
        }
      });
      req.on("error", reject);
      req.end();
    });

    console.log("InfluxDB is available\n");
  } catch (error) {
    console.error("Error: InfluxDB is not reachable at localhost:8086");
    console.error("   Make sure the container is running: docker-compose up");
    process.exit(1);
  }

  const files = fs
    .readdirSync(RESULTS_DIR)
    .filter((file) => file.endsWith(".json") && !file.endsWith(".zip"));

  if(files.length === 0) {
    console.log("No JSON files found in results/");
    return;
  }

  console.log(`Found ${files.length} JSON files\n`);

  let totalPoints = 0;
  let processedFiles = 0;

  for(const file of files) {
    const filePath = path.join(RESULTS_DIR, file);
    console.log(`Processing file: ${file}...`);

    // Derive test_scenario from filename (e.g. "springboot-average.json" → "average").
    // k6 has a built-in tag called "scenario" (always "default" when using stages)
    // that silently overrides any custom options.tags.scenario value, so we inject
    // test_scenario directly from the filename instead.
    const fileBasename = path.basename(file, ".json");
    const dashIdx = fileBasename.indexOf("-");
    const testScenario = dashIdx !== -1 ? fileBasename.slice(dashIdx + 1) : fileBasename;
    console.log(`   └─ Detected test_scenario: "${testScenario}"`);

    try {
      const points = readK6File(filePath);
      console.log(`   └─ ${points.length} data points extracted`);

      if(points.length === 0) {
        console.log(`   └─ No data points found\n`);
        continue;
      }

      // Find the earliest timestamp in this file so we can normalize all
      // points to start at BASE_EPOCH, making sequential test runs from
      // different projects align on the same time axis in Grafana.
      const fileStartMs = points.reduce((min, p) => {
        const t = new Date(p.data.time).getTime();
        return t < min ? t : min;
      }, Infinity);

      console.log(`   └─ Test start: ${new Date(fileStartMs).toISOString()} → normalized to 2024-01-01T00:00:00Z`);

      const batchSize = 1000;
      const numBatches = Math.ceil(points.length / batchSize);

      for(let i = 0; i < points.length; i += batchSize) {
        const batch = points.slice(i, i + batchSize);

        const batchLineProtocol = batch.map((point) => convertToLineProtocol(point, fileStartMs, testScenario)).join("\n");

        try {
          await sendToInflux(batchLineProtocol);
          const batchNum = Math.floor(i / batchSize) + 1;
          console.log(`   └─ Batch ${batchNum}/${numBatches} loaded (${batch.length} points)`);
        } catch (error) {
          console.error(`   └─ Batch error: ${error.message}`);
        }
      }

      totalPoints += points.length;
      processedFiles++;
      console.log();
    } catch (error) {
      console.error(`Error processing ${file}:`, error.message);
    }
  }

  console.log("\nUpload completed:");
  console.log(`   ├─ Files processed: ${processedFiles}/${files.length}`);
  console.log(`   └─ Points loaded: ${totalPoints}`);
}

main().catch(console.error);