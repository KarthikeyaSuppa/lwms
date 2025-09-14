document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("tbody");

  function makeRowEditable(row) {
    const cells = row.querySelectorAll("td:not(:last-child)");
    cells.forEach((cell, index) => {
      const label = cell.querySelector(".label");
      if (label && index === 0) {
        const input = document.createElement("input");
        input.type = "text"; input.className = "editable-input"; input.value = label.textContent.trim();
        cell.innerHTML = ""; cell.appendChild(input);
      }
      if (index === 1) {
        const input = document.createElement("input");
        input.type = "text"; input.className = "editable-input"; input.value = label.textContent.trim();
        cell.innerHTML = ""; cell.appendChild(input);
      }
      if (index === 2) {
        const textarea = document.createElement("textarea");
        textarea.className = "editable-input"; textarea.value = label.textContent.trim(); textarea.rows = 3;
        cell.innerHTML = ""; cell.appendChild(textarea);
      }
      if (index === 3) {
        const currentValue = label.textContent.trim();
        const container = document.createElement('div'); container.className = 'parent-category-cell-container';
        const select = document.createElement('div'); select.className = 'parent-category-select'; select.textContent = currentValue === '-' ? 'None (Top-level)' : currentValue; select.dataset.currentValue = currentValue === '-' ? '' : currentValue;
        const dropdown = document.createElement('div'); dropdown.className = 'parent-category-dropdown';
        const options = ['', 'Electronics', 'Furniture', 'Tools', 'Safety Equipment'];
        options.forEach(optionText => {
          const item = document.createElement('div'); item.className = `parent-category-dropdown-item ${optionText === select.dataset.currentValue ? 'selected' : ''}`; item.textContent = optionText === '' ? 'None (Top-level)' : optionText; item.dataset.value = optionText; dropdown.appendChild(item);
        });
        select.addEventListener('click', (e) => { e.stopPropagation(); toggleParentCategoryDropdown(dropdown); });
        dropdown.querySelectorAll('.parent-category-dropdown-item').forEach(item => {
          item.addEventListener('click', (e) => {
            e.stopPropagation(); const selectedValue = item.dataset.value;
            select.textContent = selectedValue === '' ? 'None (Top-level)' : selectedValue; select.dataset.currentValue = selectedValue;
            dropdown.querySelectorAll('.parent-category-dropdown-item').forEach(dropdownItem => dropdownItem.classList.remove('selected'));
            item.classList.add('selected'); dropdown.style.display = 'none';
          });
        });
        container.appendChild(select); container.appendChild(dropdown);
        cell.innerHTML = ''; cell.appendChild(container);
      }
    });
  }

  function saveRowChanges(row) {
    const cells = row.querySelectorAll("td");
    cells.forEach((cell, index) => {
      if (index === 0) {
        const input = cell.querySelector(".editable-input");
        if (input) { const label = document.createElement("span"); label.className = "label"; label.textContent = input.value; cell.innerHTML = ""; cell.appendChild(label); }
      } else if (index === 1) {
        const input = cell.querySelector(".editable-input");
        if (input) { const label = document.createElement("span"); label.className = "label"; label.textContent = input.value; cell.innerHTML = ""; cell.appendChild(label); }
      } else if (index === 2) {
        const textarea = cell.querySelector("textarea.editable-input");
        if (textarea) { const label = document.createElement("span"); label.className = "label"; label.textContent = textarea.value; cell.innerHTML = ""; cell.appendChild(label); }
      } else if (index === 3) {
        const container = cell.querySelector('.parent-category-cell-container');
        if (container) { const select = container.querySelector('.parent-category-select'); const newValue = select.dataset.currentValue; const label = document.createElement('span'); label.className = 'label'; label.textContent = newValue === '' ? '-' : newValue; cell.innerHTML = ''; cell.appendChild(label); }
      } else if (index === 4) {
        const actionsCell = row.querySelector("td:last-child"); actionsCell.innerHTML = "<button class=\"btn-edit-label edit-btn\">Edit Category</button>";
      }
    });
  }

  function deleteRow(row) {
    if (confirm("Are you sure you want to delete this category?")) { row.remove(); }
  }

  tbody.addEventListener("click", (e) => {
    const target = e.target; const row = target.closest("tr"); if (!row) return;
    if (target.classList.contains("edit-btn")) {
      makeRowEditable(row);
      const actionsCell = row.querySelector("td:last-child");
      actionsCell.innerHTML = `
        <div class="action-icons-container">
          <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
          <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
        </div>`;
    }
    if (target.classList.contains("save-btn")) { saveRowChanges(row); }
    if (target.classList.contains("delete-btn")) { deleteRow(row); }
  });

  function toggleParentCategoryDropdown(dropdown) {
    document.querySelectorAll('.parent-category-dropdown').forEach(dd => { if (dd !== dropdown) { dd.style.display = 'none'; } });
    dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
  }

  document.addEventListener('click', (e) => {
    if (!e.target.closest('.parent-category-cell-container')) {
      document.querySelectorAll('.parent-category-dropdown').forEach(dropdown => { dropdown.style.display = 'none'; });
    }
  });

  const addCategoryIcon = document.getElementById("addCategoryIcon");
  const addCategoryModalOverlay = document.getElementById("addCategoryModalOverlay");
  const closeAddCategoryModal = document.getElementById("closeAddCategoryModal");
  const addCategoryForm = document.getElementById("addCategoryForm");
  const searchInput = document.getElementById("searchInput");
  const searchIcon = document.getElementById("searchIcon");

  const addParentCategorySelect = document.getElementById('addParentCategorySelect');
  const addParentCategoryDropdown = document.getElementById('addParentCategoryDropdown');

  if (addParentCategorySelect && addParentCategoryDropdown) {
    addParentCategorySelect.addEventListener('click', (e) => { e.stopPropagation(); toggleParentCategoryDropdown(addParentCategoryDropdown); });
    addParentCategoryDropdown.querySelectorAll('.parent-category-dropdown-item').forEach(item => {
      item.addEventListener('click', (e) => {
        e.stopPropagation(); const selectedValue = item.dataset.value;
        addParentCategorySelect.textContent = selectedValue === '' ? 'None (Top-level category)' : selectedValue; addParentCategorySelect.dataset.currentValue = selectedValue;
        addParentCategoryDropdown.querySelectorAll('.parent-category-dropdown-item').forEach(dropdownItem => dropdownItem.classList.remove('selected'));
        item.classList.add('selected'); addParentCategoryDropdown.style.display = 'none';
      });
    });
  }

  function normalize(text){ return (text||"").toString().toLowerCase().trim(); }
  function filterRows(){
    const query = normalize(searchInput.value);
    const rows = tbody.querySelectorAll("tr");
    rows.forEach(row => {
      const cells = row.querySelectorAll("td");
      const categoryName = normalize(cells[0]?.querySelector(".editable-input")?.value || cells[0]?.textContent);
      const categoryCode = normalize(cells[1]?.querySelector(".editable-input")?.value || cells[1]?.textContent);
      const description = normalize(cells[2]?.querySelector(".editable-input")?.value || cells[2]?.textContent);
      const parentCategory = normalize(cells[3]?.querySelector(".editable-input")?.value || cells[3]?.textContent);
      const matches = categoryName.includes(query) || categoryCode.includes(query) || description.includes(query) || parentCategory.includes(query);
      row.style.display = matches ? "" : "none";
    });
  }
  if (searchInput) searchInput.addEventListener("input", filterRows);
  if (searchIcon) searchIcon.addEventListener("click", filterRows);

  addCategoryIcon.addEventListener("click", () => { addCategoryModalOverlay.style.display = "flex"; document.body.style.overflow = "hidden"; });
  closeAddCategoryModal.addEventListener("click", () => { addCategoryModalOverlay.style.display = "none"; document.body.style.overflow = ""; });
  addCategoryModalOverlay.addEventListener("click", (e) => { if (e.target === addCategoryModalOverlay) { addCategoryModalOverlay.style.display = "none"; document.body.style.overflow = ""; } });

  addCategoryForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const newCategoryData = {
      categoryName: document.getElementById("addCategoryName").value,
      categoryCode: document.getElementById("addCategoryCode").value,
      description: document.getElementById("addDescription").value,
      parentCategory: addParentCategorySelect ? addParentCategorySelect.dataset.currentValue : '',
    };
    addCategoryToTable(newCategoryData);
    addCategoryModalOverlay.style.display = "none"; document.body.style.overflow = ""; addCategoryForm.reset();
    if (addParentCategorySelect) {
      addParentCategorySelect.textContent = 'None (Top-level category)'; addParentCategorySelect.dataset.currentValue = '';
      addParentCategoryDropdown.querySelectorAll('.parent-category-dropdown-item').forEach(item => { item.classList.remove('selected'); if (item.dataset.value === '') { item.classList.add('selected'); } });
    }
  });

  function addCategoryToTable(data){
    const newRow = document.createElement("tr");
    newRow.innerHTML = `
      <td><span class="label">${data.categoryName}</span></td>
      <td><span class="label">${data.categoryCode}</span></td>
      <td><span class="label">${data.description}</span></td>
      <td><span class="label">${data.parentCategory === "" ? "-" : data.parentCategory}</span></td>
      <td><button class="btn-edit-label edit-btn">Edit Category</button></td>`;
    tbody.appendChild(newRow);
    if (typeof filterRows === "function" && searchInput && searchInput.value.trim() !== "") { filterRows(); }
  }
}); 