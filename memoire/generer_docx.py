"""Rebuild the editable thesis from the reviewed chapters and original figures."""
from pathlib import Path
import re,json
from docx import Document
from docx.shared import Cm,Pt,RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.section import WD_SECTION_START
from docx.enum.table import WD_TABLE_ALIGNMENT,WD_CELL_VERTICAL_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn

ROOT=Path(__file__).resolve().parent
D=Document();SEC=D.sections[0]
SEC.page_width=Cm(21);SEC.page_height=Cm(29.7)
SEC.top_margin=Cm(2.3);SEC.bottom_margin=Cm(2.3);SEC.left_margin=Cm(3);SEC.right_margin=Cm(2.2)
SEC.header_distance=Cm(1.1);SEC.footer_distance=Cm(1.1)
WIDTH=15.8
styles=D.styles
for name in ['Normal','Title','Subtitle','Heading 1','Heading 2','Heading 3','Caption','Header','Footer']:
    st=styles[name];st.font.name='Times New Roman';st.font.color.rgb=RGBColor(0,0,0)
    st.element.get_or_add_rPr().append(OxmlElement('w:lang'))
    st.element.rPr[-1].set(qn('w:val'),'fr-FR')
normal=styles['Normal'];normal.font.size=Pt(12)
normal.paragraph_format.line_spacing=1.3;normal.paragraph_format.space_after=Pt(7)
normal.paragraph_format.widow_control=True
for name,size in [('Heading 1',18),('Heading 2',14),('Heading 3',12)]:
    st=styles[name];st.font.size=Pt(size);st.font.bold=True
    st.paragraph_format.space_before=Pt(16);st.paragraph_format.space_after=Pt(10)
    st.paragraph_format.keep_with_next=True
styles['Heading 1'].paragraph_format.page_break_before=True
styles['Title'].font.size=Pt(24);styles['Title'].font.bold=True
styles['Caption'].font.size=Pt(10.5);styles['Caption'].font.italic=False
styles['Caption'].paragraph_format.space_after=Pt(6)
styles['Caption'].paragraph_format.line_spacing=1.05
for name in ['TOC 1','TOC 2']:
    if name not in styles:styles.add_style(name,1)
    styles[name].font.name='Times New Roman';styles[name].font.size=Pt(11)
    styles[name].font.color.rgb=RGBColor(0,0,0)
    styles[name].paragraph_format.space_after=Pt(5)
    styles[name].paragraph_format.line_spacing=1.05
    styles[name].paragraph_format.left_indent=Cm(.5 if name.endswith('2') else 0)
styles['TOC 1'].font.bold=True
settings=D.settings.element
up=OxmlElement('w:updateFields');up.set(qn('w:val'),'true');settings.append(up)
D.core_properties.title='Gestion et suivi des stagiaires au MEF'
D.core_properties.subject='Mémoire de Licence en Informatique — projet MEF SIIGFP'
D.core_properties.author='';D.core_properties.last_modified_by=''
D.core_properties.comments=''

bookmark_id=0
def bookmark(p,name):
    global bookmark_id
    bookmark_id+=1;start=OxmlElement('w:bookmarkStart');start.set(qn('w:id'),str(bookmark_id));start.set(qn('w:name'),name)
    end=OxmlElement('w:bookmarkEnd');end.set(qn('w:id'),str(bookmark_id));p._p.insert(0,start);p._p.append(end)
def field(p,instr,cache=''):
    a=OxmlElement('w:fldChar');a.set(qn('w:fldCharType'),'begin');p.add_run()._r.append(a)
    a=OxmlElement('w:instrText');a.set(qn('xml:space'),'preserve');a.text=' '+instr+' ';p.add_run()._r.append(a)
    a=OxmlElement('w:fldChar');a.set(qn('w:fldCharType'),'separate');p.add_run()._r.append(a)
    p.add_run(cache)
    a=OxmlElement('w:fldChar');a.set(qn('w:fldCharType'),'end');p.add_run()._r.append(a)
def rich(p,s):
    # Live references to caption sequence bookmarks.
    pos=0
    for m in re.finditer(r'\b([Ff]igure|[Tt]ableau) (\d+)\b',s):
        p.add_run(s[pos:m.start()]);p.add_run(m.group(1)+' ')
        field(p,f'REF {m.group(1).lower()}_{m.group(2)} \\h',m.group(2));pos=m.end()
    p.add_run(s[pos:])
def para(s,style=None):
    p=D.add_paragraph(style=style);rich(p,s)
    if style is None:p.alignment=WD_ALIGN_PARAGRAPH.JUSTIFY
    return p
def page_num(sec,fmt='decimal',start=1):
    sec.footer.is_linked_to_previous=False
    n=OxmlElement('w:pgNumType');n.set(qn('w:fmt'),fmt);n.set(qn('w:start'),str(start));sec._sectPr.append(n)
    p=sec.footer.paragraphs[0];p.alignment=WD_ALIGN_PARAGRAPH.CENTER
    field(p,'PAGE','1');p.style=styles['Footer']
    for r in p.runs:r.font.size=Pt(10)

# Cover: original logos embedded with unchanged proportions.
p=D.add_paragraph();p.paragraph_format.space_after=Pt(36)
p.paragraph_format.tab_stops.add_tab_stop(Cm(11.4))
p.add_run().add_picture('C:/Users/Andomalala/Downloads/83bcdb20-6569-490e-92f8-003f0d6f5ee8.jpg',width=Cm(6.2))
p.add_run('\t');p.add_run().add_picture('C:/Users/Andomalala/Downloads/R.png',width=Cm(3.9))
p=para('Gestion et suivi des stagiaires\nau Ministère de l’Économie\net des Finances','Title');p.alignment=WD_ALIGN_PARAGRAPH.CENTER
p.paragraph_format.space_after=Pt(18)
p=para('Conception et réalisation d’une application web\nProjet MEF SIIGFP');p.alignment=WD_ALIGN_PARAGRAPH.CENTER;p.paragraph_format.space_after=Pt(26)
p=para('Par\n[À CONFIRMER : nom et prénoms]\nNuméro étudiant : [À CONFIRMER]');p.alignment=WD_ALIGN_PARAGRAPH.CENTER;p.paragraph_format.space_after=Pt(26)
p=para('Mémoire présenté en vue de l’obtention du grade de\nLicence en Informatique\nOption Développement d’applications\nIT University');p.alignment=WD_ALIGN_PARAGRAPH.CENTER;p.paragraph_format.space_after=Pt(20)
p=para('Année universitaire et date de soutenance : [À CONFIRMER]');p.alignment=WD_ALIGN_PARAGRAPH.CENTER;p.paragraph_format.space_after=Pt(18)
p=para('Encadreur pédagogique : [À CONFIRMER]\nEncadreur professionnel : [À CONFIRMER]\nPrésident et membres du jury : [À CONFIRMER]');p.alignment=WD_ALIGN_PARAGRAPH.LEFT
for r in p.runs:r.font.size=Pt(11)

pre=D.add_section(WD_SECTION_START.NEW_PAGE);page_num(pre,'lowerRoman',1)

# Read content before writing navigation caches.
chapters=[p.read_text(encoding='utf-8') for p in sorted((ROOT/'chapitres').glob('*.md'))]
headings=[];figures=[];tables=[]
for text in chapters:
    for line in text.splitlines():
        if line.startswith('# '):headings.append((1,line[2:]))
        elif line.startswith('## '):headings.append((2,line[3:]))
        elif line.startswith(':::figure '):figures.append(line[10:].split('|')[1])
        elif line.startswith(':::table '):tables.append(line[9:])
def auto_list(title,instr,entries):
    p=D.add_paragraph(title,'Heading 1');p.paragraph_format.page_break_before=(title!='Table des matières')
    p=D.add_paragraph();p.paragraph_format.space_after=Pt(3)
    a=OxmlElement('w:fldChar');a.set(qn('w:fldCharType'),'begin');p.add_run()._r.append(a)
    a=OxmlElement('w:instrText');a.set(qn('xml:space'),'preserve');a.text=' '+instr+' ';p.add_run()._r.append(a)
    a=OxmlElement('w:fldChar');a.set(qn('w:fldCharType'),'separate');p.add_run()._r.append(a)
    for lev,t in entries:D.add_paragraph(t,'TOC '+str(lev))
    p=D.add_paragraph();a=OxmlElement('w:fldChar');a.set(qn('w:fldCharType'),'end');p.add_run()._r.append(a)
    p=para('Champ automatique — actualiser dans Word pour afficher les numéros de page.')
    for r in p.runs:r.italic=True;r.font.size=Pt(10)
auto_list('Table des matières','TOC \\o "1-2" \\h \\z \\u',headings)
auto_list('Liste des tableaux','TOC \\h \\z \\c "Tableau"',[(1,f'Tableau {i+1} — {t}') for i,t in enumerate(tables)])
auto_list('Liste des figures','TOC \\h \\z \\c "Figure"',[(1,f'Figure {i+1} — {t}') for i,t in enumerate(figures)])

fig_no=table_no=0
def caption(kind,num,title):
    p=D.add_paragraph(style='Caption');p.alignment=WD_ALIGN_PARAGRAPH.CENTER
    p.add_run(kind+' ');field(p,f'SEQ {kind} \\* ARABIC',str(num))
    # Bookmark the entire label; REF will be replaced with a number-only bookmark below.
    # The sequence result alone receives the bookmark for live numeric references.
    global bookmark_id
    bookmark_id+=1
    start=OxmlElement('w:bookmarkStart');start.set(qn('w:id'),str(bookmark_id));start.set(qn('w:name'),kind.lower()+'_'+str(num))
    end=OxmlElement('w:bookmarkEnd');end.set(qn('w:id'),str(bookmark_id))
    runs=p._p.findall(qn('w:r'))
    # Runs: label, begin, instruction, separate, cached result, end.
    idx=p._p.index(runs[1]);p._p.insert(idx,start);p._p.append(end)
    p.add_run(' — '+title)
    return p
def table(title,rows):
    global table_no
    table_no+=1;cap=caption('Tableau',table_no,title);cap.paragraph_format.keep_with_next=True
    n=len(rows[0]);t=D.add_table(rows=1,cols=n);t.alignment=WD_TABLE_ALIGNMENT.CENTER;t.autofit=False
    if n==4: widths=[2.6,3.4,4.5,5.3]
    elif n==3:widths=[3.4,4.7,7.7]
    else:widths=[4,11.8]
    if title=='Acteurs et responsabilités observées':widths=[2.5,4.0,9.3]
    if title=='Modalités de clôture présentes dans le code':widths=[2.5,3.4,4.2,5.7]
    for col,w in zip(t.columns,widths):col.width=Cm(w)
    for i,row in enumerate(rows):
        cells=t.rows[0].cells if i==0 else t.add_row().cells
        for j,value in enumerate(row):
            c=cells[j];c.width=Cm(widths[j]);c.vertical_alignment=WD_CELL_VERTICAL_ALIGNMENT.CENTER
            pp=c.paragraphs[0];pp.paragraph_format.line_spacing=1.05;pp.paragraph_format.space_after=Pt(4);pp.paragraph_format.space_before=Pt(4)
            rr=pp.add_run(value);rr.font.size=Pt(10.5);rr.bold=(i==0)
            if i==0:rr.font.color.rgb=RGBColor(255,255,255)
            props=c._tc.get_or_add_tcPr();sh=OxmlElement('w:shd');sh.set(qn('w:fill'),'354F60' if i==0 else ('F3F5F7' if i%2==0 else 'FFFFFF'));props.append(sh)
            mar=OxmlElement('w:tcMar')
            for side in ['top','left','bottom','right']:
                item=OxmlElement('w:'+side);item.set(qn('w:w'),'85');item.set(qn('w:type'),'dxa');mar.append(item)
            props.append(mar)
            borders=OxmlElement('w:tcBorders')
            for side in ['top','left','bottom','right']:
                item=OxmlElement('w:'+side);item.set(qn('w:val'),'single');item.set(qn('w:sz'),'4');item.set(qn('w:color'),'D9D9D9');borders.append(item)
            props.append(borders)
        trPr=t.rows[i]._tr.get_or_add_trPr();trPr.append(OxmlElement('w:cantSplit'))
        if i==0:trPr.append(OxmlElement('w:tblHeader'))
    D.add_paragraph().paragraph_format.space_after=Pt(2)
def process(text,body_start=False):
    global fig_no
    lines=text.splitlines();i=0
    while i<len(lines):
        line=lines[i].strip();i+=1
        if not line:continue
        if line.startswith('# '):
            p=D.add_paragraph(line[2:],'Heading 1')
            if body_start:p.paragraph_format.page_break_before=False;body_start=False
        elif line.startswith('## '):D.add_paragraph(line[3:],'Heading 2')
        elif line.startswith(':::table '):
            title=line[9:];rows=[]
            while i<len(lines) and lines[i].strip()!=':::':
                rows.append([x.strip() for x in lines[i].split('|')]);i+=1
            i+=1;table(title,rows)
        elif line.startswith(':::figure '):
            f,title,source=line[10:].split('|');fig_no+=1
            p=D.add_paragraph();p.alignment=WD_ALIGN_PARAGRAPH.CENTER;p.paragraph_format.keep_with_next=True
            p.paragraph_format.space_before=Pt(7);p.paragraph_format.space_after=Pt(4)
            shape=p.add_run().add_picture(str(ROOT/'illustrations'/f),width=Cm(WIDTH))
            shape._inline.docPr.set('descr',title+' — '+source)
            p=caption('Figure',fig_no,title);p.paragraph_format.keep_with_next=True
            p=para(source);p.alignment=WD_ALIGN_PARAGRAPH.CENTER;p.paragraph_format.line_spacing=1.0
            for r in p.runs:r.font.size=Pt(9.5);r.italic=True
        else:para(line)

process(chapters[0])
main=D.add_section(WD_SECTION_START.NEW_PAGE);page_num(main,'decimal',1)
process(chapters[1],True)
for c in chapters[2:]:process(c)

# All headings are black and have no decorative rule inherited from a theme.
for s in D.styles:
    if s.type==1 and (s.name.startswith('Heading') or s.name in ['Title','Subtitle']):
        s.font.color.rgb=RGBColor(0,0,0)
        pp=s.element.find(qn('w:pPr'))
        if pp is not None:
            for b in pp.findall(qn('w:pBdr')):pp.remove(b)
for p in D.paragraphs:
    if p.style.name.startswith('Heading'):p.paragraph_format.keep_with_next=True

output=ROOT/'Memoire_Licence_MEF_SIIGFP.docx';D.save(output)
combined='\n\n'.join(chapters);(ROOT/'MEMOIRE_COMPLET.md').write_text(combined,encoding='utf-8')
summary={'output':str(output),'paragraphs':len(D.paragraphs),'tables':table_no,'figures':fig_no,'words_markdown':len(combined.split()),'sections':len(D.sections)}
(ROOT/'tmp/docx_structure.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2),encoding='utf-8')
print(json.dumps(summary,ensure_ascii=False))
