// Shared toast utility for all LWMS pages
(function(){
  class ToastManager {
    constructor() {
      this.container = this.ensureContainer();
    }

    ensureContainer() {
      let container = document.getElementById('toast-container');
      if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
      }
      return container;
    }

    show(message, variant = 'success', timeoutMs = 3000) {
      const toast = document.createElement('div');
      toast.className = `toast toast-${variant}`;
      toast.setAttribute('role', 'status');
      toast.setAttribute('aria-live', 'polite');
      toast.innerHTML = `
        <span class="msg"></span>
        <button class="close-btn" aria-label="Close" title="Close">&times;</button>
      `;
      toast.querySelector('.msg').textContent = message ?? '';
      toast.querySelector('.close-btn').addEventListener('click', () => toast.remove());
      this.container.appendChild(toast);
      if (timeoutMs > 0) setTimeout(() => toast.remove(), timeoutMs);
    }

    success(message) { this.show(message, 'success'); }
    error(message) { this.show(message, 'error', 5000); }
  }

  window.toastManager = new ToastManager();
})();
