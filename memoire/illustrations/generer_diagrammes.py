from pathlib import Path
from PIL import Image,ImageDraw,ImageFont
import math
OUT=Path(__file__).resolve().parent
FONT='C:/Windows/Fonts/arial.ttf'; BOLD='C:/Windows/Fonts/arialbd.ttf'
INK='#172B3A'; LINE='#45616F'; GREEN='#E9F3EB'; BLUE='#EAF0F5'; GRAY='#F4F4F4'
def canvas(h=1000):
    im=Image.new('RGB',(1600,h),'white');return im,ImageDraw.Draw(im)
def font(n=34,b=False):return ImageFont.truetype(BOLD if b else FONT,n)
def txt(d,xy,s,n=34,b=False,anchor='mm',fill=INK):d.multiline_text(xy,s,font=font(n,b),anchor=anchor,align='center',spacing=12,fill=fill)
def box(d,rect,title,body='',fill=BLUE):
    x,y,w,h=rect; d.rounded_rectangle((x,y,x+w,y+h),radius=15,fill=fill,outline=LINE,width=3)
    if body:
        txt(d,(x+w/2,y+40),title,34,True);txt(d,(x+w/2,y+h/2+20),body,30)
    else:txt(d,(x+w/2,y+h/2),title,34,True)
def arrow(d,a,b,label='',n=28):
    d.line([a,b],fill=LINE,width=4); angle=math.atan2(b[1]-a[1],b[0]-a[0]);size=17
    pts=[b,(b[0]-size*math.cos(angle-.45),b[1]-size*math.sin(angle-.45)),(b[0]-size*math.cos(angle+.45),b[1]-size*math.sin(angle+.45))]
    d.polygon(pts,fill=LINE)
    if label:
        x,y=(a[0]+b[0])/2,(a[1]+b[1])/2-20
        bounds=d.textbbox((x,y),label,font=font(n),anchor='mm');d.rectangle((bounds[0]-8,bounds[1]-5,bounds[2]+8,bounds[3]+5),fill='white');txt(d,(x,y),label,n)
def save(im,name):im.save(OUT/name,dpi=(220,220))

im,d=canvas(820)
for y,title,body in [(45,'ADMIN','Dossiers et affectations\nComptes et documents\nDécisions de clôture'),(300,'ENCADREUR','Dossiers affectés\nExamen des rapports\nÉvaluations et suivi'),(555,'STAGIAIRE','Dossier personnel\nRédaction des rapports\nSoumission et correction')]:
    box(d,(50,y,380,200),title,fill=GREEN)
    box(d,(610,y,920,200),body,fill=BLUE);arrow(d,(430,y+100),(610,y+100))
save(im,'acteurs.png')

im,d=canvas(930)
box(d,(180,30,1240,175),'Navigateur','Pages React · formulaires · History API',GREEN)
arrow(d,(800,205),(800,325),'HTTP /api · JSON, multipart et contenus binaires')
box(d,(180,325,1240,230),'Application Spring Boot','Filtre de session → contrôleurs → services\nPolitique d’accès et règles métier')
arrow(d,(530,555),(400,695),'JPA / JDBC')
arrow(d,(1070,555),(1200,695),'Fichiers')
box(d,(100,695,600,180),'PostgreSQL','Données et métadonnées\nContraintes · triggers · vues',GRAY)
box(d,(900,695,600,180),'Stockage documentaire','Octets PDF, JPEG, PNG\nRépertoire configuré',GRAY)
save(im,'architecture.png')

im,d=canvas(1270)
nodes={
 'stg':(50,40,450,140,'stagiaires'),
 'ac':(50,290,450,140,'stagiaire_academique'),
 'dept':(1100,40,450,140,'departements'),
 'str':(1100,290,450,140,'structures_accueil'),
 'stage':(1100,590,450,140,'stages'),
 'part':(575,590,450,140,'stage_participant'),
 'ment':(50,590,450,140,'encadreurs'),
 'rep':(20,1030,365,140,'rapports_activite'),
 'eval':(420,1030,365,140,'evaluations_\nfinales'),
 'evt':(820,1030,365,140,'stage_participant_\nevenements'),
 'sou':(1220,1030,365,140,'soutenances')}
for k,(x,y,w,h,label) in nodes.items():box(d,(x,y,w,h),label,fill=GREEN if k=='part' else BLUE)
arrow(d,(275,290),(275,180),'stagiaire_id')
arrow(d,(650,590),(350,430),'profil académique')
arrow(d,(1025,660),(1100,660))
arrow(d,(1325,590),(1325,430),'structure')
arrow(d,(1325,290),(1325,180),'département')
arrow(d,(575,660),(500,660))
for a,b in [((202,1030),(650,730)),((602,1030),(750,730)),((1002,1030),(850,730)),((1402,1030),(950,730))]:arrow(d,a,b)
txt(d,(800,1225),'Flèche : table porteuse de la clé étrangère → table référencée',28)
save(im,'donnees.png')

im,d=canvas(1100)
box(d,(420,20,760,130),'Clients API React',fill=GREEN)
arrow(d,(800,150),(800,230),'Requête et réponse DTO')
box(d,(420,230,760,150),'Contrôleurs de gestion','Routes HTTP et délégation')
arrow(d,(800,380),(800,470))
box(d,(420,470,760,190),'Services métier','Orchestration des règles\nTransactions et transformations DTO')
box(d,(20,470,330,190),'Accès','DossierAccess\nService',GRAY)
box(d,(1250,470,330,190),'Règles','ParticipationRules\nService',GRAY)
arrow(d,(420,560),(350,560));arrow(d,(1180,560),(1250,560))
arrow(d,(800,660),(800,760))
box(d,(420,760,760,130),'Repositories JPA et entités')
arrow(d,(800,890),(800,960))
box(d,(420,960,760,110),'PostgreSQL',fill=GRAY)
save(im,'composants.png')

im,d=canvas(1160)
box(d,(170,25,1260,135),'Demande ADMIN','Accès · verrou · participation modifiable · transition autorisée',GREEN)
arrow(d,(800,160),(800,235))
box(d,(170,235,1260,100),'Modalité transmise à l’API')
for x,title,body in [(30,'Ordinaire','Pièces obligatoires\nET rapports achevés validés'),(560,'Dérogation pièces','Manquants consignés\nRapports toujours exigés'),(1090,'Forcée','Pièces et rapports\nmanquants consignés')]:
    arrow(d,(800,335),(x+240,445))
    box(d,(x,445,480,225),title,body)
    arrow(d,(x+240,670),(x+240,795))
box(d,(170,795,1260,155),'Écriture transactionnelle','CLOTUREE · date de clôture · événement CLOTURE · historique')
# Branch arrows converge at the shared operation.
d.line([(270,795),(270,775),(800,775),(800,795)],fill=LINE,width=4)
d.line([(1330,795),(1330,775),(800,775)],fill=LINE,width=4)
arrow(d,(800,950),(800,1020))
box(d,(170,1020,1260,110),'Expiration des alertes liées à la participation',fill=GRAY)
save(im,'cloture.png')

im,d=canvas(1110)
xs=[180,590,1010,1430]
for x,t in zip(xs,['Acteur','Interface React','API et règles','PostgreSQL']):
    box(d,(x-155,20,310,100),t,fill=GREEN if x==180 else BLUE)
    d.line((x,120,x,1045),fill='#A4B2BA',width=3)
for y,a,b,t in [(205,0,1,'Rédiger le brouillon'),(300,1,2,'POST rapport'),(390,2,3,'Créer BROUILLON'),(480,0,1,'Soumettre'),(570,1,2,'PATCH statut SOUMIS'),(660,2,3,'État et date de soumission')]:arrow(d,(xs[a],y),(xs[b],y),t,27)
d.rectangle((20,735,1580,825),fill=GRAY)
txt(d,(800,780),'Puis l’encadreur affecté, distinct du propriétaire, examine le rapport',31,True)
arrow(d,(590,900),(1010,900),'PATCH VALIDE ou A_CORRIGER',26)
arrow(d,(1010,1000),(1430,1000),'État, dates et historique',27)
save(im,'rapports.png')
print('Six figures originales produites')
