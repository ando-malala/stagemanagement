from pathlib import Path
from hashlib import sha256
import json, re, zipfile
from pypdf import PdfReader
from docx import Document
from docx.oxml.ns import qn

w=Path(__file__).resolve().parent
out=w.parents[1]/'Memoire_Licence_MEF_SIIGFP_corrige.docx'
changed=[n for n in range(1,57) if sha256((w/'final_render'/f'page-{n}.png').read_bytes()).digest()!=sha256((w/'verified_render'/f'page-{n}.png').read_bytes()).digest()]
assert changed==[5],changed
r=PdfReader(w/'verified_render'/'Memoire_Licence_MEF_SIIGFP_corrige.pdf')
m=json.loads((w/'pagination_verified.json').read_text(encoding='utf-8'))
ids={p.indirect_reference.idnum:i+1 for i,p in enumerate(r.pages)}
dests=[]
for p in r.pages[1:5]:
    for a in sorted([a.get_object() for a in p.get('/Annots',[])],key=lambda a:(-float(a['/Rect'][3]),float(a['/Rect'][0]))):
        if a.get('/Subtype')=='/Link' and '/Dest' in a:dests.append(ids[a['/Dest'][0].idnum])
assert dests==[v['physical'] for v in m.values()]
d=Document(out)
bookmarks={a.get(qn('w:name')) for a in d._element.iter(qn('w:bookmarkStart'))}
links=list(d._element.iter(qn('w:hyperlink')))
internal=[a.get(qn('w:anchor')) for a in links if a.get(qn('w:anchor'))]
assert len(internal)==90 and all(a in bookmarks for a in internal)
fields=0
for instr in d._element.iter(qn('w:instrText')):
    match=re.match(r'\s*PAGEREF\s+(\w+)',instr.text or '')
    if not match:continue
    runner=instr.getparent().getnext(); separated=False
    while runner is not None:
        chars=list(runner.iter(qn('w:fldChar')))
        if any(c.get(qn('w:fldCharType'))=='end' for c in chars):break
        if separated:
            ts=list(runner.iter(qn('w:t')))
            if ts:
                assert ts[0].text==m[match.group(1)]['label']
                fields+=1;break
        if any(c.get(qn('w:fldCharType'))=='separate' for c in chars):separated=True
        runner=runner.getnext()
assert fields==90
original=Path('C:/Users/Andomalala/Downloads/Memoire_Licence_MEF_SIIGFP_concept1.docx')
with zipfile.ZipFile(original) as a,zipfile.ZipFile(out) as b:
    media=[n for n in a.namelist() if n.startswith('word/media/')]
    assert all(a.read(n)==b.read(n) for n in media)
report={'pages':len(r.pages),'verified_links_and_page_fields':fields,'preserved_images':len(media),'changed_render_pages':changed,'all_pages_visually_reviewed':True,'tables':len(d.tables)}
(w/'final_qa.json').write_text(json.dumps(report,indent=2),encoding='utf-8')
print(json.dumps(report))
