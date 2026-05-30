const fs = require('fs');

const html = fs.readFileSync('c:/Users/LENOVO/SDET/SeleniumVedata/page_source_error.html', 'utf8');

console.log("=== SEARCHING FOR WORDS ===");
const words = ['Edit', 'Ubah', 'Delete', 'Hapus', 'v-list'];
words.forEach(w => {
    const count = (html.match(new RegExp(w, 'gi')) || []).length;
    console.log(`Word '${w}': found ${count} times`);
});

console.log("\n=== CONTEXT OF 'EDIT' ===");
const editRegex = /<[^>]*?>[^<]*?(Edit|Ubah)[^<]*?<\/[^>]*?>/gi;
let m;
let count = 0;
while ((m = editRegex.exec(html)) !== null && count < 10) {
    count++;
    console.log(`[${count}] ${m[0].substring(0, 300)}`);
}
