# مصادر المحتوى الصناعي — الدفعة الأولى

## المصدر الأول: لستة الغراء واللواصق.doc
- 38 بندًا مرقمًا في صفحتين.
- عناوين مشروعات، وليست وصفات مثبتة أو نتائج اختبارات.
- أمثلة: غراء المكبس، غراء بوندي الحيواني، سيليكات الصوديوم، الهوت ميلت، لواصق الخشب والمعادن، لواصق الجوانات.

## المصدر الثاني: عوازل كهرباء وحرارة وكابلات.doc
- 33 بندًا مرقمًا في صفحتين (مع تجاهل الصفوف الفارغة اللاحقة).
- أمثلة: ورنيش عازل، عوازل محولات، كابلات 11 كيلوفولت، العزل المائي، مواد تبطين الأفران.

## ضوابط
- لا تستخدم ملف Hilti Products.xlsx؛ استبعده المالك صراحة.
- لا تستخدم الملف التعريفي لتطبيق Documents.
- أي نسب أو وصفات أو ادعاءات مقاومة للحرارة/الجهد/الماء تحتاج مصادر مستقلة واختبارات.
- تمييز واضح بين عنوان مشروع، وصفة منشورة، وصفة مختبرة، وصيغة معتمدة للإنتاج.
- لا يُنصح بتطبيق مواد عزل جهد عالٍ أو ملامسة مياه الشرب أو التطبيقات الطبية دون تقييم امتثال متخصص.

## 2026-10-27 reconciliation
Both owner documents were retrieved and read in full (two pages each). Schema 2
preserves the existing display title and adds `originalText` and `originalNotes`.
Transcriptions join wrapped lines, remove bidi controls and normalize obvious
text-extraction artifacts. They are not byte-exact facsimiles; final visual
reconciliation against rendered pages is pending. Ambiguous source spellings
(e.g. "بالستر", "البرسسور", "الجرنجريت") are retained, not asserted as chemical names.
ADH-017 retains the source's 20-minute claim; INS-001 retains BONDY notes;
INS-033 retains the two-component wording. These remain unverified claims.
Blank numbered rows 34–36 in the second source are not catalog products.

### First referenced educational additions
- PubChem Poly(vinyl acetate):
  https://pubchem.ncbi.nlm.nih.gov/compound/Polyvinyl%20acetate
  Scope: polymer identity/use in adhesives only; not a commercial grade or recipe.
- NCBI MeSH Polyvinyl Alcohol:
  https://www.ncbi.nlm.nih.gov/mesh/68011142
  Scope: distinction from acetate polymer and hydroxyl substitution.
- Henkel Hot melt adhesives:
  https://next.henkel-adhesives.com/us/en/products.html/producttype_2.3.5.4.1.4.html
  Scope: molten application, cooling bond and reactive hot-melt distinction.
All three reviewed 2026-10-27. Definitions are original short paraphrases; links
open externally. No processing temperatures, ratios, test performance, food,
medical or high-voltage suitability are inferred from these references.
Egyptian aliases are editorial ambiguity notes, not authoritative identities.

### Reliability model
UNVERIFIED, LITERATURE_REFERENCED, LABORATORY_TESTED, PRODUCTION_VALIDATED.
Tested/production status requires a test report reference; sensitive instructions
additionally require technical approval. No formulation in this release satisfies
these gates and no manufacturing recipe is bundled. The mass calculator is a
clearly labeled user-input arithmetic tool and cannot confer reliability status.
