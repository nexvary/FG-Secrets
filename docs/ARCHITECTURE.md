# Architecture decisions — 2026-10-27

- Kotlin/Compose, Navigation Compose and AndroidViewModel expose lifecycle-aware
  StateFlow. Room stores topic records and independent favorite keys; DataStore
  stores interface language. No network permission, telemetry or account system.
- Import validates the entire versioned JSON before one Room transaction. Topic
  upserts preserve independent favorites. No destructive database migration.
  Future schema changes require migrations; exported schemas belong in git.
- Display titles and normalized source transcriptions are separate. Unverified
  source claims remain visibly unverified. Search normalizes Arabic accents,
  tatweel and alef variants without altering the stored source text.
- BigDecimal calculations: mass only, explicit g/kg/t units, exactly 100 percent,
  no density or volume assumptions. Invalid, negative and ambiguous grouped
  inputs fail closed. Arabic and Persian digits and decimal separator work.
- Cost = sum of batch expenses. Saleable output = gross output * (1 - loss/100).
  Unit cost = batch cost / saleable output. Price = unit cost / (1 - margin/100).
  Margin means profit divided by price, not markup. Internal division precision
  is 12 decimals; UI displays up to 4. No currency conversion or live prices.
- Dark FG Machines identity, scalable text, scrollable content, safe Scaffold
  insets and IME padding. Arabic RTL and English LTR apply to the interface;
  original Arabic content is retained. No claim of 71 English translations.
- Broader sections are added when actual content exists. Current categories
  reflect the two supplied source lists, avoiding empty manufactured catalogues.
- Privacy: backup disabled, no sensitive permissions; reference links use the
  external browser and require internet. Full original DOCs are not bundled.
