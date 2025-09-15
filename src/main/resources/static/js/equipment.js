document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("tbody");
  const searchInput = document.getElementById("searchInput");
  const addEquipmentIcon = document.getElementById('addEquipmentIcon');
  const addEquipmentModalOverlay = document.getElementById('addEquipmentModalOverlay');
  const closeAddEquipmentModal = document.getElementById('closeAddEquipmentModal');
  const addEquipmentForm = document.getElementById('addEquipmentForm');
  const addStatusSelect = document.getElementById('addStatus');
  const addStatusDropdown = document.getElementById('addStatusDropdown');

  const API_BASE = "/equipment/api"; // also available at /lwms/equipment/api

  // Search functionality
  if (searchInput) {
    searchInput.addEventListener("input", () => {
      loadEquipment();
    });
  }

  function filterTableLocal(term) {
    const q = (term || '').toLowerCase();
    const rows = tbody.querySelectorAll("tr");
    rows.forEach(row => {
      const cells = row.querySelectorAll("td");
      let found = false;
      cells.forEach(cell => {
        const cellText = (cell.textContent || '').toLowerCase();
        if (cellText.includes(q)) found = true;
      });
      row.style.display = found ? "" : "none";
    });
  }

  // Add Equipment Modal functionality
  if (addEquipmentIcon) {
    addEquipmentIcon.addEventListener('click', () => {
      addEquipmentModalOverlay.style.display = 'flex';
      document.body.style.overflow = 'hidden';
    });
  }

  // Modal status dropdown behavior
  if (addStatusSelect && addStatusDropdown) {
    addStatusSelect.addEventListener('click', (e) => {
      e.stopPropagation();
      addStatusDropdown.classList.toggle('shown');
    });
    addStatusDropdown.querySelectorAll('.dropdown-item').forEach(item => {
      item.addEventListener('click', (e) => {
        e.stopPropagation();
        addStatusSelect.textContent = item.dataset.value;
        addStatusSelect.dataset.currentValue = item.dataset.value;
        addStatusDropdown.querySelectorAll('.dropdown-item').forEach(d => d.classList.remove('selected'));
        item.classList.add('selected');
        addStatusDropdown.classList.remove('shown');
      });
    });
  }

  if (closeAddEquipmentModal) {
    closeAddEquipmentModal.addEventListener('click', () => {
      addEquipmentModalOverlay.style.display = 'none';
      document.body.style.overflow = '';
    });
  }

  if (addEquipmentModalOverlay) {
    addEquipmentModalOverlay.addEventListener('click', (e) => {
      if (e.target === addEquipmentModalOverlay) {
        addEquipmentModalOverlay.style.display = 'none';
        document.body.style.overflow = '';
      }
    });
  }

  // Render helpers
  function renderRows(list){
    tbody.innerHTML = '';
    list.forEach(eq => {
      const row = document.createElement('tr');
      row.dataset.id = eq.equipmentId;
      row.innerHTML = `
        <td><span class="label">${eq.equipmentName ?? ''}</span></td>
        <td><span class="label">${eq.equipmentType ?? ''}</span></td>
        <td><span class="label">${eq.serialNumber ?? ''}</span></td>
        <td><span class="label">${eq.location ?? ''}</span></td>
        <td><span class="label status-label">${eq.status ?? ''}</span></td>
        <td><span class="label readonly-label">${eq.purchaseDate ?? ''}</span></td>
        <td><span class="label readonly-label">${eq.warrantyExpiry ?? ''}</span></td>
        <td>
          <div class="action-icons-container">
            <button class="btn-edit-label edit-btn">Edit Equipment</button>
          </div>
        </td>`;
      tbody.appendChild(row);
      updateStatusLabelStyles(row);
    });
  }

  async function loadEquipment(){
    const q = searchInput?.value?.trim();
    const url = q ? `${API_BASE}?q=${encodeURIComponent(q)}` : API_BASE;
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) { console.error('Failed to load equipment'); return; }
    const data = await res.json();
    renderRows(Array.isArray(data) ? data : []);
  }

  // Handle new equipment submission
  if (addEquipmentForm) {
    addEquipmentForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const payload = {
        equipmentName: document.getElementById('addEquipmentName').value,
        equipmentType: document.getElementById('addEquipmentType').value,
        serialNumber: document.getElementById('addSerialNumber').value,
        location: document.getElementById('addLocation').value,
        status: document.getElementById('addStatus').dataset.currentValue,
        purchaseDate: document.getElementById('addPurchaseDate').value,
        warrantyExpiry: document.getElementById('addWarrantyExpiry').value
      };
      const res = await fetch(API_BASE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) {
        const err = await res.text();
        alert(`Failed to add equipment: ${err || res.status}`);
        return;
      }
      const created = await res.json();
      const newRow = document.createElement('tr');
      newRow.dataset.id = created.equipmentId;
      newRow.innerHTML = `
        <td><span class="label">${created.equipmentName ?? payload.equipmentName}</span></td>
        <td><span class="label">${created.equipmentType ?? payload.equipmentType}</span></td>
        <td><span class="label">${created.serialNumber ?? payload.serialNumber}</span></td>
        <td><span class="label">${created.location ?? payload.location}</span></td>
        <td><span class="label status-label">${created.status ?? payload.status}</span></td>
        <td><span class="label readonly-label">${created.purchaseDate ?? payload.purchaseDate}</span></td>
        <td><span class="label readonly-label">${created.warrantyExpiry ?? payload.warrantyExpiry}</span></td>
        <td>
          <div class="action-icons-container">
            <button class="btn-edit-label edit-btn">Edit Equipment</button>
          </div>
        </td>`;
      tbody.prepend(newRow);
      addEquipmentForm.reset();
      addEquipmentModalOverlay.style.display = 'none';
      document.body.style.overflow = '';
      updateStatusLabelStyles(newRow);
    });
  }

  function updateStatusLabelStyles(row) {
    const statusLabel = row.querySelector('.status-label');
    if (!statusLabel) return;
    const statusText = statusLabel.textContent.trim();
    if (statusText === 'Active') {
      statusLabel.style.background = 'rgba(40, 167, 69, 0.3)';
      statusLabel.style.borderColor = 'rgba(40, 167, 69, 0.6)';
      statusLabel.style.color = '#155724';
    } else if (statusText === 'Maintenance') {
      statusLabel.style.background = 'rgba(255, 193, 7, 0.3)';
      statusLabel.style.borderColor = 'rgba(255, 193, 7, 0.6)';
      statusLabel.style.color = '#856404';
    } else if (statusText === 'Inactive') {
      statusLabel.style.background = 'rgba(220, 53, 69, 0.3)';
      statusLabel.style.borderColor = 'rgba(220, 53, 69, 0.6)';
      statusLabel.style.color = '#721c24';
    } else {
      statusLabel.style.background = 'linear-gradient(180deg, #d7b785, #caa475)';
      statusLabel.style.borderColor = 'rgba(62,47,25,.5)';
      statusLabel.style.color = '#3e2f19';
    }
  }

  function autoResizeInput(input) {
    const temp = document.createElement('span');
    temp.style.visibility = 'hidden';
    temp.style.position = 'absolute';
    temp.style.fontSize = window.getComputedStyle(input).fontSize;
    temp.style.fontFamily = window.getComputedStyle(input).fontFamily;
    temp.style.fontWeight = window.getComputedStyle(input).fontWeight;
    temp.textContent = input.value || input.placeholder || 'M';
    document.body.appendChild(temp);
    const width = Math.max(temp.offsetWidth + 20, 60);
    input.style.width = Math.min(width, 200) + 'px';
    document.body.removeChild(temp);
  }

  function makeRowEditable(row) {
    const cells = row.querySelectorAll('td');
    cells.forEach((cell, index) => {
      if (index === 7) {
        cell.innerHTML = `
          <div class="action-icons-container">
            <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
            <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
          </div>
        `;
        return;
      }

      const label = cell.querySelector('.label');
      if (!label) return;

      const cellText = label.textContent.trim();

      if (index >= 0 && index <= 3) {
        const input = document.createElement('input');
        input.type = 'text';
        input.className = 'editable-input';
        input.value = cellText;
        cell.innerHTML = '';
        cell.appendChild(input);
        autoResizeInput(input);
        return;
      }

      if (index === 4) {
        const container = document.createElement('div');
        container.className = 'dropdown-container';
        const select = document.createElement('div');
        select.className = 'status-select';
        select.textContent = cellText;
        select.dataset.currentValue = cellText;
        const dropdown = document.createElement('div');
        dropdown.className = 'status-dropdown';
        ['Active','Maintenance','Inactive'].forEach(opt => {
          const item = document.createElement('div');
          item.className = 'dropdown-item' + (opt === cellText ? ' selected' : '');
          item.dataset.value = opt;
          item.textContent = opt;
          dropdown.appendChild(item);
        });
        select.addEventListener('click', (e) => {
          e.stopPropagation();
          dropdown.classList.toggle('shown');
        });
        dropdown.querySelectorAll('.dropdown-item').forEach(item => {
          item.addEventListener('click', (e) => {
            e.stopPropagation();
            select.textContent = item.dataset.value;
            select.dataset.currentValue = item.dataset.value;
            dropdown.querySelectorAll('.dropdown-item').forEach(d => d.classList.remove('selected'));
            item.classList.add('selected');
            dropdown.classList.remove('shown');
          });
        });
        container.appendChild(select);
        container.appendChild(dropdown);
        cell.innerHTML = '';
        cell.appendChild(container);
        return;
      }

      if (index === 5 || index === 6) {
        const readonlyLabel = document.createElement('span');
        readonlyLabel.className = 'readonly-label';
        readonlyLabel.textContent = cellText;
        cell.innerHTML = '';
        cell.appendChild(readonlyLabel);
        return;
      }
    });
  }

  document.addEventListener('click', (e) => {
    if (!e.target.closest('.dropdown-container')) {
      document.querySelectorAll('.status-dropdown').forEach(dd => dd.classList.remove('shown'));
    }
  });

  async function saveRowChanges(row) {
    const id = row.dataset.id;
    if (!id) return;
    const cells = row.querySelectorAll('td');
    const equipmentName = cells[0]?.querySelector('.editable-input')?.value?.trim();
    const equipmentType = cells[1]?.querySelector('.editable-input')?.value?.trim();
    const serialNumber = cells[2]?.querySelector('.editable-input')?.value?.trim();
    const location = cells[3]?.querySelector('.editable-input')?.value?.trim() || cells[3]?.textContent?.trim();
    const status = cells[4]?.querySelector('.status-select')?.dataset?.currentValue;

    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
      body: JSON.stringify({ equipmentName, equipmentType, serialNumber, location, status })
    });
    if (!res.ok) {
      const err = await res.text();
      alert(`Failed to update equipment: ${err || res.status}`);
      return;
    }
    const updated = await res.json();

    cells[0].innerHTML = `<span class="label">${updated.equipmentName ?? ''}</span>`;
    cells[1].innerHTML = `<span class=\"label\">${updated.equipmentType ?? ''}</span>`;
    cells[2].innerHTML = `<span class=\"label\">${updated.serialNumber ?? ''}</span>`;
    cells[3].innerHTML = `<span class=\"label\">${updated.location ?? ''}</span>`;
    cells[4].innerHTML = `<span class=\"label status-label\">${updated.status ?? ''}</span>`;
    updateStatusLabelStyles(row);
    const actionsCell = row.querySelector('td:last-child');
    actionsCell.innerHTML = `
      <div class="action-icons-container">
        <button class="btn-edit-label edit-btn">Edit Equipment</button>
      </div>`;
  }

  function deleteRowUI(row) { row.remove(); }

  async function deleteRow(row) {
    if (!confirm('Are you sure you want to delete this equipment?')) return;
    const id = row.dataset.id;
    if (!id) { deleteRowUI(row); return; }
    const res = await fetch(`${API_BASE}/${encodeURIComponent(id)}`, { method: 'DELETE' });
    if (!res.ok) {
      const err = await res.text();
      alert(`Failed to delete equipment: ${err || res.status}`);
      return;
    }
    deleteRowUI(row);
  }

  if (tbody) {
    tbody.addEventListener("click", (e) => {
      const target = e.target;
      const row = target.closest("tr");
      if (!row) return;
      if (target.classList.contains("edit-btn")) {
        makeRowEditable(row);
        const actionsCell = row.querySelector('td:last-child');
        actionsCell.innerHTML = `
          <div class="action-icons-container">
            <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
            <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
          </div>`;
      } else if (target.classList.contains("save-btn")) {
        saveRowChanges(row);
      } else if (target.classList.contains("delete-btn")) {
        deleteRow(row);
      }
    });
  }

  // Initial load
  loadEquipment();
}); 