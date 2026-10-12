import { useCallback, useMemo, useState } from 'react';
import EntityPage from '../../components/common/EntityPage.jsx';
import { createReferentiel, listReferentiel } from '../../api/referentielApi.js';

const simpleFields = [
  { name: 'libelle', label: 'Libellé', required: true },
  {
    name: 'actif',
    label: 'Actif',
    type: 'select',
    valueType: 'boolean',
    defaultValue: 'true',
    options: [
      { value: 'true', label: 'Oui' },
      { value: 'false', label: 'Non' }
    ]
  }
];

const simpleColumns = [
  { key: 'id', label: 'ID' },
  { key: 'libelle', label: 'Libellé' },
  { key: 'actif', label: 'Actif', render: (row) => row.actif ? 'Oui' : 'Non' }
];

const REFERENTIELS = [
  {
    id: 'departements',
    title: 'Départements',
    endpoint: '/api/referentiels/organisation/departements',
    fields: [
      { name: 'libelle', label: 'Libellé', required: true },
      { name: 'statutCode', label: 'Statut', defaultValue: 'ACTIF', required: true }
    ],
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'libelle', label: 'Libellé' },
      { key: 'statut', label: 'Statut' }
    ]
  },
  {
    id: 'structures',
    title: 'Structures d’accueil',
    endpoint: '/api/referentiels/organisation/structures-accueil',
    fields: [
      { name: 'departementId', label: 'ID département', type: 'number', required: true },
      { name: 'libelle', label: 'Libellé', required: true },
      { name: 'localite', label: 'Localité' },
      { name: 'porte', label: 'Porte' },
      { name: 'contact', label: 'Contact' },
      { name: 'courriel', label: 'Courriel', type: 'email' },
      ...simpleFields.slice(1)
    ],
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'libelle', label: 'Libellé' },
      { key: 'departement', label: 'Département' },
      { key: 'localite', label: 'Localité' },
      { key: 'actif', label: 'Actif', render: (row) => row.actif ? 'Oui' : 'Non' }
    ]
  },
  {
    id: 'cadres',
    title: 'Cadres de stage',
    endpoint: '/api/referentiels/organisation/cadres-stage',
    fields: [
      { name: 'themeStage', label: 'Thème du stage', required: true },
      { name: 'activiteConfieStagiaire', label: 'Activités confiées', type: 'textarea' },
      { name: 'objectifStage', label: 'Objectif du stage', type: 'textarea' },
      ...simpleFields.slice(1)
    ],
    columns: [
      { key: 'id', label: 'ID' },
      { key: 'themeStage', label: 'Thème' },
      { key: 'objectifStage', label: 'Objectif' },
      { key: 'actif', label: 'Actif', render: (row) => row.actif ? 'Oui' : 'Non' }
    ]
  },
  { id: 'etablissements', title: 'Établissements', endpoint: '/api/referentiels/academiques/etablissements', fields: simpleFields, columns: simpleColumns },
  { id: 'filieres', title: 'Filières', endpoint: '/api/referentiels/academiques/filieres', fields: simpleFields, columns: simpleColumns },
  { id: 'niveaux', title: 'Niveaux d’étude', endpoint: '/api/referentiels/academiques/niveaux-etude', fields: simpleFields, columns: simpleColumns },
  { id: 'mois', title: 'Mois de stage', endpoint: '/api/referentiels/academiques/mois-stage', fields: simpleFields, columns: simpleColumns }
];

export default function ReferentielsPage() {
  const [activeId, setActiveId] = useState(REFERENTIELS[0].id);
  const active = REFERENTIELS.find((item) => item.id === activeId) ?? REFERENTIELS[0];

  const fetcher = useCallback(() => listReferentiel(active.endpoint), [active.endpoint]);
  const creator = useCallback((payload) => createReferentiel(active.endpoint, payload), [active.endpoint]);
  const emptyForm = useMemo(() => buildInitialForm(active.fields), [active.fields]);

  return (
    <div className="page-stack">
      <div className="segmented" role="tablist" aria-label="Référentiels">
        {REFERENTIELS.map((item) => (
          <button
            type="button"
            key={item.id}
            className={item.id === active.id ? 'active' : ''}
            onClick={() => setActiveId(item.id)}
          >
            {item.title}
          </button>
        ))}
      </div>

      <EntityPage
        title={active.title}
        description="Gestion des données de référence utilisées par les modules métier."
        columns={active.columns}
        fields={active.fields}
        fetcher={fetcher}
        creator={creator}
        canCreate
        createLabel="Créer"
        emptyForm={emptyForm}
      />
    </div>
  );
}

function buildInitialForm(fields) {
  return fields.reduce((acc, field) => {
    acc[field.name] = field.defaultValue ?? '';
    return acc;
  }, {});
}
