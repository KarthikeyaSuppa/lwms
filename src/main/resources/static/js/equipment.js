document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("tbody");
  const searchInput = document.getElementById("searchInput");
  const addEquipmentIcon = document.getElementById('addEquipmentIcon');
  const addEquipmentModalOverlay = document.getElementById('addEquipmentModalOverlay');
  const closeAddEquipmentModal = document.getElementById('closeAddEquipmentModal');
  const addEquipmentForm = document.getElementById('addEquipmentForm');
  const addStatusSelect = document.getElementById('addStatus');
  const addStatusDropdown = document.getElementById('addStatusDropdown');

  let nextId = 6;

  // Search functionality
  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      filterTable(e.target.value);
    });
  }

  function filterTable(searchTerm) {
    const term = (searchTerm || '').toLowerCase();
    const rows = tbody.querySelectorAll("tr");
    rows.forEach(row => {
      const cells = row.querySelectorAll("td");
      let found = false;
      cells.forEach(cell => {
        const cellText = (cell.textContent || '').toLowerCase();
        if (cellText.includes(term)) found = true;
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

  // Handle new equipment submission
  if (addEquipmentForm) {
    addEquipmentForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const newEquipment = {
        equipmentName: document.getElementById('addEquipmentName').value,
        equipmentType: document.getElementById('addEquipmentType').value,
        serialNumber: document.getElementById('addSerialNumber').value,
        location: document.getElementById('addLocation').value,
        status: document.getElementById('addStatus').dataset.currentValue,
        purchaseDate: document.getElementById('addPurchaseDate').value,
        warrantyExpiry: document.getElementById('addWarrantyExpiry').value
      };

      const newRow = document.createElement('tr');
      newRow.dataset.id = nextId++;
      newRow.innerHTML = `
        <td><span class="label">${newEquipment.equipmentName}</span></td>
        <td><span class="label">${newEquipment.equipmentType}</span></td>
        <td><span class="label">${newEquipment.serialNumber}</span></td>
        <td><span class="label">${newEquipment.location}</span></td>
        <td><span class="label status-label">${newEquipment.status}</span></td>
        <td><span class="label readonly-label">${newEquipment.purchaseDate}</span></td>
        <td><span class="label readonly-label">${newEquipment.warrantyExpiry}</span></td>
        <td>
          <div class="action-icons-container">
            <button class="btn-edit-label edit-btn">Edit Equipment</button>
          </div>
        </td>
      `;
      tbody.appendChild(newRow);

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

  function saveRowChanges(row) {
    const cells = row.querySelectorAll('td');
    cells.forEach((cell, index) => {
      let value = '';
      const input = cell.querySelector('.editable-input');
      const select = cell.querySelector('.status-select');
      const readonlyLabel = cell.querySelector('.readonly-label');

      if (input) {
        value = input.value;
        const label = document.createElement('span');
        label.className = 'label';
        label.textContent = value;
        cell.innerHTML = '';
        cell.appendChild(label);
      } else if (select) {
        value = select.dataset.currentValue || select.textContent.trim();
        const label = document.createElement('span');
        label.className = 'label status-label';
        label.textContent = value;
        cell.innerHTML = '';
        cell.appendChild(label);
        updateStatusLabelStyles(row);
      } else if (readonlyLabel) {
        // keep as is
      }
    });

    const actionsCell = row.querySelector('td:last-child');
    actionsCell.innerHTML = `
      <div class="action-icons-container">
        <button class="btn-edit-label edit-btn">Edit Equipment</button>
      </div>
    `;
  }

  function deleteRow(row) {
    if (confirm('Are you sure you want to delete this equipment?')) {
      row.remove();
    }
  }

  if (tbody) {
    tbody.addEventListener("click", (e) => {
      const target = e.target;
      const row = target.closest("tr");
      if (!row) return;
      if (target.classList.contains("edit-btn")) {
        makeRowEditable(row);
      } else if (target.classList.contains("save-btn")) {
        saveRowChanges(row);
      } else if (target.classList.contains("delete-btn")) {
        deleteRow(row);
      }
    });
  }
}); 