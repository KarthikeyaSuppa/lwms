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

	const explicit = document.querySelector('#profileModal span[th\\:text*="user.role.roleName"]');
	let role = explicit?.textContent?.trim() || '';
	if (!role) {
		const candidates = [...document.querySelectorAll('#profileModal span')].map(s => s.textContent.trim());
		role = candidates.find(t => /(Admin|Manager|Supervisor|Inventory Controller|Operator|Viewer)/i.test(t)) || '';
	}
	if (!/^(Admin|Manager)$/i.test(role)) {
		const settingsLink = document.querySelector('a.nav-link[title="Settings"], a.nav-link[href$="/settings"]');
		if (settingsLink) settingsLink.closest('li')?.remove();
	}

	let permsRaw = null;
	try { permsRaw = document.querySelector('meta[name="permissions-json"]')?.getAttribute('content'); } catch(_){}
	if (!permsRaw) permsRaw = document.body?.dataset?.permissionsJson || null;
	let perms = {}; try { if (permsRaw) perms = JSON.parse(permsRaw); } catch(_){}

	function hideCardIfDenied(selector, key, required) {
		const el = document.querySelector(selector);
		if (!el) return;
		const val = (perms?.[key] || '').toLowerCase();
		const ok = required === 'read' ? (val === 'read' || val === 'read_write' || val === 'full')
			: required === 'write' ? (val === 'read_write' || val === 'full')
			: (val === 'full');
		if (!ok) el.closest('.col-md-4, .col-lg-3')?.remove();
	}
	// Maintenance: require inventory read for maintenance schedule (or replace with its own key later)
	hideCardIfDenied('a[th\\:href="@{/maintenance-schedule}"]', 'inventory', 'read');
	// Inventory Movements depends on inventory
	hideCardIfDenied('a[th\\:href="@{/inventory-movements}"]', 'inventory', 'read');
	// Reports depends on reports
	hideCardIfDenied('a[th\\:href="@{/reports}"]', 'reports', 'read');
}); 