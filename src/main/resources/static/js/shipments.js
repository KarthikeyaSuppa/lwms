document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("tbody");
  const API_BASE = "/shipments/api"; // also /lwms/shipments/api
  function toDatetimeLocal(text){ if(!text || text === '-') return ''; return text.replace(' ', 'T'); }
  function applyBadges(row){
    const typeCell = row.cells[1]; const statusCell = row.cells[5];
    const type = (typeCell?.textContent || '').trim(); const status = (statusCell?.textContent || '').trim();
    if(typeCell){ const span = typeCell.querySelector('.label'); if(span){ span.classList.remove('badge-inbound','badge-outbound'); if(type === 'Inbound') span.classList.add('badge-inbound'); if(type === 'Outbound') span.classList.add('badge-outbound'); span.style.display='block'; span.style.width='100%'; } }
    if(statusCell){ const span = statusCell.querySelector('.label'); if(span){ span.classList.remove('badge-planned','badge-intransit','badge-delivered','badge-cancelled'); const key = status.toLowerCase(); if(key.includes('planned')) span.classList.add('badge-planned'); else if(key.includes('in transit')) span.classList.add('badge-intransit'); else if(key.includes('delivered')) span.classList.add('badge-delivered'); else if(key.includes('cancelled')) span.classList.add('badge-cancelled'); span.style.display='block'; span.style.width='100%'; } }
  }
  function renderRows(list){
    tbody.innerHTML = '';
    list.forEach(s => {
      const tr = document.createElement('tr');
      tr.dataset.id = s.shipmentId;
      tr.innerHTML = `
        <td><span class="label">${s.shipmentNumber ?? ''}</span></td>
        <td><span class="label">${s.shipmentType ?? ''}</span></td>
        <td><span class="label">${s.supplier ?? '-'}</span></td>
        <td><span class="label">${s.origin ?? ''}</span></td>
        <td><span class="label">${s.destination ?? ''}</span></td>
        <td><span class="label">${s.status ?? ''}</span></td>
        <td><span class="label">${s.expectedDelivery ?? '-'}</span></td>
        <td><span class="label">${s.actualDelivery ?? '-'}</span></td>
        <td><span class="label">${s.totalValue ?? '0.00'}</span></td>
        <td><button class="btn-edit-label edit-btn">Edit Shipment</button></td>`;
      tbody.appendChild(tr); applyBadges(tr);
    });
  }
  async function loadShipments(){
    const q = document.getElementById('searchInput')?.value?.trim();
    const url = q ? `${API_BASE}?q=${encodeURIComponent(q)}` : API_BASE;
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) { console.error('Failed to load shipments'); return; }
    const data = await res.json(); renderRows(Array.isArray(data) ? data : []);
  }

  function makeRowEditable(row){
    const cells = row.querySelectorAll('td:not(:last-child)');
    cells.forEach((cell, index) => {
      const label = cell.querySelector('.label');
      if (label && index === 0) { const input = document.createElement('input'); input.type = 'text'; input.className = 'editable-input'; input.placeholder = 'SHIP2025XXXX'; input.value = label.textContent.trim(); cell.innerHTML = ''; cell.appendChild(input); }
      if (index === 1) { const currentValue = label?.textContent.trim() || 'Inbound'; const container = document.createElement('div'); container.className = 'ship-cell-container'; const select = document.createElement('div'); select.className = 'ship-select'; select.textContent = currentValue; select.dataset.currentValue = currentValue; const dropdown = document.createElement('div'); dropdown.className = 'ship-dropdown'; ['Inbound','Outbound'].forEach(v => { const item = document.createElement('div'); item.className = `ship-dropdown-item ${v === currentValue ? 'selected' : ''}`; item.textContent = v; item.dataset.value = v; dropdown.appendChild(item); }); select.addEventListener('click', (e) => { e.stopPropagation(); toggleShipDropdown(dropdown); }); dropdown.addEventListener('click', (e) => { const item = e.target.closest('.ship-dropdown-item'); if(!item) return; e.stopPropagation(); select.textContent = item.dataset.value; select.dataset.currentValue = item.dataset.value; dropdown.querySelectorAll('.ship-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display = 'none'; }); container.appendChild(select); container.appendChild(dropdown); cell.innerHTML = ''; cell.appendChild(container); }
      if (index === 2) { const input = document.createElement('input'); input.type = 'text'; input.className = 'editable-input'; input.placeholder = 'Supplier ID or Name'; input.value = label?.textContent.trim() || ''; cell.innerHTML = ''; cell.appendChild(input); }
      if (index === 3 || index === 4) { const input = document.createElement('input'); input.type = 'text'; input.className = 'editable-input'; input.placeholder = 'City, Country'; input.value = label?.textContent.trim() || ''; cell.innerHTML = ''; cell.appendChild(input); }
      if (index === 5) { const currentValue = label?.textContent.trim() || 'Planned'; const container = document.createElement('div'); container.className = 'ship-cell-container'; const select = document.createElement('div'); select.className = 'ship-select'; select.textContent = currentValue; select.dataset.currentValue = currentValue; const dropdown = document.createElement('div'); dropdown.className = 'ship-dropdown'; ['Planned','In Transit','Delivered','Cancelled'].forEach(v => { const item = document.createElement('div'); item.className = `ship-dropdown-item ${v === currentValue ? 'selected' : ''}`; item.textContent = v; item.dataset.value = v; dropdown.appendChild(item); }); select.addEventListener('click', (e) => { e.stopPropagation(); toggleShipDropdown(dropdown); }); dropdown.addEventListener('click', (e) => { const item = e.target.closest('.ship-dropdown-item'); if(!item) return; e.stopPropagation(); select.textContent = item.dataset.value; select.dataset.currentValue = item.dataset.value; dropdown.querySelectorAll('.ship-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display = 'none'; }); container.appendChild(select); container.appendChild(dropdown); cell.innerHTML = ''; cell.appendChild(container); }
      if (index === 6 || index === 7) { const input = document.createElement('input'); input.type = 'datetime-local'; input.className = 'editable-input'; input.placeholder = 'YYYY-MM-DD HH:MM'; input.value = toDatetimeLocal(label?.textContent.trim() || ''); cell.innerHTML = ''; cell.appendChild(input); }
      if (index === 8) { const input = document.createElement('input'); input.type = 'number'; input.step = '0.01'; input.className = 'editable-input'; input.value = (label?.textContent.trim() || '0').replace(/,/g, ''); cell.innerHTML = ''; cell.appendChild(input); }
    });
  }

  async function saveRowChanges(row){
    const id = row.dataset.id; if (!id) return;
    const cells = row.querySelectorAll('td');
    const shipmentNumber = cells[0].querySelector('.editable-input')?.value?.trim();
    const shipmentType = cells[1].querySelector('.ship-select')?.dataset?.currentValue;
    const supplier = cells[2].querySelector('.editable-input')?.value?.trim();
    const origin = cells[3].querySelector('.editable-input')?.value?.trim();
    const destination = cells[4].querySelector('.editable-input')?.value?.trim();
    const status = cells[5].querySelector('.ship-select')?.dataset?.currentValue;
    const expectedDelivery = cells[6].querySelector('.editable-input')?.value?.trim();
    const actualDelivery = cells[7].querySelector('.editable-input')?.value?.trim();
    const totalValue = cells[8].querySelector('.editable-input')?.value?.trim();

    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, {
      method: 'PATCH', headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify({ shipmentNumber, shipmentType, supplier, origin, destination, status, expectedDelivery, actualDelivery, totalValue })
    });
    if (!res.ok) { const err = await res.text(); alert(`Failed to update shipment: ${err || res.status}`); return; }
    const updated = await res.json();
    cells[0].innerHTML = `<span class="label">${updated.shipmentNumber ?? ''}</span>`;
    cells[1].innerHTML = `<span class="label">${updated.shipmentType ?? ''}</span>`;
    cells[2].innerHTML = `<span class="label">${updated.supplier ?? (supplier || '-')}</span>`;
    cells[3].innerHTML = `<span class="label">${updated.origin ?? ''}</span>`;
    cells[4].innerHTML = `<span class="label">${updated.destination ?? ''}</span>`;
    cells[5].innerHTML = `<span class="label">${updated.status ?? ''}</span>`;
    cells[6].innerHTML = `<span class="label">${updated.expectedDelivery ?? '-'}</span>`;
    cells[7].innerHTML = `<span class="label">${updated.actualDelivery ?? '-'}</span>`;
    cells[8].innerHTML = `<span class="label">${updated.totalValue ?? '0.00'}</span>`;
    const actionsCell = row.querySelector('td:last-child'); actionsCell.innerHTML = '<button class="btn-edit-label edit-btn">Edit Shipment</button>';
    applyBadges(row);
  }

  async function deleteRow(row){ if(!confirm('Are you sure you want to delete this shipment?')) return; const id=row.dataset.id; if(!id){ row.remove(); return; } const res=await fetch(`${API_BASE}/${encodeURIComponent(id)}`, { method:'DELETE' }); if(!res.ok){ const err=await res.text(); alert(`Failed to delete shipment: ${err || res.status}`); return; } row.remove(); }

  tbody.addEventListener('click', (e) => {
    const target = e.target; const row = target.closest('tr'); if (!row) return;
    if (target.classList.contains('edit-btn')){ const actionsCell = row.querySelector('td:last-child'); actionsCell.innerHTML='<div class="action-icons-container"><img src="/images/correct.png" class="action-icon save-btn" alt="Save"><img src="/images/trash.png" class="action-icon delete-btn" alt="Delete"></div>'; makeRowEditable(row); }
    if (target.classList.contains('save-btn')) { saveRowChanges(row); }
    if (target.classList.contains('delete-btn')) { deleteRow(row); }
  });

  function toggleShipDropdown(dropdown){ document.querySelectorAll('.ship-dropdown').forEach(dd => { if(dd !== dropdown) dd.style.display = 'none'; }); dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block'; }
  document.addEventListener('click', (e) => { if(!e.target.closest('.ship-cell-container')){ document.querySelectorAll('.ship-dropdown').forEach(dd => dd.style.display = 'none'); } });

  const addCategoryIcon = document.getElementById('addCategoryIcon');
  const addCategoryModalOverlay = document.getElementById('addCategoryModalOverlay');
  const closeAddCategoryModal = document.getElementById('closeAddCategoryModal');
  const addCategoryForm = document.getElementById('addCategoryForm');
  const searchInput = document.getElementById('searchInput');
  const searchIcon = document.getElementById('searchIcon');
  // supplier searchable dropdown (datalist like inventory.js)
  const supInput = document.getElementById('addSupplier');
  const supList = document.getElementById('supplierOptions');
  const supIdHidden = document.getElementById('addSupplierId');
  async function searchSuppliers(q){
    const url = q ? `/suppliers/api?q=${encodeURIComponent(q)}` : '/suppliers/api';
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) return [];
    return res.json();
  }
  function bindSupplierDatalist(){
    if(!supInput || !supList || !supIdHidden) return;
    let lastResults = [];
    supInput.addEventListener('input', async () => {
      const q = supInput.value.trim();
      const results = await searchSuppliers(q);
      lastResults = Array.isArray(results) ? results : [];
      supList.innerHTML = '';
      lastResults.forEach(s => {
        const opt = document.createElement('option');
        opt.value = s.supplierName;
        opt.dataset.id = s.supplierId;
        supList.appendChild(opt);
      });
    });
    supInput.addEventListener('change', () => {
      const name = supInput.value.trim();
      const match = Array.from(supList.options).find(o => o.value.toLowerCase() === name.toLowerCase());
      if (match && match.dataset.id) {
        supIdHidden.value = match.dataset.id;
      } else {
        // no exact match -> clear id so backend can resolve by name best-effort
        supIdHidden.value = '';
      }
    });
  }
  bindSupplierDatalist();

  function normalize(text){ return (text || '').toString().toLowerCase().trim(); }
  function filterRows(){ loadShipments(); }
  if (searchInput) searchInput.addEventListener('input', () => { loadShipments(); });
  if (searchIcon) searchIcon.addEventListener('click', () => { loadShipments(); });

  function wireModalDropdown(selectId, dropdownId, hiddenSelectId){ const select = document.getElementById(selectId); const dropdown = document.getElementById(dropdownId); const hidden = document.getElementById(hiddenSelectId); if(!select || !dropdown || !hidden) return; select.addEventListener('click', (e) => { e.stopPropagation(); toggleShipDropdown(dropdown); }); dropdown.addEventListener('click', (e) => { const item = e.target.closest('.ship-dropdown-item'); if(!item) return; e.stopPropagation(); const val = item.dataset.value; select.textContent = val; select.dataset.currentValue = val; hidden.value = val; dropdown.querySelectorAll('.ship-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display = 'none'; }); }
  wireModalDropdown('addShipmentTypeSelect','addShipmentTypeDropdown','addShipmentType');
  wireModalDropdown('addStatusSelect','addStatusDropdown','addStatus');

  if (addCategoryIcon) addCategoryIcon.addEventListener('click', () => { addCategoryModalOverlay.style.display = 'flex'; document.body.style.overflow = 'hidden'; });
  if (closeAddCategoryModal) closeAddCategoryModal.addEventListener('click', () => { addCategoryModalOverlay.style.display = 'none'; document.body.style.overflow = ''; });
  if (addCategoryModalOverlay) addCategoryModalOverlay.addEventListener('click', (e) => { if (e.target === addCategoryModalOverlay) { addCategoryModalOverlay.style.display = 'none'; document.body.style.overflow = ''; } });

  if (addCategoryForm) addCategoryForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
      shipmentNumber: document.getElementById('addShipmentNumber').value,
      shipmentType: document.getElementById('addShipmentType').value,
      supplierId: document.getElementById('addSupplierId')?.value || null,
      supplierName: document.getElementById('addSupplier')?.value || null,
      origin: document.getElementById('addOrigin').value,
      destination: document.getElementById('addDestination').value,
      status: document.getElementById('addStatus').value,
      expectedDelivery: document.getElementById('addExpected').value,
      actualDelivery: document.getElementById('addActual').value,
      totalValue: document.getElementById('addTotalValue').value,
    };
    // Server accepts either supplierId or name; send both keys normalized
    const res = await fetch(API_BASE, { method: 'POST', headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' }, body: JSON.stringify({
      shipmentNumber: payload.shipmentNumber,
      shipmentType: payload.shipmentType,
      supplierId: payload.supplierId ? Number(payload.supplierId) : undefined,
      supplier: payload.supplierName || undefined,
      origin: payload.origin,
      destination: payload.destination,
      status: payload.status,
      expectedDelivery: payload.expectedDelivery,
      actualDelivery: payload.actualDelivery,
      totalValue: payload.totalValue
    }) });
    if (!res.ok) { const err = await res.text(); alert(`Failed to add shipment: ${err || res.status}`); return; }
    const created = await res.json();
    const newRow = document.createElement('tr');
    newRow.dataset.id = created.shipmentId;
    newRow.innerHTML = `
      <td><span class="label">${created.shipmentNumber ?? payload.shipmentNumber}</span></td>
      <td><span class="label">${created.shipmentType ?? payload.shipmentType}</span></td>
      <td><span class="label">${(created.supplier ?? payload.supplierName) || '-'}</span></td>
      <td><span class="label">${created.origin ?? payload.origin}</span></td>
      <td><span class="label">${created.destination ?? payload.destination}</span></td>
      <td><span class="label">${created.status ?? payload.status}</span></td>
      <td><span class="label">${(created.expectedDelivery ?? payload.expectedDelivery) || '-'}</span></td>
      <td><span class="label">${(created.actualDelivery ?? payload.actualDelivery) || '-'}</span></td>
      <td><span class="label">${created.totalValue ?? payload.totalValue}</span></td>
      <td><button class="btn-edit-label edit-btn">Edit Shipment</button></td>`;
    tbody.prepend(newRow); applyBadges(newRow); addCategoryModalOverlay.style.display='none'; document.body.style.overflow=''; addCategoryForm.reset();
  });

  function enhanceSelect(selectEl){ if (!selectEl) return; const wrapper = selectEl.closest('.select-wrapper'); const expand = () => { const visibleCount = Math.min(6, selectEl.options?.length || 6); selectEl.setAttribute('size', String(visibleCount)); if (wrapper) wrapper.classList.add('expanded'); }; const collapse = () => { selectEl.removeAttribute('size'); if (wrapper) wrapper.classList.remove('expanded'); }; selectEl.addEventListener('focus', expand); selectEl.addEventListener('blur', collapse); selectEl.addEventListener('change', () => { setTimeout(collapse, 0); }); selectEl.addEventListener('keydown', (e) => { if (e.key === 'Escape') { collapse(); selectEl.blur(); } }); }
  enhanceSelect(document.getElementById('addShipmentType')); enhanceSelect(document.getElementById('addStatus'));

  // Initial load
  loadShipments();
}); 