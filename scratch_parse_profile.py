import re
from bs4 import BeautifulSoup

with open("page_source_employee_profile.html", "r", encoding="utf-8") as f:
    html = f.read()

soup = BeautifulSoup(html, "html.parser")

print("=== TABLES ===")
tables = soup.find_all("table")
print(f"Found {len(tables)} tables")

for idx, table in enumerate(tables):
    print(f"\nTable {idx}:")
    headers = [th.get_text().strip() for th in table.find_all("th")]
    print(f"Headers: {headers}")
    
    rows = table.find_all("tr")
    print(f"Rows count: {len(rows)}")
    for r_idx, row in enumerate(rows[:5]):
        cells = [td.get_text().strip() for td in row.find_all("td")]
        if cells:
            print(f"  Row {r_idx}: {cells}")

print("\n=== CLASS SEARCH ===")
# Find anything with table or list class
for el in soup.find_all(class_=re.compile("table|list|grid", re.I)):
    if el.name in ["div", "section"]:
        text = el.get_text().strip().replace('\n', ' ')[:100]
        if text:
            print(f"Tag={el.name} Class={el.get('class')} Text={text}")
