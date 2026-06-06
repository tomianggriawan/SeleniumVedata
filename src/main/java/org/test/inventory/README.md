# Inventory DDT Framework - VEDATA

Framework Data-Driven Testing (DDT) dengan arsitektur **Page Object Model (POM)**  
untuk menguji sistem inventory di: `https://web.vedata.id/inventory/setting/item/form`

## Setup

### 1. Tempatkan File Excel
Letakkan file `Katalog_Produk_YAVA_Lengkap.xlsx` di:
```
src/main/resources/Katalog_Produk_YAVA_Lengkap.xlsx
```

### 2. Struktur Kolom Excel (header baris pertama)
| Kolom | Field | Contoh |
|-------|-------|--------|
| A | ItemName | "Minuman Energi ABC" |
| B | Brand | "ABC Corp" |
| C | Unit | "Karton" |
| D | Category | "Minuman / Energi" atau "Minuman" |
| E | OptionName | "Kemasan" |
| F | OptionVariant | "Botol 250ml" |

### 3. Jalankan Test
```bash
# Dengan rtk (token saving)
rtk mvn test

# Hanya TC01 (Dummy CRUD)
rtk mvn test -Dtest=InventoryTest#testDummyCRUD

# Hanya TC02 (Excel DDT)
rtk mvn test -Dtest=InventoryTest#testExcelCRU
```

## Struktur File
```
src/main/java/org/test/inventory/
├── ExcelReader.java      → Apache POI reader (LinkedHashSet unique + skip blank)
├── BaseTest.java         → @BeforeSuite login, @AfterSuite quit
├── UnitPage.java         → POM Tab Unit (CRUD)
├── CategoryPage.java     → POM Tab Category (Parent/Child logic)
├── OptionPage.java       → POM Tab Option (Add Variant)
├── ItemPage.java         → POM Tab Item (SKU, Barcode, Dimensi)
└── InventoryTest.java    → @Test TC01 Dummy CRUD + TC02 Excel CRU
```
