package org.test.inventory;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * ExcelReader - Utility Apache POI untuk membaca data Katalog_Produk_YAVA_Lengkap.xlsx.
 *
 * Tanggung Jawab (Single Responsibility):
 * - Membaca file .xlsx dan mengembalikan list data unik.
 *
 * Struktur Kolom Excel (kolom A–F, header di baris pertama):
 *   A: ItemName   — Nama Item
 *   B: Brand      — Brand
 *   C: Unit       — Satuan
 *   D: Category   — Kategori (bisa "Parent / Child" atau "Parent")
 *   E: OptionName — Nama Option/Varian
 *   F: OptionVariant — Nilai Variant (misal: "250ml", "500ml")
 */
public class ExcelReader {

    private static final int COL_ITEM_NAME     = 0;
    private static final int COL_BRAND         = 1;
    private static final int COL_UNIT          = 2;
    private static final int COL_CATEGORY      = 3;
    private static final int COL_OPTION_NAME   = 4;
    private static final int COL_OPTION_VARIANT = 5;

    // ==================== Data Model ====================

    /**
     * InventoryRow - Representasi satu baris data inventory dari Excel.
     * Keunikan ditentukan oleh ItemName (case-insensitive).
     */
    public static class InventoryRow {
        public final String itemName;
        public final String brand;
        public final String unit;
        public final String category;
        public final String optionName;
        public final String optionVariant;

        public InventoryRow(String itemName, String brand, String unit,
                            String category, String optionName, String optionVariant) {
            this.itemName      = itemName.trim();
            this.brand         = brand.trim();
            this.unit          = unit.trim();
            this.category      = category.trim();
            this.optionName    = optionName.trim();
            this.optionVariant = optionVariant.trim();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InventoryRow)) return false;
            return this.itemName.equalsIgnoreCase(((InventoryRow) o).itemName);
        }

        @Override
        public int hashCode() {
            return itemName.toLowerCase().hashCode();
        }

        @Override
        public String toString() {
            return String.format("[ItemName='%s', Brand='%s', Unit='%s', Category='%s', OptionName='%s', OptionVariant='%s']",
                    itemName, brand, unit, category, optionName, optionVariant);
        }
    }

    // ==================== Public API ====================

    /**
     * Membaca file Excel dan mengembalikan List data unik (tanpa duplikat ItemName).
     * Baris dengan kolom kosong akan dilewati (skip).
     *
     * @param filePath Path absolut ke file .xlsx
     * @return List unik InventoryRow berurutan sesuai urutan di Excel
     * @throws IOException jika file tidak ditemukan atau tidak dapat dibaca
     */
    public static List<InventoryRow> readUniqueRows(String filePath) throws IOException {
        LinkedHashSet<InventoryRow> uniqueSet = new LinkedHashSet<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                System.out.println("  [WARN] ExcelReader: Sheet ke-0 tidak ditemukan.");
                return new ArrayList<>();
            }

            int totalRows     = sheet.getLastRowNum();
            int skippedCount  = 0;
            int duplicateCount = 0;

            System.out.println("  [INFO] ExcelReader: Membaca " + totalRows + " baris data dari '" + filePath + "'...");

            for (int rowIdx = 1; rowIdx <= totalRows; rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) { skippedCount++; continue; }

                String itemName      = getCellStringValue(row, COL_ITEM_NAME);
                String brand         = getCellStringValue(row, COL_BRAND);
                String unit          = getCellStringValue(row, COL_UNIT);
                String category      = getCellStringValue(row, COL_CATEGORY);
                String optionName    = getCellStringValue(row, COL_OPTION_NAME);
                String optionVariant = getCellStringValue(row, COL_OPTION_VARIANT);

                if (isBlank(itemName) || isBlank(brand) || isBlank(unit) ||
                    isBlank(category) || isBlank(optionName) || isBlank(optionVariant)) {
                    System.out.println("  [SKIP] Baris " + (rowIdx + 1) + ": ada cell kosong, diabaikan.");
                    skippedCount++;
                    continue;
                }

                InventoryRow newRow = new InventoryRow(itemName, brand, unit, category, optionName, optionVariant);
                if (!uniqueSet.add(newRow)) {
                    System.out.println("  [DUPLICATE] Baris " + (rowIdx + 1) + ": ItemName '" + itemName + "' sudah ada, diabaikan.");
                    duplicateCount++;
                }
            }

            System.out.println("  [INFO] ExcelReader: Selesai. Total unik=" + uniqueSet.size()
                    + ", Skipped=" + skippedCount + ", Duplicate=" + duplicateCount);
        }

        return new ArrayList<>(uniqueSet);
    }

    /**
     * Mendapatkan daftar Unit yang unik dari data Excel.
     */
    public static List<String> getUniqueUnits(List<InventoryRow> rows) {
        LinkedHashSet<String> unitSet = new LinkedHashSet<>();
        for (InventoryRow row : rows) {
            if (!isBlank(row.unit)) unitSet.add(row.unit.trim());
        }
        return new ArrayList<>(unitSet);
    }

    /**
     * Mendapatkan daftar Category yang unik dari data Excel.
     */
    public static List<String> getUniqueCategories(List<InventoryRow> rows) {
        LinkedHashSet<String> catSet = new LinkedHashSet<>();
        for (InventoryRow row : rows) {
            if (!isBlank(row.category)) catSet.add(row.category.trim());
        }
        return new ArrayList<>(catSet);
    }

    /**
     * Mendapatkan pasangan unik [OptionName, OptionVariant] dari data Excel.
     * De-duplikasi berdasarkan OptionName sebagai key.
     */
    public static List<String[]> getUniqueOptions(List<InventoryRow> rows) {
        LinkedHashMap<String, String> optMap = new LinkedHashMap<>();
        for (InventoryRow row : rows) {
            if (!isBlank(row.optionName) && !optMap.containsKey(row.optionName.trim())) {
                optMap.put(row.optionName.trim(), row.optionVariant.trim());
            }
        }
        List<String[]> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : optMap.entrySet()) {
            result.add(new String[]{entry.getKey(), entry.getValue()});
        }
        return result;
    }

    // ==================== Private Helpers ====================

    /**
     * Membaca nilai string dari cell dengan menangani berbagai CellType.
     */
    private static String getCellStringValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double numVal = cell.getNumericCellValue();
                return (numVal == Math.floor(numVal))
                    ? String.valueOf((long) numVal)
                    : String.valueOf(numVal);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
            case _NONE:
            default:
                return "";
        }
    }

    /**
     * Cek apakah string kosong, null, atau hanya whitespace.
     */
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
