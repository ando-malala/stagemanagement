import ResourceDetail from '../../components/common/ResourceDetail.jsx';
import { getEncadreur, updateEncadreur } from '../../api/encadreurApi.js';
import BusinessEditor from '../../components/common/BusinessEditor.jsx';
import EncadreurCharge from './EncadreurCharge.jsx';
import EncadreurStatut from './EncadreurStatut.jsx';
import CompteEncadreur from './CompteEncadreur.jsx';
import { useAuth } from '../../hooks/useAuth.js';
import { can } from '../../utils/permissions.js';

const fields = [["nom", "Nom"], ["prenoms", "Prénoms"], ["fonction", "Fonction"], ["departement", "Département"], ["localite", "Localité"], ["porte", "Porte"], ["contact", "Contact"], ["courriel", "Courriel"], ["capaciteMaxStagiaires", "Capacité maximale déclarée"], ["identifiantUtilisateur", "Compte associé"], ["statut", "Statut"]];
const links = [];

export default function EncadreurDetailPage({ params, navigate }) {
  const { utilisateur } = useAuth();
  return <ResourceDetail id={params.id} title="Fiche encadreur" fetcher={getEncadreur} fields={fields} links={links} backTo="/encadreurs" navigate={navigate}>
    {(data, reload) => <><BusinessEditor kind="encadreur" initial={data} save={(payload) => updateEncadreur(params.id, payload)} onSaved={reload} />
      {can(utilisateur, 'gererUtilisateurs') && <CompteEncadreur key={params.id} id={params.id} />}
      <EncadreurStatut key={`${data.id}/${data.statut}`} encadreur={data} onSaved={reload} />
      <EncadreurCharge key={`${data.id}/${data.capaciteMaxStagiaires}`} id={data.id} /></>}
  </ResourceDetail>;
}
