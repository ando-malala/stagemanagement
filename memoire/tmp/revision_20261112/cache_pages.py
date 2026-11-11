from pathlib import Path
import json,re,sys
from pypdf import PdfReader
from docx import Document
from docx.oxml.ns import qn
W=Path(__file__).resolve().parent
source=Path(sys.argv[1])
r=PdfReader(source)
dpath=W.parents[1]/'Memoire_Licence_MEF_SIIGFP_corrige.docx'
d=Document(dpath)
s=json.loads((W/'structure.json').read_text(encoding='utf-8'))
entries=[(title,name) for title,level,name in s['headings']]
for typ in ['Tableau','Figure']:
    entries += [(f'{lab} {n} — {title}',name) for lab,n,title,name in s['captions'] if lab==typ]
pagesbyid={p.indirect_reference.idnum:i+1 for i,p in enumerate(r.pages)}
destinations=[]
for page in r.pages[1:5]:
    annotations=sorted([a.get_object() for a in page.get('/Annots',[])],key=lambda a:(-float(a['/Rect'][3]),float(a['/Rect'][0])))
    for a in annotations:
        if a.get('/Subtype')=='/Link' and '/Dest' in a:
            dest=a['/Dest'];destinations.append((pagesbyid[dest[0].idnum],float(dest[3])))
assert len(destinations)==len(entries),(len(destinations),len(entries))
intro=next(destinations[i][0] for i,(title,name) in enumerate(entries) if title=='Introduction générale')
def roman(n):
    out=''
    for value,letter in [(10,'x'),(9,'ix'),(5,'v'),(4,'iv'),(1,'i')]:
        while n>=value:out+=letter;n-=value
    return out
mapping={name:{'title':title,'physical':pg,'label':str(pg-intro+1) if pg>=intro else roman(pg-1)} for (title,name),(pg,y) in zip(entries,destinations)}
changed=0
for p in d.paragraphs:
    instructions=list(p._p.iter(qn('w:instrText')))
    for instr in instructions:
        m=re.match(r'\s*PAGEREF\s+(\w+)',instr.text or '')
        if not m:continue
        name=m.group(1);value=mapping[name]['label']
        runner=instr.getparent().getnext();separated=False
        while runner is not None:
            chars=list(runner.iter(qn('w:fldChar')))
            if any(c.get(qn('w:fldCharType'))=='end' for c in chars):break
            if separated:
                ts=list(runner.iter(qn('w:t')))
                if ts:
                    ts[0].text=value
                    for extra in ts[1:]:extra.text=''
                    changed+=1;break
            if any(c.get(qn('w:fldCharType'))=='separate' for c in chars):separated=True
            runner=runner.getnext()
assert changed==90,changed
d.save(dpath)
(W/'pagination_verified.json').write_text(json.dumps(mapping,ensure_ascii=False,indent=2),encoding='utf-8')
print('Cached page numbers',changed,'physical pages',len(r.pages),'intro page',intro)
