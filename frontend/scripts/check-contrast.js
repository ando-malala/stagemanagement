import { readFileSync } from 'node:fs';

// Check actual opaque CSS tokens; gradients are checked at both endpoints.
// This checks palette combinations, not computed browser styles or WCAG conformance.
const css = readFileSync(new URL('../src/styles/global.css', import.meta.url), 'utf8');
const tokens = Object.fromEntries([...css.matchAll(/--([\w-]+):\s*(#[\da-f]{6});/gi)].map(([, name, value]) => [name, value]));
function luminance(hex) {
  const channels = hex.slice(1).match(/../g).map(value => {
    const channel = parseInt(value, 16) / 255;
    return channel <= .04045 ? channel / 12.92 : ((channel + .055) / 1.055) ** 2.4;
  });
  return channels[0] * .2126 + channels[1] * .7152 + channels[2] * .0722;
}
const checks = [];
function check(foreground, backgrounds, minimum = 4.5) {
  for (const background of backgrounds) {
    if (!tokens[foreground] || !tokens[background]) throw new Error(`Token manquant : ${foreground} / ${background}`);
    const values = [luminance(tokens[foreground]), luminance(tokens[background])].sort((a, b) => b - a);
    const ratio = (values[0] + .05) / (values[1] + .05);
    checks.push({ foreground, background, ratio: ratio.toFixed(2), minimum, pass: ratio >= minimum });
  }
}
check('text', ['surface', 'surface-subtle', 'bg', 'primary-soft']);
check('muted', ['surface', 'surface-subtle', 'surface-hover', 'bg', 'primary-soft', 'accent-soft']);
check('primary', ['surface', 'primary-soft', 'surface-hover']);
check('on-brand', ['primary', 'primary-dark', 'danger', 'danger-dark', 'sidebar', 'sidebar-deep', 'sidebar-active']);
check('on-brand-muted', ['primary', 'primary-dark', 'sidebar', 'sidebar-deep']);
check('nav-text', ['sidebar', 'sidebar-deep', 'sidebar-active']);
check('nav-caption', ['sidebar', 'sidebar-deep']);
check('nav-accent', ['primary', 'primary-dark', 'sidebar', 'sidebar-deep', 'sidebar-active']);
check('success', ['surface', 'success-soft', 'accent-soft']);
check('warning', ['surface', 'warning-soft']);
check('danger', ['surface', 'danger-soft']);
check('correction', ['correction-soft']);
check('neutral', ['neutral-soft']);
check('accent', ['accent-soft']);
check('violet', ['violet-soft', 'surface']);
check('control-border', ['surface', 'surface-subtle'], 3);
check('focus', ['surface', 'surface-subtle', 'bg'], 3);

const failures = checks.filter(result => !result.pass);
if (failures.length) {
  console.table(failures);
  process.exitCode = 1;
} else {
  const textChecks = checks.filter(result => result.minimum === 4.5);
  const boundaryChecks = checks.filter(result => result.minimum === 3);
  console.log(`${checks.length} combinaisons conformes aux seuils contrôlés.`);
  console.log(`Texte : minimum ${Math.min(...textChecks.map(result => Number(result.ratio))).toFixed(2)}:1 (seuil 4.5:1).`);
  console.log(`Champs et focus : minimum ${Math.min(...boundaryChecks.map(result => Number(result.ratio))).toFixed(2)}:1 (seuil 3:1).`);
}
