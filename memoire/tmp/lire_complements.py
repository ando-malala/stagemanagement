from pathlib import Path
from pypdf import PdfReader
from docx import Document
from openpyxl import load_workbook
from docx.oxml.ns import qn
import json,difflib
R=Path(__file__).resolve().parents[2];O=R/'memoire/tmp/complements';O.mkdir(exist_ok=True)
S=Path('C:/Users/Andomalala/Downloads')
pdf=PdfReader(S/'CDC_suivi_stagiaires.pdf')
text='\n\n'.join(f'=== PAGE {i+1} ===\n{p.extract_text() or ""}' for i,p in enumerate(pdf.pages))
(O/'cdc.txt').write_text(text,encoding='utf-8')
wb=load_workbook(S/'Estimation_taches_ETU003156.xlsm',data_only=False,keep_vba=True)
cached=load_workbook(S/'Estimation_taches_ETU003156.xlsm',data_only=True,keep_vba=True)
res={}
for w in wb:
    rows=[]
    for row in w:
        vals=[]
        for c in row:
            if c.value is not None:
                v=c.value
                vals.append({'cell':c.coordinate,'value':str(v),'cached':str(cached[w.title][c.coordinate].value) if c.data_type=='f' else None})
        if vals: rows.append(vals)
    res[w.title]={'rows':rows,'merged':list(map(str,w.merged_cells.ranges)),'dimensions':w.calculate_dimension()}
(O/'taches.json').write_text(json.dumps(res,ensure_ascii=False,indent=2),encoding='utf-8')
d=Document(S/'Memoire_Licence_MEF_SIIGFP_concept1.docx')
paras=[{'index':i,'style':p.style.name,'text':p.text} for i,p in enumerate(d.paragraphs)]
tables=[[[c.text for c in r.cells] for r in t.rows] for t in d.tables]
(O/'concept.json').write_text(json.dumps({'paragraphs':paras,'tables':tables},ensure_ascii=False,indent=2),encoding='utf-8')
(O/'concept.txt').write_text('\n'.join(f'{p["index"]:03d} [{p["style"]}] {p["text"]}' for p in paras)+'\n\nTABLES\n'+json.dumps(tables,ensure_ascii=False,indent=2),encoding='utf-8')
old=Document(R/'memoire/Memoire_Licence_MEF_SIIGFP.docx')
diff='\n'.join(difflib.unified_diff([p.text for p in old.paragraphs],[p.text for p in d.paragraphs],fromfile='version initiale',tofile='concept1',lineterm=''))
(O/'modifications_utilisateur.diff').write_text(diff,encoding='utf-8')
print('CDC:',len(pdf.pages),'pages;',len(text),'caractères')
print('Classeur:',[(w.title,w.max_row,w.max_column) for w in wb])
print('Concept:',len(paras),'paragraphes;',len(tables),'tableaux;',len(d.inline_shapes),'images;',len(diff),'caractères de différences')
print('Styles titres:',[(p['index'],p['style'],p['text']) for p in paras if p['style'].startswith(('Heading','Title','Titre'))])
