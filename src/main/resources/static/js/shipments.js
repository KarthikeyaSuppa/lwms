document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("tbody");
  function toDatetimeLocal(text){ if(!text || text === '-') return ''; return text.replace(' ', 'T'); }
  function applyBadges(row){
    const typeCell = row.cells[1]; const statusCell = row.cells[5];
    const type = (typeCell?.textContent || '').trim(); const status = (statusCell?.textContent || '').trim();
    if(typeCell){ const span = typeCell.querySelector('.label'); if(span){ span.classList.remove('badge-inbound','badge-outbound'); if(type === 'Inbound') span.classList.add('badge-inbound'); if(type === 'Outbound') span.classList.add('badge-outbound'); } }
    if(statusCell){ const span = statusCell.querySelector('.label'); if(span){ span.classList.remove('badge-planned','badge-intransit','badge-delivered','badge-cancelled'); const key = status.toLowerCase(); if(key.includes('planned')) span.classList.add('badge-planned'); else if(key.includes('in transit')) span.classList.add('badge-intransit'); else if(key.includes('delivered')) span.classList.add('badge-delivered'); else if(key.includes('cancelled')) span.classList.add('badge-cancelled'); } }
  }
  tbody.querySelectorAll('tr').forEach(applyBadges);

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

  function saveRowChanges(row){
    const cells = row.querySelectorAll('td');
    cells.forEach((cell, index) => {
      if (index === 0) { const input = cell.querySelector('.editable-input'); if (input) { const label = document.createElement('span'); label.className = 'label'; label.textContent = input.value; cell.innerHTML = ''; cell.appendChild(label); } }
      else if (index === 1) { const typeSelect = cell.querySelector('.ship-select'); if (typeSelect) { const label = document.createElement('span'); label.className = 'label'; label.textContent = typeSelect.dataset.currentValue || typeSelect.textContent; cell.innerHTML = ''; cell.appendChild(label); applyBadges(row); } }
      else if (index === 2 || index === 3 || index === 4) { const input = cell.querySelector('.editable-input'); if (input) { const label = document.createElement('span'); label.className = 'label'; label.textContent = input.value || '-'; cell.innerHTML = ''; cell.appendChild(label); } }
      else if (index === 5) { const statusSelect = cell.querySelector('.ship-select'); if (statusSelect) { const label = document.createElement('span'); label.className = 'label'; label.textContent = statusSelect.dataset.currentValue || statusSelect.textContent; cell.innerHTML = ''; cell.appendChild(label); applyBadges(row); } }
      else if (index === 6 || index === 7) { const input = cell.querySelector('.editable-input'); if (input) { const label = document.createElement('span'); label.className = 'label'; label.textContent = input.value || '-'; cell.innerHTML = ''; cell.appendChild(label); } }
      else if (index === 8) { const input = cell.querySelector('.editable-input'); if (input) { const label = document.createElement('span'); label.className = 'label'; const num = parseFloat(input.value || '0'); label.textContent = isNaN(num) ? '0.00' : num.toLocaleString(undefined, { minimumFractionDigits:2, maximumFractionDigits:2 }); cell.innerHTML = ''; cell.appendChild(label); } }
      else if (index === 9) { const actionsCell = row.querySelector('td:last-child'); actionsCell.innerHTML = '<button class="btn-edit-label edit-btn">Edit Shipment</button>'; }
    });
  }

  function deleteRow(row){ if (confirm('Are you sure you want to delete this shipment?')) row.remove(); }

  tbody.addEventListener('click', (e) => {
    const target = e.target; const row = target.closest('tr'); if (!row) return;
    if (target.classList.contains('edit-btn')) { makeRowEditable(row); const actionsCell = row.querySelector('td:last-child'); actionsCell.innerHTML = '<div class="action-icons-container"><img src="/images/correct.png" class="action-icon save-btn" alt="Save"><img src="/images/trash.png" class="action-icon delete-btn" alt="Delete"></div>'; }
    if (target.classList.contains('save-btn')) { saveRowChanges(row); applyBadges(row); }
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

  function normalize(text){ return (text || '').toString().toLowerCase().trim(); }
  function filterRows(){ const query = normalize(searchInput.value); const rows = tbody.querySelectorAll('tr'); rows.forEach(row => { const cells = row.querySelectorAll('td'); const values = [0,1,2,3,4,5,6,7,8].map(i => normalize(cells[i]?.querySelector('.editable-input')?.value || cells[i]?.textContent)); row.style.display = values.some(v => v.includes(query)) ? '' : 'none'; }); }
  if (searchInput) searchInput.addEventListener('input', filterRows);
  if (searchIcon) searchIcon.addEventListener('click', filterRows);

  if (addCategoryIcon) addCategoryIcon.addEventListener('click', () => { addCategoryModalOverlay.style.display = 'flex'; document.body.style.overflow = 'hidden'; });
  if (closeAddCategoryModal) closeAddCategoryModal.addEventListener('click', () => { addCategoryModalOverlay.style.display = 'none'; document.body.style.overflow = ''; });
  if (addCategoryModalOverlay) addCategoryModalOverlay.addEventListener('click', (e) => { if (e.target === addCategoryModalOverlay) { addCategoryModalOverlay.style.display = 'none'; document.body.style.overflow = ''; } });

  function wireModalDropdown(selectId, dropdownId, hiddenSelectId){ const select = document.getElementById(selectId); const dropdown = document.getElementById(dropdownId); const hidden = document.getElementById(hiddenSelectId); if(!select || !dropdown || !hidden) return; select.addEventListener('click', (e) => { e.stopPropagation(); toggleShipDropdown(dropdown); }); dropdown.addEventListener('click', (e) => { const item = e.target.closest('.ship-dropdown-item'); if(!item) return; e.stopPropagation(); const val = item.dataset.value; select.textContent = val; select.dataset.currentValue = val; hidden.value = val; dropdown.querySelectorAll('.ship-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display = 'none'; }); }
  wireModalDropdown('addShipmentTypeSelect','addShipmentTypeDropdown','addShipmentType');
  wireModalDropdown('addStatusSelect','addStatusDropdown','addStatus');

  if (addCategoryForm) addCategoryForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const data = { shipmentNumber: document.getElementById('addShipmentNumber').value, shipmentType: document.getElementById('addShipmentType').value, supplier: document.getElementById('addSupplier').value, origin: document.getElementById('addOrigin').value, destination: document.getElementById('addDestination').value, status: document.getElementById('addStatus').value, expected: document.getElementById('addExpected').value, actual: document.getElementById('addActual').value, totalValue: document.getElementById('addTotalValue').value };
    const totalValueFormatted = (parseFloat(data.totalValue || '0') || 0).toLocaleString(undefined,{minimumFractionDigits:2, maximumFractionDigits:2});
    const newRow = document.createElement('tr');
    newRow.innerHTML = `<td><span class="label">${data.shipmentNumber}</span></td><td><span class="label">${data.shipmentType}</span></td><td><span class="label">${data.supplier || '-'}</span></td><td><span class="label">${data.origin}</span></td><td><span class="label">${data.destination}</span></td><td><span class="label">${data.status}</span></td><td><span class="label">${data.expected || '-'}</span></td><td><span class="label">${data.actual || '-'}</span></td><td><span class="label">${totalValueFormatted}</span></td><td><button class="btn-edit-label edit-btn">Edit Shipment</button></td>`;
    applyBadges(newRow); tbody.appendChild(newRow); if (typeof filterRows === 'function' && searchInput && searchInput.value.trim() !== '') { filterRows(); }
  });

  function enhanceSelect(selectEl){ if (!selectEl) return; const wrapper = selectEl.closest('.select-wrapper'); const expand = () => { const visibleCount = Math.min(6, selectEl.options?.length || 6); selectEl.setAttribute('size', String(visibleCount)); if (wrapper) wrapper.classList.add('expanded'); }; const collapse = () => { selectEl.removeAttribute('size'); if (wrapper) wrapper.classList.remove('expanded'); }; selectEl.addEventListener('focus', expand); selectEl.addEventListener('blur', collapse); selectEl.addEventListener('change', () => { setTimeout(collapse, 0); }); selectEl.addEventListener('keydown', (e) => { if (e.key === 'Escape') { collapse(); selectEl.blur(); } }); }
  enhanceSelect(document.getElementById('addShipmentType')); enhanceSelect(document.getElementById('addStatus'));
}); 