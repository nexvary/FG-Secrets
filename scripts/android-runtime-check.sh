#!/usr/bin/env bash
# Preserve real screenshots even when an assertion fails; retain Gradle exit code.
set +e
./gradlew :app:connectedDebugAndroidTest --stacktrace
check_result=$?
mkdir -p screenshots
adb pull /sdcard/Pictures/FG-Secrets/. screenshots/
capture_result=$?
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
if [ "$check_result" -ne 0 ]; then exit "$check_result"; fi
if [ "$capture_result" -ne 0 ]; then exit "$capture_result"; fi
python3 - <<'PYTHON'
from pathlib import Path
images = list(Path("screenshots").glob("*.png"))
assert len(images) >= 8, f"Expected 8 real screenshots, found {len(images)}"
assert all(image.stat().st_size > 1000 for image in images)
print(f"Verified {len(images)} Android 15 screenshots")
PYTHON
