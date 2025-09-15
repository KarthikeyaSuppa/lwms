document.addEventListener('DOMContentLoaded', () => {
  const tbody = document.querySelector('tbody');
  const API_BASE = '/inventory-movements/api'; // also /lwms/inventory-movements/api

  function renderRows(list){
    tbody.innerHTML = '';
    list.forEach(m => {
      const tr = document.createElement('tr');
      tr.dataset.id = m.movementId;
      tr.innerHTML = `
        <td><span class="label">${m.itemCode ?? ''}</span></td>
        <td><span class="label">${m.movementType ?? ''}</span></td>
        <td><span class="label">${m.quantity ?? 0}</span></td>
        <td><span class="label">${m.fromLocation ?? '-'}</span></td>
        <td><span class="label">${m.toLocation ?? '-'}</span></td>
        <td><span class="label">${m.referenceType ?? ''}</span></td>
        <td><span class="label">${m.referenceId ?? ''}</span></td>
        <td><span class="label">${m.notes ?? '-'}</span></td>
        <td><button class="btn-edit-label edit-btn">Edit Movement</button></td>`;
      tbody.appendChild(tr);
    });
  }

  async function loadMovements(){
    const q = document.getElementById('searchInput')?.value?.trim();
    const url = q ? `${API_BASE}?q=${encodeURIComponent(q)}` : API_BASE;
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) { console.error('Failed to load inventory movements'); return; }
    const data = await res.json();
    renderRows(Array.isArray(data) ? data : []);
  }

  function makeRowEditable(row){
    const cells = row.querySelectorAll('td:not(:last-child)');
    cells.forEach((cell, index) => {
      const label = cell.querySelector('.label');
      if (index === 0){
        const currentValue = label.textContent.trim();
        const container = document.createElement('div'); container.className='inv-cell-container';
        const select = document.createElement('div'); select.className='inv-select'; select.textContent=currentValue; select.dataset.currentValue=currentValue;
        const dropdown = document.createElement('div'); dropdown.className='inv-dropdown';
        [currentValue].forEach(v=>{ const item=document.createElement('div'); item.className=`inv-dropdown-item selected`; item.textContent=v; item.dataset.value=v; dropdown.appendChild(item); });
        select.addEventListener('click',(e)=>{ e.stopPropagation(); toggleInvDropdown(dropdown); });
        dropdown.addEventListener('click',(e)=>{ const item=e.target.closest('.inv-dropdown-item'); if(!item) return; e.stopPropagation(); select.textContent=item.dataset.value; select.dataset.currentValue=item.dataset.value; dropdown.querySelectorAll('.inv-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display='none'; });
        container.appendChild(select); container.appendChild(dropdown); cell.innerHTML=''; cell.appendChild(container);
      }
      if (index === 1){
        const currentValue = label.textContent.trim();
        const container = document.createElement('div'); container.className='inv-cell-container';
        const select = document.createElement('div'); select.className='inv-select'; select.textContent=currentValue; select.dataset.currentValue=currentValue;
        const dropdown = document.createElement('div'); dropdown.className='inv-dropdown';
        ['IN','OUT','TRANSFER','ADJUSTMENT'].forEach(v=>{ const item=document.createElement('div'); item.className=`inv-dropdown-item ${v===currentValue?'selected':''}`; item.textContent=v; item.dataset.value=v; dropdown.appendChild(item); });
        select.addEventListener('click',(e)=>{ e.stopPropagation(); toggleInvDropdown(dropdown); });
        dropdown.addEventListener('click',(e)=>{ const item=e.target.closest('.inv-dropdown-item'); if(!item) return; e.stopPropagation(); select.textContent=item.dataset.value; select.dataset.currentValue=item.dataset.value; dropdown.querySelectorAll('.inv-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display='none'; });
        container.appendChild(select); container.appendChild(dropdown); cell.innerHTML=''; cell.appendChild(container);
      }
      if (label && index === 2){ const input=document.createElement('input'); input.type='number'; input.className='editable-input'; input.min='1'; input.value=label.textContent.trim(); cell.innerHTML=''; cell.appendChild(input); }
      if (label && index === 3){ const input=document.createElement('input'); input.type='text'; input.className='editable-input'; input.value=label.textContent.trim(); cell.innerHTML=''; cell.appendChild(input); }
      if (label && index === 4){ const input=document.createElement('input'); input.type='text'; input.className='editable-input'; input.value=label.textContent.trim(); cell.innerHTML=''; cell.appendChild(input); }
      if (index === 5){
        const currentValue = label.textContent.trim();
        const container = document.createElement('div'); container.className='inv-cell-container';
        const select = document.createElement('div'); select.className='inv-select'; select.textContent=currentValue; select.dataset.currentValue=currentValue;
        const dropdown = document.createElement('div'); dropdown.className='inv-dropdown';
        ['Shipment','Transfer','Adjustment','Sale'].forEach(v=>{ const item=document.createElement('div'); item.className=`inv-dropdown-item ${v===currentValue?'selected':''}`; item.textContent=v; item.dataset.value=v; dropdown.appendChild(item); });
        select.addEventListener('click',(e)=>{ e.stopPropagation(); toggleInvDropdown(dropdown); });
        dropdown.addEventListener('click',(e)=>{ const item=e.target.closest('.inv-dropdown-item'); if(!item) return; e.stopPropagation(); select.textContent=item.dataset.value; select.dataset.currentValue=item.dataset.value; dropdown.querySelectorAll('.inv-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display='none'; });
        container.appendChild(select); container.appendChild(dropdown); cell.innerHTML=''; cell.appendChild(container);
      }
      if (label && index === 6){ const input=document.createElement('input'); input.type='text'; input.className='editable-input'; input.value=label.textContent.trim(); cell.innerHTML=''; cell.appendChild(input); }
      if (label && index === 7){ const textarea=document.createElement('textarea'); textarea.className='editable-input'; textarea.rows=2; textarea.value=label.textContent.trim(); cell.innerHTML=''; cell.appendChild(textarea); }
    });
  }

  async function saveRowChanges(row){
    const id = row.dataset.id; if (!id) return;
    const cells=row.querySelectorAll('td');
    const itemCode = cells[0].querySelector('.inv-select')?.dataset?.currentValue || cells[0].textContent.trim();
    const movementType = cells[1].querySelector('.inv-select')?.dataset?.currentValue || cells[1].textContent.trim();
    const quantity = parseInt(cells[2].querySelector('.editable-input')?.value ?? '0', 10);
    const fromLocation = cells[3].querySelector('.editable-input')?.value?.trim() || '';
    const toLocation = cells[4].querySelector('.editable-input')?.value?.trim() || '';
    const referenceType = cells[5].querySelector('.inv-select')?.dataset?.currentValue || cells[5].textContent.trim();
    const referenceId = cells[6].querySelector('.editable-input')?.value?.trim() || '';
    const notes = cells[7].querySelector('textarea.editable-input')?.value?.trim() || '';

    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, {
      method: 'PATCH', headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify({ itemCode, movementType, quantity, fromLocation, toLocation, referenceType, referenceId, notes })
    });
    if (!res.ok) { const err = await res.text(); alert(`Failed to update movement: ${err || res.status}`); return; }

    const updated = await res.json();
    cells[0].innerHTML = `<span class="label">${updated.itemCode ?? itemCode}</span>`;
    cells[1].innerHTML = `<span class=\"label\">${updated.movementType ?? movementType}</span>`;
    cells[2].innerHTML = `<span class=\"label\">${updated.quantity ?? quantity}</span>`;
    cells[3].innerHTML = `<span class=\"label\">${(updated.fromLocation ?? fromLocation) || '-'}</span>`;
    cells[4].innerHTML = `<span class=\"label\">${(updated.toLocation ?? toLocation) || '-'}</span>`;
    cells[5].innerHTML = `<span class=\"label\">${updated.referenceType ?? referenceType}</span>`;
    cells[6].innerHTML = `<span class=\"label\">${updated.referenceId ?? referenceId}</span>`;
    cells[7].innerHTML = `<span class=\"label\">${(updated.notes ?? notes) || '-'}</span>`;
    const actionsCell=row.querySelector('td:last-child'); actionsCell.innerHTML='<button class="btn-edit-label edit-btn">Edit Movement</button>';
  }

  async function deleteRow(row){ if(!confirm('Are you sure you want to delete this movement?')) return; const id=row.dataset.id; if(!id){ row.remove(); return; } const res=await fetch(`${API_BASE}/${encodeURIComponent(id)}`, { method:'DELETE' }); if(!res.ok){ const err=await res.text(); alert(`Failed to delete movement: ${err || res.status}`); return; } row.remove(); }

  tbody.addEventListener('click', (e) => {
    const target=e.target; const row=target.closest('tr'); if(!row) return;
    if (target.classList.contains('edit-btn')){ const actionsCell=row.querySelector('td:last-child'); actionsCell.innerHTML='<div class="action-icons-container"><img src="/images/correct.png" class="action-icon save-btn" alt="Save"><img src="/images/trash.png" class="action-icon delete-btn" alt="Delete"></div>'; makeRowEditable(row); }
    if (target.classList.contains('save-btn')) saveRowChanges(row);
    if (target.classList.contains('delete-btn')) deleteRow(row);
  });

  function toggleInvDropdown(dropdown){ document.querySelectorAll('.inv-dropdown').forEach(dd => { if(dd !== dropdown) dd.style.display = 'none'; }); dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block'; }
  document.addEventListener('click', (e) => { if(!e.target.closest('.inv-cell-container')){ document.querySelectorAll('.inv-dropdown').forEach(dd => dd.style.display = 'none'); } });

  const addMovementIcon=document.getElementById('addMovementIcon');
  const addMovementModalOverlay=document.getElementById('addMovementModalOverlay');
  const closeAddMovementModal=document.getElementById('closeAddMovementModal');
  const addMovementForm=document.getElementById('addMovementForm');
  const searchInput=document.getElementById('searchInput');
  const searchIcon=document.getElementById('searchIcon');

  function normalize(text){ return (text||'').toString().toLowerCase().trim(); }
  function filterRows(){ const query=normalize(searchInput.value); const rows=tbody.querySelectorAll('tr'); rows.forEach(row=>{ const cells=row.querySelectorAll('td'); const values=[0,1,2,3,4,5,6,7].map(i => normalize(cells[i]?.querySelector('.editable-input')?.value || cells[i]?.textContent)); row.style.display = values.some(v => v.includes(query)) ? '' : 'none'; }); }
  if (searchInput) searchInput.addEventListener('input', () => { loadMovements(); });
  if (searchIcon) searchIcon.addEventListener('click', () => { loadMovements(); });

  if (addMovementIcon) addMovementIcon.addEventListener('click', ()=>{ addMovementModalOverlay.style.display='flex'; document.body.style.overflow='hidden'; });
  if (closeAddMovementModal) closeAddMovementModal.addEventListener('click', ()=>{ addMovementModalOverlay.style.display='none'; document.body.style.overflow=''; });
  if (addMovementModalOverlay) addMovementModalOverlay.addEventListener('click', (e)=>{ if(e.target===addMovementModalOverlay){ addMovementModalOverlay.style.display='none'; document.body.style.overflow=''; } });

  function wireInvModalDropdown(selectId, dropdownId, hiddenId){ const select=document.getElementById(selectId); const dropdown=document.getElementById(dropdownId); const hidden=document.getElementById(hiddenId); if(!select || !dropdown || !hidden) return; select.addEventListener('click', (e)=>{ e.stopPropagation(); toggleInvDropdown(dropdown); }); dropdown.addEventListener('click', (e)=>{ const item=e.target.closest('.inv-dropdown-item'); if(!item) return; e.stopPropagation(); const val=item.dataset.value; select.textContent=val; select.dataset.currentValue=val; hidden.value=val; dropdown.querySelectorAll('.inv-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display='none'; }); }
  wireInvModalDropdown('addItemIdSelect','addItemIdDropdown','addItemId');
  wireInvModalDropdown('addMovementTypeSelect','addMovementTypeDropdown','addMovementType');
  wireInvModalDropdown('addReferenceTypeSelect','addReferenceTypeDropdown','addReferenceType');

  if (addMovementForm) addMovementForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const payload = {
      itemCode: document.getElementById('addItemId').value,
      movementType: document.getElementById('addMovementType').value,
      quantity: parseInt(document.getElementById('addQuantity').value, 10),
      fromLocation: document.getElementById('addFromLocation').value,
      toLocation: document.getElementById('addToLocation').value,
      referenceType: document.getElementById('addReferenceType').value,
      referenceId: document.getElementById('addReferenceId').value,
      notes: document.getElementById('addNotes').value
    };
    const res = await fetch(API_BASE, { method: 'POST', headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' }, body: JSON.stringify(payload) });
    if (!res.ok) { const err = await res.text(); alert(`Failed to add movement: ${err || res.status}`); return; }
    const created = await res.json();
    const newRow = document.createElement('tr');
    newRow.dataset.id = created.movementId;
    newRow.innerHTML = `
      <td><span class="label">${created.itemCode ?? payload.itemCode}</span></td>
      <td><span class="label">${created.movementType ?? payload.movementType}</span></td>
      <td><span class="label">${created.quantity ?? payload.quantity}</span></td>
      <td><span class="label">${(created.fromLocation ?? payload.fromLocation) || '-'}</span></td>
      <td><span class="label">${(created.toLocation ?? payload.toLocation) || '-'}</span></td>
      <td><span class="label">${created.referenceType ?? payload.referenceType}</span></td>
      <td><span class="label">${created.referenceId ?? payload.referenceId}</span></td>
      <td><span class="label">${(created.notes ?? payload.notes) || '-'}</span></td>
      <td><button class="btn-edit-label edit-btn">Edit Movement</button></td>`;
    tbody.prepend(newRow);
    addMovementModalOverlay.style.display='none'; document.body.style.overflow=''; addMovementForm.reset();
  });

  // Initial load
  loadMovements();
}); 