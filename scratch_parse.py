import re
from bs4 import BeautifulSoup

with open("c:/Users/LENOVO/SDET/SeleniumVedata/page_source_error.html", "r", encoding="utf-8") as f:
    html = f.read()

soup = BeautifulSoup(html, "html.parser")

print("=== ALL OVERLAYS/DIALOGS ===")
for overlay in soup.find_all(class_=re.compile("v-overlay|v-dialog")):
    print(f"Overlay tag: {overlay.name}, Classes: {overlay.get('class')}")
    # Print child inputs and labels
    labels = [l.get_text(strip=True) for l in overlay.find_all("label")]
    inputs = [f"tag={i.name} id={i.get('id')} name={i.get('name')}" for i in overlay.find_all("input")]
    buttons = [b.get_text(strip=True) for b in overlay.find_all("button")]
    print(f"  Labels: {labels}")
    print(f"  Inputs: {inputs}")
    print(f"  Buttons: {buttons}")
    print("-" * 50)

print("\n=== ALL INPUTS ON PAGE ===")
for inp in soup.find_all("input"):
    print(f"Input: tag={inp.name} id={inp.get('id')} class={inp.get('class')} placeholder={inp.get('placeholder')} value={inp.get('value')}")
    # find parent field or label
    parent = inp.parent
    while parent and parent.name != "body":
        if "v-field" in parent.get("class", []):
            print(f"  Parent v-field classes: {parent.get('class')}")
            # print label in this v-field if any
            lbl = parent.find("label")
            if lbl:
                print(f"  Label in v-field: '{lbl.get_text(strip=True)}'")
            break
        parent = parent.parent

print("\n=== TABLE ROWS ===")
for row in soup.find_all("tr"):
    cells = [c.get_text(strip=True) for c in row.find_all(["td", "th"])]
    print(f"Row: {cells}")
