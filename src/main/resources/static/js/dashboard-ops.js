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

	// Parse permissions JSON from model if present
	let permsRaw = null;
	try { permsRaw = document.querySelector('meta[name="permissions-json"]')?.getAttribute('content'); } catch(_){}
	if (!permsRaw) {
		// try inline via Thymeleaf variable rendered as data attribute on body (fallback, if added later)
		permsRaw = document.body?.dataset?.permissionsJson || null;
	}
	let perms = {};
	try { if (permsRaw) perms = JSON.parse(permsRaw); } catch(_) {}

	function hideCardIfDenied(selector, key, required) {
		const el = document.querySelector(selector);
		if (!el) return;
		const val = (perms?.[key] || '').toLowerCase();
		const ok = required === 'read' ? (val === 'read' || val === 'read_write' || val === 'full')
			: required === 'write' ? (val === 'read_write' || val === 'full')
			: (val === 'full');
		if (!ok) el.closest('.col-md-4, .col-lg-3')?.remove();
	}
	// Map cards to permission keys
	hideCardIfDenied('a[th\\:href="@{/inventory}"]', 'inventory', 'read');
	hideCardIfDenied('a[th\\:href="@{/shipments}"]', 'shipments', 'read');
	// Master data and ops depend on inventory permission
	hideCardIfDenied('a[th\\:href="@{/equipment}"]', 'inventory', 'read');
	hideCardIfDenied('a[th\\:href="@{/locations}"]', 'inventory', 'read');
	hideCardIfDenied('a[th\\:href="@{/categories}"]', 'inventory', 'read');
	hideCardIfDenied('a[th\\:href="@{/suppliers}"]', 'inventory', 'read');
}); 