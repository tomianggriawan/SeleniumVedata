const fs = require('fs');
const html = fs.readFileSync('C:/Users/LENOVO/vedata-test/page_source_employee_add.html', 'utf8');

// Find all elements with classes containing overlay or menu
const regex = /class="([^"]*?(?:overlay|menu)[^"]*?)"/gi;
const classes = new Set();
let match;
while ((match = regex.exec(html)) !== null) {
    classes.add(match[1]);
}
console.log("=== DETECTED OVERLAY/MENU CLASSES ===");
console.log(Array.from(classes));
