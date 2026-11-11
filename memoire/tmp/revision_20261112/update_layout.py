import os,sys,time,subprocess,json
from pathlib import Path
LO=Path('C:/Program Files/LibreOffice/program')
dll=os.add_dll_directory(str(LO));sys.path.insert(0,str(LO));import uno
from com.sun.star.beans import PropertyValue
W=Path(__file__).resolve().parent
docpath=W.parents[1]/'Memoire_Licence_MEF_SIIGFP_corrige.docx'
def prop(k,v):
    p=PropertyValue();p.Name=k;p.Value=v;return p
profile=(W/'lo_profile').as_uri()
process=subprocess.Popen([str(LO/'soffice.exe'),'-env:UserInstallation='+profile,'--headless','--invisible','--norestore','--nodefault','--nofirststartwizard','--accept=socket,host=127.0.0.1,port=20117;urp;StarOffice.ComponentContext'],creationflags=subprocess.CREATE_NO_WINDOW)
local=uno.getComponentContext();resolver=local.ServiceManager.createInstanceWithContext('com.sun.star.bridge.UnoUrlResolver',local)
try:
    for attempt in range(40):
        try:ctx=resolver.resolve('uno:socket,host=127.0.0.1,port=20117;urp;StarOffice.ComponentContext');break
        except Exception:time.sleep(.5)
    desktop=ctx.ServiceManager.createInstanceWithContext('com.sun.star.frame.Desktop',ctx)
    doc=desktop.loadComponentFromURL(docpath.as_uri(),'_blank',0,(prop('Hidden',True),prop('ReadOnly',False),prop('UpdateDocMode',3)))
    print('Indexes',doc.DocumentIndexes.Count,flush=True)
    for _ in range(3):
        doc.TextFields.refresh()
        for i in range(doc.DocumentIndexes.Count):doc.DocumentIndexes.getByIndex(i).update()
        doc.refresh()
    doc.storeAsURL(docpath.as_uri(),(prop('FilterName','Office Open XML Text'),prop('Overwrite',True)))
    doc.storeToURL((W/'layout.pdf').as_uri(),(prop('FilterName','writer_pdf_Export'),prop('Overwrite',True)))
    controller=doc.getCurrentController();cursor=controller.getViewCursor()
    pages={}
    for name in doc.Bookmarks.ElementNames:
        if name.startswith(('section_','fig_','tab_')):
            cursor.gotoRange(doc.Bookmarks.getByName(name).Anchor,False)
            pages[name]=cursor.getPage()
    (W/'bookmark_pages.json').write_text(json.dumps(pages,indent=2),encoding='utf-8')
    print('Pages',doc.CurrentController.PageCount,'Bookmarks',len(pages),flush=True)
    doc.close(True);desktop.terminate()
finally:
    try:process.wait(timeout=10)
    except subprocess.TimeoutExpired:process.terminate()
