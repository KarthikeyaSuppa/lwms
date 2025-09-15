document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("tbody");
  const API_BASE = "/shipment-items/api"; // also /lwms/shipment-items/api

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
        const select = document.createElement('div'); select.className = 'item-select'; select.textContent = currentValue; select.dataset.currentValue = currentValue;
        const dropdown = document.createElement('div'); dropdown.className = 'item-dropdown';
        [currentValue].forEach(v => { const item = document.createElement('div'); item.className = `item-dropdown-item selected`; item.textContent = v; item.dataset.value = v; dropdown.appendChild(item); });
        select.addEventListener('click', (e)=>{ e.stopPropagation(); toggleItemDropdown(dropdown); });
        dropdown.addEventListener('click', (e)=>{ const item = e.target.closest('.item-dropdown-item'); if(!item) return; e.stopPropagation(); select.textContent = item.dataset.value; select.dataset.currentValue = item.dataset.value; dropdown.querySelectorAll('.item-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display = 'none'; });
        container.appendChild(select); container.appendChild(dropdown); cell.innerHTML = ''; cell.appendChild(container);
      }
      if (index === 1){
        const currentValue = label.textContent.trim();
        const input = document.createElement('input'); input.type = 'text'; input.className = 'editable-input'; input.value = currentValue; cell.innerHTML = ''; cell.appendChild(input);
      }
      if (label && index === 2){ const input = document.createElement('input'); input.type = 'number'; input.className = 'editable-input'; input.min = '1'; input.value = label.textContent.trim(); cell.innerHTML = ''; cell.appendChild(input); }
      if (label && index === 3){ const input = document.createElement('input'); input.type = 'number'; input.className = 'editable-input'; input.step = '0.01'; input.min = '0'; input.value = label.textContent.trim().replace('$',''); cell.innerHTML=''; cell.appendChild(input); }
      if (label && index === 4){ const q = row.querySelectorAll('input[type="number"]')[0]; const p = row.querySelectorAll('input[type="number"]')[1]; const out = label; const update = ()=>{ const quantity = parseFloat(q?.value||0); const unitPrice = parseFloat(p?.value||0); const total = quantity*unitPrice; out.textContent = fmtCurrency(total); }; q && q.addEventListener('input', update); p && p.addEventListener('input', update); }
    });
  }

  async function saveRowChanges(row){
    const id = row.dataset.id; if (!id) return;
    const cells = row.querySelectorAll('td');
    const shipmentNumber = cells[0].querySelector('.item-select')?.dataset?.currentValue || cells[0].textContent.trim();
    const itemCode = cells[1].querySelector('.editable-input')?.value?.trim() || cells[1].textContent.trim();
    const quantity = parseInt(cells[2].querySelector('.editable-input')?.value ?? '0', 10);
    const unitPrice = parseFloat(cells[3].querySelector('.editable-input')?.value ?? '0');

    // We need shipmentId and itemId; if UI uses codes, backend endpoints should accept IDs; skipping lookup here, assuming another UI flow supplies IDs
    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, {
      method: 'PATCH', headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify({ quantity, unitPrice })
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

  function wireItemModalDropdown(selectId, dropdownId, hiddenId){ const select = document.getElementById(selectId); const dropdown = document.getElementById(dropdownId); const hidden = document.getElementById(hiddenId); if(!select || !dropdown || !hidden) return; select.addEventListener('click', (e)=>{ e.stopPropagation(); toggleItemDropdown(dropdown); }); dropdown.addEventListener('click', (e)=>{ const item = e.target.closest('.item-dropdown-item'); if(!item) return; e.stopPropagation(); const val = item.dataset.value; select.textContent = val; select.dataset.currentValue = val; hidden.value = val; dropdown.querySelectorAll('.item-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display = 'none'; }); }
  wireItemModalDropdown('addShipmentIdSelect','addShipmentIdDropdown','addShipmentId');
  wireItemModalDropdown('addItemIdSelect','addItemIdDropdown','addItemId');

  if (addShipmentItemForm) addShipmentItemForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
      shipmentId: parseInt(document.getElementById('addShipmentId').value.replace(/\D/g,''), 10) || undefined,
      itemId: parseInt(document.getElementById('addItemId').value.replace(/\D/g,''), 10) || undefined,
      quantity: parseInt(document.getElementById('addQuantity').value, 10),
      unitPrice: parseFloat(document.getElementById('addUnitPrice').value)
    };
    const res = await fetch(API_BASE, { method: 'POST', headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' }, body: JSON.stringify(payload) });
    if (!res.ok) { const err = await res.text(); alert(`Failed to add shipment item: ${err || res.status}`); return; }
    const created = await res.json();
    const newRow = document.createElement('tr');
    newRow.dataset.id = created.shipmentItemId;
    newRow.innerHTML = `
      <td><span class="label">${created.shipmentNumber ?? document.getElementById('addShipmentId').value}</span></td>
      <td><span class="label">${created.itemCode ?? document.getElementById('addItemId').value}</span></td>
      <td><span class="label">${created.quantity ?? payload.quantity}</span></td>
      <td><span class="label">${fmtCurrency(created.unitPrice ?? payload.unitPrice)}</span></td>
      <td><span class="label">${fmtCurrency(created.totalPrice)}</span></td>
      <td><button class="btn-edit-label edit-btn">Edit Item</button></td>`;
    tbody.prepend(newRow);
    addShipmentItemModalOverlay.style.display = 'none';
    document.body.style.overflow = '';
    addShipmentItemForm.reset();
  });

  // Initial load: attempt to parse shipment number from URL query ?shipment=... if available
  const urlParams = new URLSearchParams(window.location.search);
  const shipmentNumber = urlParams.get('shipment');
  if (shipmentNumber) loadItemsByShipmentNumber(shipmentNumber);
}); 