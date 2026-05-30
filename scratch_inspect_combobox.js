const fs = require('fs');
const html = fs.readFileSync('C:/Users/LENOVO/vedata-test/page_source_employee_add.html', 'utf8');

// Find context of emp-contract-type - get bigger context after
const target = 'emp-contract-type';
let index = html.indexOf(target);
if (index !== -1) {
    // Start from the label onwards
    const start = Math.max(0, index - 100);
    const end = Math.min(html.length, index + 2500);
    console.log("=== CONTRACT TYPE COMBOBOX FULL HTML ===");
    const snippet = html.substring(start, end);
    console.log(snippet);
}

// Also check the v-field role=combobox and what it has
console.log("\n=== ALL COMBOBOX ROLES ===");
const comboboxRegex = /role="combobox"([^>]*)>/gi;
let m;
while ((m = comboboxRegex.exec(html)) !== null) {
    console.log("Attrs:", m[1].trim().substring(0, 200));
}
