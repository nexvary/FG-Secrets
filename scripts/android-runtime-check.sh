#!/usr/bin/env bash
# Preserve real screenshots even when an assertion fails; retain Gradle exit code.
set +e
gradle :app:connectedDebugAndroidTest --stacktrace
check_result=$?
mkdir -p screenshots
adb pull /sdcard/Android/data/com.fgmachines.secrets/files/screenshots/. screenshots/
exit "$check_result"
