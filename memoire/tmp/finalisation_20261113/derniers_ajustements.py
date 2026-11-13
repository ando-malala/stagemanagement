from pathlib import Path
import json,re
from docx import Document
from docx.oxml.ns import qn
from docx.shared import Pt
W=Path(__file__).resolve().parent
p=W.parents[1]/'Memoire_Licence_MEF_SIIGFP_finalise.docx'
d=Document(p)
for par in d.paragraphs:
    if par.text.startswith('Ces différentes perspectives permettraient'):
        par.text='Ces évolutions amélioreraient les performances et la maintenance de l’application, pour une utilisation durable au sein de la Cellule SIGFP.'
        for r in par.runs:r.font.name='Times New Roman';r.font.size=Pt(12)
m=json.loads((W/'pagination.json').read_text(encoding='utf-8'))
for par in d.paragraphs:
    for e in list(par._p.iter(qn('w:instrText'))):
        s=re.match(r'\s*PAGEREF\s+(\w+)',e.text or '')
        if s and not m[s[1]]['label'].isdigit():
            cur=e.getparent().getprevious();remove=[]
            while cur is not None:
                remove.append(cur)
                if any(x.get(qn('w:fldCharType'))=='end' for x in cur.iter(qn('w:fldChar'))):break
                cur=cur.getnext()
            for el in remove:par._p.remove(el)
            r=par.add_run(m[s[1]]['label']);r.font.name='Times New Roman';r.font.size=Pt(11)
d.save(p)
print('Final adjustments saved')
