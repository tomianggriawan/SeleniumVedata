const fs = require('fs');
const html = fs.readFileSync('page_source_employee_add.html', 'utf8');

const kw = 'emp-hire-date';
const idx = html.indexOf(kw);
if (idx !== -1) {
    const start = Math.max(0, idx - 100);
    const end = Math.min(html.length, idx + 1800);
    console.log(html.slice(start, end));
} else {
    console.log("emp-hire-date not found!");
}
