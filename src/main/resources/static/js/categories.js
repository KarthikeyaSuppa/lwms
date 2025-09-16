document.addEventListener("DOMContentLoaded", () => {
  const tbody = document.querySelector("tbody");
  const addCategoryIcon = document.getElementById("addCategoryIcon");
  const addCategoryModalOverlay = document.getElementById("addCategoryModalOverlay");
  const closeAddCategoryModal = document.getElementById("closeAddCategoryModal");
  const addCategoryForm = document.getElementById("addCategoryForm");
  const searchInput = document.getElementById("searchInput");
  const searchIcon = document.getElementById("searchIcon");

  const API_BASE = "/categories/api"; // also available at /lwms/categories/api

  function normalize(text){ return (text||"").toString().toLowerCase().trim(); }

  function renderRows(categories){
    tbody.innerHTML = "";
    categories.forEach(cat => {
      const row = document.createElement("tr");
      row.dataset.id = cat.categoryId;
      row.innerHTML = 
        <td><span class="label"></span></td>
        <td><span class="label"></span></td>
        <td><span class="label"></span></td>
        <td><button class="btn-edit-label edit-btn">Edit Category</button></td>;
      tbody.appendChild(row);
    });
  }

  async function loadCategories(){
    const q = searchInput?.value?.trim();
    const url = q ? ${API_BASE}?q= : API_BASE;
    const res = await fetch(url, { headers: { "Accept": "application/json" } });
    if (!res.ok) { console.error("Failed to load categories"); return; }
    const data = await res.json();
    renderRows(Array.isArray(data) ? data : []);
  }

  function makeRowEditable(row) {
    const cells = row.querySelectorAll("td:not(:last-child)");
    cells.forEach((cell, index) => {
      const label = cell.querySelector(".label");
      if (!label) return;
      if (index === 0 || index === 1) {
        const input = document.createElement("input");
        input.type = "text"; input.className = "editable-input"; input.value = label.textContent.trim();
        cell.innerHTML = ""; cell.appendChild(input);
      }
      if (index === 2) {
        const textarea = document.createElement("textarea");
        textarea.className = "editable-input"; textarea.value = label.textContent.trim(); textarea.rows = 3;
        cell.innerHTML = ""; cell.appendChild(textarea);
      }
    });
  }

  async function saveRowChanges(row) {
    const id = row.dataset.id;
    if (!id) return;
    const cells = row.querySelectorAll("td");
    const categoryName = cells[0]?.querySelector(".editable-input")?.value?.trim();
    const categoryCode = cells[1]?.querySelector(".editable-input")?.value?.trim();
    const description = cells[2]?.querySelector("textarea.editable-input")?.value?.trim();

    try {
      const res = await fetch(${API_BASE}/, {
        method: "PATCH",
        headers: { "Content-Type": "application/json", "Accept": "application/json" },
        body: JSON.stringify({ categoryName, categoryCode, description })
      });
      
      if (!res.ok) {
        const errText = await res.text();
        throw new Error(errText || HTTP );
      }
      
      const updated = await res.json();

      // Re-render row in read mode using returned values
      cells[0].innerHTML = <span class="label"></span>;
      cells[1].innerHTML = <span class="label"></span>;
      cells[2].innerHTML = <span class="label"></span>;
      const actionsCell = row.querySelector("td:last-child");
      actionsCell.innerHTML = "<button class=\"btn-edit-label edit-btn\">Edit Category</button>";
      
      window.toastManager.success('Category updated successfully');
    } catch (error) {
      window.toastManager.error(Failed to update category: );
    }
  }

  async function deleteRow(row) {
    if (!confirm("Are you sure you want to delete this category?")) return;
    const id = row.dataset.id;
    
    try {
      if (!id) { 
        row.remove(); 
        window.toastManager.success('Category deleted successfully');
        return; 
      }
      
      const res = await fetch(${API_BASE}/, { method: "DELETE" });
      
      if (!res.ok) {
        const errText = await res.text();
        throw new Error(errText || HTTP );
      }
      
      row.remove();
      window.toastManager.success('Category deleted successfully');
    } catch (error) {
      window.toastManager.error(Failed to delete category: );
    }
  }

  function filterRowsLocal(){
    const query = normalize(searchInput.value);
    const rows = tbody.querySelectorAll("tr");
    rows.forEach(row => {
      const cells = row.querySelectorAll("td");
      const categoryName = normalize(cells[0]?.textContent);
      const categoryCode = normalize(cells[1]?.textContent);
      const description = normalize(cells[2]?.textContent);
      const matches = categoryName.includes(query) || categoryCode.includes(query) || description.includes(query);
      row.style.display = matches ? "" : "none";
    });
  }

  // Event delegation for row actions
  tbody.addEventListener("click", (e) => {
    const target = e.target; const row = target.closest("tr"); if (!row) return;
    if (target.classList.contains("edit-btn")) {
      makeRowEditable(row);
      const actionsCell = row.querySelector("td:last-child");
      actionsCell.innerHTML = 
        <div class="action-icons-container">
          <img src="/images/correct.png" class="action-icon save-btn" alt="Save">
          <img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
        </div>;
    }
    if (target.classList.contains("save-btn")) { saveRowChanges(row); }
    if (target.classList.contains("delete-btn")) { deleteRow(row); }
  });

  // Server-side search on input/click (by name/code/description)
  function onSearch(){ loadCategories(); }
  if (searchInput) searchInput.addEventListener("input", onSearch);
  if (searchIcon) searchIcon.addEventListener("click", onSearch);

  // Add modal open/close
  addCategoryIcon.addEventListener("click", () => { addCategoryModalOverlay.style.display = "flex"; document.body.style.overflow = "hidden"; });
  closeAddCategoryModal.addEventListener("click", () => { addCategoryModalOverlay.style.display = "none"; document.body.style.overflow = ""; });
  addCategoryModalOverlay.addEventListener("click", (e) => { if (e.target === addCategoryModalOverlay) { addCategoryModalOverlay.style.display = "none"; document.body.style.overflow = ""; } });

  // Create category
  addCategoryForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const payload = {
      categoryName: document.getElementById("addCategoryName").value,
      categoryCode: document.getElementById("addCategoryCode").value,
      description: document.getElementById("addDescription").value,
    };
    
    try {
      const res = await fetch(API_BASE, {
        method: "POST",
        headers: { "Content-Type": "application/json", "Accept": "application/json" },
        body: JSON.stringify(payload)
      });
      
      if (!res.ok) {
        const errText = await res.text();
        throw new Error(errText || HTTP );
      }
      
      const created = await res.json();
      // Prepend new row
      const newRow = document.createElement("tr");
      newRow.dataset.id = created.categoryId;
      newRow.innerHTML = 
        <td><span class="label"></span></td>
        <td><span class="label"></span></td>
        <td><span class="label"></span></td>
        <td><button class="btn-edit-label edit-btn">Edit Category</button></td>;
      tbody.prepend(newRow);

      addCategoryModalOverlay.style.display = "none"; document.body.style.overflow = ""; addCategoryForm.reset();
      window.toastManager.success('Category created successfully');
    } catch (error) {
      window.toastManager.error(Failed to create category: );
    }
  });

  // Initial load
  loadCategories();
});
