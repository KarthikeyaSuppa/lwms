$(document).ready(function() {
    const loginForm = $('.login-form');
    const usernameOrEmailInput = $('#usernameOrEmail');
    const passwordInput = $('#password');
    const eyeIcon = $('#eye-icon');

    // Function to validate email format
    function isEmailValid(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }

    // Function to validate username format (no spaces, minimum length)
    function isUsernameValid(username) {
        return username.length >= 3 && !/\s/.test(username);
    }

    // Function to validate usernameOrEmail field
    function validateUsernameOrEmail(value) {
        if (value.includes('@')) {
            return isEmailValid(value) ? '' : 'Please enter a valid email address.';
        } else {
            return isUsernameValid(value) ? '' : 'Username must be at least 3 characters and contain no spaces.';
        }
    }

    // Function to show/hide password
    function togglePasswordVisibility() {
        const passwordField = passwordInput;
        
        if (passwordField.attr('type') === 'password') {
            passwordField.attr('type', 'text');
            eyeIcon.attr('src', '/images/Eye--Streamline-Iconoir.svg');
            eyeIcon.attr('alt', 'Hide Password');
        } else {
            passwordField.attr('type', 'password');
            eyeIcon.attr('src', '/images/Eye-Closed--Streamline-Iconoir.svg');
            eyeIcon.attr('alt', 'Show Password');
        }
    }

    // Password toggle event
    $('#password-toggle').on('click', togglePasswordVisibility);

    // Real-time validation for usernameOrEmail
    usernameOrEmailInput.on('input', function() {
        const value = $(this).val();
        const error = validateUsernameOrEmail(value);
        
        if (error) {
            $(this).addClass('is-invalid');
            // Show error message
            if ($('#usernameOrEmail-feedback').length === 0) {
                $(this).closest('.form-with-icon').after(`<div id="usernameOrEmail-feedback" class="invalid-feedback">${error}</div>`);
            } else {
                $('#usernameOrEmail-feedback').text(error);
            }
        } else {
            $(this).removeClass('is-invalid').addClass('is-valid');
            $('#usernameOrEmail-feedback').remove();
        }
    });

    // Real-time validation for password (basic check)
    passwordInput.on('input', function() {
        const value = $(this).val();
        
        if (value.length === 0) {
            $(this).removeClass('is-valid is-invalid');
        } else if (value.length < 8) {
            $(this).removeClass('is-valid').addClass('is-invalid');
            if ($('#password-feedback').length === 0) {
                $(this).closest('.form-with-icon').after('<div id="password-feedback" class="invalid-feedback">Password must be at least 6 characters.</div>');
            }
        } else {
            $(this).removeClass('is-invalid').addClass('is-valid');
            $('#password-feedback').remove();
        }
    });

    // Form submission validation
    loginForm.on('submit', function(event) {
        let isValid = true;
        
        // Validate usernameOrEmail
        const usernameOrEmail = usernameOrEmailInput.val().trim();
        if (!usernameOrEmail) {
            usernameOrEmailInput.addClass('is-invalid');
            if ($('#usernameOrEmail-feedback').length === 0) {
                usernameOrEmailInput.closest('.form-with-icon').after('<div id="usernameOrEmail-feedback" class="invalid-feedback">Username or Email is required.</div>');
            }
            isValid = false;
        } else {
            const error = validateUsernameOrEmail(usernameOrEmail);
            if (error) {
                usernameOrEmailInput.addClass('is-invalid');
                if ($('#usernameOrEmail-feedback').length === 0) {
                    usernameOrEmailInput.closest('.form-with-icon').after(`<div id="usernameOrEmail-feedback" class="invalid-feedback">${error}</div>`);
                }
                isValid = false;
            }
        }

        // Validate password
        const password = passwordInput.val();
        if (!password) {
            passwordInput.addClass('is-invalid');
            if ($('#password-feedback').length === 0) {
                passwordInput.closest('.form-with-icon').after('<div id="password-feedback" class="invalid-feedback">Password is required.</div>');
            }
            isValid = false;
        }

        // Show loading state
        if (isValid) {
            const submitButton = loginForm.find('button[type="submit"]');
            const originalText = submitButton.text();
            submitButton.prop('disabled', true).html('<span class="spinner-border spinner-border-sm me-2"></span>Signing In...');
            
            // Re-enable after 3 seconds if still on page (fallback)
            setTimeout(() => {
                submitButton.prop('disabled', false).text(originalText);
            }, 3000);
        } else {
            event.preventDefault();
        }
    });

    // Clear validation on focus
    usernameOrEmailInput.on('focus', function() {
        $(this).removeClass('is-invalid');
        $('#usernameOrEmail-feedback').remove();
    });

    passwordInput.on('focus', function() {
        $(this).removeClass('is-invalid');
        $('#password-feedback').remove();
    });

    // Auto-focus on first field
    usernameOrEmailInput.focus();

    // Enter key navigation
    usernameOrEmailInput.on('keypress', function(e) {
        if (e.which === 13) { // Enter key
            passwordInput.focus();
        }
    });

    passwordInput.on('keypress', function(e) {
        if (e.which === 13) { // Enter key
            loginForm.submit();
        }
    });
}); 