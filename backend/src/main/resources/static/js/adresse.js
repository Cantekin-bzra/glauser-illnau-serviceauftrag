// Toggle Objektadresse und Verrechnungsadresse
document.addEventListener('DOMContentLoaded', function () {

  // Objektadresse: Felder sperren wenn «gleich wie Kunde»
  const objektCb = document.getElementById('objektGleichKunde');
  const objektFields = document.getElementById('objekt-fields');
  if (objektCb && objektFields) {
    function toggleObjekt() {
      objektFields.classList.toggle('form-disabled', objektCb.checked);
      objektFields.querySelectorAll('input').forEach(i => i.disabled = objektCb.checked);
    }
    objektCb.addEventListener('change', toggleObjekt);
    toggleObjekt();
  }

  // Verrechnungsadresse: Felder zeigen wenn «abweichend»
  const verrCb = document.getElementById('verrechnungAbweichend');
  const verrFields = document.getElementById('verrechnung-fields');
  if (verrCb && verrFields) {
    function toggleVerrechnung() {
      verrFields.classList.toggle('form-disabled', !verrCb.checked);
      verrFields.querySelectorAll('input').forEach(i => i.disabled = !verrCb.checked);
    }
    verrCb.addEventListener('change', toggleVerrechnung);
    toggleVerrechnung();
  }
});
