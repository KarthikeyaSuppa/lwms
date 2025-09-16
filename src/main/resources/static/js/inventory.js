document.addEventListener('DOMContentLoaded', () => {
  // Search functionality
  const searchInput = document.getElementById('searchInput');
  const table = document.getElementById('inventoryTable');
  const tbody = document.querySelector('#inventoryTable tbody');
  const API_BASE = '/inventory/api';

  function renderRows(list){
    tbody.innerHTML = '';
    list.forEach(item => {
      const tr = document.createElement('tr');
      tr.dataset.id = item.itemId;
      tr.innerHTML = 
        <td data-label="Item Code"><span class="label"></span></td>
        <td data-label="Item Name"><span class="label"></span></td>
        <td data-label="Category"><span class="label"></span></td>
        <td><span class="label"></span></td>
        <td><span class="label"></span></td>
        <td><span class="label"></span></td>
        <td><span class="label"></span></td>
        <td><span class="label">q{(item.unitPrice ?? 0).toString()}</span></td>
        <td data-label="Status"><span class="label" data-status=""></span></td>
        <td>
          <button class="btn-edit-label edit-btn">Edit Item</button>
          <div class="action-icons-container">
            <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
            <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
          </div>
        </td>;
      tbody.appendChild(tr);
    });
  }

  function computeStatus(item){
    const q = Number(item.quantity ?? 0);
    const min = Number(item.minStockLevel ?? 0);
    const max = Number(item.maxStockLevel ?? 0);
    if (q <= 0) return 'out-of-stock';
    if (q < min) return 'low-stock';
    if (q > max) return 'overstock';
    return 'optimal';
  }
  function humanizeStatus(s){
    if (s === 'out-of-stock') return 'Out of Stock';
    if (s === 'low-stock') return 'Low Stock';
    if (s === 'overstock') return 'Overstock';
    return 'Optimal';
  }

  async function loadInventory(){
    const q = searchInput?.value?.trim();
    const url = q ? ${API_BASE}?q= : API_BASE;
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) { console.error('Failed to load inventory'); return; }
    const data = await res.json();
    renderRows(Array.isArray(data) ? data : []);
  }

  if (searchInput && table) {
    searchInput.addEventListener('input', function() {
      loadInventory();
    });
  }

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

  async function saveRow(row) {
    const id = row.dataset.id; 
    if (!id) return;
    
    const cells = row.querySelectorAll('td');
    const payload = {
      itemCode: cells[0].querySelector('input')?.value?.trim(),
      itemName: cells[1].querySelector('input')?.value?.trim(),
      category: cells[2].querySelector('input')?.value?.trim(),
      location: cells[3].querySelector('input')?.value?.trim(),
      quantity: Number(cells[4].querySelector('input')?.value ?? '0'),
      minStockLevel: Number(cells[5].querySelector('input')?.value ?? '0'),
      maxStockLevel: Number(cells[6].querySelector('input')?.value ?? '0'),
      unitPrice: Number(cells[7].querySelector('input')?.value ?? '0'),
    };
    
    try {
      const res = await fetch(${API_BASE}/, { 
        method:'PATCH', 
        headers:{'Content-Type':'application/json','Accept':'application/json'}, 
        body: JSON.stringify(payload) 
      });
      
      if (!res.ok){ 
        const err = await res.text(); 
        throw new Error(err || HTTP );
      }
      
      const updated = await res.json();
      
      // Re-render
      cells[0].innerHTML = <span class="label"></span>;
      cells[1].innerHTML = <span class="label"></span>;
      cells[2].innerHTML = <span class="label"></span>;
      cells[3].innerHTML = <span class="label"></span>;
      cells[4].innerHTML = <span class="label"></span>;
      cells[5].innerHTML = <span class="label"></span>;
      cells[6].innerHTML = <span class="label"></span>;
      cells[7].innerHTML = <span class="label">q{(updated.unitPrice ?? payload.unitPrice).toString()}</span>;
      row.classList.remove('edit-mode');
      
      window.toastManager.success('Inventory item updated successfully');
    } catch (error) {
      window.toastManager.error(Failed to update item: );
    }
  }

  async function deleteRow(row){
    const id = row.dataset.id; 
    if (!id){ 
      row.remove(); 
      window.toastManager.success('Inventory item deleted successfully');
      return; 
    }
    
    if (!confirm('Are you sure you want to delete this item?')) return;
    
    try {
      const res = await fetch(${API_BASE}/, { method:'DELETE' });
      
      if (!res.ok){ 
        const err = await res.text(); 
        throw new Error(err || HTTP );
      }
      
      row.remove();
      window.toastManager.success('Inventory item deleted successfully');
    } catch (error) {
      window.toastManager.error(Failed to delete item: );
    }
  }

  const tbodyExisting = document.querySelector('#inventoryTable tbody');
  if (tbodyExisting) {
    tbodyExisting.addEventListener('click', (e) => {
      const target = e.target;
      const row = target.closest('tr');
      if (!row) return;
      if (target.classList.contains('edit-btn')) {
        makeRowEditable(row);
      } else if (target.classList.contains('save-btn')) {
        saveRow(row);
      } else if (target.classList.contains('delete-btn')) {
        deleteRow(row);
      }
    });
  }

  // Add Item Modal logic
  const addItemIcon = document.getElementById('addItemIcon');
  const addItemModalOverlay = document.getElementById('addItemModalOverlay');
  const closeAddItemModal = document.getElementById('closeAddItemModal');
  const addItemForm = document.getElementById('addItemForm');

  if (addItemIcon) { addItemIcon.addEventListener('click', () => { addItemModalOverlay.style.display = 'flex'; document.body.style.overflow = 'hidden'; }); }
  if (closeAddItemModal) { closeAddItemModal.addEventListener('click', () => { addItemModalOverlay.style.display = 'none'; document.body.style.overflow = ''; }); }
  if (addItemModalOverlay) { addItemModalOverlay.addEventListener('click', (e) => { if (e.target === addItemModalOverlay) { addItemModalOverlay.style.display = 'none'; document.body.style.overflow = ''; } }); }

  if (addItemForm) {
    addItemForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const payload = {
        itemCode: document.getElementById('addItemCode').value,
        itemName: document.getElementById('addItemName').value,
        category: document.getElementById('addCategory').value,
        location: document.getElementById('addLocation').value,
        quantity: Number(document.getElementById('addQuantity').value),
        minStockLevel: 0,
        maxStockLevel: 0,
        unitPrice: Number(document.getElementById('addUnitPrice').value)
      };
      
      try {
        const res = await fetch(API_BASE, { 
          method:'POST', 
          headers:{'Content-Type':'application/json','Accept':'application/json'}, 
          body: JSON.stringify(payload) 
        });
        
        if (!res.ok){ 
          const err = await res.text(); 
          throw new Error(err || HTTP );
        }
        
        const created = await res.json();
        const tr = document.createElement('tr');
        tr.dataset.id = created.itemId;
        tr.innerHTML = 
          <td data-label="Item Code"><span class="label"></span></td>
          <td data-label="Item Name"><span class="label"></span></td>
          <td data-label="Category"><span class="label"></span></td>
          <td><span class="label"></span></td>
          <td><span class="label"></span></td>
          <td><span class="label"></span></td>
          <td><span class="label"></span></td>
          <td><span class="label">q{(created.unitPrice ?? payload.unitPrice).toString()}</span></td>
          <td data-label="Status"><span class="label" data-status=""></span></td>
          <td>
            <button class="btn-edit-label edit-btn">Edit Item</button>
            <div class="action-icons-container">
              <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
              <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
            </div>
          </td>;
        table.querySelector('tbody').prepend(tr);
        addItemModalOverlay.style.display = 'none';
        document.body.style.overflow = '';
        addItemForm.reset();
        
        window.toastManager.success('Inventory item created successfully');
      } catch (error) {
        window.toastManager.error(Failed to create item: );
      }
    });
  }

  // Fix icon paths if needed
  document.querySelectorAll('.action-icons-container img[src="trash.png"]').forEach(el => { el.src = '/images/trash.png'; });
  document.querySelectorAll('.action-icons-container img[src="correct.png"]').forEach(el => { el.src = '/images/correct.png'; });

  // Initial load
  loadInventory();
});
