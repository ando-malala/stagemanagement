import { runApiChecks } from './apiReadiness.js';

try {
  const result = await runApiChecks({ baseUrl: process.env.MEF_API_URL || 'http://localhost:8080', token: process.env.MEF_TEST_TOKEN });
  for (const check of result.checks) console.log(`${check.ok ? 'OK' : 'ÉCHEC'} GET ${check.path} : ${check.status ?? 'sans réponse'}${check.reason ? ` — ${check.reason}` : ''}${check.items != null ? ` (${check.items} éléments)` : ''}`);
  if (result.scope === 'public') console.log('Contrôle public uniquement. Un jeton de session de test MEF_TEST_TOKEN est nécessaire aux lectures authentifiées.');
  console.log('Aucun parcours d’écriture ni contrôle visuel exécuté.');
  process.exitCode = result.ok ? (result.scope === 'authenticated' ? 0 : 2) : 1;
} catch {
  console.error('Configuration du contrôle API invalide. Vérifiez MEF_API_URL (origine HTTP(S), sans identifiants).');
  process.exitCode = 1;
}
