from pathlib import Path
from copy import deepcopy
import json,re
from docx import Document
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Pt, Inches

W=Path(__file__).resolve().parent
out=W.parents[1]/'Memoire_Licence_MEF_SIIGFP_revise.docx'
d=Document('C:/Users/Andomalala/Downloads/Memoire_Licence_MEF_SIIGFP_corrige.docx')
def replace(p,old,new):
    ts=list(p._p.iter(qn('w:t')))
    text=''.join(t.text or '' for t in ts)
    if old not in text:return
    # Preserve unaffected run formatting, including headings and captions.
    start=text.index(old); end=start+len(old); pos=0
    for t in ts:
        v=t.text or ''; a=pos;b=pos+len(v);pos=b
        if b<=start or a>=end:continue
        t.text=v[:max(0,start-a)]+(new if a<=start<b else '')+v[max(0,end-a):]

ps=d.paragraphs
thanks=[
"La réalisation de ce mémoire a été rendue possible grâce au soutien de nombreuses personnes. Je souhaite leur exprimer ma reconnaissance pour l’aide apportée au cours de ma formation et de mon stage de fin d’études.",
"J’adresse mes remerciements à l’ensemble du corps enseignant et du personnel administratif de l’IT University. La qualité de la formation reçue et les connaissances transmises ont constitué une base essentielle pour mener ce projet et préparer mon entrée dans la vie professionnelle.",
"Je remercie le Ministère de l’Économie et des Finances, ainsi que l’ensemble du personnel de la Cellule SIGFP, pour leur accueil et pour l’opportunité de réaliser ce stage. Leur disponibilité et les échanges au quotidien m’ont permis de mieux comprendre les besoins du service et d’enrichir mon expérience professionnelle.",
"Ma reconnaissance s’adresse également à ma famille, à mes parents et à mes proches pour leur soutien et leurs encouragements tout au long de mes études. Leur confiance m’a aidé à persévérer et à mener ce travail à son terme.",
"Enfin, je remercie toutes les personnes qui ont contribué, par leurs conseils, leur aide ou leur présence, à la réalisation de ce mémoire. Je leur témoigne ici ma sincère gratitude."
]
for i,txt in zip([49,50,51,54,55],thanks):
    p=ps[i];p.clear();p.add_run(txt).font.size=Pt(12)
    p.paragraph_format.left_indent=Inches(0)
    p.paragraph_format.right_indent=Inches(0)
    p.paragraph_format.first_line_indent=Inches(0.5)
    p.paragraph_format.space_before=Pt(12)
    p.paragraph_format.space_after=Pt(6)
    p.paragraph_format.line_spacing=1.15
    for np in list(p._p.pPr.findall(qn('w:numPr'))):p._p.pPr.remove(np)
for i in [52,53]:ps[i]._p.getparent().remove(ps[i]._p)
for p in d.paragraphs:
    if 'La validation s’appuie sur les vérifications présentées en section 2.12.' in p.text:
        replace(p,'La validation s’appuie sur les vérifications présentées en section 2.12. ', '')
    replace(p,'Les résultats obtenus doivent être rapprochés des limites de validation décrites en section 2.12 :','Les résultats obtenus doivent être appréciés au regard des limites de validation :')
    replace(p,'puis la validation, le déploiement et les difficultés rencontrées','puis le déploiement et les difficultés rencontrées')
    replace(p,'2.13','2.12')
    replace(p,'2.14','2.13')

# Rebuild the three navigation fields from the current document content.
# The supplied copy has stale links and missing caption page fields.
sdts=list(d._element.body.findall(qn('w:sdt')))
groups=[[],[],[]]
bookmark_id=max(int(b.get(qn('w:id'))) for b in d._element.iter(qn('w:bookmarkStart')))+1
for p in d.paragraphs:
    t=p.text.strip();group=None;level=1
    if p.style.name.startswith('Heading'):
        group=0;level=int(p.style.name.split()[-1])
    elif re.match(r'Tableau \d+\s',t):group=1
    elif re.match(r'Figure \d+\s',t):group=2
    if group is None:continue
    name=f'nav_{group}_{len(groups[group])+1}'
    begin=OxmlElement('w:bookmarkStart');begin.set(qn('w:id'),str(bookmark_id));begin.set(qn('w:name'),name)
    end=OxmlElement('w:bookmarkEnd');end.set(qn('w:id'),str(bookmark_id));bookmark_id+=1
    p._p.insert(1 if p._p.pPr is not None else 0,begin);p._p.append(end)
    groups[group].append({'title':t,'name':name,'level':level})
def run(p,text=None,instr=None,char=None):
    r=OxmlElement('w:r');p.append(r)
    if text is not None:
        e=OxmlElement('w:t');e.text=text;r.append(e)
    if instr:
        e=OxmlElement('w:instrText');e.set(qn('xml:space'),'preserve');e.text=instr;r.append(e)
    if char:
        e=OxmlElement('w:fldChar');e.set(qn('w:fldCharType'),char);r.append(e)
    return r
for g,sdt in enumerate(sdts):
    content=sdt.find(qn('w:sdtContent'))
    sample=next(p for p in content.findall(qn('w:p')) if p.find(qn('w:hyperlink')) is not None)
    fmt=deepcopy(sample.find(qn('w:pPr')))
    for c in list(content):content.remove(c)
    for j,e in enumerate(groups[g]):
        p=OxmlElement('w:p');p.append(deepcopy(fmt));content.append(p)
        pp=p.find(qn('w:pPr'))
        ind=pp.find(qn('w:ind'));ind.set(qn('w:left'),str((e['level']-1)*260));ind.set(qn('w:firstLine'),'0')
        for tab in pp.iter(qn('w:tab')):tab.set(qn('w:leader'),'dot')
        if j==0:
            run(p,char='begin');run(p,instr=(' TOC \\o "1-3" \\h \\z \\u ' if g==0 else f' TOC \\c "{["","Tableau","Figure"][g]}" \\h \\z '));run(p,char='separate')
        h=OxmlElement('w:hyperlink');h.set(qn('w:anchor'),e['name']);p.append(h);run(h,text=e['title'])
        r=run(p);r.append(OxmlElement('w:tab'))
        run(p,char='begin');run(p,instr=f' PAGEREF {e["name"]} ');run(p,char='separate');run(p,text='1');run(p,char='end')
        if j==len(groups[g])-1:run(p,char='end')
pg=d.sections[1]._sectPr.find(qn('w:pgNumType'))
pg.set(qn('w:fmt'),'lowerRoman')
d.save(out)
(W/'entries.json').write_text(json.dumps(groups,ensure_ascii=False,indent=2),encoding='utf-8')
print(out)
print([len(g) for g in groups])
