# Offline content packs — schema 2

Library > Import JSON topic pack uses Android's document picker; no broad storage
permission or network connection is needed. Preview shows the record count and
asks the app user to confirm the additive update. Existing IDs are updated;
missing IDs are retained, and favorites survive. The original bundled seed uses
INSERT IGNORE, so reopening the app does not overwrite imported records.

UTF-8 JSON, maximum 8 MiB and 20,000 records per pack:

```json
{
  "schemaVersion": 2,
  "topics": [{
    "id": "NEW-001",
    "title": "عنوان موضوع جديد",
    "category": "قسم صناعي",
    "source": "اسم مرجع القائمة",
    "sourceItem": 1,
    "status": "عنوان أولي — غير محقق",
    "originalText": "النص المنقول من المصدر",
    "originalNotes": "ملاحظة اختيارية"
  }]
}
```

IDs match `[A-Z0-9][A-Z0-9-]{0,63}`; source/item pairs must be unique within a
pack. All fields except originalNotes are mandatory. Empty packs, unsupported
schemas, duplicates, oversized records and reliability claims other than the
literal unverified title status are rejected before the transaction.

These are locally chosen, UNTRUSTED title packs: no scripts, HTML, URLs to
execute, operating instructions, formulas or reliability upgrades are parsed.
The app never authenticates a pack publisher or claims its technical accuracy.
This mechanism is intentionally limited to titles and transcribed source text.
Signed, versioned, reviewed scientific-content updates are a separate future
gate; do not represent this local import as that capability.
