#!/bin/sh
set -eu

MAVEN_VERSION=3.9.11
WRAPPER_BASE="${MAVEN_USER_HOME:-${HOME}/.m2}/wrapper/dists/apache-maven-${MAVEN_VERSION}"
MAVEN_HOME="${WRAPPER_BASE}/apache-maven-${MAVEN_VERSION}"
MAVEN_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.zip"

if [ ! -x "${MAVEN_HOME}/bin/mvn" ]; then
  mkdir -p "${WRAPPER_BASE}"
  ARCHIVE="${WRAPPER_BASE}/apache-maven-${MAVEN_VERSION}-bin.zip"
  if [ ! -f "${ARCHIVE}" ]; then
    if command -v curl >/dev/null 2>&1; then
      curl -fsSL "${MAVEN_URL}" -o "${ARCHIVE}"
    elif command -v wget >/dev/null 2>&1; then
      wget -q "${MAVEN_URL}" -O "${ARCHIVE}"
    else
      echo "Maven Wrapper needs curl or wget to download Maven ${MAVEN_VERSION}." >&2
      exit 1
    fi
  fi
  if ! command -v unzip >/dev/null 2>&1; then
    echo "Maven Wrapper needs unzip to install Maven ${MAVEN_VERSION}." >&2
    exit 1
  fi
  unzip -q -o "${ARCHIVE}" -d "${WRAPPER_BASE}"
fi

exec "${MAVEN_HOME}/bin/mvn" "$@"
