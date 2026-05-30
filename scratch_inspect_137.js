const fs = require('fs');
const html = fs.readFileSync('C:/Users/LENOVO/vedata-test/page_source_employee_add.html', 'utf8');

const target = 'input-v-137';
const index = html.indexOf(target);
if (index !== -1) {
    const start = Math.max(0, index - 300);
    const end = Math.min(html.length, index + 300);
    console.log(html.substring(start, end));
} else {
    console.log("Not found input-v-137");
}
