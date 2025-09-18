document.addEventListener("DOMContentLoaded", () => {
	const tbody = document.querySelector("tbody");

	function makeRowEditable(row) {
		const cells = row.querySelectorAll("td:not(:last-child)");
		cells.forEach((cell, index) => {
			const label = cell.querySelector(".label");
			if (label && index === 0) {
				const input = document.createElement("input");
				input.type = "text";
				input.className = "editable-input";
				input.value = label.textContent.trim();
				cell.innerHTML = "";
				cell.appendChild(input);
			}
			if (index === 1) {
				const textarea = document.createElement("textarea");
				textarea.className = "editable-input";
				textarea.value = (label ? label.textContent.trim() : "");
				textarea.rows = 3;
				cell.innerHTML = "";
				cell.appendChild(textarea);
			}
			if (index === 2) {
				const textarea = document.createElement("textarea");
				textarea.className = "editable-input";
				textarea.value = (label ? label.textContent.trim() : "");
				textarea.rows = 4;
				cell.innerHTML = "";
				cell.appendChild(textarea);
			}
		});
	}

	function saveRowChanges(row) {
		const cells = row.querySelectorAll("td");
		cells.forEach((cell, index) => {
			if (index === 0) {
				const input = cell.querySelector(".editable-input");
				if (input) {
					const label = document.createElement("span");
					label.className = "label";
					label.textContent = input.value;
					cell.innerHTML = "";
					cell.appendChild(label);
				}
			} else if (index === 1) {
				const textarea = cell.querySelector("textarea.editable-input");
				if (textarea) {
					const label = document.createElement("span");
					label.className = "label";
					label.textContent = textarea.value;
					cell.innerHTML = "";
					cell.appendChild(label);
				}
			} else if (index === 2) {
				const textarea = cell.querySelector("textarea.editable-input");
				if (textarea) {
					const label = document.createElement("span");
					label.className = "label";
					label.textContent = textarea.value;
					cell.innerHTML = "";
					cell.appendChild(label);
				}
			} else if (index === 3) {
				const actionsCell = row.querySelector("td:last-child");
				actionsCell.innerHTML = "<button class=\"btn-edit-label edit-btn\">Edit Role</button>";
			}
		});
	}

	function deleteRow(row) {
		if (confirm("Are you sure you want to delete this role?")) {
			row.remove();
		}
	}

	tbody?.addEventListener("click", (e) => {
		const target = e.target;
		const row = target.closest("tr");
		if (!row) return;

		if (target.classList.contains("edit-btn")) {
			makeRowEditable(row);
			const actionsCell = row.querySelector("td:last-child");
			actionsCell.innerHTML = `
				<div class="action-icons-container">
					<img src="/images/correct.png" class="action-icon save-btn" alt="Save">
					<img src="/images/trash.png" class="action-icon delete-btn" alt="Delete">
				</div>
			`;
		}
		if (target.classList.contains("save-btn")) {
			saveRowChanges(row);
		}
		if (target.classList.contains("delete-btn")) {
			deleteRow(row);
		}
	});

	const searchInput = document.getElementById("searchInput");
	const searchIcon = document.getElementById("searchIcon");

	function normalize(text) { return (text || "").toString().toLowerCase().trim(); }

	function filterRows() {
		const query = normalize(searchInput.value);
		const rows = tbody.querySelectorAll("tr");
		rows.forEach(row => {
			const cells = row.querySelectorAll("td");
			const roleName = normalize(cells[0]?.querySelector(".editable-input")?.value || cells[0]?.textContent);
			const description = normalize(cells[1]?.querySelector(".editable-input")?.value || cells[1]?.textContent);
			const permissions = normalize(cells[2]?.querySelector(".editable-input")?.value || cells[2]?.textContent);
			const matches = roleName.includes(query) || description.includes(query) || permissions.includes(query);
			row.style.display = matches ? "" : "none";
		});
	}

	searchInput?.addEventListener("input", filterRows);
	searchIcon?.addEventListener("click", filterRows);


}); 