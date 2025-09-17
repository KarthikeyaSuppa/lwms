document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("tbody");
  const API_BASE = "/shipment-items/api"; // also /lwms/shipment-items/api
  const SHIP_API = "/shipments/api"; // also /lwms/shipments/api
  const INV_API = "/inventory/api"; // also /lwms/inventory/api

  async function searchShipments(q){
    const url = q ? `${SHIP_API}?q=${encodeURIComponent(q)}` : SHIP_API;
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) return [];
    const data = await res.json();
    return Array.isArray(data) ? data : [];
  }

  async function searchInventory(q){
    const url = q ? `${INV_API}?q=${encodeURIComponent(q)}` : INV_API;
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) return [];
    const data = await res.json();
    return Array.isArray(data) ? data : [];
  }

  function renderSearchableDropdown({ dropdown, items, getLabel, getId, onSelect, includeSearch }){
    if (!dropdown) return;
    dropdown.innerHTML = '';
    let input = null;
    if (includeSearch) {
      const searchWrap = document.createElement('div');
      searchWrap.className = 'dropdown-search';
      input = document.createElement('input');
      input.type = 'text';
      input.placeholder = 'Search...';
      searchWrap.appendChild(input);
      dropdown.appendChild(searchWrap);
    }

    const renderItems = (list) => {
      dropdown.querySelectorAll('.item-dropdown-item').forEach(el => el.remove());
      list.forEach(obj => {
        const el = document.createElement('div');
        el.className = 'item-dropdown-item';
        const label = getLabel(obj) ?? '';
        el.textContent = label;
        el.dataset.id = getId(obj);
        el.dataset.value = label;
        dropdown.appendChild(el);
      });
    };
    renderItems(items);

    dropdown.addEventListener('click', (e) => {
      const item = e.target.closest('.item-dropdown-item');
      if (!item) return;
      e.stopPropagation();
      onSelect({ id: item.dataset.id, label: item.dataset.value });
      dropdown.style.display = 'none';
    });

    if (input) input.addEventListener('click', e => e.stopPropagation());

    return { searchInput: input, rerender: renderItems };
  }

  function attachSearchableDropdown({ select, dropdown, hidden, fetcher, getLabel, getId, onSelected }){
    if (!select || !dropdown) return;
    async function openAndLoad(){
      toggleItemDropdown(dropdown);
      if (!dropdown.dataset.loaded) {
        const list = await fetcher('');
        const hook = renderSearchableDropdown({ dropdown, items: list, getLabel, getId, includeSearch: (select.tagName !== 'INPUT'), onSelect: ({ id, label }) => {
          if (select.tagName === 'INPUT') select.value = label; else select.textContent = label;
          select.dataset.currentValue = label;
          select.dataset.id = id;
          if (hidden) hidden.value = id;
          if (onSelected) onSelected({ id, label });
        }});
        dropdown.dataset.loaded = '1';
        if (hook && hook.searchInput) {
          hook.searchInput.addEventListener('input', async () => {
            const q = hook.searchInput.value.trim();
            const data = await fetcher(q);
            hook.rerender(data);
          });
        }
      }
    }
    const onFocus = async (e) => { e.stopPropagation(); await openAndLoad(); };
    const onClick = async (e) => { e.stopPropagation(); await openAndLoad(); };
    select.addEventListener('focus', onFocus);
    select.addEventListener('click', onClick);
    if (select.tagName === 'INPUT') {
      select.addEventListener('input', async () => {
        if (!dropdown.dataset.loaded) return; // wait until opened once
        const q = select.value.trim();
        const data = await fetcher(q);
        const hook = renderSearchableDropdown({ dropdown, items: data, getLabel, getId, includeSearch: false, onSelect: ({ id, label }) => {
          select.value = label; select.dataset.currentValue = label; select.dataset.id = id; if (hidden) hidden.value = id; if (onSelected) onSelected({ id, label }); dropdown.style.display = 'none';
        }});
        dropdown.style.display = 'block';
      });
    }
  }

  async function loadItemsByShipmentNumber(number){
    if (!number) return;
    const res = await fetch(`${API_BASE}/by-shipment-number/${encodeURIComponent(number)}`, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) { console.error('Failed to load shipment items'); return; }
    const data = await res.json();
    renderRows(Array.isArray(data) ? data : []);
  }

  function fmtCurrency(n){
    const v = typeof n === 'number' ? n : parseFloat(n || '0');
    return "$" + (isNaN(v) ? 0 : v).toLocaleString('en-US',{ minimumFractionDigits:2, maximumFractionDigits:2 });
  }

  function renderRows(list){
    tbody.innerHTML = '';
    list.forEach(it => {
      const tr = document.createElement('tr');
      tr.dataset.id = it.shipmentItemId;
      tr.innerHTML = `
        <td><span class="label">${it.shipmentNumber ?? ''}</span></td>
        <td><span class="label">${it.itemCode ?? ''}</span></td>
        <td><span class="label">${it.quantity ?? 0}</span></td>
        <td><span class="label">${fmtCurrency(it.unitPrice)}</span></td>
        <td><span class="label">${fmtCurrency(it.totalPrice)}</span></td>
        <td><button class="btn-edit-label edit-btn">Edit Item</button></td>`;
      tbody.appendChild(tr);
    });
  }

  function makeRowEditable(row){
    const cells = row.querySelectorAll('td:not(:last-child)');
    cells.forEach((cell, index) => {
      const label = cell.querySelector('.label');
      if (index === 0){
        const currentValue = label.textContent.trim();
        const container = document.createElement('div'); container.className = 'item-cell-container';
        const select = document.createElement('input'); select.type = 'text'; select.className = 'item-select'; select.value = currentValue; select.dataset.currentValue = currentValue; select.dataset.id = '';
        const dropdown = document.createElement('div'); dropdown.className = 'item-dropdown';
        container.appendChild(select); container.appendChild(dropdown); cell.innerHTML = ''; cell.appendChild(container);
        attachSearchableDropdown({ select, dropdown, hidden: null, fetcher: searchShipments, getLabel: s => s.shipmentNumber, getId: s => s.shipmentId });
      }
      if (index === 1){
        const currentValue = label.textContent.trim();
        const container = document.createElement('div'); container.className = 'item-cell-container';
        const select = document.createElement('input'); select.type = 'text'; select.className = 'item-select'; select.value = currentValue; select.dataset.currentValue = currentValue; select.dataset.id = '';
        const dropdown = document.createElement('div'); dropdown.className = 'item-dropdown';
        container.appendChild(select); container.appendChild(dropdown); cell.innerHTML = ''; cell.appendChild(container);
        attachSearchableDropdown({ select, dropdown, hidden: null, fetcher: searchInventory, getLabel: i => i.itemCode, getId: i => i.itemId, onSelected: async ({ id, label }) => {
          // Prefer searching by label (itemCode) to narrow to one result
          let list = await searchInventory(label || '');
          let found = Array.isArray(list) ? list.find(i => Number(i.itemId) === Number(id)) : null;
          if (!found) {
            list = await searchInventory('');
            found = Array.isArray(list) ? list.find(i => Number(i.itemId) === Number(id)) : null;
          }
          selectedUnitPrice = found && typeof found.unitPrice === 'number' ? found.unitPrice : (found && found.unitPrice ? parseFloat(found.unitPrice) : 0);
        }});
      }
      if (label && index === 2){ const input = document.createElement('input'); input.type = 'number'; input.className = 'editable-input'; input.min = '1'; input.value = label.textContent.trim(); cell.innerHTML = ''; cell.appendChild(input); }
      if (label && index === 3){ const input = document.createElement('input'); input.type = 'number'; input.className = 'editable-input'; input.step = '0.01'; input.min = '0'; input.disabled = true; input.value = label.textContent.trim().replace('$',''); cell.innerHTML=''; cell.appendChild(input); }
      if (label && index === 4){ const q = row.querySelectorAll('input[type="number"]')[0]; const p = row.querySelectorAll('input[type="number"]')[1]; const out = label; const update = ()=>{ const quantity = parseFloat(q?.value||0); const unitPrice = parseFloat(p?.value||0); const total = quantity*unitPrice; out.textContent = fmtCurrency(total); }; q && q.addEventListener('input', update); p && p.addEventListener('input', update); }
    });
  }

  async function saveRowChanges(row){
    const id = row.dataset.id; if (!id) return;
    const cells = row.querySelectorAll('td');
    const shipmentSelect = cells[0].querySelector('.item-select');
    const itemSelect = cells[1].querySelector('.item-select');
    const shipmentNumber = shipmentSelect?.dataset?.currentValue || cells[0].textContent.trim();
    const itemCode = itemSelect?.dataset?.currentValue || cells[1].textContent.trim();
    const quantity = parseInt(cells[2].querySelector('.editable-input')?.value ?? '0', 10);
    const unitPrice = parseFloat(cells[3].querySelector('.editable-input')?.value ?? '0');

    const body = { quantity, unitPrice };
    const shipmentIdStr = shipmentSelect?.dataset?.id;
    const itemIdStr = itemSelect?.dataset?.id;
    const shipmentId = shipmentIdStr ? parseInt(shipmentIdStr, 10) : undefined;
    const itemId = itemIdStr ? parseInt(itemIdStr, 10) : undefined;
    if (!isNaN(shipmentId)) body.shipmentId = shipmentId;
    if (!isNaN(itemId)) body.itemId = itemId;

    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, {
      method: 'PATCH', headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify(body)
    });
    if (!res.ok) { const err = await res.text(); alert(`Failed to update shipment item: ${err || res.status}`); return; }

    const updated = await res.json();
    cells[0].innerHTML = `<span class="label">${updated.shipmentNumber ?? shipmentNumber}</span>`;
    cells[1].innerHTML = `<span class=\"label\">${updated.itemCode ?? itemCode}</span>`;
    cells[2].innerHTML = `<span class=\"label\">${updated.quantity ?? quantity}</span>`;
    cells[3].innerHTML = `<span class=\"label\">${fmtCurrency(updated.unitPrice ?? unitPrice)}</span>`;
    cells[4].innerHTML = `<span class=\"label\">${fmtCurrency(updated.totalPrice)}</span>`;
    const actionsCell = row.querySelector('td:last-child'); actionsCell.innerHTML = '<button class="btn-edit-label edit-btn">Edit Item</button>';
  }

  async function deleteRow(row){
    if (!confirm('Are you sure you want to delete this shipment item?')) return;
    const id = row.dataset.id; if (!id) { row.remove(); return; }
    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, { method: 'DELETE' });
    if (!res.ok) { const err = await res.text(); alert(`Failed to delete shipment item: ${err || res.status}`); return; }
    row.remove();
  }

  tbody.addEventListener('click', (e) => {
    const target = e.target; const row = target.closest('tr'); if (!row) return;
    if (target.classList.contains('edit-btn')){ const actionsCell = row.querySelector('td:last-child'); actionsCell.innerHTML = '<div class="action-icons-container"><img src="/images/correct.png" class="action-icon save-btn" alt="Save"><img src="/images/trash.png" class="action-icon delete-btn" alt="Delete"></div>'; makeRowEditable(row); }
    if (target.classList.contains('save-btn')){ saveRowChanges(row); }
    if (target.classList.contains('delete-btn')){ deleteRow(row); }
  });

  function toggleItemDropdown(dropdown){ document.querySelectorAll('.item-dropdown').forEach(dd => { if(dd !== dropdown) dd.style.display = 'none'; }); dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block'; }
  document.addEventListener('click', (e) => { if(!e.target.closest('.item-cell-container')){ document.querySelectorAll('.item-dropdown').forEach(dd => dd.style.display = 'none'); } });

  const addShipmentItemIcon = document.getElementById('addShipmentItemIcon');
  const addShipmentItemModalOverlay = document.getElementById('addShipmentItemModalOverlay');
  const closeAddShipmentItemModal = document.getElementById('closeAddShipmentItemModal');
  const addShipmentItemForm = document.getElementById('addShipmentItemForm');
  const searchInput = document.getElementById('searchInput');
  const searchIcon = document.getElementById('searchIcon');

  function normalize(text){ return (text||'').toString().toLowerCase().trim(); }
  function filterRows(){ const q = normalize(searchInput.value); const rows = tbody.querySelectorAll('tr'); rows.forEach(row => { const cells = row.querySelectorAll('td'); const values = [0,1,2,3,4].map(i => normalize(cells[i]?.querySelector('.editable-input')?.value || cells[i]?.textContent)); row.style.display = values.some(v => v.includes(q)) ? '' : 'none'; }); }
  if (searchInput) searchInput.addEventListener('input', filterRows);
  if (searchIcon) searchIcon.addEventListener('click', filterRows);

  if (addShipmentItemIcon) addShipmentItemIcon.addEventListener('click', () => { addShipmentItemModalOverlay.style.display = 'flex'; document.body.style.overflow = 'hidden'; });
  if (closeAddShipmentItemModal) closeAddShipmentItemModal.addEventListener('click', () => { addShipmentItemModalOverlay.style.display = 'none'; document.body.style.overflow = ''; });
  if (addShipmentItemModalOverlay) addShipmentItemModalOverlay.addEventListener('click', (e) => { if (e.target === addShipmentItemModalOverlay){ addShipmentItemModalOverlay.style.display = 'none'; document.body.style.overflow = ''; } });

  // Wire searchable dropdowns in Add modal; also auto-fill unit price from inventory
  (function(){
    const shipSelect = document.getElementById('addShipmentIdSelect');
    const shipDropdown = document.getElementById('addShipmentIdDropdown');
    const shipHidden = document.getElementById('addShipmentId');
    attachSearchableDropdown({ select: shipSelect, dropdown: shipDropdown, hidden: shipHidden, fetcher: searchShipments, getLabel: s => s.shipmentNumber, getId: s => s.shipmentId });

    const itemSelect = document.getElementById('addItemIdSelect');
    const itemDropdown = document.getElementById('addItemIdDropdown');
    const itemHidden = document.getElementById('addItemId');
    let selectedUnitPrice = 0;
    attachSearchableDropdown({ select: itemSelect, dropdown: itemDropdown, hidden: itemHidden, fetcher: searchInventory, getLabel: i => i.itemCode, getId: i => i.itemId, onSelected: async ({ id, label }) => {
      // Prefer searching by label (itemCode) to narrow to one result
      let list = await searchInventory(label || '');
      let found = Array.isArray(list) ? list.find(i => Number(i.itemId) === Number(id)) : null;
      if (!found) {
        list = await searchInventory('');
        found = Array.isArray(list) ? list.find(i => Number(i.itemId) === Number(id)) : null;
      }
      selectedUnitPrice = found && typeof found.unitPrice === 'number' ? found.unitPrice : (found && found.unitPrice ? parseFloat(found.unitPrice) : 0);
    }});
    // store for submit
    itemSelect.dataset.priceRef = '1';
    itemSelect.addEventListener('change', () => {});
    itemSelect.getSelectedUnitPrice = () => selectedUnitPrice;
  })();

  if (addShipmentItemForm) addShipmentItemForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const shipmentId = parseInt(document.getElementById('addShipmentId').value, 10);
    const itemId = parseInt(document.getElementById('addItemId').value, 10);
    if (!shipmentId || !itemId) { alert('Please select a shipment and an item.'); return; }
    const payload = {
      shipmentId,
      itemId,
      quantity: parseInt(document.getElementById('addQuantity').value, 10),
    };
    const res = await fetch(API_BASE, { method: 'POST', headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' }, body: JSON.stringify(payload) });
    if (!res.ok) { const err = await res.text(); alert(`Failed to add shipment item: ${err || res.status}`); return; }
    const created = await res.json();
    const newRow = document.createElement('tr');
    newRow.dataset.id = created.shipmentItemId;
    newRow.innerHTML = `
      <td><span class="label">${created.shipmentNumber ?? (document.getElementById('addShipmentIdSelect').dataset.currentValue || '')}</span></td>
      <td><span class="label">${created.itemCode ?? (document.getElementById('addItemIdSelect').dataset.currentValue || '')}</span></td>
      <td><span class="label">${created.quantity ?? payload.quantity}</span></td>
      <td><span class="label">${fmtCurrency(created.unitPrice)}</span></td>
      <td><span class="label">${fmtCurrency(created.totalPrice)}</span></td>
      <td><button class="btn-edit-label edit-btn">Edit Item</button></td>`;
    tbody.prepend(newRow);
    addShipmentItemModalOverlay.style.display = 'none';
    document.body.style.overflow = '';
    addShipmentItemForm.reset();
    // reset selects visual
    const shipSel = document.getElementById('addShipmentIdSelect'); if (shipSel) { shipSel.value = ''; shipSel.dataset.currentValue=''; shipSel.dataset.id=''; }
    const itemSel = document.getElementById('addItemIdSelect'); if (itemSel) { itemSel.value = ''; itemSel.dataset.currentValue=''; itemSel.dataset.id=''; }
  });

  // Initial load: attempt to parse shipment number from URL query ?shipment=... if available
  const urlParams = new URLSearchParams(window.location.search);
  const shipmentNumber = urlParams.get('shipment');
  if (shipmentNumber) {
    loadItemsByShipmentNumber(shipmentNumber);
  } else {
    // load all
    (async () => {
      const res = await fetch(`${API_BASE}`, { headers: { 'Accept': 'application/json' } });
      if (!res.ok) { console.error('Failed to load shipment items'); return; }
      const data = await res.json();
      renderRows(Array.isArray(data) ? data : []);
    })();
  }
}); 