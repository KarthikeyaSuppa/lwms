document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("tbody");

  // Apply status classes to all existing status labels
  document.querySelectorAll('td:nth-child(9) .label').forEach(label => {
    const text = label.textContent.trim();
    if (text === 'Active') label.classList.add('status-active');
    else if (text === 'Inactive') label.classList.add('status-inactive');
  });

  const API_BASE = "/locations/api"; // also available at /lwms/locations/api

  function toggleDropdown(dropdown) {
    document.querySelectorAll('.type-dropdown, .status-dropdown').forEach(dd => {
      if (dd !== dropdown) { dd.style.display = 'none'; dd.classList.remove('shown'); }
    });
    if (dropdown.classList.contains('shown')) {
      dropdown.classList.remove('shown');
      dropdown.style.display = 'none';
    } else {
      const selectButton = dropdown.previousElementSibling;
      dropdown.style.position = 'absolute';
      dropdown.style.top = '100%';
      dropdown.style.left = '0';
      dropdown.style.width = '100%';
      dropdown.style.zIndex = '9999';
      dropdown.classList.add('shown');
      dropdown.style.display = 'block';
    }
  }

  document.addEventListener('click', (e) => {
    if (!e.target.closest('.dropdown-container')) {
      document.querySelectorAll('.type-dropdown, .status-dropdown').forEach(dropdown => {
        dropdown.style.display = 'none';
        dropdown.classList.remove('shown');
      });
    }
  });

  function makeRowEditable(row) {
    const cells = row.querySelectorAll("td:not(:last-child)");
    cells.forEach((cell, index) => {
      const label = cell.querySelector('.label');
      if (label && index > 0 && index < 7) { // Editable: Zone..Current Load
        const input = document.createElement('input');
        input.type = (index === 5 || index === 6) ? 'number' : 'text';
        input.className = 'editable-input';
        input.value = label.textContent.trim();
        cell.innerHTML = '';
        cell.appendChild(input);
      }
      if (index === 7) { // Location Type
        const currentValue = label.textContent.trim();
        const container = document.createElement('div');
        container.className = 'dropdown-container';
        const select = document.createElement('div');
        select.className = 'type-select';
        select.textContent = currentValue;
        select.dataset.currentValue = currentValue;
        const dropdown = document.createElement('div');
        dropdown.className = 'type-dropdown';
        const options = ['Storage','Receiving','Shipping','Quality'];
        options.forEach(opt => {
          const item = document.createElement('div');
          item.className = 'dropdown-item' + (opt === currentValue ? ' selected' : '');
          item.dataset.value = opt; item.textContent = opt; dropdown.appendChild(item);
        });
        select.addEventListener('click', (e) => { e.stopPropagation(); toggleDropdown(dropdown); });
        dropdown.querySelectorAll('.dropdown-item').forEach(item => {
          item.addEventListener('click', (e) => {
            e.stopPropagation();
            const v = item.dataset.value; select.textContent = v; select.dataset.currentValue = v;
            dropdown.querySelectorAll('.dropdown-item').forEach(d => d.classList.remove('selected'));
            item.classList.add('selected'); dropdown.style.display = 'none'; dropdown.classList.remove('shown');
          });
        });
        container.appendChild(select); container.appendChild(dropdown);
        cell.innerHTML = ''; cell.appendChild(container);
      }
      if (index === 8) { // Status
        const currentValue = label.textContent.trim();
        const container = document.createElement('div');
        container.className = 'dropdown-container';
        const select = document.createElement('div');
        select.className = 'status-select'; select.textContent = currentValue; select.dataset.currentValue = currentValue;
        if (currentValue === 'Active') {
          select.style.color = '#155724'; select.style.backgroundColor = 'rgba(40, 167, 69, 0.3)'; select.style.borderColor = 'rgba(40, 167, 69, 0.6)';
        } else if (currentValue === 'Inactive') {
          select.style.color = '#721c24'; select.style.backgroundColor = 'rgba(220, 53, 69, 0.3)'; select.style.borderColor = 'rgba(220, 53, 69, 0.6)';
        }
        const dropdown = document.createElement('div');
        dropdown.className = 'status-dropdown';
        const options = ['Active','Inactive'];
        options.forEach(opt => {
          const item = document.createElement('div');
          item.className = 'dropdown-item ' + (opt === 'Active' ? 'active' : 'inactive') + (opt === currentValue ? ' selected' : '');
          item.dataset.value = opt; item.textContent = opt; dropdown.appendChild(item);
        });
        select.addEventListener('click', (e) => { e.stopPropagation(); toggleDropdown(dropdown); });
        dropdown.querySelectorAll('.dropdown-item').forEach(item => {
          item.addEventListener('click', (e) => {
            e.stopPropagation();
            const v = item.dataset.value; select.textContent = v; select.dataset.currentValue = v;
            if (v === 'Active') { select.style.color = '#155724'; select.style.backgroundColor = 'rgba(40, 167, 69, 0.3)'; select.style.borderColor = 'rgba(40, 167, 69, 0.6)'; }
            else { select.style.color = '#721c24'; select.style.backgroundColor = 'rgba(220, 53, 69, 0.3)'; select.style.borderColor = 'rgba(220, 53, 69, 0.6)'; }
            dropdown.querySelectorAll('.dropdown-item').forEach(d => d.classList.remove('selected'));
            item.classList.add('selected'); dropdown.style.display = 'none'; dropdown.classList.remove('shown');
          });
        });
        container.appendChild(select); container.appendChild(dropdown);
        cell.innerHTML = ''; cell.appendChild(container);
      }
    });
  }

  async function saveRowChanges(row) {
    const id = row.dataset.id;
    if (!id) return;
    const cells = row.querySelectorAll('td');
    const zone = cells[1]?.querySelector('.editable-input')?.value?.trim();
    const aisle = cells[2]?.querySelector('.editable-input')?.value?.trim();
    const rack = cells[3]?.querySelector('.editable-input')?.value?.trim();
    const shelf = cells[4]?.querySelector('.editable-input')?.value?.trim();
    const capacity = parseInt(cells[5]?.querySelector('.editable-input')?.value ?? '0', 10);
    const currentLoad = parseInt(cells[6]?.querySelector('.editable-input')?.value ?? '0', 10);
    const locationType = cells[7]?.querySelector('.type-select')?.dataset?.currentValue;
    const isActiveText = cells[8]?.querySelector('.status-select')?.dataset?.currentValue;
    const isActive = isActiveText === 'Active';

    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify({ zone, aisle, rack, shelf, capacity, currentLoad, locationType, isActive })
    });
    if (!res.ok) {
      const err = await res.text();
      alert(`Failed to update location: ${err || res.status}`);
      return;
    }
    const updated = await res.json();

    // Re-render row
    cells[0].innerHTML = `<span class="label">${updated.locationCode ?? ''}</span>`;
    cells[1].innerHTML = `<span class=\"label\">${updated.zone ?? ''}</span>`;
    cells[2].innerHTML = `<span class=\"label\">${updated.aisle ?? ''}</span>`;
    cells[3].innerHTML = `<span class=\"label\">${updated.rack ?? ''}</span>`;
    cells[4].innerHTML = `<span class=\"label\">${updated.shelf ?? ''}</span>`;
    cells[5].innerHTML = `<span class=\"label\">${updated.capacity ?? ''}</span>`;
    cells[6].innerHTML = `<span class=\"label\">${updated.currentLoad ?? ''}</span>`;
    cells[7].innerHTML = `<span class=\"label\">${updated.locationType ?? ''}</span>`;
    const statusSpan = document.createElement('span');
    statusSpan.className = `label ${updated.isActive ? 'status-active' : 'status-inactive'}`;
    statusSpan.textContent = updated.isActive ? 'Active' : 'Inactive';
    statusSpan.style.background = updated.isActive ? 'rgba(40, 167, 69, 0.3)' : 'rgba(220, 53, 69, 0.3)';
    statusSpan.style.borderColor = updated.isActive ? 'rgba(40, 167, 69, 0.6)' : 'rgba(220, 53, 69, 0.6)';
    statusSpan.style.color = updated.isActive ? '#155724' : '#721c24';
    cells[8].innerHTML = ''; cells[8].appendChild(statusSpan);
    const actionsCell = row.querySelector('td:last-child'); actionsCell.innerHTML = '<button class="btn-edit-label edit-btn">Edit Location</button>';
  }

  async function deleteRow(row) {
    if (!confirm('Are you sure you want to delete this location?')) return;
    const id = row.dataset.id;
    if (!id) { row.remove(); return; }
    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, { method: 'DELETE' });
    if (!res.ok) {
      const err = await res.text();
      alert(`Failed to delete location: ${err || res.status}`);
      return;
    }
    row.remove();
  }

  function createLocationCode(zone, aisle, rack, shelf){
    return `${zone}${aisle}-${rack}-${shelf}`;
  }

  function renderRows(list){
    tbody.innerHTML = '';
    list.forEach(loc => {
      const row = document.createElement('tr');
      row.dataset.id = loc.locationId;
      row.innerHTML = `
        <td><span class="label">${loc.locationCode ?? createLocationCode(loc.zone, loc.aisle, loc.rack, loc.shelf)}</span></td>
        <td><span class="label">${loc.zone ?? ''}</span></td>
        <td><span class="label">${loc.aisle ?? ''}</span></td>
        <td><span class="label">${loc.rack ?? ''}</span></td>
        <td><span class="label">${loc.shelf ?? ''}</span></td>
        <td><span class="label">${loc.capacity ?? ''}</span></td>
        <td><span class="label">${loc.currentLoad ?? ''}</span></td>
        <td><span class="label">${loc.locationType ?? ''}</span></td>
        <td><span class="label ${loc.isActive ? 'status-active' : 'status-inactive'}" style="background: ${loc.isActive ? 'rgba(40, 167, 69, 0.3)' : 'rgba(220, 53, 69, 0.3)'}; border-color: ${loc.isActive ? 'rgba(40, 167, 69, 0.6)' : 'rgba(220, 53, 69, 0.6)'}; color: ${loc.isActive ? '#155724' : '#721c24'};">${loc.isActive ? 'Active' : 'Inactive'}</span></td>
        <td><button class="btn-edit-label edit-btn">Edit Location</button></td>`;
      tbody.appendChild(row);
    });
  }

  async function loadLocations(){
    const q = document.getElementById('searchInput')?.value?.trim();
    const url = q ? `${API_BASE}?q=${encodeURIComponent(q)}` : API_BASE;
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) { console.error('Failed to load locations'); return; }
    const data = await res.json();
    renderRows(Array.isArray(data) ? data : []);
  }

  if (tbody) {
    tbody.addEventListener('click', (e) => {
      const target = e.target; const row = target.closest('tr'); if (!row) return;
      if (target.classList.contains('edit-btn')) {
        makeRowEditable(row);
        const actionsCell = row.querySelector('td:last-child');
        actionsCell.innerHTML = `
          <div class="action-icons-container">
            <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
            <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
          </div>`;
      }
      if (target.classList.contains('save-btn')) { saveRowChanges(row); }
      if (target.classList.contains('delete-btn')) { deleteRow(row); }
    });
  }

  const addLocationIcon = document.getElementById('addLocationIcon');
  const addLocationModalOverlay = document.getElementById('addLocationModalOverlay');
  const closeAddLocationModal = document.getElementById('closeAddLocationModal');
  const addLocationForm = document.getElementById('addLocationForm');
  const searchInput = document.getElementById('searchInput');
  const searchIcon = document.getElementById('searchIcon');

  function normalize(text){ return (text || '').toString().toLowerCase().trim(); }
  function filterRows(){
    const query = normalize(searchInput.value);
    const rows = tbody.querySelectorAll('tr');
    rows.forEach(row => {
      const cells = row.querySelectorAll('td');
      const locationCode = normalize(cells[0]?.querySelector('.editable-input')?.value || cells[0]?.textContent);
      const zone = normalize(cells[1]?.querySelector('.editable-input')?.value || cells[1]?.textContent);
      const aisle = normalize(cells[2]?.querySelector('.editable-input')?.value || cells[2]?.textContent);
      const rack = normalize(cells[3]?.querySelector('.editable-input')?.value || cells[3]?.textContent);
      const shelf = normalize(cells[4]?.querySelector('.editable-input')?.value || cells[4]?.textContent);
      const locationType = normalize(cells[7]?.querySelector('.editable-input')?.value || cells[7]?.textContent);
      const status = normalize(cells[8]?.querySelector('.editable-input')?.value || cells[8]?.textContent);
      const matches = locationCode.includes(query) || zone.includes(query) || aisle.includes(query) || rack.includes(query) || shelf.includes(query) || locationType.includes(query) || status.includes(query);
      row.style.display = matches ? '' : 'none';
    });
  }
  if (searchInput) searchInput.addEventListener('input', filterRows);
  if (searchIcon) searchIcon.addEventListener('click', filterRows);

  if (addLocationIcon) {
    addLocationIcon.addEventListener('click', () => {
      addLocationModalOverlay.style.display = 'flex'; document.body.style.overflow = 'hidden';
    });
  }

  if (closeAddLocationModal) {
    closeAddLocationModal.addEventListener('click', () => {
      addLocationModalOverlay.style.display = 'none'; document.body.style.overflow = '';
    });
  }

  if (addLocationModalOverlay) {
    addLocationModalOverlay.addEventListener('click', (e) => {
      if (e.target === addLocationModalOverlay) {
        addLocationModalOverlay.style.display = 'none'; document.body.style.overflow = '';
      }
    });
  }

  if (addLocationForm) {
    const addLocationTypeSelect = document.getElementById('addLocationType');
    const addLocationTypeDropdown = addLocationTypeSelect ? addLocationTypeSelect.nextElementSibling : null;
    const addIsActiveSelect = document.getElementById('addIsActive');
    const addIsActiveDropdown = addIsActiveSelect ? addIsActiveSelect.nextElementSibling : null;

    if (addLocationTypeSelect && addLocationTypeDropdown) {
      addLocationTypeSelect.addEventListener('click', (e) => { e.stopPropagation(); toggleDropdown(addLocationTypeDropdown); });
      addLocationTypeDropdown.querySelectorAll('.dropdown-item').forEach(item => {
        item.addEventListener('click', (e) => {
          e.stopPropagation(); const v = item.dataset.value;
          addLocationTypeSelect.textContent = v; addLocationTypeSelect.dataset.currentValue = v;
          addLocationTypeDropdown.querySelectorAll('.dropdown-item').forEach(d => d.classList.remove('selected'));
          item.classList.add('selected'); addLocationTypeDropdown.style.display = 'none';
        });
      });
    }

    if (addIsActiveSelect && addIsActiveDropdown) {
      addIsActiveSelect.addEventListener('click', (e) => { e.stopPropagation(); toggleDropdown(addIsActiveDropdown); });
      addIsActiveDropdown.querySelectorAll('.dropdown-item').forEach(item => {
        item.addEventListener('click', (e) => {
          e.stopPropagation(); const v = item.dataset.value;
          addIsActiveSelect.textContent = v; addIsActiveSelect.dataset.currentValue = v;
          if (v === 'Active') { addIsActiveSelect.style.color = '#155724'; addIsActiveSelect.style.backgroundColor = 'rgba(40, 167, 69, 0.3)'; addIsActiveSelect.style.borderColor = 'rgba(40, 167, 69, 0.6)'; }
          else { addIsActiveSelect.style.color = '#721c24'; addIsActiveSelect.style.backgroundColor = 'rgba(220, 53, 69, 0.3)'; addIsActiveSelect.style.borderColor = 'rgba(220, 53, 69, 0.6)'; }
          addIsActiveDropdown.querySelectorAll('.dropdown-item').forEach(d => d.classList.remove('selected'));
          item.classList.add('selected'); addIsActiveDropdown.style.display = 'none';
        });
      });
    }

    addLocationForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const zone = document.getElementById('addZone').value;
      const aisle = document.getElementById('addAisle').value;
      const rack = document.getElementById('addRack').value;
      const shelf = document.getElementById('addShelf').value;
      const locationCode = `${zone}${aisle}-${rack}-${shelf}`;
      const payload = {
        zone,
        aisle,
        rack,
        shelf,
        capacity: parseInt(document.getElementById('addCapacity').value, 10),
        currentLoad: parseInt(document.getElementById('addCurrentLoad').value, 10),
        locationType: document.getElementById('addLocationType').dataset.currentValue,
        isActive: document.getElementById('addIsActive').dataset.currentValue === 'Active'
      };
      const res = await fetch(API_BASE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) {
        const err = await res.text();
        alert(`Failed to add location: ${err || res.status}`);
        return;
      }
      const created = await res.json();
      const newRow = document.createElement('tr');
      newRow.dataset.id = created.locationId;
      newRow.innerHTML = `
        <td><span class="label">${created.locationCode ?? locationCode}</span></td>
        <td><span class="label">${created.zone ?? zone}</span></td>
        <td><span class="label">${created.aisle ?? aisle}</span></td>
        <td><span class="label">${created.rack ?? rack}</span></td>
        <td><span class="label">${created.shelf ?? shelf}</span></td>
        <td><span class="label">${created.capacity ?? payload.capacity}</span></td>
        <td><span class="label">${created.currentLoad ?? payload.currentLoad}</span></td>
        <td><span class="label">${created.locationType ?? payload.locationType}</span></td>
        <td><span class="label ${created.isActive ? 'status-active' : 'status-inactive'}" style="background: ${created.isActive ? 'rgba(40, 167, 69, 0.3)' : 'rgba(220, 53, 69, 0.3)'}; border-color: ${created.isActive ? 'rgba(40, 167, 69, 0.6)' : 'rgba(220, 53, 69, 0.6)'}; color: ${created.isActive ? '#155724' : '#721c24'};">${created.isActive ? 'Active' : 'Inactive'}</span></td>
        <td><button class="btn-edit-label edit-btn">Edit Location</button></td>`;
      tbody.prepend(newRow);
      addLocationModalOverlay.style.display = 'none'; document.body.style.overflow = ''; addLocationForm.reset();
    });
  }

  // Initial load
  loadLocations();
}); 