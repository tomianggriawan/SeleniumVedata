const fs = require('fs');

try {
  const html = fs.readFileSync('c:/Users/LENOVO/vedata-test/page_source_employee_error.html', 'utf8');
  
  // Find all <input ...> tags
  const inputRegex = /<(input|textarea|select)\b([^>]*)\/?>/gi;
  let match;
  console.log("=== ALL INPUTS & VALUES IN ERROR PAGE ===");
  while ((match = inputRegex.exec(html)) !== null) {
    const tag = match[1];
    const attrsStr = match[2];
    
    // Parse attributes
    const idMatch = attrsStr.match(/id=["']([^"']*)["']/i);
    const typeMatch = attrsStr.match(/type=["']([^"']*)["']/i);
    const valueMatch = attrsStr.match(/value=["']([^"']*)["']/i);
    const labelMatch = attrsStr.match(/aria-label=["']([^"']*)["']/i);
    
    const id = idMatch ? idMatch[1] : '';
    const type = typeMatch ? typeMatch[1] : '';
    const value = valueMatch ? valueMatch[1] : '';
    const label = labelMatch ? labelMatch[1] : '';
    
    // Try to find if there is a value inside select or textarea
    let innerValue = '';
    if (tag === 'textarea' || tag === 'select') {
      const closeTagRegex = new RegExp(`</${tag}>`, 'i');
      const startIdx = match.index + match[0].length;
      const endMatch = closeTagRegex.exec(html.substring(startIdx));
      if (endMatch) {
        innerValue = html.substring(startIdx, startIdx + endMatch.index).trim();
        innerValue = innerValue.replace(/<[^>]*>/g, '').trim(); // Remove nested tags
      }
    }
    
    console.log(`Tag=${tag.toUpperCase()} | id=${id} | type=${type} | label='${label}' | val='${value || innerValue}'`);
  }
} catch (e) {
  console.error("Error reading file:", e);
}
