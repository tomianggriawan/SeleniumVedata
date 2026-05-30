import re
from bs4 import BeautifulSoup

with open("C:/Users/LENOVO/vedata-test/page_source_employee_add.html", "r", encoding="utf-8") as f:
    html = f.read()

soup = BeautifulSoup(html, "html.parser")

print("=== ALL INPUTS & LABELS ===")
for inp in soup.find_all(["input", "textarea", "select"]):
    inp_id = inp.get('id')
    inp_type = inp.get('type')
    inp_name = inp.get('name')
    inp_val = inp.get('value', '')
    
    # Try to find a label element linked by "for" attribute
    label_text = ""
    if inp_id:
        lbl = soup.find("label", attrs={"for": inp_id})
        if lbl:
            label_text = lbl.get_text(strip=True)
            
    # If no label found by "for", search parents for label
    if not label_text:
        parent = inp.parent
        while parent and parent.name != "body":
            if "v-field" in parent.get("class", []):
                lbl = parent.find("label")
                if lbl:
                    label_text = lbl.get_text(strip=True)
                break
            parent = parent.parent
            
    print(f"Tag={inp.name} | id={inp_id} | type={inp_type} | label='{label_text}' | val='{inp_val}'")
