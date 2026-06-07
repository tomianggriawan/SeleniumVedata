package org.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.common.BasePage;
import org.pages.settingpage.CompanyPage;

/**
 * LoginPage - Page Object Class representing the Login page of VEDATA.
 */
public class LoginPage extends BasePage {

    // ==================== Locators ====================
    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.id("kc-login");
    private final By usernameLabel = By.xpath("//label[@for='username']");
    private final By passwordLabel = By.xpath("//label[@for='password']");
    
    // Validation messages (Keycloak displays errors as helper text or form group helper alerts)
    private final By emailRequiredMessage = By.xpath("//*[contains(text(), 'wajib diisi') or contains(text(), 'required') or @id='input-error-username' or @id='input-error-password']");
    private final By passwordRequiredMessage = By.xpath("//*[contains(text(), 'wajib diisi') or contains(text(), 'required') or @id='input-error-username' or @id='input-error-password']");
    
    // Dashboard indicators
    private final By dashboardTitle = By.xpath("//div[contains(@class, 'v-card-title') and (contains(text(), 'Blank Page') or contains(text(), 'Dashboard'))]");
    private final By leftSidebar = By.className("leftSidebar");

    // Potential logo selectors
    private final String[] logoCssSelectors = {
            "#kc-header-wrapper",
            ".pf-v5-c-brand",
            ".logo",
            ".v-img"
    };

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ==================== Actions ====================

    public void navigateToLoginPage(String baseUrl) {
        navigateTo(baseUrl);
    }

    public boolean isTitleValid() {
        String title = getPageTitle().toLowerCase();
        return title.contains("vedata") || title.contains("sign in");
    }

    public boolean isLogoDisplayed() {
        for (String selector : logoCssSelectors) {
            if (isDisplayed(By.cssSelector(selector), 2)) {
                System.out.println("  [INFO] Logo ditemukan dengan selector: " + selector);
                return true;
            }
        }
        return false;
    }

    public boolean isUsernameLabelDisplayed() {
        return isDisplayed(usernameLabel);
    }

    public boolean isPasswordLabelDisplayed() {
        return isDisplayed(passwordLabel);
    }

    public boolean isUsernamePlaceholderDisplayed() {
        return isDisplayed(usernameInput);
    }

    public boolean isPasswordPlaceholderDisplayed() {
        return isDisplayed(passwordInput);
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayed(loginButton);
    }

    public void clickUsernameInput() {
        click(usernameInput);
    }

    public void clickPasswordInput() {
        click(passwordInput);
    }

    public void clickPasswordLabel() {
        click(passwordLabel);
    }

    public boolean isEmailRequiredMessageDisplayed() {
        return isDisplayed(emailRequiredMessage);
    }

    public boolean isPasswordRequiredMessageDisplayed() {
        return isDisplayed(passwordRequiredMessage);
    }

    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public LoginPage clickLogin() {
        click(loginButton);
        return this;
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public LoginPage loginExpectingFailure(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return this;
    }

    public DashboardPage loginExpectingSuccess(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return new DashboardPage(driver);
    }

    public void refresh() {
        refreshPage();
    }

    public boolean isDashboardDisplayed() {
        return isDisplayed(dashboardTitle, 10) || isDisplayed(leftSidebar, 10);
    }

    public boolean isStillOnLoginPage() {
        String currentUrl = getCurrentUrl();
        return (currentUrl.contains("keycloak.rumahaplikasi.com") || currentUrl.contains("vedata.id")) && !currentUrl.contains("dashboard");
    }

    public boolean isUrlContainingDashboard() {
        return getCurrentUrl().contains("dashboard");
    }

    private void waitForPageReady(String targetUrl, long extraSleepMs) {
        try {
            // Langkah 1: Periksa apakah URL masih Keycloak OAuth callback
            Thread.sleep(1000); // beri sedikit waktu SPA memproses fragment
            if (isKeycloakCallback()) {
                System.out.println("  [WARN] Keycloak OAuth callback terdeteksi. Navigasi ulang ke: " + targetUrl);
                driver.navigate().to(targetUrl);
                Thread.sleep(2000);
            }

            // Langkah 2: Tunggu sidebar muncul (bukti sesi aktif & halaman ter-render)
            boolean sidebarReady = isPresent(leftSidebar, 15);
            if (!sidebarReady) {
                System.out.println("  [WARN] Sidebar tidak muncul dalam 15 detik. URL: " + getCurrentUrl());
            } else {
                System.out.println("  [INFO] Sidebar terdeteksi. Halaman siap: " + getCurrentUrl());
            }

            // Langkah 3: Extra sleep agar konten halaman ter-render penuh
            if (extraSleepMs > 0) {
                Thread.sleep(extraSleepMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
