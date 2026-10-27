# Current product correction — 2026-10-27

Owner clarification supersedes the original text-first product direction.
The essential experience is: category → industry → infographic image → save/share,
with a written description beneath the image. Owner DOCs supply industry names
only; their text is not the public educational content.

## Current change
- New category-led visual home and industry catalog.
- Two source-referenced educational images: industrial grease and hot melt.
- Images rendered offline with Android Arabic text shaping and measured height.
- PNG save through Android document picker; image sharing through a restricted
  FileProvider cache directory with temporary read grants. No storage permission.
- Pinch magnification, written accessible duplicate text and source links.
- Original document transcriptions retained in data for provenance but removed
  from public industry detail screens; unsupported titles marked in preparation.
- Original 71 IDs retained; old topic favorites remain navigable.
- Categories include adhesives, oils/greases, perfumes, metal care and insulation.
  Perfume and metal-care content is explicitly not published yet.

## Validation for this change
Source commit: 1df414a2abba9b68255ee6e9df18d0cbe87ac465.
CI https://github.com/nexvary/FG-Secrets/actions/runs/40559607453 succeeded:
real APK build, JVM tests, lint and five Android 15 emulator tests.
The new test renders each published guide, encodes PNG, decodes it and verifies dimensions.
Eight actual UI screenshots were collected; they have NOT been visually reviewed
in this session. Native document-picker save and external share round trips have
NOT been verified. No physical-device test was performed.
Release: https://github.com/nexvary/FG-Secrets/releases/tag/preview-0.2.0-1df414a
APK: https://github.com/nexvary/FG-Secrets/releases/download/preview-0.2.0-1df414a/FG-Secrets-0.2.0-debug.apk
This preview still reports version 0.2.0; distinguish it by the source-hash tag.
It is debug-signed and is an initial product-direction correction, not a completed
encyclopedia or a visually approved release. Signature/checksum were not independently
rechecked locally in this session. Do not substitute the historical APK below.
Only two educational images exist; no complete manufacturing recipes are claimed.

## Next work
Inspect CI for this commit; correct failures; review actual screenshots and image
exports, then expand the reviewed visual content, particularly wood/leather/book
glues, perfumes and metal care. Do not recreate source-document paragraphs as
educational articles.

---

# Previous release evidence (historical)
# FG Secrets — verified engineering checkpoint
Date: 2026-10-27
Branch: feature/industrial-catalog-foundation
PR: https://github.com/nexvary/FG-Secrets/pull/1
Last verified application/test/workflow commit: b3bba96476a848a8739b99692f19e5b66d0b709a

## Verified deliverable
- Version 0.2.0, versionCode 2, package com.fgmachines.secrets.
- Minimum Android 8.0 / API 26; target/compile Android 15 / API 35.
- CI: https://github.com/nexvary/FG-Secrets/actions/runs/40545974235
- Real Gradle Android build, lint and 16 JVM tests passed.
- Four Android 15 x86_64 emulator tests passed, zero failures/errors.
- Independent PR run 40545983136 also passed the same complete checks.
- Eight rendered Compose screenshots collected and visually inspected: home,
  source details, favorites, tools, mass scaling, cost results, English LTR
  settings, and landscape with enlarged text. No generated mockups were used.
- Source records remain Arabic; interface language supports Arabic RTL/English LTR.
- Release: https://github.com/nexvary/FG-Secrets/releases/tag/preview-0.2.0-b3bba96
- Direct APK: https://github.com/nexvary/FG-Secrets/releases/download/preview-0.2.0-b3bba96/FG-Secrets-0.2.0-debug.apk
- Runtime screenshots/reports: https://github.com/nexvary/FG-Secrets/releases/download/preview-0.2.0-b3bba96/android-15-runtime-evidence.zip
- Downloaded release APK rechecked locally: SHA-256 matches published checksum,
  APK Signature Scheme v2 verifies, package/version/min/target inspected.
- APK size: 10,039,898 bytes; ABIs arm64-v8a, armeabi-v7a, x86 and x86_64.
- SHA-256: 02cb2c530910c13380bc238b4be555f586bdbcf5af53012d9df52b9abe4b70d3
- No sensitive permissions; the merged APK requests only its own signature-level
  DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION. Backup is disabled.
- This is a DEBUG-signed experimental APK. CI debug keys may change; upgrading
  a later preview can require uninstalling and losing locally saved data.

## Implemented
- Kotlin/Compose, MVVM with lifecycle-aware StateFlow, Navigation Compose.
- Room database with transactional import, independent topic/material favorites,
  and exported schema; DataStore persists interface language.
- Home, dynamic categories, normalized Arabic search, source details, favorites,
  dictionary, references/library, calculators and settings/about.
- Offline JSON title packs: validate/preview/import up to 8 MiB / 20,000 entries,
  preserve favorites, reject duplicate source assignments, prevent startup seed
  from overwriting imported updates. See CONTENT_PACKS.md.
- Exact BigDecimal mass scaling, g/kg/t conversions, Arabic/Persian decimal
  entry and exact 100% totals. Even tiny components retain mass internally;
  display rounding is explicitly described.
- Production cost model with batch costs, saleable output after loss, unit cost
  and profit margin as a fraction of selling price (not markup).
- Evidence/reliability model with published reference, test-report and sensitive
  application review gates. Imported title packs cannot upgrade reliability.
- Full FG Machines palette, factory launcher icon, explicit back navigation,
  insets and scroll support. Calculator fields remain composed during typing.

## Content actually available
- 71 retained display titles: 38 adhesives and 33 insulation/cable/thermal items.
- Both owner DOCs read in full; normalized original-text transcriptions and
  source notes preserve details omitted by the initial shortened display titles.
- Two referenced material definitions, five scoped technical notes and one
  referenced hot-melt overview. All references/review dates are in
  CONTENT_PROVENANCE.md and the app; definitions work offline.
- All 71 source-product records are UNVERIFIED. No manufacturing formulation,
  laboratory validation or production validation is bundled or claimed.
- Final visual reconciliation of the DOC transcriptions is still pending;
  transcriptions are normalized text, not byte-exact document facsimiles.
- Hilti Products.xlsx remains excluded.

## Verification coverage
- JVM: 10 arithmetic/evidence tests + 6 catalog/search/import-validation tests.
- Android: repeatable Room import, atomic duplicate-source rejection and favorites;
  file DB close/reopen retaining both topic and material favorites; search,
  navigation/back, favorites, both calculators, language changes and landscape.
- Local JVM tests (16), lint and APK build also passed on the implementation
  before the final palette/test-frame-capture refinements. Final CI above is
  authoritative for the delivered source commit.
- Found and fixed: removed SDK `tools` package in CI, API-27-only theme attribute
  with API-26 minimum, focus loss from lazy calculator fields, stale native
  screenshots, and inherited Material purple colors.

## Remaining work / next priorities
1. Physical Android 15 installation, ColorOS keyboard/back/rotation QA.
2. End-to-end document-picker import QA and accessibility/TalkBack review.
3. Visually reconcile both original DOCs, then enrich one complete industry at a
   time with primary references, actual quality methods, troubleshooting and
   reviewed educational diagrams. Preserve uncertainty and source wording.
4. Source and technically review actual formulations before publishing ratios;
   add laboratory/production status only with documented evidence.
5. Benchmark thousands of entries, then optimize search/import based on evidence.
6. Define signed scientific-content packs, versions, rollback and migrations;
   current local title import does not authenticate a content publisher.
7. Establish production signing, upgrade continuity and distribution readiness.

This is an installable engineering checkpoint, not completion of all content and
all sixty requested improvement stages. No physical-device test was available.

## Resume safely
Read the current branch HEAD and this checkpoint, then inspect the linked CI and
release. The final checkpoint commit only adds documentation, the exported Room
schema and compiler-state ignore rules; application/test/workflow code remains
identical to the verified commit above. Continue with the remaining priorities;
do not recreate the scaffold or re-label source titles as validated recipes.
