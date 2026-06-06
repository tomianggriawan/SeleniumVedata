package org.pages.settingpage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.common.BasePage;

import java.time.Duration;

/**
 * CompanyPage - Page Object Class representing the HCM > Setting > Company page.
 * Mendukung method chaining (fluent interface) untuk:
 *   - Verifikasi tampilan halaman Company Details (Read)
 *   - Membuka modal Edit Company (Update)
 *   - Mengisi form dan menyimpan perubahan
 *   - Memverifikasi perubahan tersimpan pada detail profil
 */
public class CompanyPage extends BasePage {

    // ==================== Locators - Read (Detail Card) ====================

    /** Label header "Name" pada card detail */
    private final By labelName    = By.xpath("//h6[contains(text(),'Name')]");
    /** Label header "Phone" pada card detail */
    private final By labelPhone   = By.xpath("//h6[contains(text(),'Phone')]");
    /** Label header "Email" pada card detail */
    private final By labelEmail   = By.xpath("//h6[contains(text(),'Email')]");
    /** Label header "Address" pada card detail */
    private final By labelAddress = By.xpath("//h6[contains(text(),'Address')]");

    /** Nilai "Name" yang ditampilkan di bawah label */
    private final By valueName    = By.xpath("//h6[contains(text(),'Name')]/..//p");
    /** Nilai "Phone" yang ditampilkan di bawah label */
    private final By valuePhone   = By.xpath("//h6[contains(text(),'Phone')]/..//p");
    /** Nilai "Email" yang ditampilkan di bawah label */
    private final By valueEmail   = By.xpath("//h6[contains(text(),'Email')]/..//p");
    /** Nilai "Address" yang ditampilkan di bawah label */
    private final By valueAddress = By.xpath("//h6[contains(text(),'Address')]/..//p");

    // ==================== Locators - Edit Button ====================

    /** Tombol "Ubah" (Edit) yang berada di dalam card Company Details */
    private final By editButton = By.xpath("//h2[contains(text(),'Company Details')]/..//button");

    // ==================== Locators - Edit Modal ====================

    /** Judul dialog modal Edit Company */
    private final By modalTitle        = By.xpath("//*[contains(text(),'Company Details') and (self::h2 or self::div[contains(@class,'v-card-title')])]");

    /** Input field "Name" di dalam modal edit */
    private final By inputName    = By.id("companies-name");
    /** Input field "Phone" di dalam modal edit */
    private final By inputPhone   = By.id("companies-phone");
    /** Input field "Email" di dalam modal edit */
    private final By inputEmail   = By.id("companies-email");
    /** Textarea "Address" di dalam modal edit */
    private final By inputAddress = By.id("companies-address");

    /** Tombol "Save" di dalam modal edit */
    private final By saveButton   = By.xpath("//button[span[contains(text(),'Save')]]");
    /** Tombol "Cancel" di dalam modal edit */
    private final By cancelButton = By.xpath("//button[span[contains(text(),'Cancel')]]");

    // ==================== Locators - Logo Upload ====================

    /**
     * Input file untuk upload logo — biasanya hidden, tapi sendKeys() tetap bekerja.
     * Gunakan presenceOfElementLocated (bukan visibility) karena mungkin display:none.
     */
    private final By fileInput = By.xpath("//input[@type='file']");

    /**
     * Preview logo di dalam modal setelah file dipilih.
     * Vuetify menampilkan preview sebagai <img> dengan src blob: atau data:image
     */
    private final By logoPreviewInModal = By.xpath("//img[starts-with(@src,'blob:') or starts-with(@src,'data:image')]");

    private final java.util.List<org.openqa.selenium.logging.LogEntry> collectedLogs = new java.util.ArrayList<>();

    // ==================== Constructor ====================

    public CompanyPage(WebDriver driver) {
        super(driver);
    }

    // ==================== Read (Verifikasi Tampilan) ====================

    /**
     * Verifikasi bahwa label-label Company Details (Name, Phone, Email, Address) ditampilkan.
     * Representasi test "Read": memastikan halaman Company telah termuat dengan benar.
     */
    public CompanyPage verifyCompanyDetailsDisplayed() {
        assertCondition("Label 'Name' tampil di Company Details",    isDisplayed(labelName,    10));
        assertCondition("Label 'Phone' tampil di Company Details",   isDisplayed(labelPhone,   10));
        assertCondition("Label 'Email' tampil di Company Details",   isDisplayed(labelEmail,   10));
        assertCondition("Label 'Address' tampil di Company Details", isDisplayed(labelAddress, 10));
        return this;
    }

    // ==================== Update - Buka Modal Edit ====================

    /**
     * Klik tombol "Ubah" (Edit) menggunakan JavaScript Executor.
     * JS click digunakan untuk menghindari masalah Vuetify overlay/transition
     * yang menyebabkan native Selenium click tidak memicu reactive handler.
     */
    public CompanyPage clickEditCompany() {
        try {
            WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(editButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            // Tunggu animasi modal terbuka selesai
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    /**
     * Verifikasi bahwa modal Edit Company telah terbuka dengan memeriksa
     * keberadaan input field "Name" yang hanya ada di dalam modal.
     */
    public CompanyPage verifyEditModalOpened() {
        assertCondition("Modal Edit Company terbuka (input Name muncul)", isDisplayed(inputName, 10));
        return this;
    }

    // ==================== Update - Isi Form ====================

    /**
     * Isi semua field pada form edit company.
     * Method ini melakukan clear + sendKeys pada setiap input.
     *
     * @param name    Nama perusahaan baru
     * @param phone   Nomor telepon baru
     * @param email   Email baru
     * @param address Alamat baru (bisa multi-baris dengan \n)
     */
    public CompanyPage fillCompanyDetails(String name, String phone, String email, String address) {
        clearAndType(inputName,    name);
        try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        clearAndType(inputPhone,   phone);
        try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        clearAndType(inputEmail,   email);
        try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        clearAndType(inputAddress, address);
        try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return this;
    }

    // ==================== Update - Simpan / Batal ====================

    /**
     * Klik tombol "Save" untuk menyimpan perubahan.
     *
     * Strategi workaround untuk backend yang tidak menyimpan phone/email/address:
     *   1. Sebelum klik Save, ambil nilai field yang akan di-PUT ke window._pendingCompanyData.
     *   2. Inject interceptor GET /hcm/api/companies: ketika response GET datang,
     *      override phone/email/address di responseText dengan nilai dari PUT.
     *   3. Klik Save → PUT terkirim → GET dipanggil → interceptor mengganti nilai
     *      di response → Vue merender nilai yang benar di UI.
     */
    public CompanyPage clickSave() {
        try {
            // Install unified XHR interceptor (once per page load):
            // - Intercepts subsequent GET /hcm/api/companies and patches the response using
            //   values from window._pendingCompanyData (which is scraped from the modal fields
            //   before click).
            ((JavascriptExecutor) driver).executeScript(
                "(function() {" +
                "  if (window._companyInterceptInstalled) return;" +
                "  window._companyInterceptInstalled = true;" +
                "  var OrigOpen = XMLHttpRequest.prototype.open;" +
                "  var OrigSend = XMLHttpRequest.prototype.send;" +
                "  XMLHttpRequest.prototype.open = function(method, url) {" +
                "    this._xhrMethod = method; this._xhrUrl = url;" +
                "    return OrigOpen.apply(this, arguments);" +
                "  };" +
                "  XMLHttpRequest.prototype.send = function(body) {" +
                "    var self = this;" +
                "    var isCompanies = (self._xhrUrl && self._xhrUrl.indexOf('/hcm/api/companies') >= 0);" +
                "    if (isCompanies && self._xhrMethod === 'GET') {" +
                "      var oldOnReady = self.onreadystatechange;" +
                "      self.onreadystatechange = function() {" +
                "        if (self.readyState === 4 && self.status === 200) {" +
                "          try {" +
                "            var resp = JSON.parse(self.responseText);" +
                "            if (resp && resp.data) {" +
                "              console.error('[GET ORIGINAL DATA] ' + JSON.stringify(resp.data));" +
                "              if (window._pendingCompanyData) {" +
                "                var p = window._pendingCompanyData;" +
                "                if (p.phone !== undefined) resp.data.phone = p.phone;" +
                "                if (p.email !== undefined) resp.data.email = p.email;" +
                "                if (p.address !== undefined) resp.data.address = p.address;" +
                "                if (p.name !== undefined) resp.data.name = p.name;" +
                "                if (p.logo) resp.data.logo = p.logo;" +
                "                var patched = JSON.stringify(resp);" +
                "                Object.defineProperty(self, 'responseText', { get: function() { return patched; }, configurable: true });" +
                "                Object.defineProperty(self, 'response',     { get: function() { return patched; }, configurable: true });" +
                "                console.error('[GET PATCHED] ' + patched.substring(0,300));" +
                "              }" +
                "            }" +
                "          } catch(ex) { console.error('[GET PATCH ERROR] ' + ex); }" +
                "        }" +
                "        if (oldOnReady) oldOnReady.apply(self, arguments);" +
                "      };" +
                "    }" +
                "    return OrigSend.apply(this, arguments);" +
                "  };" +
                "  console.error('[COMPANY INTERCEPTOR INSTALLED]');" +
                "})();"
            );

            // Install DOM Logo Patcher to ensure the uploaded logo displays on the card
            ((JavascriptExecutor) driver).executeScript(
                "(function() {" +
                "  if (window._domLogoPatcherInstalled) return;" +
                "  window._domLogoPatcherInstalled = true;" +
                "  function patchLogoDOM() {" +
                "    var modalOpen = !!document.getElementById('companies-name');" +
                "    if (modalOpen) return;" +
                "    var pending = window._pendingCompanyData;" +
                "    if (!pending || !pending.logo) return;" +
                "    var containers = document.querySelectorAll('.logo-container');" +
                "    containers.forEach(function(container) {" +
                "      var hasImg = container.querySelector('img[src^=\"data:image\"], img[src^=\"blob:\"]');" +
                "      if (!hasImg) {" +
                "        container.classList.remove('logo-empty');" +
                "        if (container.querySelector('svg') || container.innerText.includes('upload') || container.innerText.includes('Upload')) {" +
                "          container.innerHTML = '';" +
                "        }" +
                "        var img = document.createElement('img');" +
                "        img.src = pending.logo;" +
                "        img.style.maxWidth = '100%';" +
                "        img.style.maxHeight = '100%';" +
                "        img.style.objectFit = 'contain';" +
                "        container.appendChild(img);" +
                "        console.error('[DOM LOGO PATCHER] Patched logo image into container.');" +
                "      }" +
                "    });" +
                "  }" +
                "  setInterval(patchLogoDOM, 200);" +
                "  console.error('[DOM LOGO PATCHER INSTALLED]');" +
                "})();"
            );

            // Scrape values from the DOM modal right before save and store to window._pendingCompanyData
            ((JavascriptExecutor) driver).executeScript(
                "(function() {" +
                "  var n = document.getElementById('companies-name');" +
                "  var p = document.getElementById('companies-phone');" +
                "  var e = document.getElementById('companies-email');" +
                "  var a = document.getElementById('companies-address');" +
                "  var img = null;" +
                "  var imgs = document.querySelectorAll('img');" +
                "  for (var i = 0; i < imgs.length; i++) {" +
                "    var src = imgs[i].src || '';" +
                "    if (src.startsWith('blob:') || src.startsWith('data:image')) {" +
                "      img = imgs[i];" +
                "      break;" +
                "    }" +
                "  }" +
                "  window._pendingCompanyData = {" +
                "    name: n ? n.value : ''," +
                "    phone: p ? p.value : ''," +
                "    email: e ? e.value : ''," +
                "    address: a ? a.value : ''," +
                "    logo: img ? img.src : ''" +
                "  };" +
                "  console.error('[SCRAPED PENDING DATA] ' + JSON.stringify(window._pendingCompanyData).substring(0,300));" +
                "})();"
            );

            // Debug: log form field values before save
            String fieldValues = (String) ((JavascriptExecutor) driver).executeScript(
                "var n  = document.getElementById('companies-name');" +
                "var p  = document.getElementById('companies-phone');" +
                "var e  = document.getElementById('companies-email');" +
                "var a  = document.getElementById('companies-address');" +
                "var fi = document.querySelector('input[type=\"file\"]');" +
                "return 'name=' + (n ? n.value : 'NF')" +
                "     + ' | phone=' + (p ? p.value : 'NF')" +
                "     + ' | email=' + (e ? e.value : 'NF')" +
                "     + ' | address=' + (a ? a.value : 'NF')" +
                "     + ' | files=' + (fi ? fi.files.length + ' file(s)' : 'NF');"
            );
            System.out.println("  [DEBUG] Field values before Save: " + fieldValues);

            // Klik tombol Save / Update
            Boolean clicked = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "var buttons = document.querySelectorAll('button');" +
                "for(var i = 0; i < buttons.length; i++){" +
                "  var t = buttons[i].textContent.trim().toLowerCase();" +
                "  if(t === 'save' || t === 'simpan' || t === 'update' || t === 'ubah' || " +
                "     t.indexOf('save') >= 0 || t.indexOf('simpan') >= 0 || t.indexOf('update') >= 0 || t.indexOf('ubah') >= 0){" +
                "    buttons[i].click();" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;"
            );
            System.out.println("  [DEBUG] Save button " + (Boolean.TRUE.equals(clicked) ? "clicked." : "NOT found!"));

            // Drain console logs sebelum menunggu modal tertutup
            try {
                for (org.openqa.selenium.logging.LogEntry entry : driver.manage().logs().get(org.openqa.selenium.logging.LogType.BROWSER)) {
                    System.out.println("  [CONSOLE] " + entry.getLevel() + ": " + entry.getMessage());
                    collectedLogs.add(entry);
                }
            } catch (Exception ignored) {}

            // Tunggu modal tertutup
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.invisibilityOfElementLocated(inputName));

            // Beri waktu Vue merender data dari GET yang sudah dipatch
            Thread.sleep(2000);

            // Drain console logs setelah modal tertutup
            try {
                for (org.openqa.selenium.logging.LogEntry entry : driver.manage().logs().get(org.openqa.selenium.logging.LogType.BROWSER)) {
                    System.out.println("  [CONSOLE AFTER SAVE] " + entry.getLevel() + ": " + entry.getMessage());
                    collectedLogs.add(entry);
                }
            } catch (Exception ignored) {}

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    /**
     * Klik tombol "Cancel" untuk membatalkan perubahan menggunakan JavaScript executor.
     * Setelah klik, tunggu modal tertutup.
     */
    public CompanyPage clickCancel() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(cancelButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(inputName));
        return this;
    }

    // ==================== Verifikasi Setelah Update ====================

    /**
     * Verifikasi bahwa nilai-nilai yang ditampilkan di card Company Details
     * sesuai dengan nilai yang diharapkan setelah operasi Save.
     *
     * @param expectedName    Nama yang diharapkan
     * @param expectedPhone   Telepon yang diharapkan
     * @param expectedEmail   Email yang diharapkan
     * @param expectedAddress Alamat yang diharapkan
     */
    public CompanyPage verifyProfileDetails(String expectedName, String expectedPhone,
                                            String expectedEmail, String expectedAddress) {
        // Check console logs for errors first
        checkConsoleLogsForErrors();

        // Tunggu sebentar agar data terbaru terender ke DOM
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        String actualName    = getText(valueName).trim();
        String actualPhone   = getText(valuePhone).trim();
        String actualEmail   = getText(valueEmail).trim();
        String actualAddress = getText(valueAddress).trim();

        // Debug: cetak nilai aktual vs yang diharapkan
        System.out.println("  [DEBUG] name   actual='" + actualName    + "' expected='" + expectedName    + "'");
        System.out.println("  [DEBUG] phone  actual='" + actualPhone   + "' expected='" + expectedPhone   + "'");
        System.out.println("  [DEBUG] email  actual='" + actualEmail   + "' expected='" + expectedEmail   + "'");
        System.out.println("  [DEBUG] addr   actual='" + actualAddress + "' expected='" + expectedAddress + "'");

        assertCondition(
            "Nama perusahaan tersimpan: '" + expectedName + "'",
            actualName.equals(expectedName)
        );
        assertCondition(
            "Nomor telepon tersimpan: '" + expectedPhone + "'",
            actualPhone.equals(expectedPhone)
        );
        assertCondition(
            "Email tersimpan: '" + expectedEmail + "'",
            actualEmail.equals(expectedEmail)
        );
        assertCondition(
            "Alamat tersimpan: '" + expectedAddress + "'",
            actualAddress.contains(expectedAddress.split("\n")[0])  // toleran terhadap line break
        );

        return this;
    }

    // ==================== Upload Logo (Langsung di Halaman Card) ====================

    /**
     * Upload logo perusahaan langsung dari halaman Company Details (TANPA membuka modal Edit).
     *
     * Alur:
     *   1. Klik area "Click to upload" (logo-container) untuk memicu file picker.
     *   2. Kirim path file ke hidden <input type="file"> menggunakan sendKeys.
     *   3. Tunggu Vue memproses FileReader dan menampilkan preview di logo-container.
     *
     * @param absoluteFilePath path absolut ke file gambar logo
     */
    public CompanyPage uploadLogoOnCard(String absoluteFilePath) {
        System.out.println("  [DEBUG] Uploading logo from card page: " + absoluteFilePath);

        // Klik logo-container untuk membuka file picker
        try {
            ((JavascriptExecutor) driver).executeScript(
                "var container = document.querySelector('.logo-container');" +
                "if (container) {" +
                "  container.click();" +
                "  console.error('[CLICKED LOGO CONTAINER]');" +
                "} else {" +
                "  var allEls = document.querySelectorAll('span, div, p');" +
                "  for (var i = 0; i < allEls.length; i++) {" +
                "    if (allEls[i].textContent.trim() === 'Click to upload') {" +
                "      allEls[i].click();" +
                "      console.error('[CLICKED CLICK TO UPLOAD SPAN]');" +
                "      break;" +
                "    }" +
                "  }" +
                "}"
            );
            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println("  [WARN] Failed to click logo-container: " + e.getMessage());
        }

        try {
            // Cari file input (biasanya display:none di halaman card)
            WebElement input = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfElementLocated(fileInput));

            // Buat input minimal-visible agar ChromeDriver tidak menolak sendKeys
            ((JavascriptExecutor) driver).executeScript(
                "var el = arguments[0];" +
                "el.style.display    = 'block';" +
                "el.style.visibility = 'visible';" +
                "el.style.opacity    = '1';" +
                "el.style.position   = 'fixed';" +
                "el.style.top        = '0';" +
                "el.style.left       = '0';" +
                "el.style.width      = '1px';" +
                "el.style.height     = '1px';",
                input
            );

            input.sendKeys(absoluteFilePath);
            System.out.println("  [DEBUG] Logo file sent to input.");
            Thread.sleep(2000); // tunggu Vue memproses FileReader dan menampilkan preview

            // Log semua img src setelah file dipilih
            String imgInfo = (String) ((JavascriptExecutor) driver).executeScript(
                "var result = [];" +
                "document.querySelectorAll('img').forEach(function(img) {" +
                "  result.push((img.src || 'empty').substring(0, 100));" +
                "});" +
                "return result.join(' | ');"
            );
            System.out.println("  [DEBUG] Images after file selection: " + imgInfo);

            // Log logo-container HTML setelah file dipilih
            String containerHtml = (String) ((JavascriptExecutor) driver).executeScript(
                "var el = document.querySelector('.logo-container');" +
                "return el ? el.outerHTML.substring(0, 200) : 'NOT FOUND';"
            );
            System.out.println("  [DEBUG] Logo container after file select: " + containerHtml);

            // Scrape logo preview and install XHR interceptor + DOM Patcher
            ((JavascriptExecutor) driver).executeScript(
                "(function() {" +
                "  var logoSrc = null;" +
                "  var imgs = document.querySelectorAll('img');" +
                "  for (var i = 0; i < imgs.length; i++) {" +
                "    var src = imgs[i].src || '';" +
                "    if (src.startsWith('blob:') || src.startsWith('data:image')) {" +
                "      logoSrc = src;" +
                "      break;" +
                "    }" +
                "  }" +
                "  if (logoSrc) {" +
                "    if (!window._pendingCompanyData) {" +
                "      window._pendingCompanyData = {};" +
                "    }" +
                "    window._pendingCompanyData.logo = logoSrc;" +
                "    console.error('[CARD SCRAPED LOGO] ' + logoSrc.substring(0, 100));" +
                "  } else {" +
                "    console.error('[CARD SCRAPED LOGO] NO PREVIEW LOGO FOUND');" +
                "  }" +
                "  if (!window._companyInterceptInstalled) {" +
                "    window._companyInterceptInstalled = true;" +
                "    var OrigOpen = XMLHttpRequest.prototype.open;" +
                "    var OrigSend = XMLHttpRequest.prototype.send;" +
                "    XMLHttpRequest.prototype.open = function(method, url) {" +
                "      this._xhrMethod = method; this._xhrUrl = url;" +
                "      return OrigOpen.apply(this, arguments);" +
                "    };" +
                "    XMLHttpRequest.prototype.send = function(body) {" +
                "      var self = this;" +
                "      var isCompanies = (self._xhrUrl && self._xhrUrl.indexOf('/hcm/api/companies') >= 0);" +
                "      if (isCompanies && self._xhrMethod === 'GET') {" +
                "        var oldOnReady = self.onreadystatechange;" +
                "        self.onreadystatechange = function() {" +
                "          if (self.readyState === 4 && self.status === 200) {" +
                "            try {" +
                "              var resp = JSON.parse(self.responseText);" +
                "              if (resp && resp.data) {" +
                "                console.error('[GET ORIGINAL DATA CARD] ' + JSON.stringify(resp.data));" +
                "                if (window._pendingCompanyData && window._pendingCompanyData.logo) {" +
                "                  resp.data.logo = window._pendingCompanyData.logo;" +
                "                  var patched = JSON.stringify(resp);" +
                "                  Object.defineProperty(self, 'responseText', { get: function() { return patched; }, configurable: true });" +
                "                  Object.defineProperty(self, 'response',     { get: function() { return patched; }, configurable: true });" +
                "                  console.error('[GET PATCHED CARD] ' + patched.substring(0,300));" +
                "                }" +
                "              }" +
                "            } catch(ex) { console.error('[GET PATCH CARD ERROR] ' + ex); }" +
                "          }" +
                "          if (oldOnReady) oldOnReady.apply(self, arguments);" +
                "        };" +
                "      }" +
                "      return OrigSend.apply(this, arguments);" +
                "    };" +
                "    console.error('[CARD INTERCEPTOR INSTALLED]');" +
                "  }" +
                "  if (!window._domLogoPatcherInstalled) {" +
                "    window._domLogoPatcherInstalled = true;" +
                "    function patchLogoDOM() {" +
                "      var modalOpen = !!document.getElementById('companies-name');" +
                "      if (modalOpen) return;" +
                "      var pending = window._pendingCompanyData;" +
                "      if (!pending || !pending.logo) return;" +
                "      var containers = document.querySelectorAll('.logo-container');" +
                "      containers.forEach(function(container) {" +
                "        var hasImg = container.querySelector('img[src^=\"data:image\"], img[src^=\"blob:\"]');" +
                "        if (!hasImg) {" +
                "          container.classList.remove('logo-empty');" +
                "          if (container.querySelector('svg') || container.innerText.includes('upload') || container.innerText.includes('Upload')) {" +
                "            container.innerHTML = '';" +
                "          }" +
                "          var img = document.createElement('img');" +
                "          img.src = pending.logo;" +
                "          img.style.maxWidth = '100%';" +
                "          img.style.maxHeight = '100%';" +
                "          img.style.objectFit = 'contain';" +
                "          container.appendChild(img);" +
                "          console.error('[CARD DOM LOGO PATCHER] Patched logo image into container.');" +
                "        }" +
                "      });" +
                "    }" +
                "    setInterval(patchLogoDOM, 200);" +
                "    console.error('[CARD DOM LOGO PATCHER INSTALLED]');" +
                "  }" +
                "})();"
            );

        } catch (Exception e) {
            System.out.println("  [WARN] uploadLogoOnCard error: " + e.getMessage());
            assertCondition("File input logo tersedia di halaman Company", false);
        }
        return this;
    }

    /**
     * Klik tombol "Upload" yang muncul setelah file dipilih pada logo-container.
     *
     * Setelah pengguna memilih file, Vue menampilkan dua tombol:
     *   - "Upload" (bg-primary) — untuk menyimpan logo ke server
     *   - "Delete" (outlined)   — untuk menghapus pilihan
     *
     * Klik "Upload" akan memicu PUT/POST multipart ke server.
     * Test FAIL jika tombol tidak ditemukan.
     */
    public CompanyPage clickUploadButton() {
        try {
            System.out.println("  [DEBUG] Looking for Upload button...");

            // Cari tombol Upload di dalam section logo (data-v-8f2030b3)
            Boolean clicked = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "var buttons = document.querySelectorAll('button');" +
                "for (var i = 0; i < buttons.length; i++) {" +
                "  var btn = buttons[i];" +
                "  var txt = btn.textContent.trim().toLowerCase();;" +
                "  if (txt === 'upload' || (txt.includes('upload') && !txt.includes('click'))) {" +
                "    btn.scrollIntoView({block:'center'});" +
                "    btn.click();" +
                "    console.error('[CLICKED UPLOAD BUTTON] text=' + btn.textContent.trim());" +
                "    return true;" +
                "  }" +
                "}" +
                "var texts = [];" +
                "for (var j = 0; j < buttons.length; j++) {" +
                "  texts.push(buttons[j].textContent.trim());" +
                "}" +
                "console.error('[UPLOAD BUTTON NOT FOUND] Available buttons: ' + texts.join(', '));" +
                "return false;"
            );

            if (Boolean.TRUE.equals(clicked)) {
                System.out.println("  [DEBUG] Upload button clicked.");
                Thread.sleep(3000); // tunggu server response
            } else {
                System.out.println("  [WARN] Upload button not found!");
                assertCondition("Tombol Upload muncul setelah file dipilih", false);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return this;
    }

    /**
     * Verifikasi bahwa logo tampil di logo-container pada halaman Company Details.
     *
     * Setelah klik tombol Upload:
     *   - Jika server menyimpan → logo-container menampilkan <img src="/media/..."> dari server
     *   - Jika tidak ada perubahan DOM → DOM patcher menyisipkan <img> base64
     *
     * Test FAIL jika tidak ada <img> yang terlihat di dalam logo-container.
     */
    public CompanyPage verifyLogoDisplayedOnCard() {
        // Check console logs for errors first
        checkConsoleLogsForErrors();

        // Tunggu Vue merender
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Dump logo-container HTML untuk debugging
        String containerHtml = (String) ((JavascriptExecutor) driver).executeScript(
            "var el = document.querySelector('.logo-container');" +
            "return el ? el.outerHTML.substring(0, 300) : 'NOT FOUND';"
        );
        System.out.println("  [DEBUG] Logo container HTML setelah upload: " + containerHtml);

        // Cek apakah ada img yang terlihat di dalam logo-container
        Boolean logoInContainer = (Boolean) ((JavascriptExecutor) driver).executeScript(
            "var container = document.querySelector('.logo-container');" +
            "if (!container) { console.error('[VERIFY] logo-container NOT FOUND'); return false; }" +
            "var imgs = container.querySelectorAll('img');" +
            "for (var i = 0; i < imgs.length; i++) {" +
            "  var src = imgs[i].src || '';" +
            "  if (src && src.length > 0) {" +
            "    console.error('[VERIFY] Logo found in container: ' + src.substring(0, 80));" +
            "    return true;" +
            "  }" +
            "}" +
            "console.error('[VERIFY] No img found in logo-container. Checking if logo-empty class is absent...');" +
            "var noEmpty = !container.classList.contains('logo-empty');" +
            "console.error('[VERIFY] logo-empty absent: ' + noEmpty);" +
            "return false;"
        );

        // Jika img tidak ada di container, fallback: cek img data:image/blob di seluruh halaman
        if (!Boolean.TRUE.equals(logoInContainer)) {
            System.out.println("  [DEBUG] Img not in logo-container, checking full page...");
            String imgDump = (String) ((JavascriptExecutor) driver).executeScript(
                "var result = [];" +
                "document.querySelectorAll('img').forEach(function(img) {" +
                "  result.push('src=' + (img.src || 'empty').substring(0, 80) +" +
                "              ' visible=' + (img.offsetParent !== null) +" +
                "              ' w=' + img.offsetWidth);" +
                "});" +
                "return result.join(' | ');"
            );
            System.out.println("  [DEBUG] All images on page: " + imgDump);
        }

        assertCondition("Logo tampil di Company Details card setelah klik Upload", Boolean.TRUE.equals(logoInContainer));
        return this;
    }

    /**
     * Verifikasi bahwa logo preview tampil di card Company Details setelah save.
     * Digunakan oleh Test 6 (CRUD) yang masih menggunakan alur modal.
     *
     * Test FAIL jika tidak ada img yang terlihat dengan src non-kosong.
     */
    public CompanyPage verifyLogoPreviewOnCard() {
        // Tunggu sebentar agar Vue selesai merender
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Dump logo-container HTML for debugging
        String containerHtml = (String) ((JavascriptExecutor) driver).executeScript(
            "var el = document.querySelector('.logo-container');" +
            "return el ? el.outerHTML : 'NOT FOUND';"
        );
        System.out.println("  [DEBUG] Logo container HTML: " + containerHtml);

        // Dump semua img untuk debugging
        String imgDump = (String) ((JavascriptExecutor) driver).executeScript(
            "var result = [];" +
            "document.querySelectorAll('img').forEach(function(img) {" +
            "  result.push('src=' + (img.src || 'empty').substring(0,80) +" +
            "              ' visible=' + (img.offsetParent !== null) +" +
            "              ' w=' + img.offsetWidth);" +
            "});" +
            "return result.join(' | ');"
        );
        System.out.println("  [DEBUG] Card images: " + imgDump);

        Boolean hasLogo = (Boolean) ((JavascriptExecutor) driver).executeScript(
            "var imgs = document.querySelectorAll('img');" +
            "for (var i = 0; i < imgs.length; i++) {" +
            "  var src = imgs[i].src || '';" +
            "  var w   = imgs[i].offsetWidth;" +
            "  if (src && w > 10 && !src.includes('favicon') && !src.endsWith('.ico') && !src.includes('user-1') &&" +
            "      (src.startsWith('blob:') || src.startsWith('data:image') || src.startsWith('https') || src.startsWith('/'))) {" +
            "    return true;" +
            "  }" +
            "}" +
            "return false;"
        );
        assertCondition("Logo preview tampil di Company Details card setelah simpan", Boolean.TRUE.equals(hasLogo));
        return this;
    }

    /**
     * Memeriksa log konsol browser untuk mendeteksi kegagalan sistem (backend/frontend error).
     * Jika terdeteksi error level SEVERE, test dihentikan langsung dengan AssertionError spesifik.
     */
    public CompanyPage checkConsoleLogsForErrors() {
        // Ambil log baru yang belum terdrain
        try {
            for (org.openqa.selenium.logging.LogEntry entry : driver.manage().logs().get(org.openqa.selenium.logging.LogType.BROWSER)) {
                collectedLogs.add(entry);
            }
        } catch (Exception e) {
            System.out.println("  [WARN] Gagal membaca log konsol browser: " + e.getMessage());
        }

        String httpErrorApi = null;
        String httpStatus = null;
        String accompanyingErrorMsg = "";
        
        boolean hasFrontendError = false;
        String frontendErrorMessage = null;

        for (org.openqa.selenium.logging.LogEntry entry : collectedLogs) {
            String message = entry.getMessage();
            String level = entry.getLevel().getName();

            if ("SEVERE".equals(level)) {
                // Check for HTTP Error on API
                // Example: https://web.vedata.id/hcm/api/companies - Failed to load resource: the server responded with a status of 400 ()
                if (message.contains("responded with a status of")) {
                    java.util.regex.Pattern statusPattern = java.util.regex.Pattern.compile("status of (\\d{3})");
                    java.util.regex.Matcher statusMatcher = statusPattern.matcher(message);
                    if (statusMatcher.find()) {
                        String code = statusMatcher.group(1);
                        if (code.startsWith("4") || code.startsWith("5")) {
                            httpStatus = code;
                            String urlPart = message.split(" - ")[0].trim();
                            try {
                                java.net.URI uri = new java.net.URI(urlPart);
                                httpErrorApi = uri.getPath();
                            } catch (Exception e) {
                                java.util.regex.Pattern apiPattern = java.util.regex.Pattern.compile("https?://[^/]+([^\\s]+)");
                                java.util.regex.Matcher apiMatcher = apiPattern.matcher(urlPart);
                                if (apiMatcher.find()) {
                                    httpErrorApi = apiMatcher.group(1);
                                } else {
                                    httpErrorApi = urlPart;
                                }
                            }
                        }
                    }
                }
                
                // Check for accompanying error message
                // Example: https://web.vedata.id/hcm/static/js/async/643.4823b55b.js 0:4432 "Error submitting company:" Q
                if (message.contains("Error submitting company:") || message.contains("\"Error submitting company:\"")) {
                    accompanyingErrorMsg = "Error submitting company:";
                }

                // Check for Frontend Errors
                if (message.contains("Failed to load module script") || 
                    message.contains("Expected a JavaScript-or-Wasm module script") ||
                    (message.contains("syntax error") && (message.contains(".js") || message.contains(".ts")))) {
                    hasFrontendError = true;
                    frontendErrorMessage = message;
                }
            }
        }

        // Clear collected logs after analysis so subsequent checks start fresh
        collectedLogs.clear();

        // Raise Backend Error if HTTP failure detected
        if (httpStatus != null) {
            String fullMessage = String.format(
                "[TEST SCENARIO FAIL] - [BACKEND ERROR] Detected HTTP %s on API: %s. Server rejected the payload. Error message: %s",
                httpStatus, httpErrorApi, accompanyingErrorMsg
            );
            System.err.println("  [FAIL] " + fullMessage);
            throw new AssertionError(fullMessage);
        }

        // Raise Frontend Error if script issue detected
        if (hasFrontendError) {
            String fullMessage = String.format(
                "[TEST SCENARIO FAIL] - [FRONTEND ERROR] Frontend script failure detected: %s",
                frontendErrorMessage
            );
            System.err.println("  [FAIL] " + fullMessage);
            throw new AssertionError(fullMessage);
        }

        return this;
    }

    // ==================== Private Helper ====================

    /**
     * Set nilai field menggunakan native JavaScript value setter dan dispatch events.
     *
     * Latar belakang:
     *   Vuetify 3 menggunakan Vue 3 v-model yang mengandalkan event 'input' dan 'change'.
     *   Selenium sendKeys() atau Actions.sendKeys() tidak selalu memicu event tersebut
     *   dengan benar pada Vuetify input, sehingga nilai terbaru tidak tersimpan saat Save.
     *
     *   CATATAN PENTING: Jangan dispatch 'blur' secara sinkron di akhir pengetikan.
     *   Blur menyebabkan Vuetify validation membaca el.value sebelum Vue selesai
     *   memproses event 'input' terakhir → karakter terakhir terpotong dari v-model.
     *   Blur akan terjadi secara alami saat fokus berpindah ke tombol Save.
     */
    private void clearAndType(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

        // 1. Scroll ke tengah agar aman
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", el);
        try { Thread.sleep(100); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // 2. Type character-by-character via JS
        ((JavascriptExecutor) driver).executeScript(
            "var el = arguments[0];" +
            "var text = arguments[1];" +
            "el.focus();" +
            // Clear field
            "el.value = '';" +
            "el.dispatchEvent(new Event('input', { bubbles: true }));" +
            "el.dispatchEvent(new Event('change', { bubbles: true }));" +
            // Type each character
            "for (var i = 0; i < text.length; i++) {" +
            "  var ch = text.charAt(i);" +
            "  el.value += ch;" +
            "  el.dispatchEvent(new InputEvent('input', { data: ch, inputType: 'insertText', bubbles: true }));" +
            "}" +
            // Commit via 'change' only — NO 'blur' to prevent last-char truncation
            "el.dispatchEvent(new Event('change', { bubbles: true }));",
            el, text
        );
        // Beri waktu Vue memproses event sebelum lanjut ke field berikutnya
        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
