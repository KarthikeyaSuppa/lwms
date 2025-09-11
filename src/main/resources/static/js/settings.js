$(document).ready(function() {
    // Settings page JavaScript functionality
    
    const profileForm = $('#profile-form');
    const passwordForm = $('#password-form');
    
    // Profile form validation
    if (profileForm.length > 0) {
        profileForm.on('submit', function(event) {
            let isValid = true;
            
            // Basic validation
            const firstName = $('#firstName').val();
            const lastName = $('#lastName').val();
            const email = $('#email').val();
            
            if (!firstName || firstName.length < 2) {
                showFieldError('#firstName', 'First name must be at least 2 characters');
                isValid = false;
            }
            
            if (!lastName || lastName.length < 2) {
                showFieldError('#lastName', 'Last name must be at least 2 characters');
                isValid = false;
            }
            
            if (!email || !isValidEmail(email)) {
                showFieldError('#email', 'Please enter a valid email address');
                isValid = false;
            }
            
            if (!isValid) {
                event.preventDefault();
            }
        });
    }
    
    // Password form validation
    if (passwordForm.length > 0) {
        passwordForm.on('submit', function(event) {
            let isValid = true;
            
            const currentPassword = $('#currentPassword').val();
            const newPassword = $('#newPassword').val();
            const confirmPassword = $('#confirmPassword').val();
            
            if (!currentPassword) {
                showFieldError('#currentPassword', 'Current password is required');
                isValid = false;
            }
            
            if (!newPassword || newPassword.length < 8) {
                showFieldError('#newPassword', 'New password must be at least 8 characters');
                isValid = false;
            }
            
            if (newPassword !== confirmPassword) {
                showFieldError('#confirmPassword', 'Passwords do not match');
                isValid = false;
            }
            
            if (!isValid) {
                event.preventDefault();
            }
        });
    }
    
    // Helper functions
    function showFieldError(fieldSelector, message) {
        const field = $(fieldSelector);
        field.addClass('is-invalid');
        
        let errorDiv = field.siblings('.invalid-feedback');
        if (errorDiv.length === 0) {
            field.after('<div class="invalid-feedback">' + message + '</div>');
        } else {
            errorDiv.text(message);
        }
    }
    
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
    
    // Clear validation on field focus
    $('input').on('focus', function() {
        $(this).removeClass('is-invalid');
        $(this).siblings('.invalid-feedback').remove();
    });
});