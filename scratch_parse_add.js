const fs = require('fs');

const html = fs.readFileSync('C:/Users/LENOVO/vedata-test/page_source_employee_add.html', 'utf8');

// A simple regex-based parser for HTML elements and labels
// Find all label elements first to match by 'for' or proximity
const labelRegex = /<label\b[^>]*?for="([^"]+)"[^>]*?>([\s\S]*?)<\/label>/gi;
const labelsMap = {};
let match;
while ((match = labelRegex.exec(html)) !== null) {
    labelsMap[match[1]] = match[2].replace(/<[^>]*>/g, '').trim();
}

console.log("=== LABELS BY FOR ATTRIBUTE ===");
console.log(labelsMap);

// Also let's find inputs and see what's around them
const inputRegex = /<(input|textarea|select)\b([^>]*?)>/gi;
console.log("\n=== ALL DETECTED INPUTS ===");
while ((match = inputRegex.exec(html)) !== null) {
    const tag = match[1];
    const attrsStr = match[2];
    
    // Parse attributes
    const idMatch = /\bid="([^"]+)"/.exec(attrsStr);
    const typeMatch = /\btype="([^"]+)"/.exec(attrsStr);
    const nameMatch = /\bname="([^"]+)"/.exec(attrsStr);
    
    const id = idMatch ? idMatch[1] : null;
    const type = typeMatch ? typeMatch[1] : null;
    const name = nameMatch ? nameMatch[1] : null;
    
    // Find label by 'for'
    let label = id ? labelsMap[id] : '';
    
    // If not found, look for surrounding text/label
    if (!label && id) {
        // Look in a 500-char window around the input for a label
        const startPos = Math.max(0, match.index - 300);
        const endPos = Math.min(html.length, match.index + 300);
        const windowText = html.substring(startPos, endPos);
        const nestedLabelMatch = /<label\b[^>]*?>([\s\S]*?)<\/label>/i.exec(windowText);
        if (nestedLabelMatch) {
            label = nestedLabelMatch[1].replace(/<[^>]*>/g, '').trim();
        }
    }
    
    // Output details
    console.log(`Tag=${tag.toUpperCase()} | id=${id || 'none'} | type=${type || 'none'} | name=${name || 'none'} | label="${label}"`);
}
