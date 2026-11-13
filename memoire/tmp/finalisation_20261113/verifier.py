from pathlib import Path
import json,re,sys
from pypdf import PdfReader
from docx import Document
from docx.oxml.ns import qn
from PIL import Image,ImageOps,ImageDraw
W=Path(__file__).resolve().parent
folder=W/sys.argv[1]
pdf=PdfReader(folder/'Memoire_Licence_MEF_SIIGFP_finalise.pdf')
structure=json.loads((W/'structure.json').read_text(encoding='utf-8'))
entries=structure['headings']+[(f'{lab} {n} — {title}',1,name) for kind in ['Tableau','Figure'] for lab,n,title,name in structure['captions'] if lab==kind]
pageids={p.indirect_reference.idnum:i+1 for i,p in enumerate(pdf.pages)}
dest=[]
for p in pdf.pages[1:5]:
    links=sorted([a.get_object() for a in p.get('/Annots',[]) if '/Dest' in a.get_object() and float(a.get_object()['/Rect'][0])<480],key=lambda a:(-float(a['/Rect'][3]),float(a['/Rect'][0])))
    for a in links:dest.append(pageids[a['/Dest'][0].idnum])
assert len(dest)==len(entries),(len(dest),len(entries))
intro=next(pg for (title,level,name),pg in zip(entries,dest) if title=='Introduction générale')
def roman(n):
    out=''
    for v,s in [(10,'x'),(9,'ix'),(5,'v'),(4,'iv'),(1,'i')]:
        while n>=v:out+=s;n-=v
    return out
mapping={name:{'title':title,'physical':pg,'label':str(pg-intro+1) if pg>=intro else roman(pg-1)} for (title,level,name),pg in zip(entries,dest)}
dpath=W.parents[1]/'Memoire_Licence_MEF_SIIGFP_finalise.docx'
d=Document(dpath);changed=0
for p in d.paragraphs:
    for instr in p._p.iter(qn('w:instrText')):
        m=re.match(r'\s*PAGEREF\s+(\w+)',instr.text or '')
        if not m:continue
        value=mapping[m[1]]['label'];runner=instr.getparent().getnext();separated=False
        while runner is not None:
            chars=list(runner.iter(qn('w:fldChar')))
            if any(c.get(qn('w:fldCharType'))=='end' for c in chars):break
            if separated:
                ts=list(runner.iter(qn('w:t')))
                if ts:
                    if ts[0].text!=value:changed+=1
                    ts[0].text=value
                    for e in ts[1:]:e.text=''
                    break
            if any(c.get(qn('w:fldCharType'))=='separate' for c in chars):separated=True
            runner=runner.getnext()
if changed:d.save(dpath)
(W/'pagination.json').write_text(json.dumps(mapping,ensure_ascii=False,indent=2),encoding='utf-8')
texts=[p.extract_text() for p in pdf.pages]
bad=[(i+1,t) for i,t in enumerate(texts) if re.search('contenu introuvable|reference source not found|erreur\s*!|error\s*!|à compléter|a completer',t,re.I)]
assert not bad,bad
print(json.dumps({'pages':len(pdf.pages),'references':len(mapping),'cached_pages_changed':changed,'errors':bad,'intro_physical':intro},ensure_ascii=False))
for start in range(1,len(pdf.pages)+1,6):
    sheet=Image.new('RGB',(1800,2660),'#bbbbbb');draw=ImageDraw.Draw(sheet)
    for j,n in enumerate(range(start,min(start+6,len(pdf.pages)+1))):
        im=Image.open(folder/f'page-{n}.png');im.thumbnail((590,840))
        x=(j%3)*600;y=(j//3)*1330
        # two rows with generous output resolution for inspecting every page.
        im=Image.open(folder/f'page-{n}.png');im.thumbnail((590,1250))
        sheet.paste(im,(x,y+25));draw.text((x+10,y+5),str(n),fill='black')
    sheet.save(folder/f'contact-{start}.jpg',quality=88)
