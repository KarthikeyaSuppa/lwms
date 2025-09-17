document.addEventListener('DOMContentLoaded', () => {
  const tbody = document.querySelector('tbody');
  const addMaintenanceIcon = document.getElementById('addMaintenanceIcon');
  const addMaintenanceModalOverlay = document.getElementById('addMaintenanceModalOverlay');
  const closeAddMaintenanceModal = document.getElementById('closeAddMaintenanceModal');
  const addMaintenanceForm = document.getElementById('addMaintenanceForm');
  const searchInput = document.getElementById('searchInput');
  const searchIcon = document.getElementById('searchIcon');
  const API_BASE = '/maintenance-schedule/api'; // also /lwms/maintenance-schedule/api

  const equipSearchInput = document.getElementById('addEquipmentCodeSearch');
  const equipDropdown = document.getElementById('equipmentCodeDropdown');
  const equipHiddenId = document.getElementById('addEquipmentId');

  const assignedSearchInput = document.getElementById('addAssignedToSearch');
  const assignedDropdown = document.getElementById('assignedToDropdown');
  const assignedHiddenId = document.getElementById('addAssignedTo');

  function toDatetimeLocal(text){ if(!text || text === '-') return ''; return text.replace(' ', 'T'); }

  async function loadMaint(){
    const q = searchInput?.value?.trim();
    const url = q ? `${API_BASE}?q=${encodeURIComponent(q)}` : API_BASE;
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) { console.error('Failed to load maintenance'); return; }
    const data = await res.json();
    renderRows(Array.isArray(data) ? data : []);
  }

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

  function renderRows(list){
    tbody.innerHTML = '';
    list.forEach(ms => {
      const tr = document.createElement('tr');
      tr.dataset.id = ms.scheduleId;
      tr.innerHTML = `
        <td><span class="label">${ms.equipmentCode ?? ''}</span></td>
        <td><span class="label">${ms.taskDescription ?? ''}</span></td>
        <td><span class="label">${ms.maintenanceType ?? ''}</span></td>
        <td><span class="label">${ms.priority ?? ''}</span></td>
        <td><span class="label">${ms.scheduledDate ?? '-'}</span></td>
        <td><span class="label">${ms.estimatedDuration ?? '-'}</span></td>
        <td><span class="label">${ms.assignedTo ?? '-'}</span></td>
        <td><span class="label">${ms.status ?? ''}</span></td>
        <td><span class="label">${ms.completedDate ?? '-'}</span></td>
        <td><span class="label">${ms.actualDuration ?? '-'}</span></td>
        <td><span class="label">${ms.cost ?? '0.00'}</span></td>
        <td><span class="label">${ms.notes ?? '-'}</span></td>
        <td><button class="btn-edit-label edit-btn">Edit</button></td>`;
      tbody.appendChild(tr); applyBadges(tr);
    });
  }

  // Equipment code searchable (shared)
  async function searchEquipment(query){
    const url = query ? `/equipment/api?q=${encodeURIComponent(query)}` : '/equipment/api';
    try{
      const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
      if(!res.ok) return [];
      const data = await res.json();
      return Array.isArray(data) ? data : [];
    }catch{ return []; }
  }

  function makeRowEditable(row){
    const cells = row.querySelectorAll('td:not(:last-child)');
    cells.forEach((cell, index) => {
      const label = cell.querySelector('.label'); if(!label) return; const val = label.textContent.trim();
      switch(index){
        case 0: { 
          // Searchable equipment code with dropdown; stores selected id in dataset.id
          const container=document.createElement('div'); container.className='ms-cell-container';
          const input=document.createElement('input'); input.type='text'; input.className='editable-input equip-select'; input.value=val; input.placeholder='Search equipment...';
          const dd=document.createElement('div'); dd.className='ms-dropdown';
          async function populate(q){
            const items = await searchEquipment(q);
            dd.innerHTML='';
            items.forEach(eq => {
              const div=document.createElement('div'); div.className='ms-dropdown-item';
              const code = eq.serialNumber || eq.equipmentName || `ID-${eq.equipmentId}`;
              div.textContent = `${code} — ${eq.equipmentName ?? ''}`.trim();
              div.dataset.id = eq.equipmentId; div.dataset.label = code;
              dd.appendChild(div);
            });
            dd.style.display = items.length ? 'block' : 'none';
          }
          input.addEventListener('focus', async ()=>{ await populate(input.value.trim()); });
          input.addEventListener('input', async ()=>{ await populate(input.value.trim()); });
          input.addEventListener('click', (e)=>{ e.stopPropagation(); toggleMsDropdown(dd); });
          dd.addEventListener('click', (e)=>{ const it=e.target.closest('.ms-dropdown-item'); if(!it) return; e.stopPropagation(); input.value = it.dataset.label; input.dataset.id = it.dataset.id; dd.style.display='none'; });
          cell.innerHTML=''; container.appendChild(input); container.appendChild(dd); cell.appendChild(container);
          break; 
        }
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

  async function saveRowChanges(row){
    const id = row.dataset.id; if (!id) return;
    const cells = row.querySelectorAll('td');
    const equipInput = cells[0].querySelector('.equip-select');
    const equipmentId = equipInput && equipInput.dataset && equipInput.dataset.id ? parseInt(equipInput.dataset.id, 10) : undefined;
    const equipmentLabel = equipInput ? equipInput.value.trim() : cells[0].textContent.trim();
    const taskDescription = cells[1].querySelector('.editable-input')?.value?.trim() || cells[1].textContent.trim();
    const maintenanceType = cells[2].querySelector('.ms-select')?.dataset?.currentValue || cells[2].textContent.trim();
    const priority = cells[3].querySelector('.ms-select')?.dataset?.currentValue || cells[3].textContent.trim();
    const scheduledDate = cells[4].querySelector('.editable-input')?.value?.trim() || '';
    const estimatedDuration = parseInt(cells[5].querySelector('.editable-input')?.value ?? '0', 10);
    const assignedToRaw = cells[6].querySelector('.editable-input')?.value?.trim() || '';
    const status = cells[7].querySelector('.ms-select')?.dataset?.currentValue || cells[7].textContent.trim();
    const completedDate = cells[8].querySelector('.editable-input')?.value?.trim() || '';
    const actualDuration = parseInt(cells[9].querySelector('.editable-input')?.value ?? '0', 10);
    const cost = cells[10].querySelector('.editable-input')?.value?.trim() || '0.00';
    const notes = cells[11].querySelector('textarea.editable-input')?.value?.trim() || '';

    const payload = {
      taskDescription,
      maintenanceType,
      priority,
      scheduledDate,
      estimatedDuration,
      status,
      completedDate,
      actualDuration,
      cost,
      notes,
    };
    if (typeof equipmentId === 'number' && !Number.isNaN(equipmentId)) payload.equipmentId = equipmentId;
    if (/^\d+$/.test(assignedToRaw)) payload.assignedTo = parseInt(assignedToRaw, 10);

    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, {
      method: 'PATCH', headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (!res.ok) { const err = await res.text(); alert(`Failed to update maintenance: ${err || res.status}`); return; }

    const updated = await res.json();
    cells[0].innerHTML = `<span class="label">${updated.equipmentCode ?? equipmentLabel}</span>`;
    cells[1].innerHTML = `<span class=\"label\">${updated.taskDescription ?? taskDescription}</span>`;
    cells[2].innerHTML = `<span class=\"label\">${updated.maintenanceType ?? maintenanceType}</span>`;
    cells[3].innerHTML = `<span class=\"label\">${updated.priority ?? priority}</span>`;
    cells[4].innerHTML = `<span class=\"label\">${updated.scheduledDate ?? (scheduledDate ? scheduledDate.replace('T',' ') : '-')}</span>`;
    cells[5].innerHTML = `<span class=\"label\">${updated.estimatedDuration ?? estimatedDuration}</span>`;
    cells[6].innerHTML = `<span class=\"label\">${(updated.assignedTo ?? assignedToRaw) || '-'}</span>`;
    cells[7].innerHTML = `<span class=\"label\">${updated.status ?? status}</span>`;
    cells[8].innerHTML = `<span class=\"label\">${updated.completedDate ?? (completedDate ? completedDate.replace('T',' ') : '-')}</span>`;
    cells[9].innerHTML = `<span class=\"label\">${(updated.actualDuration ?? actualDuration) || '-'}</span>`;
    cells[10].innerHTML = `<span class=\"label\">${updated.cost ?? cost}</span>`;
    cells[11].innerHTML = `<span class=\"label\">${(updated.notes ?? notes) || '-'}</span>`;
    const actionsCell = row.querySelector('td:last-child'); actionsCell.innerHTML = '<button class="btn-edit-label edit-btn">Edit</button>';
    applyBadges(row);
  }

  function applyBadgesRow(row){ applyBadges(row); }

  // UPDATED: call backend to delete before removing from DOM
  async function deleteRow(row){
    if (!confirm('Delete this maintenance task?')) return;
    const id = row.dataset.id;
    if (!id) { row.remove(); return; }
    try {
      const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, { method: 'DELETE', headers: { 'Accept': 'application/json' } });
      if (!res.ok) {
        const err = await res.text();
        alert(`Failed to delete maintenance: ${err || res.status}`);
        return;
      }
      row.remove();
    } catch (e) {
      console.error(e);
      alert('Failed to delete maintenance. Please try again.');
    }
  }

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
  if(searchInput) searchInput.addEventListener('input', () => { loadMaint(); });
  if(searchIcon) searchIcon.addEventListener('click', () => { loadMaint(); });

  function wireModalDropdown(selectId, dropdownId, hiddenId){ const select=document.getElementById(selectId); const dropdown=document.getElementById(dropdownId); const hidden=document.getElementById(hiddenId); if(!select || !dropdown || !hidden) return; select.addEventListener('click',(e)=>{ e.stopPropagation(); toggleMsDropdown(dropdown); }); dropdown.addEventListener('click',(e)=>{ const item=e.target.closest('.ms-dropdown-item'); if(!item) return; e.stopPropagation(); const val=item.dataset.value; select.textContent=val; select.dataset.currentValue=val; hidden.value=val; dropdown.querySelectorAll('.ms-dropdown-item').forEach(i=>i.classList.remove('selected')); item.classList.add('selected'); dropdown.style.display='none'; }); }
  wireModalDropdown('addMaintenanceTypeSelect','addMaintenanceTypeDropdown','addMaintenanceType');
  wireModalDropdown('addPrioritySelect','addPriorityDropdown','addPriority');
  wireModalDropdown('addStatusSelect','addStatusDropdown','addStatus');

  // Equipment code searchable dropdown
  async function searchEquipment(query){
    const url = query ? `/equipment/api?q=${encodeURIComponent(query)}` : '/equipment/api';
    try{
      const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
      if(!res.ok) return [];
      const data = await res.json();
      return Array.isArray(data) ? data : [];
    }catch{ return []; }
  }
  function renderEquipDropdown(items){
    if(!equipDropdown) return;
    equipDropdown.innerHTML='';
    if(!items.length){ equipDropdown.style.display='none'; return; }
    items.forEach(eq => {
      const div = document.createElement('div');
      div.className='ms-dropdown-item';
      const code = eq.serialNumber || eq.equipmentName || `ID-${eq.equipmentId}`;
      div.textContent = `${code} — ${eq.equipmentName ?? ''}`.trim();
      div.dataset.id = eq.equipmentId;
      div.dataset.label = code;
      equipDropdown.appendChild(div);
    });
    equipDropdown.style.display='block';
  }
  if(equipSearchInput && equipDropdown){
    equipSearchInput.addEventListener('input', async (e)=>{
      const q = e.target.value.trim();
      equipHiddenId.value = '';
      const items = await searchEquipment(q);
      renderEquipDropdown(items);
    });
    equipSearchInput.addEventListener('focus', async ()=>{
      const q = equipSearchInput.value.trim();
      const items = await searchEquipment(q);
      renderEquipDropdown(items);
    });
    equipDropdown.addEventListener('click', (e)=>{
      const item = e.target.closest('.ms-dropdown-item'); if(!item) return;
      equipSearchInput.value = item.dataset.label;
      equipHiddenId.value = item.dataset.id;
      equipDropdown.style.display='none';
    });
  }

  // Assigned To searchable dropdown (Users)
  let cachedUsers = null;
  async function fetchAllUsers(){
    if (cachedUsers) return cachedUsers;
    try {
      const res = await fetch('/lwms/users', { headers: { 'Accept': 'application/json' } });
      if (!res.ok) return [];
      const data = await res.json();
      cachedUsers = Array.isArray(data) ? data : [];
      return cachedUsers;
    } catch { return []; }
  }
  function filterUsers(list, q){
    if (!q) return list.slice(0, 20);
    const qq = q.toLowerCase();
    return list.filter(u =>
      (u.username && u.username.toLowerCase().includes(qq)) ||
      (u.email && u.email.toLowerCase().includes(qq)) ||
      ((u.firstName||'').toLowerCase().includes(qq)) ||
      ((u.lastName||'').toLowerCase().includes(qq))
    ).slice(0, 20);
  }
  function renderAssignedDropdown(items){
    if(!assignedDropdown) return;
    assignedDropdown.innerHTML='';
    if(!items.length){ assignedDropdown.style.display='none'; return; }
    items.forEach(u => {
      const div = document.createElement('div');
      div.className='ms-dropdown-item';
      const label = `${u.username||''} — ${u.firstName||''} ${u.lastName||''}`.trim();
      div.textContent = label;
      div.dataset.id = u.userId;
      div.dataset.label = label;
      assignedDropdown.appendChild(div);
    });
    assignedDropdown.style.display='block';
  }
  if (assignedSearchInput && assignedDropdown){
    assignedSearchInput.addEventListener('input', async (e)=>{
      const list = await fetchAllUsers();
      const items = filterUsers(list, e.target.value.trim());
      assignedHiddenId.value = '';
      renderAssignedDropdown(items);
    });
    assignedSearchInput.addEventListener('focus', async ()=>{
      const list = await fetchAllUsers();
      const items = filterUsers(list, assignedSearchInput.value.trim());
      renderAssignedDropdown(items);
    });
    assignedDropdown.addEventListener('click', (e)=>{
      const item = e.target.closest('.ms-dropdown-item'); if(!item) return;
      assignedSearchInput.value = item.dataset.label;
      assignedHiddenId.value = item.dataset.id;
      assignedDropdown.style.display='none';
    });
  }

  if (addMaintenanceIcon) addMaintenanceIcon.addEventListener('click', ()=>{ addMaintenanceModalOverlay.style.display='flex'; document.body.style.overflow='hidden'; });
  if (closeAddMaintenanceModal) closeAddMaintenanceModal.addEventListener('click', ()=>{ addMaintenanceModalOverlay.style.display='none'; document.body.style.overflow=''; });
  if (addMaintenanceModalOverlay) addMaintenanceModalOverlay.addEventListener('click', (e)=>{ if(e.target===addMaintenanceModalOverlay){ addMaintenanceModalOverlay.style.display='none'; document.body.style.overflow=''; } });

  if (addMaintenanceForm) addMaintenanceForm.addEventListener('submit', async (e)=>{
    e.preventDefault();
    if (!equipHiddenId || !equipHiddenId.value) { alert('Please select an Equipment Code from the list.'); return; }
    const assignedTo = assignedHiddenId && assignedHiddenId.value ? parseInt(assignedHiddenId.value, 10) : null;

    const payload = {
      equipmentId: parseInt(equipHiddenId.value, 10),
      taskDescription: document.getElementById('addTaskDescription').value,
      maintenanceType: document.getElementById('addMaintenanceType').value,
      priority: document.getElementById('addPriority').value,
      scheduledDate: document.getElementById('addScheduledDate').value,
      estimatedDuration: parseInt(document.getElementById('addEstimatedDuration').value || '0', 10),
      assignedTo,
      status: document.getElementById('addStatus').value,
      completedDate: document.getElementById('addCompletedDate').value,
      actualDuration: document.getElementById('addActualDuration').value ? parseInt(document.getElementById('addActualDuration').value, 10) : null,
      cost: document.getElementById('addCost').value,
      notes: document.getElementById('addNotes').value,
      createdBy: parseInt(document.getElementById('addCreatedBy').value, 10)
    };
    const res = await fetch(API_BASE, { method: 'POST', headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' }, body: JSON.stringify(payload) });
    if (!res.ok) { const err = await res.text(); alert(`Failed to add maintenance: ${err || res.status}`); return; }
    const created = await res.json();
    const newRow = document.createElement('tr');
    newRow.dataset.id = created.scheduleId;
    newRow.innerHTML = `
      <td><span class="label">${created.equipmentCode ?? (equipSearchInput?.value || '')}</span></td>
      <td><span class="label">${created.taskDescription ?? payload.taskDescription}</span></td>
      <td><span class="label">${created.maintenanceType ?? payload.maintenanceType}</span></td>
      <td><span class="label">${created.priority ?? payload.priority}</span></td>
      <td><span class="label">${created.scheduledDate ?? (payload.scheduledDate ? payload.scheduledDate.replace('T',' ') : '-')}</span></td>
      <td><span class="label">${created.estimatedDuration ?? payload.estimatedDuration}</span></td>
      <td><span class="label">${created.assignedTo ?? (assignedTo ?? '-')}</span></td>
      <td><span class="label">${created.status ?? payload.status}</span></td>
      <td><span class="label">${created.completedDate ?? (payload.completedDate ? payload.completedDate.replace('T',' ') : '-')}</span></td>
      <td><span class="label">${created.actualDuration ?? (payload.actualDuration ?? '-')}</span></td>
      <td><span class="label">${created.cost ?? payload.cost}</span></td>
      <td><span class="label">${created.notes ?? (payload.notes || '-')}</span></td>
      <td><button class="btn-edit-label edit-btn">Edit</button></td>`;
    tbody.prepend(newRow);
    applyBadges(newRow);
    addMaintenanceModalOverlay.style.display='none'; document.body.style.overflow=''; addMaintenanceForm.reset();
  });

  // Initial load
  loadMaint();
}); 