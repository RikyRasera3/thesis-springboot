FROM postgres:18

COPY thesis-schema.sql /docker-entrypoint-initdb.d/01-thesis-schema.sql
COPY thesis-data.sql /docker-entrypoint-initdb.d/02-thesis-data.sql