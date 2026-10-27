package com.fgmachines.secrets.data

/** Small curated starter dictionary. Definitions only; no formulation or suitability claims. */
data class Reference(val title: String, val url: String, val reviewedOn: String)
data class Material(val id: String, val arabic: String, val english: String, val aliases: String, val descriptionAr: String, val descriptionEn: String, val reference: Reference)
val dictionary = listOf(
    Material("MAT-PVAC", "بولي أسيتات الفينيل", "Poly(vinyl acetate) — PVAc", "الغراء الأبيض: اسم تجاري عام، وليس تحديدًا كيميائيًا كافيًا", "بوليمر يُستخدم في اللواصق. عبارة PVA قد تكون ملتبسة؛ افحص الاسم الكامل ونشرة المورد قبل اختيار الخامة.", "A polymer used in adhesives. The abbreviation PVA is ambiguous; confirm the full name and supplier data sheet.", Reference("PubChem — Poly(vinyl acetate)", "https://pubchem.ncbi.nlm.nih.gov/compound/Polyvinyl%20acetate", "2026-10-27")),
    Material("MAT-PVOH", "بولي كحول الفينيل", "Poly(vinyl alcohol) — PVOH / PVA", "PVA: قد يُقصد به بولي كحول الفينيل أو يُستعمل تجاريًا للغراء الأبيض", "بوليمر يختلف كيميائيًا عن بولي أسيتات الفينيل؛ يُحضر باستبدال مجموعات الأسيتات بمجموعات هيدروكسيل. لا يُستبدل أحدهما بالآخر اعتمادًا على الاختصار.", "Chemically distinct from poly(vinyl acetate); acetate groups are replaced with hydroxyl groups. An abbreviation alone does not establish interchangeability.", Reference("NCBI MeSH — Polyvinyl Alcohol", "https://www.ncbi.nlm.nih.gov/mesh/68011142", "2026-10-27"))
)
val hotMeltReference = Reference("Henkel — Hot melt adhesives", "https://next.henkel-adhesives.com/us/en/products.html/producttype_2.3.5.4.1.4.html", "2026-10-27")
