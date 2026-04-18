#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

export GRADLE_USER_HOME="$ROOT/.gradle-user-home"
mkdir -p "$GRADLE_USER_HOME"

bootstrap_jdk() {
  local jdk_dir="$ROOT/.tools/jdk-17"
  if [[ -x "$jdk_dir/bin/java" ]]; then
    export JAVA_HOME="$jdk_dir"
    return 0
  fi
  local tmp archive extract_dir top
  mkdir -p "$ROOT/.tools"
  archive="$ROOT/.tools/temurin-17-jdk.tar.gz"
  echo "正在下载 Eclipse Temurin JDK 17 到 $ROOT/.tools（仅首次需要）…"
  curl -fsSL \
    "https://api.adoptium.net/v3/binary/latest/17/ga/linux/x64/jdk/hotspot/normal/eclipse?project=jdk" \
    -o "$archive"
  extract_dir="$ROOT/.tools/_jdk_extract"
  rm -rf "$extract_dir"
  mkdir -p "$extract_dir"
  tar -xzf "$archive" -C "$extract_dir"
  top="$(find "$extract_dir" -mindepth 1 -maxdepth 1 -type d | head -1)"
  rm -rf "$jdk_dir"
  mv "$top" "$jdk_dir"
  rm -rf "$extract_dir"
  export JAVA_HOME="$jdk_dir"
}

if [[ -n "${JAVA_HOME:-}" && -x "${JAVA_HOME}/bin/java" ]]; then
  :
elif command -v java >/dev/null 2>&1; then
  :
else
  bootstrap_jdk
fi

exec "$ROOT/gradlew" --no-daemon assembleDebug "$@"
