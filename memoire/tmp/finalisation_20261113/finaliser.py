from pathlib import Path
import json,re,hashlib
from docx import Document
from docx.shared import Cm,Pt,RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH,WD_TAB_ALIGNMENT,WD_TAB_LEADER
from docx.enum.style import WD_STYLE_TYPE
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.text.paragraph import Paragraph

W=Path(__file__).resolve().parent
SRC=Path('C:/Users/Andomalala/Downloads/Memoire_Licence_MEF_SIIGFP_revise.docx')
OUT=W.parents[1]/'Memoire_Licence_MEF_SIIGFP_finalise.docx'
d=Document(SRC)
original=d.paragraphs
def field(p,code,value):
    for kind in ['begin','code','separate','value','end']:
        r=p.add_run()
        if kind in ['begin','separate','end']:
            e=OxmlElement('w:fldChar');e.set(qn('w:fldCharType'),kind);r._r.append(e)
        elif kind=='code':
            e=OxmlElement('w:instrText');e.set(qn('xml:space'),'preserve');e.text=' '+code+' ';r._r.append(e)
        else:r.text=value

bid=max([int(e.get(qn('w:id'))) for e in d.element.iter(qn('w:bookmarkStart'))]+[0])+1
def bookmark(p,name):
    global bid
    bid+=1
    s=OxmlElement('w:bookmarkStart');s.set(qn('w:id'),str(bid));s.set(qn('w:name'),name)
    e=OxmlElement('w:bookmarkEnd');e.set(qn('w:id'),str(bid))
    p._p.insert(1 if p._p.pPr is not None else 0,s);p._p.append(e)

for name,size in [('Normal',12),('Caption',11),('Heading 1',16),('Heading 2',14),('Heading 3',12),('Title',20)]:
    try:s=d.styles[name]
    except KeyError:s=d.styles.add_style(name,WD_STYLE_TYPE.PARAGRAPH)
    s.font.name='Times New Roman';s.font.size=Pt(size);s.font.color.rgb=RGBColor(0,0,0)
    s.paragraph_format.widow_control=True
    s.paragraph_format.line_spacing=1.5 if name=='Normal' else 1.15
    s.paragraph_format.space_after=Pt(6)
    if name.startswith('Heading'):
        s.font.bold=True;s.paragraph_format.keep_with_next=True
        s.paragraph_format.space_before=Pt(12)

headings=[];captions=[]
for i,p in enumerate(original):
    t=p.text.strip();fmt=p.paragraph_format
    # Cover has a deliberate layout; preserve its logos, credits, and blocks.
    if i<15:continue
    fmt.widow_control=True
    fmt.left_indent=Cm(0);fmt.right_indent=Cm(0)
    if fmt.tab_stops is not None:fmt.tab_stops.clear_all()
    for r in p.runs:
        r.font.name='Times New Roman';r.font.color.rgb=RGBColor(0,0,0)
    # Remove accidental hand-typed indentation while retaining inline emphasis.
    for r in p.runs:
        if r.text.strip():
            r.text=r.text.lstrip(' \t');break
        elif r.text and not r._r.xpath('.//w:drawing'):r.text=''
    if p.style.name.startswith('Heading'):
        level=int(p.style.name[-1]);name=f'memoire_section_{len(headings)+1}'
        bookmark(p,name);headings.append((t,level,name))
        fmt.alignment=WD_ALIGN_PARAGRAPH.LEFT;fmt.first_line_indent=Cm(0)
        fmt.line_spacing=1.15;fmt.keep_with_next=True;fmt.keep_together=True
        fmt.space_before=Pt(0 if level==1 else 12);fmt.space_after=Pt(10 if level==1 else 6)
        fmt.page_break_before=level==1 or (t.startswith('Annexe ') and not t.startswith('Annexe A '))
        for r in p.runs:r.font.size=Pt({1:16,2:14,3:12}[level]);r.font.bold=True
    elif re.match(r'^(Figure|Tableau)\s+\d+',t):
        match=re.match(r'^(Figure|Tableau)\s+(\d+)\s*[—–-]\s*(.*)',t)
        label,n,title=match.groups();name=f'memoire_{label.lower()}_{n}'
        p.clear();p.style=d.styles['Caption'];p.add_run(label+' ')
        field(p,f'SEQ {label} \\* ARABIC',n);p.add_run(' — '+title);bookmark(p,name)
        captions.append((label,int(n),title,name))
        fmt=p.paragraph_format;fmt.alignment=WD_ALIGN_PARAGRAPH.CENTER
        fmt.first_line_indent=Cm(0);fmt.line_spacing=1.0;fmt.space_before=Pt(6);fmt.space_after=Pt(8)
        fmt.keep_together=True;fmt.keep_with_next=False;fmt.page_break_before=False
        for r in p.runs:r.font.name='Times New Roman';r.font.size=Pt(11);r.font.italic=True
        prev=p._p.getprevious()
        nex=p._p.getnext()
        if nex is not None and nex.tag==qn('w:tbl'):
            fmt.keep_with_next=True
        if prev is not None and prev.tag==qn('w:p'):Paragraph(prev,d._body).paragraph_format.keep_with_next=True
        elif prev is not None and prev.tag==qn('w:tbl'):
            for e in prev.xpath('./w:tr[last()]/w:tc/w:p'):Paragraph(e,d._body).paragraph_format.keep_with_next=True
    elif p._p.xpath('.//w:drawing'):
        fmt.alignment=WD_ALIGN_PARAGRAPH.CENTER;fmt.first_line_indent=Cm(0);fmt.line_spacing=1.0
        fmt.space_before=Pt(6);fmt.space_after=Pt(0);fmt.keep_with_next=True;fmt.keep_together=True
    elif t in ['Table des matières','Liste des tableaux','Liste des figures']:
        fmt.alignment=WD_ALIGN_PARAGRAPH.LEFT;fmt.first_line_indent=Cm(0);fmt.line_spacing=1.15
        fmt.space_before=Pt(0);fmt.space_after=Pt(14);fmt.keep_with_next=True;fmt.page_break_before=True
        for r in p.runs:r.font.size=Pt(16);r.font.bold=True
    else:
        fmt.line_spacing=1.5;fmt.space_before=Pt(0);fmt.space_after=Pt(6)
        fmt.alignment=WD_ALIGN_PARAGRAPH.JUSTIFY;fmt.first_line_indent=Cm(.75)
        fmt.keep_with_next=False;fmt.keep_together=False
        if t.startswith(('Source','[','Note')) or (18<i<34):fmt.first_line_indent=Cm(0)
        for r in p.runs:r.font.size=Pt(12)

# A4, binding margin, and distinct preliminary/body pagination.
for i,s in enumerate(d.sections):
    s.page_width=Cm(21);s.page_height=Cm(29.7)
    s.left_margin=Cm(3);s.right_margin=Cm(2.5);s.top_margin=Cm(2.5);s.bottom_margin=Cm(2.5)
    s.header_distance=Cm(1.25);s.footer_distance=Cm(1.25)
    pg=s._sectPr.find(qn('w:pgNumType'))
    if pg is None:pg=OxmlElement('w:pgNumType');s._sectPr.append(pg)
    pg.set(qn('w:start'),'1');pg.set(qn('w:fmt'),'lowerRoman' if i==1 else 'decimal')
    s.footer.is_linked_to_previous=False
    for p in s.footer.paragraphs:p.clear()
    if i:
        p=s.footer.paragraphs[0];p.alignment=WD_ALIGN_PARAGRAPH.CENTER
        p.paragraph_format.line_spacing=1.0;p.paragraph_format.space_after=Pt(0)
        field(p,'PAGE','1')
        for r in p.runs:r.font.name='Times New Roman';r.font.size=Pt(10)

maxwidth=Cm(15.5)
for shape in d.inline_shapes:
    if shape.width>maxwidth:
        ratio=maxwidth/shape.width;shape.width=maxwidth;shape.height=int(shape.height*ratio)
for table in d.tables:
    table.autofit=False
    widths=[c.width for c in table.columns];total=sum(widths)
    for col,width in zip(table.columns,widths):col.width=int(maxwidth*width/total)
    for ri,row in enumerate(table.rows):
        trpr=row._tr.get_or_add_trPr()
        for e in list(trpr):
            if e.tag==qn('w:trHeight'):trpr.remove(e)
        e=OxmlElement('w:cantSplit');trpr.append(e)
        if ri==0:e=OxmlElement('w:tblHeader');trpr.append(e)
        for ci,cell in enumerate(row.cells):
            cell.width=int(maxwidth*widths[ci]/total)
            pr=cell._tc.get_or_add_tcPr()
            mar=OxmlElement('w:tcMar')
            for side,val in [('top',70),('bottom',70),('left',90),('right',90)]:
                e=OxmlElement('w:'+side);e.set(qn('w:w'),str(val));e.set(qn('w:type'),'dxa');mar.append(e)
            pr.append(mar)
            for p in cell.paragraphs:
                f=p.paragraph_format;f.line_spacing=1.0;f.space_before=Pt(0);f.space_after=Pt(3)
                f.first_line_indent=Cm(0);f.left_indent=Cm(0);f.right_indent=Cm(0)
                f.alignment=WD_ALIGN_PARAGRAPH.LEFT;f.widow_control=True
                for r in p.runs:r.font.name='Times New Roman';r.font.size=Pt(10.5)

# Replace fragile generated lists with linked entries and valid page-reference fields.
sdts=[e for e in d.element.body if e.tag==qn('w:sdt')]
entries=[headings,[(f'{lab} {n} — {title}',1,name) for lab,n,title,name in captions if lab=='Tableau'],[(f'{lab} {n} — {title}',1,name) for lab,n,title,name in captions if lab=='Figure']]
for block,items in zip(sdts,entries):
    for title,level,name in items:
        e=OxmlElement('w:p');block.addprevious(e);p=Paragraph(e,d._body)
        f=p.paragraph_format;f.line_spacing=1.0;f.space_after=Pt(5);f.keep_together=True
        f.left_indent=Cm((level-1)*.45);f.first_line_indent=Cm(0);f.right_indent=Cm(.6)
        f.tab_stops.add_tab_stop(Cm(15.5-(level-1)*.45),WD_TAB_ALIGNMENT.RIGHT,WD_TAB_LEADER.DOTS)
        link=OxmlElement('w:hyperlink');link.set(qn('w:anchor'),name);link.set(qn('w:history'),'1')
        r=OxmlElement('w:r');pr=OxmlElement('w:rPr')
        font=OxmlElement('w:rFonts');font.set(qn('w:ascii'),'Times New Roman');font.set(qn('w:hAnsi'),'Times New Roman');pr.append(font)
        sz=OxmlElement('w:sz');sz.set(qn('w:val'),'22');pr.append(sz)
        if level==1 and block is sdts[0]:pr.append(OxmlElement('w:b'))
        r.append(pr);t=OxmlElement('w:t');t.text=title;r.append(t);link.append(r);p._p.append(link)
        p.add_run('\t');field(p,f'PAGEREF {name} \\h','1')
        for run in p.runs:run.font.name='Times New Roman';run.font.size=Pt(11)
    block.getparent().remove(block)

# Keep a section break from adding a second, empty page before the next heading.
original[15].paragraph_format.page_break_before=False
original[54].paragraph_format.page_break_before=False
for e in d.settings.element.findall(qn('w:updateFields')):d.settings.element.remove(e)
d.save(OUT)
(W/'structure.json').write_text(json.dumps({'headings':headings,'captions':captions,'source_sha256':hashlib.sha256(SRC.read_bytes()).hexdigest()},ensure_ascii=False,indent=2),encoding='utf-8')
print(OUT)
