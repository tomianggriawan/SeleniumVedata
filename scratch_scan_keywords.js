const fs = require('fs');
const html = fs.readFileSync('C:/Users/LENOVO/vedata-test/page_source_employee_error.html', 'utf8');

const keywords = ['required', 'must', 'invalid', 'error', 'wajib', 'salah', 'tidak boleh', 'kosong'];
keywords.forEach(kw => {
    const count = (html.match(new RegExp(kw, 'gi')) || []).length;
    console.log(`Keyword '${kw}': found ${count} times`);
});

console.log("\n=== CONTEXT OF 'REQUIRED' ===");
let index = html.indexOf('required');
let count = 0;
while (index !== -1 && count < 5) {
    count++;
    console.log(`[${count}] ${html.substring(Math.max(0, index - 100), Math.min(html.length, index + 150))}`);
    index = html.indexOf('required', index + 1);
}
