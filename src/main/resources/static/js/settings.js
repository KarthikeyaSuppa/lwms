document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("tbody");
  const searchInput = document.getElementById("searchInput");
  const toastContainer = document.getElementById('toast-container');

  function showToast(message, variant = 'success', timeoutMs = 2500) {
    const toast = document.createElement('div');
    toast.className = `toast ${variant === 'error' ? 'toast-error' : 'toast-success'}`;
    toast.innerHTML = `<span class="msg">${message}</span><button class="close-btn" aria-label="Close">×</button>`;
    toast.querySelector('.close-btn').addEventListener('click', () => toast.remove());
    toastContainer.appendChild(toast);
    setTimeout(() => toast.remove(), timeoutMs);
  }

  fetch('/lwms/users').then(r => r.json()).then(users => {
    tbody.innerHTML = '';
    users.forEach(u => appendUserRow(u));
  }).catch(() => {});

  function formatDate(d) {
    if (!d) return '';
    const date = new Date(d);
    return isNaN(date) ? '' : date.toLocaleDateString('en-US', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  function appendUserRow(u) {
    const tr = document.createElement('tr');
    tr.dataset.userId = u.userId;
    tr.innerHTML = `
      <td><span class="label">${u.username}</span></td>
      <td><span class="label">${u.email}</span></td>
      <td><span class="label">${u.firstName}</span></td>
      <td><span class="label">${u.lastName}</span></td>
      <td><span class="label">${u.roleName || ''}</span></td>
      <td><span class="label">${u.active ? 'Active' : 'Inactive'}</span></td>
      <td><span class="label">${formatDate(u.createdAt)}</span></td>
      <td><span class="label">${formatDate(u.lastLogin)}</span></td>
      <td><button class="btn-edit-label edit-btn">Edit User</button></td>
    `;
    tbody.appendChild(tr);
  }

  searchInput.addEventListener("input", (e) => { filterTable(e.target.value); });
  function filterTable(searchTerm) {
    const term = searchTerm.toLowerCase();
    [...tbody.querySelectorAll('tr')].forEach(row => {
      const cells = row.querySelectorAll('td');
      // Search by Username (0), First Name (2), Last Name (3), Role (4), Status (5). Excludes Email (1)
      const indices = [0, 2, 3, 4, 5];
      const found = indices.some(i => (cells[i]?.textContent || '').toLowerCase().includes(term));
      row.style.display = found ? '' : 'none';
    });
  }

  function autoResizeInput(input) {
    const temp = document.createElement('span');
    temp.style.visibility = 'hidden'; temp.style.position = 'absolute';
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
    const cells = row.querySelectorAll('td:not(:last-child)');
    cells.forEach((cell, index) => {
      const label = cell.querySelector('.label');
      if (label) {
        if (index === 0 || index === 6 || index === 7) {
          const readonlyLabel = document.createElement('span');
          readonlyLabel.className = 'readonly-label';
          readonlyLabel.textContent = label.textContent; cell.innerHTML = ''; cell.appendChild(readonlyLabel);
        } else if (index === 4) {
          const container = document.createElement('div'); container.className = 'role-cell-container';
          const select = document.createElement('div'); select.className = 'role-select'; select.textContent = label.textContent; select.dataset.currentValue = label.textContent;
          const dropdown = document.createElement('div'); dropdown.className = 'role-dropdown';
          const roles = ['Admin', 'Manager', 'Supervisor', 'Inventory Controller', 'Operator', 'Viewer'];
          roles.forEach(role => { const item = document.createElement('div'); item.className = `role-dropdown-item ${role === label.textContent ? 'selected' : ''}`; item.textContent = role; item.dataset.value = role; dropdown.appendChild(item); });
          select.addEventListener('click', (e) => { e.stopPropagation(); toggleRoleDropdown(dropdown); });
          dropdown.querySelectorAll('.role-dropdown-item').forEach(item => { item.addEventListener('click', (e) => { e.stopPropagation(); const selectedRole = item.dataset.value; select.textContent = selectedRole; select.dataset.currentValue = selectedRole; dropdown.querySelectorAll('.role-dropdown-item').forEach(dropdownItem => { dropdownItem.classList.remove('selected'); }); item.classList.add('selected'); dropdown.style.display = 'none'; }); });
          container.appendChild(select); container.appendChild(dropdown);
          cell.innerHTML = ''; cell.appendChild(container);
        } else if (index === 5) {
          const container = document.createElement('div'); container.className = 'role-cell-container';
          const select = document.createElement('div'); select.className = 'role-select'; select.textContent = label.textContent; select.dataset.currentValue = label.textContent;
          const dropdown = document.createElement('div'); dropdown.className = 'role-dropdown';
          const statuses = ['Active', 'Inactive'];
          statuses.forEach(st => { const item = document.createElement('div'); item.className = `role-dropdown-item ${st === label.textContent ? 'selected' : ''}`; item.textContent = st; item.dataset.value = st; dropdown.appendChild(item); });
          select.addEventListener('click', (e) => { e.stopPropagation(); toggleRoleDropdown(dropdown); });
          dropdown.querySelectorAll('.role-dropdown-item').forEach(item => { item.addEventListener('click', (e) => { e.stopPropagation(); const selected = item.dataset.value; select.textContent = selected; select.dataset.currentValue = selected; dropdown.querySelectorAll('.role-dropdown-item').forEach(dropdownItem => { dropdownItem.classList.remove('selected'); }); item.classList.add('selected'); dropdown.style.display = 'none'; }); });
          container.appendChild(select); container.appendChild(dropdown);
          cell.innerHTML = ''; cell.appendChild(container);
        } else {
          const input = document.createElement('input'); input.type = 'text'; input.className = 'editable-input'; input.value = label.textContent; input.addEventListener('input', () => autoResizeInput(input)); input.addEventListener('focus', () => autoResizeInput(input)); autoResizeInput(input); cell.innerHTML = ''; cell.appendChild(input);
        }
      }
    });
  }

  function toggleRoleDropdown(dropdown) {
    document.querySelectorAll('.role-dropdown').forEach(dd => { if (dd !== dropdown) { dd.style.display = 'none'; } });
    dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
  }

  function saveRowChanges(row) {
    const cells = row.querySelectorAll('td:not(:last-child)');
    const updated = {};
    cells.forEach((cell, index) => {
      const input = cell.querySelector('.editable-input');
      const roleContainer = cell.querySelector('.role-cell-container');
      let value = null;
      if (input) {
        value = input.value.trim();
      } else if (roleContainer) {
        const select = roleContainer.querySelector('.role-select');
        value = select.dataset.currentValue;
      } else {
        const label = cell.querySelector('.label');
        value = label ? label.textContent.trim() : '';
      }
      if (index === 1) updated.email = value;
      if (index === 2) updated.firstName = value;
      if (index === 3) updated.lastName = value;
      if (index === 4) updated.roleName = value;
      if (index === 5) updated.active = (value.toLowerCase() === 'active');
    });

    const userId = row.dataset.userId;
    if (!userId) { showToast('Missing user id', 'error'); return; }

    fetch(`/lwms/users/${encodeURIComponent(userId)}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updated)
    }).then(async (res) => {
      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || 'Update failed');
      }
      return res.json();
    }).then((u) => {
      cells.forEach((cell, index) => {
        if (index === 6 || index === 7) return;
        const roleContainer = cell.querySelector('.role-cell-container');
        const input = cell.querySelector('.editable-input');
        let displayValue = '';
        switch(index){
          case 1: displayValue = u.email; break;
          case 2: displayValue = u.firstName; break;
          case 3: displayValue = u.lastName; break;
          case 4: displayValue = u.roleName; break;
          case 5: displayValue = u.active ? 'Active' : 'Inactive'; break;
          default: break;
        }
        if (index === 0) return;
        if (roleContainer || input) {
          const label = document.createElement('span');
          label.className = 'label';
          label.textContent = displayValue || (cell.querySelector('.label')?.textContent || '');
          cell.innerHTML = '';
          cell.appendChild(label);
        }
      });
      const actionsCell = row.querySelector('td:last-child');
      actionsCell.innerHTML = '<button class="btn-edit-label edit-btn">Edit User</button>';
      showToast('User updated successfully', 'success');
    }).catch((err) => { showToast(err.message || 'Update failed', 'error'); });
  }

  function deleteRow(row) {
    const userId = row.dataset.userId;
    if (!userId) { row.remove(); showToast('User deleted successfully', 'success'); return; }
    if (!confirm('Are you sure you want to delete this user?')) return;
    fetch(`/lwms/users/${encodeURIComponent(userId)}`, { method: 'DELETE' }).then(res => {
      if (!res.ok && res.status !== 204) throw new Error('Delete failed');
      row.remove();
      showToast('User deleted successfully', 'success');
    }).catch(err => showToast(err.message || 'Delete failed', 'error'));
  }

  tbody.addEventListener("click", (e) => {
    const target = e.target;
    if (target.classList.contains("edit-btn")) {
      const row = target.closest("tr"); makeRowEditable(row);
      const actionsCell = row.querySelector('td:last-child');
      actionsCell.innerHTML = `
        <div class="action-icons-container">
          <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
          <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
        </div>
      `;
    }
    if (target.classList.contains("save-btn")) { const row = target.closest("tr"); saveRowChanges(row); }
    if (target.classList.contains("delete-btn")) { const row = target.closest("tr"); deleteRow(row); }
  });

  const addUserIcon = document.getElementById('addUserIcon');
  const addUserModalOverlay = document.getElementById('addUserModalOverlay');
  const closeAddUserModal = document.getElementById('closeAddUserModal');
  const addUserForm = document.getElementById('addUserForm');
  addUserIcon.addEventListener('click', () => { addUserModalOverlay.style.display = 'flex'; document.body.style.overflow = 'hidden'; });
  closeAddUserModal.addEventListener('click', () => { addUserModalOverlay.style.display = 'none'; document.body.style.overflow = ''; });
  addUserModalOverlay.addEventListener('click', (e) => { if (e.target === addUserModalOverlay) { addUserModalOverlay.style.display = 'none'; document.body.style.overflow = ''; } });
  addUserForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const firstName = document.getElementById('addFirstName').value.trim();
    const lastName = document.getElementById('addLastName').value.trim();
    const email = document.getElementById('addEmail').value.trim();
    const username = document.getElementById('addUsername').value.trim();
    const password = document.getElementById('addPassword').value;
    const roleName = document.getElementById('addRole').value;

    fetch('/lwms/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ firstName, lastName, email, username, password, roleName })
    }).then(async (res) => {
      if (!res.ok) { const msg = await res.text(); throw new Error(msg || 'Create failed'); }
      return res.json();
    }).then((u) => {
      appendUserRow(u);
      addUserModalOverlay.style.display = 'none';
      document.body.style.overflow = '';
      addUserForm.reset();
      showToast('User created successfully', 'success');
    }).catch(err => showToast(err.message || 'Create failed', 'error'));
  });
}); 