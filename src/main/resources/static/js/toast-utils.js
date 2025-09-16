// Shared toast utility for all LWMS pages
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
    toast.className = 	oast ;
    toast.innerHTML = 
      <span class="msg"></span>
      <button class="close-btn" aria-label="Close"></button>
    ;
    
    toast.querySelector('.close-btn').addEventListener('click', () => toast.remove());
    this.container.appendChild(toast);
    
    setTimeout(() => toast.remove(), timeoutMs);
  }

  success(message) {
    this.show(message, 'success');
  }

  error(message) {
    this.show(message, 'error');
  }
}

// Global toast manager instance
window.toastManager = new ToastManager();
