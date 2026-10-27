package com.fgmachines.secrets.data

/** Small curated starter dictionary. Definitions only; no formulation or suitability claims. */
data class Reference(val title: String, val url: String, val reviewedOn: String)
data class Material(val id: String, val arabic: String, val english: String, val aliases: String, val descriptionAr: String, val descriptionEn: String, val reference: Reference)
val dictionary = listOf(
    Material("MAT-PVAC", "بولي أسيتات الفينيل", "Poly(vinyl acetate) — PVAc", "الغراء الأبيض: اسم تجاري عام، وليس تحديدًا كيميائيًا كافيًا", "بوليمر يُستخدم في اللواصق. عبارة PVA قد تكون ملتبسة؛ افحص الاسم الكامل ونشرة المورد قبل اختيار الخامة.", "A polymer used in adhesives. The abbreviation PVA is ambiguous; confirm the full name and supplier data sheet.", Reference("PubChem — Poly(vinyl acetate)", "https://pubchem.ncbi.nlm.nih.gov/compound/Polyvinyl%20acetate", "2026-10-27")),
    Material("MAT-PVOH", "بولي كحول الفينيل", "Poly(vinyl alcohol) — PVOH / PVA", "PVA: قد يُقصد به بولي كحول الفينيل أو يُستعمل تجاريًا للغراء الأبيض", "بوليمر يختلف كيميائيًا عن بولي أسيتات الفينيل؛ يُحضر باستبدال مجموعات الأسيتات بمجموعات هيدروكسيل. لا يُستبدل أحدهما بالآخر اعتمادًا على الاختصار.", "Chemically distinct from poly(vinyl acetate); acetate groups are replaced with hydroxyl groups. An abbreviation alone does not establish interchangeability.", Reference("NCBI MeSH — Polyvinyl Alcohol", "https://www.ncbi.nlm.nih.gov/mesh/68011142", "2026-10-27"))
)
val hotMeltReference = Reference("Henkel — Hot melt adhesives", "https://next.henkel-adhesives.com/us/en/products.html/producttype_2.3.5.4.1.4.html", "2026-10-27")

/** Reference-backed notes are separate from unverified source-product records. */
data class KnowledgeNote(val titleAr: String, val titleEn: String, val textAr: String, val textEn: String, val reference: Reference)
val knowledgeNotes: Map<String, List<KnowledgeNote>> = mapOf(
    "ADH-007" to listOf(KnowledgeNote(
        "تخفيف الغراء: لماذا لا نعمم نسبة واحدة؟", "Dilution depends on the actual product",
        "تذكر Titebond أن التخفيف الزائد لبعض لواصقها المائية يقلل قوة الربط، وأن معظم منتجاتها مصممة للخشب غير المدهون. هذه ملاحظات لمنتجات الشركة وليست وصفة تصنيع أو قاعدة لكل غراء أبيض. راجع نشرة الدرجة المستخدمة، ولا تستنتج هويتها من اختصار PVA.",
        "Titebond warns that excessive dilution can reduce the bond strength of its water-based glues; most are designed for bare wood. These are supplier-specific observations, not a manufacturing recipe or a rule for every white glue. Check the actual grade's data sheet; PVA alone does not identify it.",
        Reference("Titebond — Wood glue FAQs", "https://titebond.com/resources/use/glues/faqs", "2026-10-27")
    )),
    "ADH-019" to listOf(
        KnowledgeNote("عيب شائع: التفحم", "Common defect: char",
            "تربط Henkel التفحم بالتعرض الحراري والتلوث والأكسدة؛ وقد تسد الرواسب المرشحات والخراطيم والفوهات. هذا مدخل لتحديد السبب، وليس تعليمات لضبط حرارة أو تنظيف معدات بعينها. يلزم الرجوع لدليل المعدة ونشرة اللاصق.",
            "Henkel associates char with heat exposure, contamination and oxidation. Deposits can obstruct filters, hoses and nozzles. This helps identify causes; it is not a temperature-setting or equipment-cleaning procedure. Consult the equipment manual and adhesive data sheet.",
            Reference("Henkel — Preventing hot melt adhesive char", "https://next.henkel-adhesives.com/my/en/articles/preventing-hot-melt-adhesive-char.html", "2026-10-27")),
        KnowledgeNote("فحص عيب فتح العبوة بعد اللصق", "Inspecting package pop opens",
            "تقترح Henkel تتبع وقت ومكان الفشل، وانتقال اللاصق للسطحين، وتمزق الألياف، وظروف الحرارة، وأي تغير بالخامة أو العملية. سجّل هذه الملاحظات قبل تغيير الإعدادات. أسباب متعددة قد تعطي عيبًا متشابهًا؛ لا توجد قيمة ضبط واحدة صالحة لكل خط.",
            "Henkel's diagnostic questions cover failure timing and location, adhesive transfer, fiber tear, ambient temperature and recent process or substrate changes. Record these observations before changing settings. Similar defects can have different causes; one setting does not fit every line.",
            Reference("Henkel — Troubleshooting hot melt pop opens", "https://next.henkel-adhesives.com/us/en/articles/troubleshooting-hot-melt-pop-opens.html", "2026-10-27"))
    ),
    "ADH-011" to listOf(KnowledgeNote("اختيار درجة سيليكات الصوديوم", "Selecting a sodium silicate grade",
        "توضح PQ أن نسبة السيليكا إلى أكسيد الصوديوم تؤثر في الذوبان والجفاف واللزوجة والكثافة، وتُختار الدرجة حسب الاستخدام. اسم سيليكات الصوديوم وحده لا يكفي لتحديد لاصق أو أداء عزل. لا تتضمن هذه المعلومة نسب تصنيع أو اعتمادًا كهربائيًا.",
        "PQ explains that the silica-to-sodium-oxide ratio affects solubility, drying, viscosity and density; grade selection depends on the application. The name alone does not define an adhesive or insulation performance. This note provides no recipe or electrical approval.",
        Reference("PQ — Sodium silicate products", "https://www.pqcorp.com/products/", "2026-10-27"))),
    "ADH-002" to listOf(KnowledgeNote("السيليكون: نوع التصلب وتوافق السطح", "Silicone cure type and substrate compatibility",
        "تعرض WACKER منتجات محايدة التصلب لأسطح لا تتحمل التصلب الأسيتوكسي. لذلك لا يكفي وصف المنتج بأنه سيليكون لاختيار مادة للأنابيب. يجب تحديد الدرجة والسطح والاستخدام من نشرتها؛ اسم المادة لا يثبت صلاحيتها لمياه الشرب أو الضغط.",
        "WACKER describes neutral-curing products for substrates that cannot tolerate acetoxy curing. Silicone alone is therefore not a sufficient specification for a pipe sealant. Confirm grade, substrate and intended use from its data sheet; the material name does not establish drinking-water or pressure suitability.",
        Reference("WACKER — Silicone sealants overview", "https://www.wacker.com/h/en-eg/medias/1026-EN.pdf", "2026-10-27")))
)
