document.addEventListener('DOMContentLoaded', () => {
  const tbody = document.querySelector('tbody');
  const addMaintenanceIcon = document.getElementById('addMaintenanceIcon');
  const addMaintenanceModalOverlay = document.getElementById('addMaintenanceModalOverlay');
  const closeAddMaintenanceModal = document.getElementById('closeAddMaintenanceModal');
  const addMaintenanceForm = document.getElementById('addMaintenanceForm');
  const searchInput = document.getElementById('searchInput');
  const searchIcon = document.getElementById('searchIcon');

  function toDatetimeLocal(text){ if(!text || text === '-') return ''; return text.replace(' ', 'T'); }

  function toggleMsDropdown(dropdown){ document.querySelectorAll('.ms-dropdown').forEach(dd => { if(dd !== dropdown) dd.style.display = 'none'; }); dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block'; }
  document.addEventListener('click', (e) => { if(!e.target.closest('.ms-cell-container')){ document.querySelectorAll('.ms-dropdown').forEach(dd => dd.style.display = 'none'); } });

  function applyBadges(row){
    const typeCell = row.cells[2]; const priorityCell = row.cells[3]; const statusCell = row.cells[7];
    const map = [
      {cell:typeCell, clsMap:{'preventive':'badge-preventive','corrective':'badge-corrective','predictive':'badge-predictive'}},
      {cell:priorityCell, clsMap:{'low':'badge-priority-low','medium':'badge-priority-medium','high':'badge-priority-high','critical':'badge-priority-critical'}},
      {cell:statusCell, clsMap:{'scheduled':'badge-status-scheduled','in progress':'badge-status-inprogress','completed':'badge-status-completed','cancelled':'badge-status-cancelled'}},
    ];
    map.forEach(({cell, clsMap}) => { if(!cell) return; const span = cell.querySelector('.label'); if(!span) return; Object.values(clsMap).forEach(c => span.classList.remove(c)); const key = (span.textContent||'').trim().toLowerCase(); if(clsMap[key]) span.classList.add(clsMap[key]); });
  }
  tbody.querySelectorAll('tr').forEach(applyBadges);

  function makeRowEditable(row){
    const cells = row.querySelectorAll('td:not(:last-child)');
    cells.forEach((cell, index) => {
      const label = cell.querySelector('.label'); if(!label) return; const val = label.textContent.trim();
      switch(index){
        case 0: case 6: { cell.innerHTML=''; const input=document.createElement('input'); input.type='text'; input.className='editable-input'; input.value=val; cell.appendChild(input); break; }
        case 1: { cell.innerHTML=''; const input=document.createElement('input'); input.type='text'; input.className='editable-input'; input.style.maxWidth='320px'; input.value=val; cell.appendChild(input); break; }
        case 2: { cell.innerHTML=''; const container=document.createElement('div'); container.className='ms-cell-container'; const select=document.createElement('div'); select.className='ms-select'; select.textContent=val; select.dataset.currentValue=val; const dd=document.createElement('div'); dd.className='ms-dropdown'; ['Preventive','Corrective','Predictive'].forEach(v=>{ const it=document.createElement('div'); it.className=`ms-dropdown-item ${v===val?'selected':''}`; it.textContent=v; it.dataset.value=v; dd.appendChild(it); }); select.addEventListener('click',(e)=>{ e.stopPropagation(); toggleMsDropdown(dd); }); dd.addEventListener('click',(e)=>{ const it=e.target.closest('.ms-dropdown-item'); if(!it) return; e.stopPropagation(); select.textContent=it.dataset.value; select.dataset.currentValue=it.dataset.value; dd.querySelectorAll('.ms-dropdown-item').forEach(x=>x.classList.remove('selected')); it.classList.add('selected'); dd.style.display='none'; }); container.appendChild(select); container.appendChild(dd); cell.appendChild(container); break; }
        case 3: { cell.innerHTML=''; const container=document.createElement('div'); container.className='ms-cell-container'; const select=document.createElement('div'); select.className='ms-select'; select.textContent=val; select.dataset.currentValue=val; const dd=document.createElement('div'); dd.className='ms-dropdown'; ['Low','Medium','High','Critical'].forEach(v=>{ const it=document.createElement('div'); it.className=`ms-dropdown-item ${v===val?'selected':''}`; it.textContent=v; it.dataset.value=v; dd.appendChild(it); }); select.addEventListener('click',(e)=>{ e.stopPropagation(); toggleMsDropdown(dd); }); dd.addEventListener('click',(e)=>{ const it=e.target.closest('.ms-dropdown-item'); if(!it) return; e.stopPropagation(); select.textContent=it.dataset.value; select.dataset.currentValue=it.dataset.value; dd.querySelectorAll('.ms-dropdown-item').forEach(x=>x.classList.remove('selected')); it.classList.add('selected'); dd.style.display='none'; }); container.appendChild(select); container.appendChild(dd); cell.appendChild(container); break; }
        case 4: case 8: { cell.innerHTML=''; const i=document.createElement('input'); i.type='datetime-local'; i.className='editable-input'; i.value=toDatetimeLocal(val); cell.appendChild(i); break; }
        case 5: case 9: { cell.innerHTML=''; const n=document.createElement('input'); n.type='number'; n.min='0'; n.step='1'; n.className='editable-input'; n.value=val||''; cell.appendChild(n); break; }
        case 7: { cell.innerHTML=''; const container=document.createElement('div'); container.className='ms-cell-container'; const select=document.createElement('div'); select.className='ms-select'; select.textContent=val; select.dataset.currentValue=val; const dd=document.createElement('div'); dd.className='ms-dropdown'; ['Scheduled','In Progress','Completed','Cancelled'].forEach(v=>{ const it=document.createElement('div'); it.className=`ms-dropdown-item ${v===val?'selected':''}`; it.textContent=v; it.dataset.value=v; dd.appendChild(it); }); select.addEventListener('click',(e)=>{ e.stopPropagation(); toggleMsDropdown(dd); }); dd.addEventListener('click',(e)=>{ const it=e.target.closest('.ms-dropdown-item'); if(!it) return; e.stopPropagation(); select.textContent=it.dataset.value; select.dataset.currentValue=it.dataset.value; dd.querySelectorAll('.ms-dropdown-item').forEach(x=>x.classList.remove('selected')); it.classList.add('selected'); dd.style.display='none'; }); container.appendChild(select); container.appendChild(dd); cell.appendChild(container); break; }
        case 10: { cell.innerHTML=''; const n=document.createElement('input'); n.type='number'; n.step='0.01'; n.min='0'; n.className='editable-input'; n.value=(val||'0').replace(/,/g,''); cell.appendChild(n); break; }
        case 11: { cell.innerHTML=''; const t=document.createElement('textarea'); t.className='editable-input'; t.rows=2; t.value=val; t.style.maxWidth='340px'; cell.appendChild(t); break; }
      }
    });
  }

  function saveRowChanges(row){
    const cells = row.querySelectorAll('td');
    cells.forEach((cell, index) => {
      if(index===0 || index===6){ const i=cell.querySelector('.editable-input'); if(i){ const s=document.createElement('span'); s.className='label'; s.textContent=i.value; cell.innerHTML=''; cell.appendChild(s);} }
      else if(index===1){ const i=cell.querySelector('.editable-input'); if(i){ const s=document.createElement('span'); s.className='label'; s.textContent=i.value; cell.innerHTML=''; cell.appendChild(s);} }
      else if(index===2 || index===3 || index===7){ const dd=cell.querySelector('.ms-select'); if(dd){ const s=document.createElement('span'); s.className='label'; s.textContent= dd.dataset.currentValue || dd.textContent; cell.innerHTML=''; cell.appendChild(s);} }
      else if(index===4 || index===8){ const i=cell.querySelector('.editable-input'); if(i){ const s=document.createElement('span'); s.className='label'; s.textContent= i.value ? i.value.replace('T',' ') : '-'; cell.innerHTML=''; cell.appendChild(s);} }
      else if(index===5 || index===9){ const i=cell.querySelector('.editable-input'); if(i){ const s=document.createElement('span'); s.className='label'; s.textContent= i.value || '-'; cell.innerHTML=''; cell.appendChild(s);} }
      else if(index===10){ const i=cell.querySelector('.editable-input'); if(i){ const s=document.createElement('span'); s.className='label'; const num=parseFloat(i.value||'0'); s.textContent = isNaN(num)?'0.00': num.toLocaleString(undefined,{minimumFractionDigits:2,maximumFractionDigits:2}); cell.innerHTML=''; cell.appendChild(s);} }
      else if(index===11){ const t=cell.querySelector('textarea.editable-input'); if(t){ const s=document.createElement('span'); s.className='label'; s.textContent=t.value || '-'; cell.innerHTML=''; cell.appendChild(s);} }
      else if(index===12){ const actionsCell = row.querySelector('td:last-child'); actionsCell.innerHTML = '<button class="btn-edit-label edit-btn">Edit</button>'; }
    });
    applyBadges(row);
  }

  function deleteRow(row){ if(confirm('Delete this maintenance task?')) row.remove(); }

  tbody.addEventListener('click', (e) => {
    const target = e.target; const row = target.closest('tr'); if(!row) return;
    if(target.classList.contains('edit-btn')){
      const actionsCell = row.querySelector('td:last-child');
      actionsCell.innerHTML = '<div class="action-icons-container"><img src="/images/correct.png" class="action-icon save-btn" alt="Save"><img src="/images/trash.png" class="action-icon delete-btn" alt="Delete"></div>';
      makeRowEditable(row);
    }
    if(target.classList.contains('save-btn')) saveRowChanges(row);
    if(target.classList.contains('delete-btn')) deleteRow(row);
  });

  function normalize(text){ return (text||'').toString().toLowerCase().trim(); }
  function filterRows(){ const q=normalize(searchInput.value); const rows=tbody.querySelectorAll('tr'); rows.forEach(row=>{ const cells=row.querySelectorAll('td'); const values=Array.from(cells).slice(0,12).map(c=> normalize(c.querySelector('.editable-input')?.value || c.textContent)); row.style.display = values.some(v=>v.includes(q)) ? '' : 'none'; }); }
  if(searchInput) searchInput.addEventListener('input', filterRows);
  if(searchIcon) searchIcon.addEventListener('click', filterRows);

  function wireModalDropdown(selectId, dropdownId, hiddenId){ const select=document.getElementById(selectId); const dropdown=document.getElementById(dropdownId); const hidden=document.getElementById(hiddenId); if(!select || !dropdown || !hidden) return; select.addEventListener('click',(e)=>{ e.stopPropagation(); toggleMsDropdown(dropdown); }); dropdown.addEventListener('click',(e)=>{ const item=e.target.closest('.ms-dropdown-item'); if(!item) return; e.stopPropagation(); const val=item.dataset.value; select.textContent=val; select.dataset.currentValue=val; hidden.value=val; dropdown.querySelectorAll('.ms-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display='none'; }); }
  wireModalDropdown('addMaintenanceTypeSelect','addMaintenanceTypeDropdown','addMaintenanceType');
  wireModalDropdown('addPrioritySelect','addPriorityDropdown','addPriority');
  wireModalDropdown('addStatusSelect','addStatusDropdown','addStatus');

  if (addMaintenanceIcon) addMaintenanceIcon.addEventListener('click', ()=>{ addMaintenanceModalOverlay.style.display='flex'; document.body.style.overflow='hidden'; });
  if (closeAddMaintenanceModal) closeAddMaintenanceModal.addEventListener('click', ()=>{ addMaintenanceModalOverlay.style.display='none'; document.body.style.overflow=''; });
  if (addMaintenanceModalOverlay) addMaintenanceModalOverlay.addEventListener('click', (e)=>{ if(e.target===addMaintenanceModalOverlay){ addMaintenanceModalOverlay.style.display='none'; document.body.style.overflow=''; } });

  if (addMaintenanceForm) addMaintenanceForm.addEventListener('submit', (e)=>{
    e.preventDefault();
    const d = { equipmentId: document.getElementById('addEquipmentId').value, taskDescription: document.getElementById('addTaskDescription').value, maintenanceType: document.getElementById('addMaintenanceType').value, priority: document.getElementById('addPriority').value, scheduledDate: document.getElementById('addScheduledDate').value, estimatedDuration: document.getElementById('addEstimatedDuration').value, assignedTo: document.getElementById('addAssignedTo').value, status: document.getElementById('addStatus').value, completedDate: document.getElementById('addCompletedDate').value, actualDuration: document.getElementById('addActualDuration').value, cost: document.getElementById('addCost').value, notes: document.getElementById('addNotes').value };
    const newRow = document.createElement('tr');
    const costFormatted = (parseFloat(d.cost||'0')||0).toLocaleString(undefined,{minimumFractionDigits:2, maximumFractionDigits:2});
    newRow.innerHTML = `<td><span class="label">${d.equipmentId}</span></td><td><span class="label">${d.taskDescription}</span></td><td><span class="label">${d.maintenanceType}</span></td><td><span class="label">${d.priority}</span></td><td><span class="label">${d.scheduledDate ? d.scheduledDate.replace('T',' ') : '-'}</span></td><td><span class="label">${d.estimatedDuration || '-'}</span></td><td><span class="label">${d.assignedTo || '-'}</span></td><td><span class="label">${d.status}</span></td><td><span class="label">${d.completedDate ? d.completedDate.replace('T',' ') : '-'}</span></td><td><span class="label">${d.actualDuration || '-'}</span></td><td><span class="label">${costFormatted}</span></td><td><span class="label">${d.notes || '-'}</span></td><td><button class="btn-edit-label edit-btn">Edit</button></td>`;
    tbody.appendChild(newRow);
    applyBadges(newRow);
    if (typeof filterRows === 'function' && searchInput && searchInput.value.trim()!=='') filterRows();
    addMaintenanceModalOverlay.style.display='none'; document.body.style.overflow=''; addMaintenanceForm.reset();
  });
}); 