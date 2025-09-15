document.addEventListener('DOMContentLoaded', () => {
  const tbody = document.querySelector('tbody');

  function makeRowEditable(row){
    const cells = row.querySelectorAll('td:not(:last-child)');
    cells.forEach((cell, index) => {
      const label = cell.querySelector('.label');
      if (index === 0){
        const currentValue = label.textContent.trim();
        const container = document.createElement('div'); container.className='inv-cell-container';
        const select = document.createElement('div'); select.className='inv-select'; select.textContent=currentValue; select.dataset.currentValue=currentValue;
        const dropdown = document.createElement('div'); dropdown.className='inv-dropdown';
        ["ITEM001","ITEM002","ITEM003","ITEM004","ITEM005"].forEach(v=>{ const item=document.createElement('div'); item.className=`inv-dropdown-item ${v===currentValue?'selected':''}`; item.textContent=v; item.dataset.value=v; dropdown.appendChild(item); });
        select.addEventListener('click',(e)=>{ e.stopPropagation(); toggleInvDropdown(dropdown); });
        dropdown.addEventListener('click',(e)=>{ const item=e.target.closest('.inv-dropdown-item'); if(!item) return; e.stopPropagation(); select.textContent=item.dataset.value; select.dataset.currentValue=item.dataset.value; dropdown.querySelectorAll('.inv-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display='none'; });
        container.appendChild(select); container.appendChild(dropdown); cell.innerHTML=''; cell.appendChild(container);
      }
      if (index === 1){
        const currentValue = label.textContent.trim();
        const container = document.createElement('div'); container.className='inv-cell-container';
        const select = document.createElement('div'); select.className='inv-select'; select.textContent=currentValue; select.dataset.currentValue=currentValue;
        const dropdown = document.createElement('div'); dropdown.className='inv-dropdown';
        ["IN","OUT","TRANSFER","ADJUSTMENT"].forEach(v=>{ const item=document.createElement('div'); item.className=`inv-dropdown-item ${v===currentValue?'selected':''}`; item.textContent=v; item.dataset.value=v; dropdown.appendChild(item); });
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
        ["Shipment","Transfer","Adjustment","Sale"].forEach(v=>{ const item=document.createElement('div'); item.className=`inv-dropdown-item ${v===currentValue?'selected':''}`; item.textContent=v; item.dataset.value=v; dropdown.appendChild(item); });
        select.addEventListener('click',(e)=>{ e.stopPropagation(); toggleInvDropdown(dropdown); });
        dropdown.addEventListener('click',(e)=>{ const item=e.target.closest('.inv-dropdown-item'); if(!item) return; e.stopPropagation(); select.textContent=item.dataset.value; select.dataset.currentValue=item.dataset.value; dropdown.querySelectorAll('.inv-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display='none'; });
        container.appendChild(select); container.appendChild(dropdown); cell.innerHTML=''; cell.appendChild(container);
      }
      if (label && index === 6){ const input=document.createElement('input'); input.type='text'; input.className='editable-input'; input.value=label.textContent.trim(); cell.innerHTML=''; cell.appendChild(input); }
      if (label && index === 7){ const textarea=document.createElement('textarea'); textarea.className='editable-input'; textarea.rows=2; textarea.value=label.textContent.trim(); cell.innerHTML=''; cell.appendChild(textarea); }
    });
  }

  function saveRowChanges(row){
    const cells=row.querySelectorAll('td');
    cells.forEach((cell, index) => {
      if (index === 0 || index === 1 || index === 5){ const dd=cell.querySelector('.inv-select'); if(dd){ const label=document.createElement('span'); label.className='label'; label.textContent=dd.dataset.currentValue || dd.textContent; cell.innerHTML=''; cell.appendChild(label);} }
      else if (index === 2 || index === 3 || index === 4 || index === 6){ const input=cell.querySelector('.editable-input'); if(input){ const label=document.createElement('span'); label.className='label'; label.textContent=input.value; cell.innerHTML=''; cell.appendChild(label);} }
      else if (index === 7){ const textarea=cell.querySelector('textarea.editable-input'); if(textarea){ const label=document.createElement('span'); label.className='label'; label.textContent=textarea.value; cell.innerHTML=''; cell.appendChild(label);} }
      else if (index === 8){ const actionsCell=row.querySelector('td:last-child'); actionsCell.innerHTML='<button class="btn-edit-label edit-btn">Edit Movement</button>'; }
    });
  }

  function deleteRow(row){ if(confirm('Are you sure you want to delete this movement?')) row.remove(); }

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
  if (searchInput) searchInput.addEventListener('input', filterRows);
  if (searchIcon) searchIcon.addEventListener('click', filterRows);

  if (addMovementIcon) addMovementIcon.addEventListener('click', ()=>{ addMovementModalOverlay.style.display='flex'; document.body.style.overflow='hidden'; });
  if (closeAddMovementModal) closeAddMovementModal.addEventListener('click', ()=>{ addMovementModalOverlay.style.display='none'; document.body.style.overflow=''; });
  if (addMovementModalOverlay) addMovementModalOverlay.addEventListener('click', (e)=>{ if(e.target===addMovementModalOverlay){ addMovementModalOverlay.style.display='none'; document.body.style.overflow=''; } });

  function wireInvModalDropdown(selectId, dropdownId, hiddenId){ const select=document.getElementById(selectId); const dropdown=document.getElementById(dropdownId); const hidden=document.getElementById(hiddenId); if(!select || !dropdown || !hidden) return; select.addEventListener('click', (e)=>{ e.stopPropagation(); toggleInvDropdown(dropdown); }); dropdown.addEventListener('click', (e)=>{ const item=e.target.closest('.inv-dropdown-item'); if(!item) return; e.stopPropagation(); const val=item.dataset.value; select.textContent=val; select.dataset.currentValue=val; hidden.value=val; dropdown.querySelectorAll('.inv-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display='none'; }); }
  wireInvModalDropdown('addItemIdSelect','addItemIdDropdown','addItemId');
  wireInvModalDropdown('addMovementTypeSelect','addMovementTypeDropdown','addMovementType');
  wireInvModalDropdown('addReferenceTypeSelect','addReferenceTypeDropdown','addReferenceType');

  if (addMovementForm) addMovementForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const data = { itemId: document.getElementById('addItemId').value, movementType: document.getElementById('addMovementType').value, quantity: document.getElementById('addQuantity').value, fromLocation: document.getElementById('addFromLocation').value, toLocation: document.getElementById('addToLocation').value, referenceType: document.getElementById('addReferenceType').value, referenceId: document.getElementById('addReferenceId').value, notes: document.getElementById('addNotes').value };
    const newRow = document.createElement('tr');
    newRow.innerHTML = `<td><span class="label">${data.itemId}</span></td><td><span class="label">${data.movementType}</span></td><td><span class="label">${data.quantity}</span></td><td><span class="label">${data.fromLocation || '-'}</span></td><td><span class="label">${data.toLocation || '-'}</span></td><td><span class="label">${data.referenceType}</span></td><td><span class="label">${data.referenceId}</span></td><td><span class="label">${data.notes || '-'}</span></td><td><button class="btn-edit-label edit-btn">Edit Movement</button></td>`;
    tbody.appendChild(newRow);
    if (typeof filterRows === 'function' && searchInput && searchInput.value.trim() !== '') filterRows();
    addMovementModalOverlay.style.display='none'; document.body.style.overflow=''; addMovementForm.reset();
  });
}); 