#!/bin/bash
# ─────────────────────────────────────────────
#  Build and run the Java Streams demo project
# ─────────────────────────────────────────────

set -e

JAVAC=$(which javac 2>/dev/null || echo "")

if [ -z "$JAVAC" ]; then
  # Try common JDK locations
  for dir in /usr/lib/jvm/java-21-openjdk-amd64 \
              /usr/lib/jvm/java-17-openjdk-amd64 \
              /usr/local/lib/jvm \
              "$JAVA_HOME"; do
    if [ -f "$dir/bin/javac" ]; then
      JAVAC="$dir/bin/javac"
      JAVA="$dir/bin/java"
      break
    fi
  done
fi

JAVA=${JAVA:-java}

if [ -z "$JAVAC" ]; then
  echo "ERROR: javac not found. Install JDK 17+:"
  echo "  Ubuntu/Debian : sudo apt install openjdk-21-jdk"
  echo "  macOS         : brew install openjdk@21"
  echo "  Windows       : https://adoptium.net"
  exit 1
fi

echo "Using: $JAVAC"
mkdir -p out
find src -name "*.java" | xargs $JAVAC -d out
echo "Compile: OK"
echo ""
$JAVA -cp out com.streams.Main
