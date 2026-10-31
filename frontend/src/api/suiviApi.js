import { apiRequest } from './apiClient.js';
import { requireList } from '../utils/businessForms.js';

const list = async (path, options) => requireList(await apiRequest(path, options));
export const listStatuts = (type, options) => list(`/api/referentiels/suivi/statuts/${encodeURIComponent(type)}`, options);
export const listTypesDocuments = (options) => list('/api/referentiels/suivi/types-documents', options);
export const listDocumentsObligatoires = (id, options) => list(`/api/stage-participants/${id}/documents-obligatoires`, options);
export const listHistoriqueStatuts = (id, options) => list(`/api/stage-participants/${id}/historique-statuts`, options);
export const listEvenements = (id, options) => list(`/api/stage-participants/${id}/evenements`, options);
export const listPeriodesRapports = (id, options) => list(`/api/stage-participants/${id}/periodes-rapports`, options);
export const listActionsParticipation = (id, options) => list(`/api/stage-participants/${id}/actions`, options);
export const listTransitions = (type, source, options) => list(`/api/referentiels/suivi/transitions/${encodeURIComponent(type)}/${encodeURIComponent(source)}`, options);
export const getChargeEncadreur = (id, options) => apiRequest(`/api/encadreurs/${id}/charge`, options);
