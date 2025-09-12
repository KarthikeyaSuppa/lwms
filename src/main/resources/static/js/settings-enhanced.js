// Enhanced Settings page JavaScript functionality with better error handling
document.addEventListener("DOMContentLoaded", function() {
    // Load users on page load
    loadUsers();
    
    // Add user modal functionality
    const addUserIcon = document.getElementById("addUserIcon");
    const addUserModalOverlay = document.getElementById("addUserModalOverlay");
    const closeAddUserModal = document.getElementById("closeAddUserModal");
    const addUserForm = document.getElementById("addUserForm");
    
    addUserIcon.addEventListener("click", function() {
        addUserModalOverlay.style.display = "flex";
    });
    
    closeAddUserModal.addEventListener("click", function() {
        addUserModalOverlay.style.display = "none";
        addUserForm.reset();
    });
    
    // Close modal when clicking outside
    addUserModalOverlay.addEventListener("click", function(e) {
        if (e.target === addUserModalOverlay) {
            addUserModalOverlay.style.display = "none";
            addUserForm.reset();
        }
    });
    
    // Handle add user form submission
    addUserForm.addEventListener("submit", function(e) {
        e.preventDefault();
        
        const submitButton = addUserForm.querySelector('button[type="submit"]');
        const originalText = submitButton.textContent;
        
        // Show loading state
        submitButton.disabled = true;
        submitButton.textContent = "Adding User...";
        
        const formData = {
            firstName: document.getElementById("addFirstName").value,
            lastName: document.getElementById("addLastName").value,
            email: document.getElementById("addEmail").value,
            username: document.getElementById("addUsername").value,
            password: document.getElementById("addPassword").value,
            role: document.getElementById("addRole").value
        };
        
        fetch("/lwms/api/users", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text || 'Failed to create user');
                });
            }
            return response.json();
        })
        .then(data => {
            addUserModalOverlay.style.display = "none";
            addUserForm.reset();
            showMessage("User added successfully!", "success");
            loadUsers(); // Reload the users list
        })
        .catch(error => {
            console.error("Error adding user:", error);
            showMessage(`Error adding user: ${error.message}`, "error");
        })
        .finally(() => {
            // Reset button state
            submitButton.disabled = false;
            submitButton.textContent = originalText;
        });
    });
    
    // Search functionality
    const searchInput = document.getElementById("searchInput");
    const searchIcon = document.getElementById("searchIcon");
    
    // Debounce search to avoid too many requests
    let searchTimeout;
    searchInput.addEventListener("input", function() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(() => {
            const searchTerm = this.value.trim();
            if (searchTerm.length > 0) {
                searchUsers(searchTerm);
            } else {
                loadUsers();
            }
        }, 300);
    });
    
    searchIcon.addEventListener("click", function() {
        const searchTerm = searchInput.value.trim();
        if (searchTerm.length > 0) {
            searchUsers(searchTerm);
        } else {
            loadUsers();
        }
    });
});

// Function to load all users
function loadUsers() {
    showLoadingState();
    
    fetch("/lwms/api/users")
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load users');
            }
            return response.json();
        })
        .then(data => {
            populateUsersTable(data);
        })
        .catch(error => {
            console.error("Error loading users:", error);
            showMessage("Error loading users. Please refresh the page.", "error");
            showEmptyState("Error loading users");
        });
}

// Function to search users
function searchUsers(searchTerm) {
    showLoadingState();
    
    fetch(`/lwms/api/users/search?searchTerm=${encodeURIComponent(searchTerm)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Search failed');
            }
            return response.json();
        })
        .then(data => {
            populateUsersTable(data);
            if (data.length === 0) {
                showEmptyState(`No users found matching "${searchTerm}"`);
            }
        })
        .catch(error => {
            console.error("Error searching users:", error);
            showMessage("Error searching users. Please try again.", "error");
            showEmptyState("Search error");
        });
}

// Function to show loading state
function showLoadingState() {
    const tbody = document.getElementById("usersTableBody");
    tbody.innerHTML = `
        <tr>
            <td colspan="9" class="text-center">
                <div class="spinner-border" role="status">
                    <span class="visually-hidden">Loading users...</span>
                </div>
            </td>
        </tr>
    `;
}

// Function to show empty state
function showEmptyState(message) {
    const tbody = document.getElementById("usersTableBody");
    tbody.innerHTML = `
        <tr>
            <td colspan="9" class="text-center">
                <p class="mb-0">${message}</p>
            </td>
        </tr>
    `;
}

// Function to populate the users table
function populateUsersTable(users) {
    const tbody = document.getElementById("usersTableBody");
    tbody.innerHTML = "";
    
    if (users.length === 0) {
        showEmptyState("No users found");
        return;
    }
    
    users.forEach(user => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td><span class="label">${escapeHtml(user.username)}</span></td>
            <td><span class="label">${escapeHtml(user.email)}</span></td>
            <td><span class="label">${escapeHtml(user.firstName)}</span></td>
            <td><span class="label">${escapeHtml(user.lastName)}</span></td>
            <td><span class="label">${escapeHtml(user.role)}</span></td>
            <td><span class="label ${user.status === 'Active' ? 'text-success' : 'text-danger'}">${escapeHtml(user.status)}</span></td>
            <td><span class="label">${formatDate(user.createdAt)}</span></td>
            <td><span class="label">${formatDate(user.lastLogin)}</span></td>
            <td>
                <div class="action-icons-container">
                    <button class="btn-edit-label edit-btn" onclick="editUser(${user.userId})">Edit User</button>
                </div>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// Function to edit user
function editUser(userId) {
    // Find the user row
    const rows = document.querySelectorAll("#usersTableBody tr");
    rows.forEach(row => {
        const editBtn = row.querySelector(".edit-btn");
        if (editBtn && editBtn.onclick.toString().includes(userId)) {
            makeRowEditable(row, userId);
        }
    });
}

// Function to make a row editable
function makeRowEditable(row, userId) {
    const cells = row.querySelectorAll("td");
    
    // Make username editable
    const usernameCell = cells[0];
    const usernameSpan = usernameCell.querySelector(".label");
    const usernameInput = document.createElement("input");
    usernameInput.type = "text";
    usernameInput.className = "editable-input";
    usernameInput.value = usernameSpan.textContent;
    usernameCell.innerHTML = "";
    usernameCell.appendChild(usernameInput);
    
    // Make email editable
    const emailCell = cells[1];
    const emailSpan = emailCell.querySelector(".label");
    const emailInput = document.createElement("input");
    emailInput.type = "email";
    emailInput.className = "editable-input";
    emailInput.value = emailSpan.textContent;
    emailCell.innerHTML = "";
    emailCell.appendChild(emailInput);
    
    // Make first name editable
    const firstNameCell = cells[2];
    const firstNameSpan = firstNameCell.querySelector(".label");
    const firstNameInput = document.createElement("input");
    firstNameInput.type = "text";
    firstNameInput.className = "editable-input";
    firstNameInput.value = firstNameSpan.textContent;
    firstNameCell.innerHTML = "";
    firstNameCell.appendChild(firstNameInput);
    
    // Make last name editable
    const lastNameCell = cells[3];
    const lastNameSpan = lastNameCell.querySelector(".label");
    const lastNameInput = document.createElement("input");
    lastNameInput.type = "text";
    lastNameInput.className = "editable-input";
    lastNameInput.value = lastNameSpan.textContent;
    lastNameCell.innerHTML = "";
    lastNameCell.appendChild(lastNameInput);
    
    // Make role editable (dropdown)
    const roleCell = cells[4];
    const roleSpan = roleCell.querySelector(".label");
    const roleSelect = document.createElement("select");
    roleSelect.className = "role-select";
    roleSelect.innerHTML = `
        <option value="Admin" ${roleSpan.textContent === "Admin" ? "selected" : ""}>Admin</option>
        <option value="Manager" ${roleSpan.textContent === "Manager" ? "selected" : ""}>Manager</option>
        <option value="Supervisor" ${roleSpan.textContent === "Supervisor" ? "selected" : ""}>Supervisor</option>
        <option value="Inventory Controller" ${roleSpan.textContent === "Inventory Controller" ? "selected" : ""}>Inventory Controller</option>
        <option value="Operator" ${roleSpan.textContent === "Operator" ? "selected" : ""}>Operator</option>
        <option value="Viewer" ${roleSpan.textContent === "Viewer" ? "selected" : ""}>Viewer</option>
    `;
    roleCell.innerHTML = "";
    roleCell.appendChild(roleSelect);
    
    // Make status editable (dropdown)
    const statusCell = cells[5];
    const statusSpan = statusCell.querySelector(".label");
    const statusSelect = document.createElement("select");
    statusSelect.className = "role-select";
    statusSelect.innerHTML = `
        <option value="Active" ${statusSpan.textContent === "Active" ? "selected" : ""}>Active</option>
        <option value="Inactive" ${statusSpan.textContent === "Inactive" ? "selected" : ""}>Inactive</option>
    `;
    statusCell.innerHTML = "";
    statusCell.appendChild(statusSelect);
    
    // Update actions cell with save and cancel buttons
    const actionsCell = cells[8];
    actionsCell.innerHTML = `
        <div class="action-icons-container">
            <button class="action-icon save-btn" onclick="saveUser(${userId})" title="Save">Save</button>
            <button class="action-icon cancel-btn" onclick="cancelEdit(${userId})" title="Cancel">Cancel</button>
            <button class="action-icon delete-btn" onclick="deleteUser(${userId})" title="Delete">Delete</button>
        </div>
    `;
}

// Function to save user changes
function saveUser(userId) {
    const row = document.querySelector(`button[onclick="saveUser(${userId})"]`).closest("tr");
    const cells = row.querySelectorAll("td");
    
    const saveButton = row.querySelector('.save-btn');
    const originalText = saveButton.textContent;
    
    // Show loading state
    saveButton.disabled = true;
    saveButton.textContent = "Saving...";
    
    const updateData = {
        userId: userId,
        username: cells[0].querySelector("input").value,
        email: cells[1].querySelector("input").value,
        firstName: cells[2].querySelector("input").value,
        lastName: cells[3].querySelector("input").value,
        role: cells[4].querySelector("select").value,
        status: cells[5].querySelector("select").value
    };
    
    fetch(`/lwms/api/users/${userId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(updateData)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || 'Failed to update user');
            });
        }
        return response.json();
    })
    .then(data => {
        showMessage("User updated successfully!", "success");
        loadUsers(); // Reload the users list
    })
    .catch(error => {
        console.error("Error updating user:", error);
        showMessage(`Error updating user: ${error.message}`, "error");
    })
    .finally(() => {
        // Reset button state
        saveButton.disabled = false;
        saveButton.textContent = originalText;
    });
}

// Function to cancel edit
function cancelEdit(userId) {
    loadUsers(); // Reload the users list to cancel changes
}

// Function to delete user
function deleteUser(userId) {
    if (confirm("Are you sure you want to delete this user? This action cannot be undone.")) {
        const deleteButton = document.querySelector(`button[onclick="deleteUser(${userId})"]`);
        const originalText = deleteButton.textContent;
        
        // Show loading state
        deleteButton.disabled = true;
        deleteButton.textContent = "Deleting...";
        
        fetch(`/lwms/api/users/${userId}`, {
            method: "DELETE"
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text || 'Failed to delete user');
                });
            }
            showMessage("User deleted successfully!", "success");
            loadUsers(); // Reload the users list
        })
        .catch(error => {
            console.error("Error deleting user:", error);
            showMessage(`Error deleting user: ${error.message}`, "error");
            
            // Reset button state on error
            deleteButton.disabled = false;
            deleteButton.textContent = originalText;
        });
    }
}

// Function to format date
function formatDate(dateString) {
    if (!dateString) return "Never";
    const date = new Date(dateString);
    return date.toLocaleDateString("en-US", {
        year: "numeric",
        month: "short",
        day: "numeric"
    });
}

// Function to escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Function to show success/error messages
function showMessage(message, type) {
    // Remove any existing messages
    const existingMessage = document.getElementById("message-alert");
    if (existingMessage) {
        existingMessage.remove();
    }
    
    // Create new message element
    const messageDiv = document.createElement("div");
    messageDiv.id = "message-alert";
    messageDiv.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-dismissible fade show`;
    messageDiv.style.position = "fixed";
    messageDiv.style.top = "80px";
    messageDiv.style.right = "20px";
    messageDiv.style.zIndex = "1060";
    messageDiv.style.maxWidth = "400px";
    
    messageDiv.innerHTML = `
        <strong>${type === 'success' ? 'Success!' : 'Error!'}</strong> ${message}
        <button type="button" class="btn-close" onclick="this.parentElement.remove()" aria-label="Close">&times;</button>
    `;
    
    document.body.appendChild(messageDiv);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        if (messageDiv.parentElement) {
            messageDiv.remove();
        }
    }, 5000);
}