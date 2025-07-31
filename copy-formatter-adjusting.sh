#!/bin/bash

# Check if base path argument is provided
if [ -z "$1" ]; then
  echo "Usage: $0 <base-path>"
  exit 1
fi

BASE_PATH="$1"
JAR_SOURCE="formatter-adjusting/target/formatter-adjusting-1.0-jar-with-dependencies.jar"
JAR_NAME="formatter-adjusting.jar"
PRE_COMMIT_SOURCE="sample-hooks/pre-commit"

# Array of relative paths to copy files to
DESTINATIONS=(
  "products-interacta-analytics-metrics-collector-cloud-function"
  "products-interacta-backend-api"
  "products-interacta-backend-api-external"
  "products-interacta-backend-plugin-chat"
  "products-interacta-backend-plugin-elasticsearch-postindexer"
  "products-interacta-backend-plugin-k8s-platform"
  "products-interacta-backend-server"
  "in-library-workflows"
  "in-library-workflows-guice"
  "in-platform-common-base"
  "in-platform-common-base-google"
  "in-platform-common-base-guice"
  "in-platform-common-sqltree"
  "in-platform-common-web"
  "in-platform-common-web-guice"
)

# Perform the copy operations
for dest in "${DESTINATIONS[@]}"; do
  FULL_PATH="$BASE_PATH/$dest"

  if [ -d "$FULL_PATH" ]; then
    # Copy JAR file
    cp "$JAR_SOURCE" "$FULL_PATH/$JAR_NAME"
    echo "Copied JAR to $FULL_PATH/$JAR_NAME"

    # Copy pre-commit hook if .git directory exists
    GIT_HOOKS_PATH="$FULL_PATH/.git/hooks"
    if [ -d "$GIT_HOOKS_PATH" ]; then
      cp "$PRE_COMMIT_SOURCE" "$GIT_HOOKS_PATH/pre-commit"
      chmod +x "$GIT_HOOKS_PATH/pre-commit"
      echo "Installed pre-commit hook in $GIT_HOOKS_PATH"
    else
      echo "Warning: .git/hooks directory not found in $FULL_PATH"
    fi

  else
    echo "Warning: Directory does not exist - $FULL_PATH"
  fi
done
