from pathlib import Path
import json,re,collections,hashlib
from pypdf import PdfReader
root=Path(__file__).resolve().parents[2]
out=root/'memoire/tmp'
downloads=Path('C:/Users/Andomalala/Downloads')
for tag,pattern in [('candy','*2638.pdf'),('tsinj','*2362.pdf')]:
    f=next(downloads.glob(pattern)); pdf=PdfReader(f)
    text='\n\n'.join(f'=== PAGE PDF {i+1} ===\n'+(p.extract_text() or '') for i,p in enumerate(pdf.pages))
    (out/f'{tag}.txt').write_text(text,encoding='utf-8')
    print(tag,len(pdf.pages),'pages',len(text),'characters')
files=[]
for base in ['backend/src','frontend/src','frontend/tests','conception','docs','scripts']:
    for f in (root/base).rglob('*'):
        if f.is_file(): files.append(f)
inventory=[{'file':str(f.relative_to(root)).replace('\\','/'),'lines':len(f.read_text(encoding='utf-8',errors='replace').splitlines()),'sha256':hashlib.sha256(f.read_bytes()).hexdigest()} for f in files if f.suffix in ['.java','.js','.jsx','.sql','.md','.properties','.ps1','.py','.css']]
(out/'inventaire.json').write_text(json.dumps(inventory,ensure_ascii=False,indent=2),encoding='utf-8')
sql=(root/'conception/Conception2.sql').read_text(encoding='utf-8')
tables=re.findall(r'CREATE TABLE\s+(\w+)',sql,re.I)
views=re.findall(r'CREATE\s+(?:OR REPLACE\s+)?VIEW\s+(\w+)',sql,re.I)
triggers=re.findall(r'CREATE TRIGGER\s+(\w+)',sql,re.I)
models=list((root/'backend/src/main/java/mg/mef/stagemanagement/model').glob('*.java'))
mapped=[]
for f in models:
    s=f.read_text(encoding='utf-8');m=re.search(r'@Table\(name\s*=\s*"([^"]+)"',s)
    if m: mapped.append(m.group(1))
stats={'tables':tables,'views':views,'triggers':triggers,'models':len(models),'sql_unmapped':sorted(set(tables)-set(mapped)),'jpa_without_sql_table':sorted(set(mapped)-set(tables)),'file_counts':dict(collections.Counter(f.suffix for f in files))}
(out/'schema.json').write_text(json.dumps(stats,ensure_ascii=False,indent=2),encoding='utf-8')
print(json.dumps(stats,ensure_ascii=False,indent=2))
