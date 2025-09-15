document.addEventListener('DOMContentLoaded', () => {
  const tabs = document.querySelectorAll('.tabstrip .tab');
  tabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
      const href = tab.getAttribute('href');
      if (!href) return;
      e.preventDefault();
      if (document.startViewTransition) {
        document.startViewTransition(() => { window.location.href = href; });
      } else {
        window.location.href = href;
      }
    });
  });
}); 