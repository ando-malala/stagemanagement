import { useCallback } from 'react';
import { getDossier, downloadBilan } from '../../api/syntheseApi.js';
import { downloadFichier } from '../../api/fichierApi.js';
import { useFetch } from '../../hooks/useFetch.js';
import Card from '../../components/common/Card.jsx';
import AppLink from '../../components/common/AppLink.jsx';
import Button from '../../components/common/Button.jsx';
import DownloadButton from '../../components/common/DownloadButton.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import Loader from '../../components/common/Loader.jsx';

export default function DossierSynthese({ id, navigate }) {
  const fetcher = useCallback(options => getDossier(id, options), [id]);
  const state = useFetch(fetcher);
  return <Card>
    <div className="section-heading"><h3>Dossier consolidé</h3><Button variant="secondary" disabled={state.loading} onClick={() => void state.reload()}>Actualiser la synthèse</Button></div>
    {state.loading ? <Loader /> : state.error ? <ErrorMessage error={state.error} /> : <>
      <Button variant="secondary" onClick={() => window.print()}>Imprimer la synthèse</Button>
      <DossierContent dossier={state.data} navigate={navigate} />
    </>}
  </Card>;
}

function Files({ files }) {
  return files.length ? <ul>{files.map(f => <li key={f.id}>{f.typePieceJointeLibelle} : {f.nomOriginal}{' '}
    <DownloadButton download={() => downloadFichier(f.id)}>Télécharger</DownloadButton></li>)}</ul> : <p>Aucune pièce déposée.</p>;
}
export function DossierContent({ dossier, navigate }) {
  return <div className="dossier-print page-stack">
    <h3>{dossier.stagiaire.nom} {dossier.stagiaire.prenoms}</h3>
    <p>{dossier.stagiaire.email} · {dossier.stagiaire.contact} · {dossier.stagiaire.statut}</p>
    <h4>Parcours académique</h4>
    <ul>{dossier.academiques.map(a => <li key={a.id}>{a.niveauEtude} — {a.filiere} — {a.etablissement} ({a.anneeEtudes})</li>)}</ul>
    <Files files={dossier.fichiers} />
    {!dossier.participations.length && <p>Aucune participation accessible.</p>}
    {dossier.participations.map(s => <article key={s.participation.id} className="dossier-participation">
      <h4>{s.participation.stage} · {s.participation.statut}</h4>
      <p>{s.participation.dateDebutStage} — {s.participation.dateFinStage} · Encadreur : {s.participation.encadreurNomComplet}</p>
      <div className="actions"><AppLink to={`/participations/${s.participation.id}`} navigate={navigate}>Ouvrir la participation</AppLink>
        <DownloadButton download={() => downloadBilan(s.participation.id)}>Bilan PDF</DownloadButton></div>
      <h4>Documents et complétude</h4><ul>{s.documentsObligatoires.map(d => <li key={d.id}>{d.libelle} : {d.present ? 'présent' : 'manquant'}</li>)}</ul>
      <Files files={s.fichiers} />
      <h4>Périodes de rapports</h4><ul>{s.periodes.map(p => <li key={p.debut}>{p.debut} — {p.fin} : {p.statutRapport ?? 'Non déposé'}{p.manquante ? ' (manquant)' : ''}</li>)}</ul>
      {s.rapports.map(({ rapport: r, observations }) => <section key={r.id}>
        <h4>Rapport du {r.debutSemaine} · {r.statut}</h4><p>{r.activite}</p><p>{r.difficultes}</p><p>{r.solutions}</p>
        <ul>{observations.map(o => <li key={o.id}>{o.typeObservation} : {o.commentaire}</li>)}</ul>
        <AppLink to={`/rapports/${r.id}`} navigate={navigate}>Consulter le rapport</AppLink>
      </section>)}
      {s.evaluation && <section><h4>Évaluation {s.evaluation.estFinalisee ? 'finalisée' : 'en cours'}</h4>
        <p>{s.evaluation.appreciationGlobale}</p><p>{s.evaluation.observationsFinales}</p>
        <ul>{s.notes.map(n => <li key={n.id}>{n.critereLibelle} : {n.note}/5 (coefficient {n.coefficient})</li>)}</ul>
        <AppLink to={`/evaluations/${s.evaluation.id}`} navigate={navigate}>Évaluation et notes</AppLink></section>}
      {s.soutenances.map(d => <p key={d.id}>Soutenance : {d.dateSoutenance} · {d.lieu}</p>)}
      <h4>Historique et événements</h4><ul>{s.historique.map(h => <li key={h.id}>{h.dateModification} : {h.statut} · {h.motif}</li>)}</ul>
      <ul>{s.evenements.map(e => <li key={e.id}>{e.dateEvenement} : {e.libelle} · {e.motif}</li>)}</ul>
    </article>)}
    <p className="small">Cette synthèse contient les informations accessibles au compte connecté.</p>
  </div>;
}
