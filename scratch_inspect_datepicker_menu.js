const fs = require('fs');
const html = fs.readFileSync('page_source_datepicker_open.html', 'utf8');

// Find elements with class containing 'dp__'
// We can use a regex to find tags with class="... dp__... "
const regex = /<([a-z0-9]+)[^>]*class="[^"]*dp__[^"]*"[^>]*>/gi;
let match;
const found = [];
while ((match = regex.exec(html)) !== null) {
    found.push(match[0]);
}

console.log(`Found ${found.length} elements with dp__ class in class attribute:`);
found.forEach((f, i) => {
    console.log(`${i}: ${f.slice(0, 150)}`);
});

// Let's search if dp__menu exists in the HTML
if (html.includes('dp__menu')) {
    console.log("Found dp__menu!");
    // Print around dp__menu
    const idx = html.indexOf('dp__menu');
    console.log(html.slice(idx - 100, idx + 1000));
} else {
    console.log("dp__menu NOT found in HTML source.");
}
