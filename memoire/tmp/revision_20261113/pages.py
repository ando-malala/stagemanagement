from pathlib import Path
import json,sys,re
from pypdf import PdfReader
from docx import Document
from docx.oxml.ns import qn
W=Path(__file__).resolve().parent
r=PdfReader(W/sys.argv[1]/'Memoire_Licence_MEF_SIIGFP_revise.pdf')
entries=sum(json.loads((W/'entries.json').read_text(encoding='utf-8')),[])
ids={p.indirect_reference.idnum:i+1 for i,p in enumerate(r.pages)}
dests=[]
for p in r.pages:
    for a in sorted([a.get_object() for a in p.get('/Annots',[])],key=lambda a:(-float(a['/Rect'][3]),float(a['/Rect'][0]))):
        if a.get('/Subtype')=='/Link' and '/Dest' in a:dests.append(ids[a['/Dest'][0].idnum])
assert len(dests)==len(entries),(len(dests),len(entries))
intro=next(pg for e,pg in zip(entries,dests) if e['title']=='Introduction générale')
def roman(n):
    return ['','i','ii','iii','iv','v','vi','vii','viii','ix','x'][n]
mapping={e['name']:{'title':e['title'],'physical':pg,'label':str(pg-intro+1) if pg>=intro else roman(pg-1)} for e,pg in zip(entries,dests)}
d=Document(W.parents[1]/'Memoire_Licence_MEF_SIIGFP_revise.docx')
count=0
for p in d._element.iter(qn('w:p')):
    for instr in p.iter(qn('w:instrText')):
        m=re.match(r'\s*PAGEREF\s+(\w+)',instr.text or '')
        if not m:continue
        node=instr.getparent().getnext();sep=False
        while node is not None:
            cs=list(node.iter(qn('w:fldChar')))
            if sep:
                ts=list(node.iter(qn('w:t')))
                if ts:
                    if len(sys.argv)>2:assert ts[0].text==mapping[m.group(1)]['label'],(m.group(1),ts[0].text,mapping[m.group(1)])
                    else:ts[0].text=mapping[m.group(1)]['label']
                    count+=1;break
            if any(c.get(qn('w:fldCharType'))=='separate' for c in cs):sep=True
            node=node.getnext()
assert count==88
if len(sys.argv)==2:d.save(W.parents[1]/'Memoire_Licence_MEF_SIIGFP_revise.docx')
(W/'pagination.json').write_text(json.dumps(mapping,ensure_ascii=False,indent=2),encoding='utf-8')
print('pages',len(r.pages),'links',count,'intro',intro)
print([(v['title'],v['label']) for v in mapping.values() if v['title'].startswith(('2.12','2.13','Remerciements'))])
