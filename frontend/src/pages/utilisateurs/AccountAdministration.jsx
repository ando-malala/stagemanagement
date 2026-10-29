import { useCallback, useState } from 'react';
import { listRoles, listUtilisateurRoles, addUtilisateurRole, removeUtilisateurRole,
  listUtilisateurDepartements, addUtilisateurDepartement, removeUtilisateurDepartement } from '../../api/utilisateurApi.js';
import { listReferentiel } from '../../api/referentielApi.js';
import { useAuth } from '../../hooks/useAuth.js';
import { useFetch } from '../../hooks/useFetch.js';
import { can } from '../../utils/permissions.js';
import { requireList, toOptions } from '../../utils/businessForms.js';
import { activeRoleOptions, canRemoveRole, requireRoleCodes } from '../../utils/utilisateurForms.js';
import Button from '../../components/common/Button.jsx';
import Input from '../../components/common/Input.jsx';
import Table from '../../components/common/Table.jsx';
import ErrorMessage from '../../components/common/ErrorMessage.jsx';
import ConfirmDialog from '../../components/common/ConfirmDialog.jsx';
import ResetPassword from './ResetPassword.jsx';

const roleColumns = [{ key: 'code', label: 'Rôle attribué' }];
const departmentColumns = [{ key: 'libelle', label: 'Département' }, { key: 'statut', label: 'Statut' }];

export default function AccountAdministration({ account }) {
  const { utilisateur } = useAuth();
  if (!can(utilisateur, 'gererUtilisateurs')) return null;
  return <div className="page-stack">
    <MembershipPanel kind="roles" account={account} />
    <MembershipPanel kind="departements" account={account} />
    <ResetPassword account={account} />
    <p className="notice">Les changements de statut et l’historique du compte ne sont pas disponibles dans cette interface.</p>
  </div>;
}

function MembershipPanel({ kind, account }) {
  const { utilisateur, retrySession } = useAuth();
  const roles = kind === 'roles';
  const title = roles ? 'Rôles du compte' : 'Départements rattachés';
  const [selected, setSelected] = useState('');
  const [confirmation, setConfirmation] = useState(null);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const fetcher = useCallback(async (options) => {
    const [assigned, catalog] = await Promise.all(roles
      ? [listUtilisateurRoles(account.id, options), listRoles(options)]
      : [listUtilisateurDepartements(account.id, options), listReferentiel('/api/referentiels/organisation/departements', options)]);
    return { assigned: roles ? requireRoleCodes(assigned) : requireList(assigned), catalog: requireList(catalog) };
  }, [roles, account.id]);
  const { data, loading, error: loadError, reload } = useFetch(fetcher);
  const assigned = data?.assigned ?? [];
  const rows = roles ? assigned.map((code) => ({ id: code, code })) : assigned;
  const options = (roles ? activeRoleOptions(data?.catalog ?? []) : toOptions(data?.catalog ?? [], ['libelle']))
    .filter((option) => !assigned.some((item) => roles ? item === option.value : item.id === option.value));
  const self = Number(account.id) === Number(utilisateur.utilisateurId);
  return <section className="page-stack">
    <div className="section-heading"><h3>{title}</h3><Button variant="secondary" onClick={reload} disabled={loading || Boolean(confirmation)}>Actualiser</Button></div>
    {message && <p className="alert alert-success" role="status">{message}</p>}
    <ErrorMessage error={loadError || error} />
    {!loading && !loadError && <form className="entity-form" onSubmit={(event) => {
      event.preventDefault(); setError(''); setMessage('');
      const choice = options.find((option) => String(option.value) === selected);
      if (!choice) { setError('Sélectionnez une valeur disponible.'); return; }
      setConfirmation({ operation: 'add', value: choice.value, label: choice.label });
    }}>
      <Input label={roles ? 'Rôle actif à ajouter' : 'Département à rattacher'} name={roles ? 'roleCode' : 'departementId'} type="select" required
        value={selected} onChange={(event) => setSelected(event.target.value)} options={options} disabled={!options.length} />
      <Button type="submit" disabled={!options.length}>Ajouter</Button>
    </form>}
    <Table columns={roles ? roleColumns : departmentColumns} data={rows} loading={loading} error={loadError} caption={title}
      renderActions={(row) => <Button variant="secondary" disabled={roles && !canRemoveRole(assigned, row.code)} onClick={() => {
        setError(''); setMessage(''); setConfirmation({ operation: 'remove', value: roles ? row.code : row.id, label: roles ? row.code : row.libelle });
      }}>Retirer</Button>} />
    {roles && !loading && !loadError && assigned.length === 1 && <p className="small">Le compte doit conserver au moins un rôle.</p>}
    <ConfirmDialog open={Boolean(confirmation)} title={confirmation?.operation === 'remove' ? 'Retirer une attribution' : 'Ajouter une attribution'}
      onClose={() => setConfirmation(null)} onConfirm={async () => {
        const { operation, value } = confirmation;
        if (roles) {
          if (operation === 'remove') await removeUtilisateurRole(account.id, value);
          else await addUtilisateurRole(account.id, { roleCode: value });
        } else {
          if (operation === 'remove') await removeUtilisateurDepartement(account.id, value);
          else await addUtilisateurDepartement(account.id, { departementId: value });
        }
        setConfirmation(null); setSelected(''); setMessage('Attributions mises à jour.');
        // Refresh /me before another admin read: removing one's own ADMIN role
        // can make this route inaccessible immediately.
        if (roles && self) await retrySession();
        else await reload();
      }}>
      <p>{confirmation?.operation === 'remove' ? 'Retirer' : 'Ajouter'} « {confirmation?.label} » pour {account.identifiant} (n° {account.id}) ?</p>
      {self && roles && confirmation?.operation === 'remove' && confirmation?.value === 'ADMIN' && <p>Vous perdrez votre accès aux fonctions d’administration.</p>}
    </ConfirmDialog>
  </section>;
}
