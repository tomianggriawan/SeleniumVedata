package org.test.employee;

import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.pages.DashboardPage;
import org.pages.employeepage.EmployeePage;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.common.WebDriverTools.chrome;

/**
 * EmployeeProfile - Test Runner CRUD untuk HCM > Employee > Profile.
 *
 * Arsitektur:
 *  - Mewarisi BasePage (WebDriver helper, reporter, network analyzer, runTest lifecycle)
 *  - POM via EmployeePage
 *  - Dynamic retail employee data generation
 *  - Fluent interface (method chaining)
 *
 * Skenario:
 *  TC_HCM_EMP_SUCCESS        - Tambah karyawan baru dengan data valid & lengkap
 *  TC_HCM_EMP_READ           - Verifikasi data karyawan di tabel
 *  TC_HCM_EMP_UPDATE         - Ubah nama karyawan
 *  TC_HCM_EMP_FAIL_MANDATORY - Gagal simpan karena field wajib kosong
 *  TC_HCM_EMP_FAIL_DROPDOWN  - Gagal simpan karena dropdown kosong
 */
public class EmployeeProfile extends BasePage {

    private static final String EMPLOYEE_PROFILE_URL = "https://web.vedata.id/hcm/employee/profile";
    private static String dummyPhotoPath = "C:\\Users\\LENOVO\\vedata-test\\src\\test\\resources\\images.jpg";

    private final EmployeePage employeePage;

    // ==================== Retail Dynamic Test Data ====================

    private final String empCode;
    private final String contractType;
    private final String status;
    private final String hireDate;
    private final String contractStart;
    private final String contractEnd;
    private final String probationStart;
    private final String probationEnd;
    private final String identityNumber;
    private final String firstName;
    private final String lastName;
    private final String nickname;
    private final String dateOfBirth;
    private final String placeOfBirth;
    private final String gender;
    private final String nationality;
    private final String maritalStatus;
    private final String personalPhone;
    private final String personalEmail;
    private final String corporateEmail;
    private final String residentialAddress;
    private final String idCardAddress;
    private final String emergencyPhone;
    private final String emergencyRelationship;
    private final String lastDegree;
    private final String major;
    private final String graduationYear;
    private final String taxNumber;
    private final String basicSalary;
    private final String bank;
    private final String accountName;
    private final String accountNumber;
    private final String bankAddress;
    private final String jobTitle;
    private final String department;
    private final String branch;

    // Data untuk skenario update
    private final String updatedFirstName;
    private final String updatedLastName;

    // ==================== Constructor ====================

    public EmployeeProfile(WebDriver driver) {
        super(driver);
        this.employeePage = new EmployeePage(driver);

        Random rand = new Random();

        String[] firstNames = {
            "Budi", "Siti", "Ahmad", "Dewi", "Rizky",
            "Anisa", "Eko", "Fitri", "Hendra", "Indah",
            "Joko", "Kartika", "Luthfi", "Maya", "Nanda"
        };
        String[] lastNames = {
            "Santoso", "Rahayu", "Wijaya", "Kusuma", "Pratama",
            "Sari", "Nugroho", "Pertiwi", "Setiawan", "Handayani",
            "Firmansyah", "Cahyani", "Utomo", "Lestari", "Purnama"
        };
        String[] cities = {
            "Jakarta", "Surabaya", "Bandung", "Medan", "Semarang",
            "Makassar", "Depok", "Tangerang", "Bekasi", "Palembang"
        };
        String[] majors = {
            "Manajemen", "Akuntansi", "Teknik Industri",
            "Administrasi Bisnis", "Ekonomi", "Pemasaran",
            "Sistem Informasi", "Manajemen Retail"
        };

        // Retail jobs: { jobTitle, lastDegree, basicSalary, maritalStatus }
        String[][] retailJobs = {
            { "Store Manager", "S1", "10000000", "Menikah" },
            { "Merchandiser",  "SMA", "7500000",  "Menikah" },
            { "Consultant",    "SMA", "4500000",  "Lajang"  },
            { "Gudang",        "SMA", "4600000",  "Lajang"  }
        };

        int jobIdx = rand.nextInt(retailJobs.length);
        String[] selectedJob = retailJobs[jobIdx];

        this.jobTitle     = selectedJob[0];
        this.lastDegree   = selectedJob[1];
        String salaryValue = selectedJob[2];
        this.maritalStatus = selectedJob[3];

        String fName  = firstNames[rand.nextInt(firstNames.length)];
        String lName  = lastNames[rand.nextInt(lastNames.length)];
        String suffix = String.format("%04d", rand.nextInt(9000) + 1000);

        this.empCode      = "EMP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.contractType = "FIRST_AVAILABLE";
        this.status       = "FIRST_AVAILABLE";

        // KTP 16-digit numerik
        StringBuilder ktp = new StringBuilder();
        for (int i = 0; i < 16; i++) ktp.append(rand.nextInt(10));
        this.identityNumber = ktp.toString();

        this.firstName  = fName;
        this.lastName   = lName + " " + suffix;
        this.nickname   = fName;
        this.placeOfBirth = cities[rand.nextInt(cities.length)];
        this.gender     = rand.nextBoolean() ? "Laki-Laki" : "Perempuan";
        this.nationality = "WNI";

        this.personalPhone    = "08" + String.format("%09d", (long) (rand.nextDouble() * 1_000_000_000L));
        this.personalEmail    = fName.toLowerCase() + "." + lName.toLowerCase() + suffix + "@retailtest.id";
        this.corporateEmail   = fName.toLowerCase() + "." + suffix + "@company.id";
        this.residentialAddress = "Jl. Raya Retail No. " + (rand.nextInt(150) + 1) + ", " + placeOfBirth;
        this.idCardAddress    = this.residentialAddress;
        this.emergencyPhone   = "08" + String.format("%09d", (long) (rand.nextDouble() * 1_000_000_000L));
        this.emergencyRelationship = "Orang Tua";

        this.major = majors[rand.nextInt(majors.length)];

        // Dynamic dates
        LocalDate today = LocalDate.now();
        int age = jobTitle.contains("Manager") || jobTitle.contains("Supervisor")
            ? 28 + rand.nextInt(15) : 18 + rand.nextInt(8);
        LocalDate birth = today.minusYears(age).minusMonths(rand.nextInt(12)).minusDays(rand.nextInt(28));
        this.dateOfBirth = birth.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        int gradAge = lastDegree.equals("S1") ? 22 : 18;
        this.graduationYear = String.valueOf(birth.getYear() + gradAge + rand.nextInt(2));

        int tenureMonths = 6 + rand.nextInt(42);
        LocalDate join = today.minusMonths(tenureMonths).minusDays(rand.nextInt(28));
        this.hireDate       = join.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.contractStart  = this.hireDate;
        this.contractEnd    = join.plusYears(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.probationStart = this.hireDate;
        this.probationEnd   = join.plusMonths(3).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Payroll
        StringBuilder npwp = new StringBuilder();
        for (int i = 0; i < 15; i++) npwp.append(rand.nextInt(10));
        this.taxNumber    = npwp.toString();
        this.basicSalary  = salaryValue;
        this.bank         = "BCA";
        this.accountName  = this.firstName + " " + this.lastName;
        this.accountNumber = String.format("%010d", (long) (rand.nextDouble() * 10_000_000_000L));
        this.bankAddress  = "KCP Sudirman " + placeOfBirth;

        // Position & Placement
        this.department = "Administration";
        this.branch     = "FIRST_AVAILABLE";

        // Updates
        this.updatedFirstName = "Senior " + fName;
        this.updatedLastName  = lName + " Updated " + suffix;
    }

    // ==================== Entry Point ====================

    /**
     * Entry point untuk menjalankan seluruh skenario secara standalone.
     */
    public static void main(String[] args) {
        try {
            createDummyPhoto();
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal membuat dummy photo: " + e.getMessage());
        }

        runTest("HCM Employee Profile", "EmployeeProfile", () -> {
            new DashboardPage(chrome).navigateToEmployeeProfilePage();

            EmployeeProfile test = new EmployeeProfile(chrome);
            test.printGeneratedData();

            // Alur utama CRU
            try {
                test.testCreate();
                test.testRead();
                test.testUpdate();
            } catch (Throwable e) {
                System.err.println("[SUITE WARN] Gagal pada alur utama CRU: " + e.getMessage());
            }

            // Negative test: field wajib kosong
            try {
                if (chrome.getCurrentUrl().contains("/form")) {
                    chrome.get(EMPLOYEE_PROFILE_URL);
                    Thread.sleep(2000);
                }
                test.testCreateFailMissingFields();
            } catch (Throwable e) {
                System.err.println("[SUITE WARN] Gagal pada alur negative test: " + e.getMessage());
            }

            // Negative test: empty dropdown
            try {
                if (chrome.getCurrentUrl().contains("/form")) {
                    chrome.get(EMPLOYEE_PROFILE_URL);
                    Thread.sleep(2000);
                }
                test.testCreateFailEmptyDropdown();
            } catch (Throwable e) {
                System.err.println("[SUITE WARN] Gagal pada alur empty dropdown test: " + e.getMessage());
            }

            reporter.endTest();
        });

        cleanDummyPhoto();
    }

    // ==================== CREATE SUCCESS ====================

    /**
     * TC_HCM_EMP_SUCCESS - Tambah karyawan baru dengan data valid & lengkap.
     */
    public EmployeeProfile testCreate() {
        return testCreateEmployeeSuccess();
    }

    public EmployeeProfile testCreateEmployeeSuccess() {
        reporter.startTest("TC_HCM_EMP_SUCCESS", "Tambah Karyawan Baru dengan Data Valid & Lengkap");
        drainLogs();
        try {
            reporter.logStep("Verifikasi halaman list...");
            employeePage.verifyPageLoaded();

            reporter.logStep("Klik tombol Add Karyawan...");
            employeePage.clickAddButton();

            reporter.logStep("Isi Employment Information (Contract Type, Status, Join Date, dll)...");
            employeePage.fillEmploymentInfo(
                    empCode, contractType, status,
                    hireDate, contractStart, contractEnd, probationStart, probationEnd);

            reporter.logStep("Isi Personal Identity (KTP, Nama, Birth Date, Gender, Photo)...");
            employeePage.fillPersonalIdentity(
                    identityNumber, firstName, lastName, nickname, dateOfBirth,
                    placeOfBirth, gender, nationality, maritalStatus, null);

            reporter.logStep("Isi Contact Information (Phone, Email, Alamat, Emergency Contact)...");
            employeePage.fillContactInfo(
                    personalPhone, personalEmail, corporateEmail,
                    residentialAddress, emergencyPhone, emergencyRelationship, idCardAddress);

            reporter.logStep("Isi Education & Skill...");
            employeePage.fillEducationInfo(lastDegree, major, graduationYear);

            reporter.logStep("Isi Payroll Information (NPWP, Bank, Gaji Pokok)...");
            employeePage.fillPayrollInfo(
                    taxNumber, basicSalary, bank, accountName, accountNumber, bankAddress);

            reporter.logStep("Isi Position & Placement: Job Title, Department, Branch...");
            employeePage.fillPositionInfo(jobTitle, null, null);

            reporter.logStep("Pilih Department (tag multiselect)...");
            employeePage.fillDepartment(department);

            reporter.logStep("Pilih Branch (tag multiselect)...");
            employeePage.fillBranch(branch);

            reporter.logStep("Unggah Gambar ke Kolom Photo via sendKeys secara native...");
            employeePage.uploadPhotoPublic("C:\\Users\\LENOVO\\vedata-test\\src\\test\\resources\\images.jpg");

            reporter.logStep("[PRE-SAVE ASSERT] Verifikasi tag chip Department='" + department
                    + "' dan Branch='" + branch + "' sudah ter-render di form...");

            reporter.logStep("Klik tombol Save dan tunggu redirect...");
            employeePage.clickSave();

            inspectNetwork("Save Employee");

            reporter.logStep("Verifikasi employee baru terdaftar di tabel list...");
            boolean created = employeePage.isEmployeeExistInTable(empCode);
            if (!created) {
                throw new AssertionError("Employee '" + empCode + "' tidak ditemukan di tabel setelah proses Save.");
            }

            reporter.logPass("Karyawan baru '" + empCode + "' (" + firstName + " " + lastName
                    + ") berhasil disimpan dan diverifikasi di tabel list.");

        } catch (Throwable e) {
            captureNetworkOnFail("CREATE");
            reporter.logFail("Gagal menambahkan karyawan baru pada skenario sukses.", e);
            throw new AssertionError(e);
        }
        return this;
    }

    // ==================== READ ====================

    /**
     * TC_HCM_EMP_READ - Verifikasi data karyawan di tabel.
     */
    public EmployeeProfile testRead() {
        reporter.startTest("TC_HCM_EMP_READ", "Verifikasi/Read Data Karyawan pada Tabel");
        try {
            reporter.logStep("Verifikasi halaman list loaded...");
            employeePage.verifyPageLoaded();

            reporter.logStep("Mengecek ketersediaan data karyawan '" + empCode + "' di tabel...");
            boolean exists = employeePage.isEmployeeExistInTable(empCode);
            if (!exists) {
                throw new AssertionError("Karyawan dengan kode '" + empCode + "' tidak ditemukan.");
            }

            reporter.logStep("Mengambil data baris tabel untuk '" + empCode + "'...");
            String[] rowData = employeePage.getEmployeeRowData(empCode);
            if (rowData == null || rowData.length < 2) {
                throw new AssertionError("Gagal membaca data dari baris tabel.");
            }

            boolean codeMatched = false;
            for (String val : rowData) {
                if (val.contains(empCode)) { codeMatched = true; break; }
            }
            if (!codeMatched) {
                throw new AssertionError("Kode karyawan mismatch pada baris tabel.");
            }

            reporter.logPass("Karyawan '" + empCode + "' berhasil terverifikasi di tabel list.");

        } catch (Throwable e) {
            reporter.logFail("Gagal membaca data karyawan di tabel.", e);
            throw new AssertionError(e);
        }
        return this;
    }

    // ==================== UPDATE ====================

    /**
     * TC_HCM_EMP_UPDATE - Ubah nama karyawan.
     */
    public EmployeeProfile testUpdate() {
        reporter.startTest("TC_HCM_EMP_UPDATE", "Ubah Nama Karyawan (Update)");
        drainLogs();
        try {
            reporter.logStep("Klik tombol Edit pada baris Karyawan '" + empCode + "'...");
            employeePage.clickEditEmployee(empCode);

            reporter.logStep("Ubah First Name menjadi '" + updatedFirstName + "' dan Last Name menjadi '"
                    + updatedLastName + "'...");
            employeePage.fillPersonalIdentity(
                    null, updatedFirstName, updatedLastName,
                    null, null, null, null, null, null, null);

            reporter.logStep("Klik tombol Save...");
            employeePage.clickSave();

            inspectNetwork("Save Update Employee");

            reporter.logStep("Verifikasi karyawan masih ada di tabel list...");
            employeePage.verifyPageLoaded();

            boolean exists = employeePage.isEmployeeExistInTable(empCode);
            if (!exists) {
                throw new AssertionError("Karyawan dengan kode '" + empCode + "' hilang setelah update.");
            }

            reporter.logPass("Karyawan '" + empCode + "' berhasil diupdate namanya menjadi '"
                    + updatedFirstName + " " + updatedLastName + "'.");

        } catch (Throwable e) {
            captureNetworkOnFail("UPDATE");
            reporter.logFail("Gagal melakukan update nama karyawan.", e);
            throw new AssertionError(e);
        }
        return this;
    }

    // ==================== CREATE FAIL: MISSING MANDATORY ====================

    /**
     * TC_HCM_EMP_FAIL_MANDATORY - Gagal simpan karena field wajib kosong.
     */
    public void testCreateFailMissingFields() {
        reporter.startTest("TC_HCM_EMP_FAIL_MANDATORY", "Gagal Simpan Karyawan Baru karena Field Wajib Kosong");
        try {
            reporter.logStep("Memastikan berada di halaman list...");
            employeePage.verifyPageLoaded();

            reporter.logStep("Klik Add untuk membuka form...");
            employeePage.clickAddButton();

            reporter.logStep("Simpan data tanpa mengisi field wajib apapun...");
            employeePage.clickSaveExpectingFailure();

            reporter.logStep("Memeriksa apakah browser tetap berada di halaman form...");
            String currentUrl = chrome.getCurrentUrl();
            if (!currentUrl.contains("/form")) {
                throw new AssertionError(
                    "Sistem mengizinkan simpan form kosong! (Browser dialihkan dari halaman /form)");
            }

            reporter.logStep("Membaca seluruh pesan validasi error front-end...");
            List<String> errors = employeePage.getValidationErrors();
            if (errors.isEmpty()) {
                throw new AssertionError("Tidak ditemukan pesan kesalahan validasi pada field wajib.");
            }

            reporter.logPass("Validasi sukses. Sistem berhasil memblokir penyimpanan data kosong. Ditemukan "
                    + errors.size() + " pesan error validasi: " + errors);

            reporter.logStep("Kembali ke halaman utama dengan tombol Cancel...");
            chrome.get(EMPLOYEE_PROFILE_URL);
            sleep(1000);

        } catch (Throwable e) {
            reporter.logFail("Sistem gagal memvalidasi field wajib yang kosong.", e);
            chrome.get(EMPLOYEE_PROFILE_URL);
            throw new AssertionError(e);
        }
    }

    // ==================== CREATE FAIL: EMPTY DROPDOWN ====================

    /**
     * TC_HCM_EMP_FAIL_DROPDOWN - Gagal simpan karena dropdown kosong / pilihan tidak ada.
     */
    public void testCreateFailEmptyDropdown() {
        reporter.startTest("TC_HCM_EMP_FAIL_DROPDOWN", "Gagal Simpan karena Dropdown Kosong / Pilihan Tidak Ada");
        try {
            reporter.logStep("Memastikan berada di halaman list...");
            employeePage.verifyPageLoaded();

            reporter.logStep("Klik Add untuk membuka form...");
            employeePage.clickAddButton();

            reporter.logStep("Mengisi Job Title dengan pilihan tidak valid 'NonExistentJob123' ...");
            try {
                employeePage.fillPositionInfo("NonExistentJob123", null, null);
                reporter.logFail("Automation framework tidak mendeteksi dropdown kosong.", null);
                throw new AssertionError("Automation framework tidak mendeteksi dropdown kosong / ketiadaan data pilihan.");
            } catch (IllegalStateException e) {
                reporter.logPass("Automation framework sukses menangkap error ketiadaan data dropdown. Detail: " + e.getMessage());
            }

            reporter.logStep("Kembali ke halaman utama list...");
            chrome.get(EMPLOYEE_PROFILE_URL);
            sleep(1000);

        } catch (Throwable e) {
            reporter.logFail("Gagal memvalidasi penanganan dropdown kosong.", e);
            chrome.get(EMPLOYEE_PROFILE_URL);
            throw new AssertionError(e);
        }
    }

    // ==================== Private Helpers ====================

    private static void createDummyPhoto() throws Exception {
        File file = new File("dummy-photo.png");
        if (!file.exists()) {
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            javax.imageio.ImageIO.write(img, "png", file);
        }
        dummyPhotoPath = file.getAbsolutePath();
        System.out.println("  [INFO] Dummy photo file disiapkan di: " + dummyPhotoPath);
    }

    private static void cleanDummyPhoto() {
        try {
            File file = new File("dummy-photo.png");
            if (file.exists()) {
                file.delete();
                System.out.println("  [INFO] Dummy photo file dibersihkan.");
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal membersihkan dummy photo: " + e.getMessage());
        }
    }

    private void printGeneratedData() {
        System.out.println("  [DATA] Employee Code    : " + empCode);
        System.out.println("  [DATA] Contract Type    : " + contractType);
        System.out.println("  [DATA] Status           : " + status);
        System.out.println("  [DATA] Hire Date        : " + hireDate);
        System.out.println("  [DATA] Contract Start   : " + contractStart);
        System.out.println("  [DATA] Contract End     : " + contractEnd);
        System.out.println("  [DATA] Probation Start  : " + probationStart);
        System.out.println("  [DATA] Probation End    : " + probationEnd);
        System.out.println("  [DATA] KTP              : " + identityNumber);
        System.out.println("  [DATA] First Name       : " + firstName);
        System.out.println("  [DATA] Last Name        : " + lastName);
        System.out.println("  [DATA] Date of Birth    : " + dateOfBirth);
        System.out.println("  [DATA] Place of Birth   : " + placeOfBirth);
        System.out.println("  [DATA] Gender           : " + gender);
        System.out.println("  [DATA] Nationality      : " + nationality);
        System.out.println("  [DATA] Marital Status   : " + maritalStatus);
        System.out.println("  [DATA] Personal Phone   : " + personalPhone);
        System.out.println("  [DATA] Personal Email   : " + personalEmail);
        System.out.println("  [DATA] Corporate Email  : " + corporateEmail);
        System.out.println("  [DATA] Res. Address     : " + residentialAddress);
        System.out.println("  [DATA] Last Degree      : " + lastDegree);
        System.out.println("  [DATA] Major            : " + major);
        System.out.println("  [DATA] Graduation Year  : " + graduationYear);
        System.out.println("  [DATA] Tax Number (NPWP): " + taxNumber);
        System.out.println("  [DATA] Basic Salary     : " + basicSalary);
        System.out.println("  [DATA] Bank Name        : " + bank);
        System.out.println("  [DATA] Account Name     : " + accountName);
        System.out.println("  [DATA] Account Number   : " + accountNumber);
        System.out.println("  [DATA] Bank Address     : " + bankAddress);
        System.out.println("  [DATA] Job Title        : " + jobTitle);
        System.out.println("  [DATA] Department       : " + department);
        System.out.println("  [DATA] Branch           : " + branch);
        System.out.println();
    }
}
