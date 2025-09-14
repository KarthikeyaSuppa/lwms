document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("tbody");

  function makeRowEditable(row){
    const cells = row.querySelectorAll('td:not(:last-child)');
    cells.forEach((cell, index) => {
      const label = cell.querySelector('.label');
      if (index === 0){
        const currentValue = label.textContent.trim();
        const container = document.createElement('div'); container.className = 'item-cell-container';
        const select = document.createElement('div'); select.className = 'item-select'; select.textContent = currentValue; select.dataset.currentValue = currentValue;
        const dropdown = document.createElement('div'); dropdown.className = 'item-dropdown';
        ["SHIP001","SHIP002","SHIP003","SHIP004"].forEach(v => { const item = document.createElement('div'); item.className = `item-dropdown-item ${v===currentValue?'selected':''}`; item.textContent = v; item.dataset.value = v; dropdown.appendChild(item); });
        select.addEventListener('click', (e)=>{ e.stopPropagation(); toggleItemDropdown(dropdown); });
        dropdown.addEventListener('click', (e)=>{ const item = e.target.closest('.item-dropdown-item'); if(!item) return; e.stopPropagation(); select.textContent = item.dataset.value; select.dataset.currentValue = item.dataset.value; dropdown.querySelectorAll('.item-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display = 'none'; });
        container.appendChild(select); container.appendChild(dropdown); cell.innerHTML = ''; cell.appendChild(container);
      }
      if (index === 1){
        const currentValue = label.textContent.trim();
        const container = document.createElement('div'); container.className = 'item-cell-container';
        const select = document.createElement('div'); select.className = 'item-select'; select.textContent = currentValue; select.dataset.currentValue = currentValue;
        const dropdown = document.createElement('div'); dropdown.className = 'item-dropdown';
        ["ITEM001","ITEM002","ITEM003","ITEM004","ITEM005"].forEach(v => { const item = document.createElement('div'); item.className = `item-dropdown-item ${v===currentValue?'selected':''}`; item.textContent = v; item.dataset.value = v; dropdown.appendChild(item); });
        select.addEventListener('click', (e)=>{ e.stopPropagation(); toggleItemDropdown(dropdown); });
        dropdown.addEventListener('click', (e)=>{ const item = e.target.closest('.item-dropdown-item'); if(!item) return; e.stopPropagation(); select.textContent = item.dataset.value; select.dataset.currentValue = item.dataset.value; dropdown.querySelectorAll('.item-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display = 'none'; });
        container.appendChild(select); container.appendChild(dropdown); cell.innerHTML = ''; cell.appendChild(container);
      }
      if (label && index === 2){ const input = document.createElement('input'); input.type = 'number'; input.className = 'editable-input'; input.min = '1'; input.value = label.textContent.trim(); cell.innerHTML = ''; cell.appendChild(input); }
      if (label && index === 3){ const input = document.createElement('input'); input.type = 'number'; input.className = 'editable-input'; input.step = '0.01'; input.min = '0'; input.value = label.textContent.trim().replace('$',''); cell.innerHTML=''; cell.appendChild(input); }
      if (label && index === 4){
        const q = row.querySelectorAll('input[type="number"]')[0];
        const p = row.querySelectorAll('input[type="number"]')[1];
        const update = ()=>{ const quantity = parseFloat(q?.value||0); const unitPrice = parseFloat(p?.value||0); const total = quantity*unitPrice; label.textContent = "$"+ total.toLocaleString('en-US',{minimumFractionDigits:2, maximumFractionDigits:2}); };
        q && q.addEventListener('input', update); p && p.addEventListener('input', update);
      }
    });
  }

  function saveRowChanges(row){
    const cells = row.querySelectorAll('td');
    cells.forEach((cell, index) => {
      if (index === 0 || index === 1){ const dd = cell.querySelector('.item-select'); if (dd){ const label = document.createElement('span'); label.className='label'; label.textContent = dd.dataset.currentValue || dd.textContent; cell.innerHTML=''; cell.appendChild(label);} }
      else if (index === 2){ const input = cell.querySelector('.editable-input'); if (input){ const label = document.createElement('span'); label.className='label'; label.textContent = input.value; cell.innerHTML=''; cell.appendChild(label);} }
      else if (index === 3){ const input = cell.querySelector('.editable-input'); if (input){ const label = document.createElement('span'); label.className='label'; const v = parseFloat(input.value||'0'); label.textContent = "$"+ v.toLocaleString('en-US',{minimumFractionDigits:2, maximumFractionDigits:2}); cell.innerHTML=''; cell.appendChild(label);} }
      else if (index === 4){ /* keep computed total */ }
      else if (index === 5){ const actionsCell = row.querySelector('td:last-child'); actionsCell.innerHTML = '<button class="btn-edit-label edit-btn">Edit Item</button>'; }
    });
  }

  function deleteRow(row){ if (confirm('Are you sure you want to delete this shipment item?')) row.remove(); }

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

  if (addShipmentItemForm) addShipmentItemForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const quantity = parseFloat(document.getElementById('addQuantity').value);
    const unitPrice = parseFloat(document.getElementById('addUnitPrice').value);
    const totalPrice = quantity * unitPrice;
    const data = { shipmentId: document.getElementById('addShipmentId').value, itemId: document.getElementById('addItemId').value, quantity, unitPrice, totalPrice };
    const newRow = document.createElement('tr');
    const fmt = (n)=>"$"+ (n||0).toLocaleString('en-US',{minimumFractionDigits:2, maximumFractionDigits:2});
    newRow.innerHTML = `<td><span class="label">${data.shipmentId}</span></td><td><span class="label">${data.itemId}</span></td><td><span class="label">${data.quantity}</span></td><td><span class="label">${fmt(data.unitPrice)}</span></td><td><span class="label">${fmt(data.totalPrice)}</span></td><td><button class="btn-edit-label edit-btn">Edit Item</button></td>`;
    tbody.appendChild(newRow);
    if (typeof filterRows === 'function' && searchInput && searchInput.value.trim() !== '') filterRows();
    addShipmentItemModalOverlay.style.display = 'none';
    document.body.style.overflow = '';
    addShipmentItemForm.reset();
  });
}); 