// Clientseitige Formularvalidierung mit Live-Feedback
(function () {
  'use strict';

  function markValid(el) {
    el.classList.remove('is-invalid');
    el.classList.add('is-valid');
    const err = el.parentElement.querySelector('.field-error');
    if (err) err.textContent = '';
  }

  function markInvalid(el, msg) {
    el.classList.add('is-invalid');
    el.classList.remove('is-valid');
    let err = el.parentElement.querySelector('.field-error');
    if (!err) {
      err = document.createElement('span');
      err.className = 'field-error';
      el.parentElement.appendChild(err);
    }
    err.textContent = msg;
  }

  function validateField(el) {
    const val = el.value.trim();

    if (el.hasAttribute('required') && !val) {
      markInvalid(el, 'Dieses Feld ist erforderlich.');
      return false;
    }
    if (el.type === 'email' && val && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val)) {
      markInvalid(el, 'Bitte gültige E-Mail-Adresse eingeben.');
      return false;
    }
    if (el.type === 'tel' && val && val.replace(/\s/g,'').length < 7) {
      markInvalid(el, 'Telefonnummer zu kurz.');
      return false;
    }
    if (el.dataset.min && parseFloat(val) < parseFloat(el.dataset.min)) {
      markInvalid(el, 'Wert muss mindestens ' + el.dataset.min + ' sein.');
      return false;
    }
    markValid(el);
    return true;
  }

  function initForm(form) {
    const fields = form.querySelectorAll('.form-control');

    // Live-Validierung bei Eingabe
    fields.forEach(el => {
      el.addEventListener('blur', () => validateField(el));
      el.addEventListener('input', () => {
        if (el.classList.contains('is-invalid')) validateField(el);
      });
    });

    // Validierung bei Submit
    form.addEventListener('submit', function (e) {
      let valid = true;
      fields.forEach(el => { if (!validateField(el)) valid = false; });
      if (!valid) {
        e.preventDefault();
        const first = form.querySelector('.is-invalid');
        if (first) first.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('form[data-validate]').forEach(initForm);
  });
})();
