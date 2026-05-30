const fs = require('fs');
const html = fs.readFileSync('C:/Users/LENOVO/vedata-test/page_source_employee_add.html', 'utf8');

// Look for v-list-item or select options
const listRegex = /<v-list-item\b[^>]*?>([\s\S]*?)<\/v-list-item>/gi;
const optionRegex = /<option\b[^>]*?>([\s\S]*?)<\/option>/gi;

console.log("=== SELECT OPTIONS ===");
let match;
while ((match = optionRegex.exec(html)) !== null) {
    console.log(match[1].trim());
}

// Check for any text inside list elements or data attributes
const listItems = [];
const itemRegex = /class="[^"]*?v-list-item[^"]*?"[^>]*?>([\s\S]*?)<\/div>/gi;
while ((match = itemRegex.exec(html)) !== null) {
    const text = match[1].replace(/<[^>]*>/g, '').trim();
    if (text) {
        listItems.push(text);
    }
}
console.log("\n=== DETECTED LIST ITEMS IN DOM ===");
console.log(listItems.slice(0, 50));
