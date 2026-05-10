# Thesis PostgreSQL Container

This module builds a Docker image based on [Postgres 18](https://www.postgresql.org/about/news/postgresql-18-released-3142/)
and initializes the database by automatically executing the scripts:

1. [thesis-schema.sql](/database/thesis-schema.sql)
2. [thesis-data.sql](/database/thesis-data.sql)

The execution order is guaranteed by the standard Postgres mechanism `/docker-entrypoint-initdb.d/` through file names:

- `01-thesis-schema.sql`
- `02-thesis-data.sql`

## Startup

Run the commands from the `database/` directory:

### Build the image

```bash
docker compose build
```

### Start the container

```bash
docker compose up -d
```

### Stop and remove the container

```bash
docker compose down
```

## Credentials

- Host from host machine: `localhost`
- Host from another container in the same Docker network: `postgres`
- Port: `5432`
- Database: `postgres`
- Username: `postgres`
- Password: `postgres`
- Application schema: `thesis`

## Initialization Note

Scripts in `/docker-entrypoint-initdb.d/` are executed only on first startup, i.e., when the data volume is empty.

If you want to recreate the database from scratch:

```bash
docker compose down -v
docker compose up --build
```

## Usage from Another Container

If the application is started as another service in the same `docker compose`, it must use `postgres` as the database host,
not `localhost`.

Example `depends_on`:

```yaml
depends_on:
  postgres:
    condition: service_healthy
```

## Test Environment

Tests have been executed installing SQL scripts on a
[Cloud SQL](https://cloud.google.com/sql?_gl=1*1mt07sm*_up*MQ..&gclid=CjwKCAjwzLHPBhBTEiwABaLsSnKN_mvr7If9AkAgTHfVeFSFXSuNmwhm30SYU3zVQobJGYhkHR6H4hoCgqEQAvD_BwE&gclsrc=aw.ds)
instance of
[Google Cloud Platform](https://cloud.google.com/?_gl=1*1o4sew4*_up*MQ..&gclid=CjwKCAjw5NvPBhAoEiwA_2egfqYaZpsdEUY6ez7ypP25M9AE5FgZq5TuEzXZf3387FQGYbhOQmVs-xoC02cQAvD_BwE&gclsrc=aw.ds)
with the following configuration:

### Configurations

- 1 vCPU 3.75 GB dedicated core
- 10 GB SSD
- Single zone