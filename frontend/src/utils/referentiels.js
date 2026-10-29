const simple = [{ key: 'libelle', label: 'Libellé' }, { key: 'actif', label: 'Actif' }];
const entry = (id, title, group, segment, kind, columns = simple) => ({ id, title, group, kind, columns, endpoint: `/api/referentiels/${group}/${segment}` });
export const referentiels = [
  entry('departements', 'Départements', 'organisation', 'departements', 'departement', [{ key: 'libelle', label: 'Libellé' }, { key: 'statut', label: 'Statut' }]),
  entry('structures', 'Structures d’accueil', 'organisation', 'structures-accueil', 'structure', [{ key: 'libelle', label: 'Libellé' }, { key: 'departement', label: 'Département' }, { key: 'localite', label: 'Localité' }, { key: 'actif', label: 'Actif' }]),
  entry('cadres', 'Cadres de stage', 'organisation', 'cadres-stage', 'cadre', [{ key: 'themeStage', label: 'Thème' }, { key: 'activiteConfieStagiaire', label: 'Activités confiées' }, { key: 'objectifStage', label: 'Objectif' }, { key: 'actif', label: 'Actif' }]),
  entry('etablissements', 'Établissements', 'academiques', 'etablissements', 'reference'),
  entry('filieres', 'Filières', 'academiques', 'filieres', 'reference'),
  entry('niveaux', 'Niveaux d’étude', 'academiques', 'niveaux-etude', 'reference'),
  entry('mois', 'Mois de stage', 'academiques', 'mois-stage', 'reference')
];
