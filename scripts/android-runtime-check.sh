#!/usr/bin/env bash
# Preserve real screenshots even when an assertion fails; retain Gradle exit code.
set +e
./gradlew :app:connectedDebugAndroidTest --stacktrace
check_result=$?
mkdir -p screenshots
adb pull /sdcard/Android/data/com.fgmachines.secrets/files/screenshots/. screenshots/
if [ "$check_result" -ne 0 ]; then
  python3 - <<'PYTHON'
from pathlib import Path
import xml.etree.ElementTree as ET
for path in Path("app/build/outputs/androidTest-results").rglob("TEST-*.xml"):
    for case in ET.parse(path).iter("testcase"):
        for failure in case.findall("failure") + case.findall("error"):
            print(case.attrib, failure.attrib, failure.text)
PYTHON
fi
exit "$check_result"
