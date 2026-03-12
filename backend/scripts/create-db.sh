#!/usr/bin/env bash
# Create finance_db so Flyway can run. Use the same user as in application-local.yml.
set -e
echo "Creating database finance_db (use your MySQL admin password if prompted)..."
mysql -u admin -p -e "CREATE DATABASE IF NOT EXISTS finance_db;"
echo "Done. Start the backend with: ./mvnw spring-boot:run -Dspring-boot.run.profiles=local"
