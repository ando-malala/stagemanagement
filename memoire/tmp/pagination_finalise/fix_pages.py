from pathlib import Path
from zipfile import ZipFile
from lxml import etree
from pypdf import PdfReader
import re, json, sys

base=Path(__file__).resolve().parent
src=base.parents[1]/'Memoire_Licence_MEF_SIIGFP_finalise.docx'
out=src.with_name('Memoire_Licence_MEF_SIIGFP_finalise_pagination_corrigee.docx')
pdf=PdfReader(base/'source'/f'{src.stem}.pdf')
ids={p.indirect_reference.idnum:i+1 for i,p in enumerate(pdf.pages)}
dests=[]
for p in pdf.pages:
    annotations=[a.get_object() for a in p.get('/Annots',[])]
    for a in sorted(annotations,key=lambda a:(-float(a['/Rect'][3]),float(a['/Rect'][0]))):
        if a.get('/Subtype')=='/Link' and '/Dest' in a and float(a['/Rect'][0])<450:
            dests.append(ids[a['/Dest'][0].idnum])
ns={'w':'http://schemas.openxmlformats.org/wordprocessingml/2006/main'}
with ZipFile(src) as z:
    xml=z.read('word/document.xml')
    root=etree.fromstring(xml)
    links=root.xpath('//w:hyperlink[@w:anchor]',namespaces=ns)
    assert len(links)==len(dests)==88,(len(links),len(dests))
    names=[h.get('{'+ns['w']+'}anchor') for h in links]
    intro=dests[names.index('memoire_section_6')]
    roman=['','i','ii','iii','iv','v','vi','vii','viii','ix','x','xi']
    mapping={name:str(pg-intro+1) if pg>=intro else roman[pg-1] for name,pg in zip(names,dests)}
    changes=[]
    seen=[]
    def patch_paragraph(m):
        p=m.group()
        anchor=re.search(rb'<w:hyperlink\b[^>]*w:anchor="([^"]+)"',p)
        if not anchor:return p
        name=anchor.group(1).decode()
        if name not in mapping:return p
        seen.append(name)
        ts=list(re.finditer(rb'<w:t(?:\s[^>]*)?>([^<]*)</w:t>',p))
        last=ts[-1]
        old=last.group(1).decode()
        assert re.fullmatch(r'[0-9ivxlc]+',old),(name,old)
        new=mapping[name]
        if old!=new:changes.append({'anchor':name,'old':old,'new':new})
        return p[:last.start(1)]+new.encode()+p[last.end(1):]
    revised=re.sub(rb'<w:p(?:\s[^>]*)?>.*?</w:p>',patch_paragraph,xml,flags=re.S)
    assert len(seen)==88
    print(json.dumps({'pages':len(pdf.pages),'references':len(seen),'changes':changes}))
    if '--write' in sys.argv:
        with ZipFile(out,'w') as target:
            for info in z.infolist():target.writestr(info,revised if info.filename=='word/document.xml' else z.read(info.filename))
        with ZipFile(out) as target:
            assert z.namelist()==target.namelist()
            for name in z.namelist():
                if name!='word/document.xml':assert z.read(name)==target.read(name),name
        # Reversibility proves the XML differs only in these numeric text values.
        a=etree.fromstring(xml);b=etree.fromstring(revised)
        at=a.xpath('//w:t',namespaces=ns);bt=b.xpath('//w:t',namespaces=ns)
        assert len(at)==len(bt)
        delta=0
        for x,y in zip(at,bt):
            if x.text!=y.text:
                assert re.fullmatch(r'[0-9ivxlc]+',x.text) and re.fullmatch(r'[0-9ivxlc]+',y.text)
                y.text=x.text;delta+=1
        assert etree.tostring(a)==etree.tostring(b)
        assert delta==len(changes)
        (base/'changes.json').write_text(json.dumps(changes,indent=2),encoding='utf-8')
        print('Verified: only',delta,'page-number text values changed; all other package contents identical.')
