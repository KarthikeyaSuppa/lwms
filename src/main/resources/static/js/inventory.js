document.addEventListener('DOMContentLoaded', () => {
  const searchInput = document.getElementById('searchInput');
  const table = document.getElementById('inventoryTable');
  const tbody = table ? table.querySelector('tbody') : null;
  const API_BASE = '/inventory/api';

  function formatCurrency(val){
    const num = Number(val ?? 0);
    return `$${num.toFixed(2)}`;
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

  function renderRows(list){
    if (!tbody) return;
    tbody.innerHTML = '';
    (list || []).forEach(item => {
      const status = computeStatus(item);
      const tr = document.createElement('tr');
      tr.dataset.id = item.itemId;
      tr.innerHTML = `
        <td data-label="Item Code"><span class="label">${item.itemCode ?? ''}</span></td>
        <td data-label="Item Name"><span class="label">${item.itemName ?? ''}</span></td>
        <td data-label="Category"><span class="label">${item.category ?? ''}</span></td>
        <td><span class="label">${item.location ?? ''}</span></td>
        <td><span class="label">${Number(item.quantity ?? 0)}</span></td>
        <td><span class="label">${Number(item.minStockLevel ?? 0)}</span></td>
        <td><span class="label">${Number(item.maxStockLevel ?? 0)}</span></td>
        <td><span class="label">${formatCurrency(item.unitPrice)}</span></td>
        <td data-label="Status"><span class="label" data-status="${status}">${humanizeStatus(status)}</span></td>
        <td>
          <button class="btn-edit-label edit-btn" type="button">Edit Item</button>
          <div class="action-icons-container">
            <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
            <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
          </div>
        </td>
      `;
      tbody.appendChild(tr);
    });
  }

  async function loadInventory(){
    try {
      const q = searchInput?.value?.trim();
      const url = q ? `${API_BASE}?q=${encodeURIComponent(q)}` : API_BASE;
      const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
      if (!res.ok) throw new Error('Failed to load inventory');
      const data = await res.json();
      renderRows(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error(e);
      window.toastManager?.error('Failed to load inventory');
    }
  }

  // Debounce helper
  function debounce(fn, ms){ let t; return (...args) => { clearTimeout(t); t = setTimeout(() => fn(...args), ms); }; }

  if (searchInput && tbody) {
    searchInput.addEventListener('input', debounce(loadInventory, 300));
  }

  // Modal wiring
  const addItemIcon = document.getElementById('addItemIcon');
  const addItemModalOverlay = document.getElementById('addItemModalOverlay');
  const closeAddItemModal = document.getElementById('closeAddItemModal');
  const addItemForm = document.getElementById('addItemForm');

  // Searchable dropdown helpers
  const catInput = document.getElementById('addCategory');
  const catList = document.getElementById('categoryOptions');
  const catIdHidden = document.getElementById('addCategoryId');
  const locInput = document.getElementById('addLocation');
  const locList = document.getElementById('locationOptions');
  const locIdHidden = document.getElementById('addLocationId');
  const supInput = document.getElementById('addSupplier');
  const supList = document.getElementById('supplierOptions');
  const supIdHidden = document.getElementById('addSupplierId');

  async function searchCategories(q){
    const url = q ? `/categories/api?q=${encodeURIComponent(q)}` : '/categories/api';
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) return [];
    return res.json();
  }
  async function searchLocations(q){
    const url = q ? `/locations/api?q=${encodeURIComponent(q)}` : '/locations/api';
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) return [];
    return res.json();
  }
  async function searchSuppliers(q){
    const url = q ? `/suppliers/api?q=${encodeURIComponent(q)}` : '/suppliers/api';
    const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
    if (!res.ok) return [];
    return res.json();
  }

  function bindDatalist(inputEl, listEl, idHiddenEl, getLabel, getId, searchFn){
    if (!inputEl || !listEl) return;
    const doSearch = debounce(async () => {
      const q = inputEl.value.trim();
      const items = await searchFn(q);
      listEl.innerHTML = '';
      (items || []).forEach(it => {
        const opt = document.createElement('option');
        opt.value = getLabel(it);
        opt.dataset.id = String(getId(it));
        listEl.appendChild(opt);
      });
    }, 250);
    inputEl.addEventListener('input', () => { idHiddenEl && (idHiddenEl.value = ''); doSearch(); });
    inputEl.addEventListener('change', () => {
      const val = inputEl.value;
      const match = Array.from(listEl.options).find(o => o.value === val);
      if (match && idHiddenEl) idHiddenEl.value = match.dataset.id || '';
    });
  }

  function ensureIdFromLabel(inputEl, listEl, idHiddenEl){
    if (!inputEl || !listEl || !idHiddenEl) return;
    if (idHiddenEl.value) return;
    const val = inputEl.value.trim();
    const match = Array.from(listEl.options).find(o => o.value === val);
    if (match) idHiddenEl.value = match.dataset.id || '';
  }

  bindDatalist(
    catInput, catList, catIdHidden,
    (c) => c.categoryCode,
    (c) => c.categoryId,
    searchCategories
  );
  bindDatalist(
    locInput, locList, locIdHidden,
    (l) => l.locationCode || `${l.zone ?? ''}${l.aisle ?? ''}-${l.rack ?? ''}-${l.shelf ?? ''}`,
    (l) => l.locationId,
    searchLocations
  );
  bindDatalist(
    supInput, supList, supIdHidden,
    (s) => s.supplierName,
    (s) => s.supplierId,
    searchSuppliers
  );

  function openAddModal(){ if (addItemModalOverlay){ addItemModalOverlay.style.display = 'flex'; document.body.style.overflow = 'hidden'; } }
  function closeAddModal(){ if (addItemModalOverlay){ addItemModalOverlay.style.display = 'none'; document.body.style.overflow = ''; } }

  if (addItemIcon) {
    addItemIcon.addEventListener('click', openAddModal);
    addItemIcon.addEventListener('keyup', (e) => { if (e.key === 'Enter' || e.key === ' ') openAddModal(); });
  }
  if (closeAddItemModal) closeAddItemModal.addEventListener('click', closeAddModal);
  if (addItemModalOverlay) addItemModalOverlay.addEventListener('click', (e) => { if (e.target === addItemModalOverlay) closeAddModal(); });

  if (addItemForm) {
    addItemForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      // Try to infer IDs if user typed exact labels
      ensureIdFromLabel(catInput, catList, catIdHidden);
      ensureIdFromLabel(locInput, locList, locIdHidden);
      ensureIdFromLabel(supInput, supList, supIdHidden);

      const payload = {
        itemCode: document.getElementById('addItemCode')?.value?.trim(),
        itemName: document.getElementById('addItemName')?.value?.trim(),
        // Prefer IDs
        categoryId: catIdHidden?.value ? Number(catIdHidden.value) : undefined,
        locationId: locIdHidden?.value ? Number(locIdHidden.value) : undefined,
        supplierId: supIdHidden?.value ? Number(supIdHidden.value) : undefined,
        // Fallback legacy labels
        category: catInput?.value?.trim(),
        location: locInput?.value?.trim(),
        quantity: Number(document.getElementById('addQuantity')?.value ?? '0'),
        minStockLevel: 0,
        maxStockLevel: 0,
        unitPrice: Number(document.getElementById('addUnitPrice')?.value ?? '0'),
      };

      // Frontend validation for required fields
      if (!payload.itemCode || !payload.itemName) {
        window.toastManager?.error('Item Code and Item Name are required');
        return;
      }
      if (!(payload.categoryId || payload.category)) {
        window.toastManager?.error('Please choose a Category');
        return;
      }
      if (!(payload.locationId || payload.location)) {
        window.toastManager?.error('Please choose a Location');
        return;
      }

      try {
        const res = await fetch(API_BASE, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
          body: JSON.stringify(payload)
        });
        if (!res.ok) {
          const text = await res.text();
          window.toastManager?.error(text || 'Create failed');
          throw new Error(text || 'Create failed');
        }
        await res.json();
        window.toastManager?.success('Inventory item created');
        closeAddModal();
        addItemForm.reset();
        if (catIdHidden) catIdHidden.value = '';
        if (locIdHidden) locIdHidden.value = '';
        if (supIdHidden) supIdHidden.value = '';
        await loadInventory();
      } catch (err) {
        console.error(err);
      }
    });
  }

  // Inline edit/save/delete
  function makeRowEditable(row) {
    const cells = row.querySelectorAll('td');
    const editableIndexes = [0,1,2,3,4,5,6,7];
    editableIndexes.forEach(idx => {
      const cell = cells[idx];
      const label = cell.querySelector('.label');
      if (!label) return;
      const input = document.createElement('input');
      input.type = (idx >= 4 && idx <= 7) ? 'number' : 'text';
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
    if (!id) { row.classList.remove('edit-mode'); return; }
    const cells = row.querySelectorAll('td');
    const [itemCode, itemName, category, location, quantity, minStock, maxStock, unitPrice] = [0,1,2,3,4,5,6,7].map(i => cells[i].querySelector('input')?.value?.trim());
    const payload = {
      itemCode, itemName,
      quantity: Number(quantity ?? '0'),
      minStockLevel: Number(minStock ?? '0'),
      maxStockLevel: Number(maxStock ?? '0'),
      unitPrice: Number(unitPrice ?? '0'),
      // Note: For inline edits we keep legacy label update for category/location to avoid extra controls
      category, location
    };
    try {
      const res = await fetch(`${API_BASE}/${id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) throw new Error(await res.text() || 'Update failed');
      const updated = await res.json();
      // Re-render single row with updated data
      const status = computeStatus(updated);
      row.innerHTML = `
        <td data-label="Item Code"><span class="label">${updated.itemCode ?? ''}</span></td>
        <td data-label="Item Name"><span class="label">${updated.itemName ?? ''}</span></td>
        <td data-label="Category"><span class="label">${updated.category ?? ''}</span></td>
        <td><span class="label">${updated.location ?? ''}</span></td>
        <td><span class="label">${Number(updated.quantity ?? 0)}</span></td>
        <td><span class="label">${Number(updated.minStockLevel ?? 0)}</span></td>
        <td><span class="label">${Number(updated.maxStockLevel ?? 0)}</span></td>
        <td><span class="label">${formatCurrency(updated.unitPrice)}</span></td>
        <td data-label="Status"><span class="label" data-status="${status}">${humanizeStatus(status)}</span></td>
        <td>
          <button class="btn-edit-label edit-btn" type="button">Edit Item</button>
          <div class="action-icons-container">
            <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
            <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
          </div>
        </td>`;
      // Exit edit mode so only the Edit button shows
      row.classList.remove('edit-mode');
      window.toastManager?.success('Item updated');
    } catch (e) {
      console.error(e);
      window.toastManager?.error('Failed to update item');
    }
  }

  async function deleteRow(row) {
    const id = row.dataset.id;
    if (!id) { row.remove(); return; }
    try {
      const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
      if (!res.ok && res.status !== 204) throw new Error(await res.text() || 'Delete failed');
      row.remove();
      window.toastManager?.success('Item deleted');
    } catch (e) {
      console.error(e);
      window.toastManager?.error('Failed to delete item');
    }
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
        if (confirm('Are you sure you want to delete this item?')) deleteRow(row);
      }
    });
  }

  // Ensure action icon paths
  document.querySelectorAll('.action-icons-container img[src="trash.png"]').forEach(el => { el.src = '/images/trash.png'; });
  document.querySelectorAll('.action-icons-container img[src="correct.png"]').forEach(el => { el.src = '/images/correct.png'; });

  // Initial load
  loadInventory();
});