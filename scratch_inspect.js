const fs = require('fs');
const html = fs.readFileSync('C:/Users/LENOVO/vedata-test/page_source_employee_add.html', 'utf8');

const targets = ['emp-hire-date', 'emp-date-of-birth'];
targets.forEach(target => {
    console.log(`=== CONTEXT FOR: ${target} ===`);
    let index = html.indexOf(target);
    if (index !== -1) {
        console.log(html.substring(index, index + 2500));
        console.log("-".repeat(50));
    }
});
