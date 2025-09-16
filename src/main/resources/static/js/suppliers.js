document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("#suppliersTbody");
  const addSupplierIcon = document.getElementById('addSupplierIcon');
  const addSupplierModalOverlay = document.getElementById('addSupplierModalOverlay');
  const closeAddSupplierModal = document.getElementById('closeAddSupplierModal');
  const addSupplierForm = document.getElementById('addSupplierForm');
  const searchInput = document.getElementById('searchInput');
  const searchIcon = document.getElementById('searchIcon');

  function statusStyles(statusText){
    const isActive = statusText === 'Active';
    return {
      background: isActive ? 'rgba(40, 167, 69, 0.3)' : 'rgba(220, 53, 69, 0.3)',
      borderColor: isActive ? 'rgba(40, 167, 69, 0.6)' : 'rgba(220, 53, 69, 0.6)',
      color: isActive ? '#155724' : '#721c24'
    };
  }

  function appendSupplierRow(s){
    const tr = document.createElement('tr');
    tr.dataset.supplierId = s.supplierId;
    const statusText = s.active ? 'Active' : 'Inactive';
    const styles = statusStyles(statusText);
    tr.innerHTML = 
      <td><span class="label"></span></td>
      <td><span class="label"></span></td>
      <td><span class="label"></span></td>
      <td><span class="label"></span></td>
      <td><span class="label"></span></td>
      <td><span class="label" style="background:;border-color:;color:;"></span></td>
      <td><button class="btn-edit-label edit-btn">Edit Supplier</button></td>;
    tbody.appendChild(tr);
  }

  function renderSuppliers(list){
    tbody.innerHTML = '';
    list.forEach(appendSupplierRow);
  }

  function normalize(text){ return (text||'').toString().toLowerCase().trim(); }
  function deriveStatusFromQuery(q){
    const n = normalize(q);
    if (n === 'active') return 'active';
    if (n === 'inactive') return 'inactive';
    return null;
  }

  async function fetchSuppliers({ q = '', status = null } = {}){
    try {
      const params = new URLSearchParams();
      if (q && q.trim().length > 0) params.append('q', q.trim());
      if (status) params.append('status', status);
      const res = await fetch(/lwms/suppliers/api);
      if (!res.ok) throw new Error('Failed to load suppliers');
      const data = await res.json();
      renderSuppliers(data);
    } catch (e) {
      window.toastManager.error(e.message || 'Failed to load suppliers');
    }
  }

  // Initial load from backend
  fetchSuppliers();

  function makeRowEditable(row) {
    const cells = row.querySelectorAll('td:not(:last-child)');
    cells.forEach((cell, index) => {
      const label = cell.querySelector('.label');
      if (label && index < 5) {
        const input = document.createElement('input');
        input.type = 'text';
        input.className = 'editable-input';
        input.value = label.textContent.trim();
        cell.innerHTML = '';
        cell.appendChild(input);
      }
      if (index === 5) {
        const status = label.textContent.trim();
        const select = document.createElement('select');
        select.className = 'editable-input';
        ['Active','Inactive'].forEach(optionText => {
          const option = document.createElement('option');
          option.value = optionText; option.textContent = optionText; if (optionText === status) option.selected = true; select.appendChild(option);
        });
        cell.innerHTML = ''; cell.appendChild(select);
      }
    });
  }

  async function saveRowChanges(row) {
    const supplierId = row.dataset.supplierId;
    if (!supplierId) { window.toastManager.error('Missing supplier id'); return; }
    const cells = row.querySelectorAll('td');
    const payload = {};
    // 0 name, 1 contact, 2 email, 3 phone, 4 address, 5 status
    const nameInput = cells[0].querySelector('.editable-input'); if (nameInput) payload.supplierName = nameInput.value.trim();
    const contactInput = cells[1].querySelector('.editable-input'); if (contactInput) payload.contactPerson = contactInput.value.trim();
    const emailInput = cells[2].querySelector('.editable-input'); if (emailInput) payload.email = emailInput.value.trim();
    const phoneInput = cells[3].querySelector('.editable-input'); if (phoneInput) payload.phone = phoneInput.value.trim();
    const addressInput = cells[4].querySelector('.editable-input'); if (addressInput) payload.address = addressInput.value.trim();
    const statusSelect = cells[5].querySelector('select.editable-input'); if (statusSelect) payload.active = (statusSelect.value === 'Active');

    try {
      const res = await fetch(/lwms/suppliers/api/, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!res.ok) throw new Error('Failed to update supplier');
      const s = await res.json();
      // Re-render row from server response
      const statusText = s.active ? 'Active' : 'Inactive';
      const styles = statusStyles(statusText);
      cells[0].innerHTML = <span class="label"></span>;
      cells[1].innerHTML = <span class="label"></span>;
      cells[2].innerHTML = <span class="label"></span>;
      cells[3].innerHTML = <span class="label"></span>;
      cells[4].innerHTML = <span class="label"></span>;
      cells[5].innerHTML = <span class="label" style="background:;border-color:;color:;"></span>;
      const actionsCell = row.querySelector('td:last-child');
      actionsCell.innerHTML = '<button class="btn-edit-label edit-btn">Edit Supplier</button>';
      window.toastManager.success('Supplier updated successfully');
    } catch (e) {
      window.toastManager.error(e.message || 'Failed to update supplier');
    }
  }

  async function deleteRow(row) {
    const supplierId = row.dataset.supplierId;
    if (!supplierId) { row.remove(); return; }
    if (!confirm('Are you sure you want to delete this supplier?')) return;
    try {
      const res = await fetch(/lwms/suppliers/api/, { method: 'DELETE' });
      if (res.status !== 204 && !res.ok) throw new Error('Failed to delete supplier');
      row.remove();
      window.toastManager.success('Supplier deleted successfully');
    } catch (e) {
      window.toastManager.error(e.message || 'Failed to delete supplier');
    }
  }

  // Event delegation for row actions
  tbody.addEventListener('click', (e) => {
    const target = e.target; const row = target.closest('tr'); if (!row) return;
    if (target.classList.contains('edit-btn')) {
      makeRowEditable(row);
      const actionsCell = row.querySelector('td:last-child');
      actionsCell.innerHTML = 
        <div class="action-icons-container">
          <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
          <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
        </div>;
    }
    if (target.classList.contains('save-btn')) { saveRowChanges(row); }
    if (target.classList.contains('delete-btn')) { deleteRow(row); }
  });

  // Search functionality
  function onSearch() {
    const q = searchInput.value.trim();
    const status = deriveStatusFromQuery(q);
    fetchSuppliers({ q, status });
  }
  if (searchInput) searchInput.addEventListener('input', onSearch);
  if (searchIcon) searchIcon.addEventListener('click', onSearch);

  // Add modal open/close
  if (addSupplierIcon) addSupplierIcon.addEventListener('click', () => { addSupplierModalOverlay.style.display = 'flex'; document.body.style.overflow = 'hidden'; });
  if (closeAddSupplierModal) closeAddSupplierModal.addEventListener('click', () => { addSupplierModalOverlay.style.display = 'none'; document.body.style.overflow = ''; });
  if (addSupplierModalOverlay) addSupplierModalOverlay.addEventListener('click', (e) => { if (e.target === addSupplierModalOverlay) { addSupplierModalOverlay.style.display = 'none'; document.body.style.overflow = ''; } });

  // Create supplier
  if (addSupplierForm) {
    addSupplierForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const payload = {
        supplierName: document.getElementById('addSupplierName').value,
        contactPerson: document.getElementById('addContactPerson').value,
        email: document.getElementById('addEmail').value,
        phone: document.getElementById('addPhone').value,
        address: document.getElementById('addAddress').value,
        active: document.getElementById('addActive').checked
      };
      try {
        const res = await fetch('/lwms/suppliers/api', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Failed to create supplier');
        const s = await res.json();
        appendSupplierRow(s);
        addSupplierModalOverlay.style.display = 'none'; document.body.style.overflow = ''; addSupplierForm.reset();
        window.toastManager.success('Supplier created successfully');
      } catch (e) {
        window.toastManager.error(e.message || 'Failed to create supplier');
      }
    });
  }
});
