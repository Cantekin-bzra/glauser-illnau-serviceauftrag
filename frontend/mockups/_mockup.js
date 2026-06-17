// Toggle Objektadresse
function initObjektAdresse() {
  const cb = document.getElementById('objekt-gleich');
  const fields = document.getElementById('objekt-fields');
  if (!cb || !fields) return;
  const toggle = () => fields.classList.toggle('form-disabled', cb.checked);
  cb.addEventListener('change', toggle);
  toggle();
}

// Toggle Verrechnungsadresse
function initVerrechnungsAdresse() {
  const cb = document.getElementById('verrechnung-abweichend');
  const fields = document.getElementById('verrechnung-fields');
  if (!cb || !fields) return;
  const toggle = () => fields.classList.toggle('form-disabled', !cb.checked);
  cb.addEventListener('change', toggle);
  toggle();
}

// Einfache Formularvalidierung mit visuellem Feedback
function initValidierung(formId) {
  const form = document.getElementById(formId);
  if (!form) return;
  form.addEventListener('submit', (e) => {
    e.preventDefault();
    let valid = true;
    form.querySelectorAll('[required]').forEach(el => {
      if (!el.value.trim()) {
        el.style.borderColor = '#ef4444';
        el.style.boxShadow = '0 0 0 3px rgba(239,68,68,.15)';
        valid = false;
      } else {
        el.style.borderColor = '#22c55e';
        el.style.boxShadow = '0 0 0 3px rgba(34,197,94,.1)';
      }
    });
    if (valid) {
      const msg = document.getElementById('success-msg');
      if (msg) { msg.style.display = 'flex'; msg.scrollIntoView({ behavior: 'smooth' }); }
    }
  });
  form.querySelectorAll('.form-control').forEach(el => {
    el.addEventListener('input', () => {
      if (el.value.trim()) {
        el.style.borderColor = '#22c55e';
        el.style.boxShadow = '0 0 0 3px rgba(34,197,94,.1)';
      }
    });
  });
}

document.addEventListener('DOMContentLoaded', () => {
  initObjektAdresse();
  initVerrechnungsAdresse();
  initValidierung('auftrag-form');
  initValidierung('disposition-form');
  initValidierung('rapport-form');
});
