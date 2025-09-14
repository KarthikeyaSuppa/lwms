document.addEventListener('DOMContentLoaded', () => {
  // Search functionality
  const searchInput = document.getElementById('searchInput');
  const table = document.getElementById('inventoryTable');
  if (searchInput && table) {
    searchInput.addEventListener('keyup', function() {
      const searchTerm = this.value.toLowerCase();
      const rows = table.querySelectorAll('tbody tr');
      rows.forEach(row => {
        const itemCode = row.querySelector('[data-label="Item Code"] .label').textContent.toLowerCase();
        const itemName = row.querySelector('[data-label="Item Name"] .label').textContent.toLowerCase();
        const category = row.querySelector('[data-label="Category"] .label').textContent.toLowerCase();
        const status = row.querySelector('[data-label="Status"] .label').textContent.toLowerCase();
        row.style.display = (itemCode.includes(searchTerm) || itemName.includes(searchTerm) || category.includes(searchTerm) || status.includes(searchTerm)) ? '' : 'none';
      });
    });
  }

  // Edit/Save/Delete functionality
  const tbody = document.querySelector('#inventoryTable tbody');
  function makeRowEditable(row) {
    const cells = row.querySelectorAll('td');
    const editableIndexes = [0,1,2,3,4,5,6,7];
    editableIndexes.forEach(idx => {
      const cell = cells[idx];
      const label = cell.querySelector('.label');
      if (!label) return;
      const input = document.createElement('input');
      input.type = (idx === 4 || idx === 5 || idx === 6) ? 'number' : (idx === 7 ? 'number' : 'text');
      if (idx === 7) input.step = '0.01';
      input.className = 'editable-input';
      input.value = label.textContent.trim().replace('$','');
      cell.innerHTML = '';
      cell.appendChild(input);
    });
    row.classList.add('edit-mode');
  }

  function saveRow(row) {
    const cells = row.querySelectorAll('td');
    const editableIndexes = [0,1,2,3,4,5,6,7];
    editableIndexes.forEach(idx => {
      const cell = cells[idx];
      const input = cell.querySelector('input');
      if (!input) return;
      const span = document.createElement('span');
      span.className = 'label';
      const val = input.value.trim();
      span.textContent = idx === 7 ? `$${Number(val).toFixed(2)}` : val;
      cell.innerHTML = '';
      cell.appendChild(span);
    });
    row.classList.remove('edit-mode');
  }

  if (tbody) {
    tbody.addEventListener('click', (e) => {
      const target = e.target;
      const row = target.closest('tr');
      if (!row) return;
      if (target.classList.contains('edit-btn')) {
        makeRowEditable(row);
      } else if (target.classList.contains('save-btn')) {
        saveRow(row);
      } else if (target.classList.contains('delete-btn')) {
        if (confirm('Are you sure you want to delete this item?')) row.remove();
      }
    });
  }

  // Add Item Modal logic
  const addItemIcon = document.getElementById('addItemIcon');
  const addItemModalOverlay = document.getElementById('addItemModalOverlay');
  const closeAddItemModal = document.getElementById('closeAddItemModal');
  const addItemForm = document.getElementById('addItemForm');

  if (addItemIcon) {
    addItemIcon.addEventListener('click', () => {
      addItemModalOverlay.style.display = 'flex';
      document.body.style.overflow = 'hidden';
    });
  }
  if (closeAddItemModal) {
    closeAddItemModal.addEventListener('click', () => {
      addItemModalOverlay.style.display = 'none';
      document.body.style.overflow = '';
    });
  }
  if (addItemModalOverlay) {
    addItemModalOverlay.addEventListener('click', (e) => {
      if (e.target === addItemModalOverlay) {
        addItemModalOverlay.style.display = 'none';
        document.body.style.overflow = '';
      }
    });
  }

  if (addItemForm) {
    addItemForm.addEventListener('submit', (e) => {
      e.preventDefault();
      const data = {
        itemCode: document.getElementById('addItemCode').value,
        itemName: document.getElementById('addItemName').value,
        category: document.getElementById('addCategory').value,
        location: document.getElementById('addLocation').value,
        quantity: document.getElementById('addQuantity').value,
        unitPrice: document.getElementById('addUnitPrice').value
      };
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td data-label="Item Code"><span class="label">${data.itemCode}</span></td>
        <td data-label="Item Name"><span class="label">${data.itemName}</span></td>
        <td data-label="Category"><span class="label">${data.category}</span></td>
        <td><span class="label">${data.location}</span></td>
        <td><span class="label">${data.quantity}</span></td>
        <td><span class="label">0</span></td>
        <td><span class="label">0</span></td>
        <td><span class="label">$${Number(data.unitPrice).toFixed(2)}</span></td>
        <td data-label="Status"><span class="label" data-status="optimal">Optimal</span></td>
        <td>
          <button class="btn-edit-label edit-btn">Edit Item</button>
          <div class="action-icons-container">
            <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
            <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
          </div>
        </td>`;
      table.querySelector('tbody').appendChild(tr);
      addItemModalOverlay.style.display = 'none';
      document.body.style.overflow = '';
      addItemForm.reset();
    });
  }

  // Initialize existing delete buttons image paths
  document.querySelectorAll('.action-icons-container img[src="trash.png"]').forEach(el => { el.src = '/images/trash.png'; });
  document.querySelectorAll('.action-icons-container img[src="correct.png"]').forEach(el => { el.src = '/images/correct.png'; });
}); 