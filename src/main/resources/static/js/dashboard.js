$(document).ready(function() {
    // Load user profile when page loads
    loadUserProfile();
    
    // Reload user profile when profile modal is opened
    $('#profileModal').on('show.bs.modal', function() {
        loadUserProfile();
    });
    
    function loadUserProfile() {
        // Show loading state
        $('#user-name').text('Loading...');
        $('#user-username').text('Loading...');
        $('#user-email').text('Loading...');
        $('#user-role').text('Loading...');
        $('#user-status').text('Loading...');
        
        $.ajax({
            url: '/lwms/api/user/profile',
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
            success: function(data) {
                // Update profile modal with user data
                $('#user-name').text(data.firstName + ' ' + data.lastName);
                $('#user-username').text(data.username);
                $('#user-email').text(data.email);
                $('#user-role').text(data.role);
                $('#user-status').text(data.status);
                
                // Update navbar brand if needed (optional)
                updateNavbarWithUserInfo(data);
            },
            error: function(xhr, status, error) {
                console.error('Error loading user profile:', error);
                showError('Failed to load user profile');
                
                // Set error state
                $('#user-name').text('Error loading data');
                $('#user-username').text('Error loading data');
                $('#user-email').text('Error loading data');
                $('#user-role').text('Error loading data');
                $('#user-status').text('Error loading data');
            }
        });
    }
    
    function updateNavbarWithUserInfo(user) {
        // Show/hide settings based on role
        if (user.role === 'Admin') {
            $('.admin-only').show();
        } else {
            $('.admin-only').hide();
        }
    }
    
    function showError(message) {
        // Create an alert div if it doesn't exist
        if ($('#error-alert').length === 0) {
            const alertHtml = `
                <div id="error-alert" class="alert alert-danger alert-dismissible fade show position-fixed" 
                     style="top: 80px; right: 20px; z-index: 1050; max-width: 400px;">
                    <span id="error-message">${message}</span>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            `;
            $('body').append(alertHtml);
            
            // Auto-hide after 5 seconds
            setTimeout(function() {
                $('#error-alert').alert('close');
            }, 5000);
        } else {
            $('#error-message').text(message);
            $('#error-alert').show();
        }
    }
    
    // Handle logout functionality
    $(document).on('click', 'a[href="/logout"]', function(e) {
        e.preventDefault();
        
        // Add confirmation dialog
        if (confirm('Are you sure you want to logout?')) {
            // Perform logout via form submission (Spring Security handles this)
            const form = $('<form>', {
                'method': 'POST',
                'action': '/logout'
            });
            
            // Add CSRF token if available
            const csrfToken = $('meta[name="_csrf"]').attr('content');
            const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
            
            if (csrfToken && csrfHeader) {
                form.append($('<input>', {
                    'type': 'hidden',
                    'name': '_csrf',
                    'value': csrfToken
                }));
            }
            
            $('body').append(form);
            form.submit();
        }
    });
    
    // Add smooth scroll for dashboard cards (optional enhancement)
    $('.box-card').on('click', function() {
        const $this = $(this);
        $this.addClass('card-clicked');
        setTimeout(function() {
            $this.removeClass('card-clicked');
        }, 200);
    });
});