$(document).ready(function() {
    const signupForm = $('#signup-form');
    const firstNameInput = $('#firstName');
    const lastNameInput = $('#lastName');
    const usernameInput = $('#username');
    const emailInput = $('#email');
    const passwordInput = $('#password');
    const confirmPasswordInput = $('#confirmPassword');

    const firstNameFeedback = $('#firstName-feedback');
    const lastNameFeedback = $('#lastName-feedback');
    const usernameFeedback = $('#username-feedback');
    const emailFeedback = $('#email-feedback');
    const passwordFeedback = $('#password-feedback');
    const confirmPasswordFeedback = $('#confirmPassword-feedback');

    // Password toggle icons
    const passwordEyeIcon = $('#password-eye-icon');
    const confirmPasswordEyeIcon = $('#confirmPassword-eye-icon');

    // Debug: Check if elements exist
    console.log('Password toggle button:', $('#password-toggle').length);
    console.log('Confirm password toggle button:', $('#confirmPassword-toggle').length);
    console.log('Password eye icon:', passwordEyeIcon.length);
    console.log('Confirm password eye icon:', confirmPasswordEyeIcon.length);

    // Function to check for spaces
    function hasSpaces(str) {
        return /\s/.test(str);
    }

    // Function to validate name/username length
    function isLengthValid(str) {
        return str.length >= 3;
    }

    // Function to validate email format
    function isEmailValid(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }

    // Function to validate password complexity
    function validatePassword(password) {
        const feedback = [];
        if (password.length < 8) {
            feedback.push('Password must be at least 8 characters long.');
        }
        if (!/[A-Z]/.test(password)) {
            feedback.push('Password must contain at least one uppercase letter.');
        }
        if (!/[a-z]/.test(password)) {
            feedback.push('Password must contain at least one lowercase letter.');
        }
        if (!/[0-9]/.test(password)) {
            feedback.push('Password must contain at least one number.');
        }
        if (!/[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]/.test(password)) {
            feedback.push('Password must contain at least one special character.');
        }
        return feedback;
    }

    // Function to toggle password visibility
    function togglePasswordVisibility(passwordField, eyeIcon) {
        console.log('Toggle password visibility called');
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

    // Password toggle events
    $('#password-toggle').on('click', function() {
        console.log('Password toggle clicked');
        togglePasswordVisibility(passwordInput, passwordEyeIcon);
    });

    $('#confirmPassword-toggle').on('click', function() {
        console.log('Confirm password toggle clicked');
        togglePasswordVisibility(confirmPasswordInput, confirmPasswordEyeIcon);
    });

    // Live validation for input fields
    firstNameInput.on('input', function() {
        const value = $(this).val();
        if (hasSpaces(value)) {
            firstNameFeedback.text('First name cannot contain spaces.');
        } else if (!isLengthValid(value)) {
            firstNameFeedback.text('First name must be at least 3 characters.');
        } else {
            firstNameFeedback.text('');
        }
    });

    lastNameInput.on('input', function() {
        const value = $(this).val();
        if (hasSpaces(value)) {
            lastNameFeedback.text('Last name cannot contain spaces.');
        } else if (!isLengthValid(value)) {
            lastNameFeedback.text('Last name must be at least 3 characters.');
        } else {
            lastNameFeedback.text('');
        }
    });

    usernameInput.on('input', function() {
        const value = $(this).val();
        if (hasSpaces(value)) {
            usernameFeedback.text('Username cannot contain spaces.');
        } else if (!isLengthValid(value)) {
            usernameFeedback.text('Username must be at least 3 characters.');
        } else {
            usernameFeedback.text('');
        }
    });

    emailInput.on('input', function() {
        const value = $(this).val();
        if (!isEmailValid(value)) {
            emailFeedback.text('Please enter a valid email address.');
        } else {
            emailFeedback.text('');
        }
    });

    passwordInput.on('input', function() {
        const password = $(this).val();
        const feedback = validatePassword(password);
        if (feedback.length > 0) {
            passwordFeedback.html(feedback.join('<br>'));
        } else {
            passwordFeedback.text('');
        }
    });

    confirmPasswordInput.on('input', function() {
        const password = passwordInput.val();
        const confirmPassword = $(this).val();
        if (password !== confirmPassword) {
            confirmPasswordFeedback.text('Passwords do not match.');
        } else {
            confirmPasswordFeedback.text('');
        }
    });

    // Form submission handler
    signupForm.on('submit', function(event) {
        let isValid = true;

        // Check for required fields
        $('input[required]').each(function() {
            if ($(this).val() === '') {
                const fieldName = $(this).attr('id');
                $(`#${fieldName}-feedback`).text('This field is required.');
                isValid = false;
            }
        });

        // Re-run all validations on submit
        const firstName = firstNameInput.val();
        const lastName = lastNameInput.val();
        const username = usernameInput.val();
        const email = emailInput.val();
        const password = passwordInput.val();
        const confirmPassword = confirmPasswordInput.val();

        if (hasSpaces(firstName) || !isLengthValid(firstName)) {
            firstNameFeedback.text(hasSpaces(firstName) ? 'First name cannot contain spaces.' : 'First name must be at least 3 characters.');
            isValid = false;
        }

        if (hasSpaces(lastName) || !isLengthValid(lastName)) {
            lastNameFeedback.text(hasSpaces(lastName) ? 'Last name cannot contain spaces.' : 'Last name must be at least 3 characters.');
            isValid = false;
        }

        if (hasSpaces(username) || !isLengthValid(username)) {
            usernameFeedback.text(hasSpaces(username) ? 'Username cannot contain spaces.' : 'Username must be at least 3 characters.');
            isValid = false;
        }

        if (!isEmailValid(email)) {
            emailFeedback.text('Please enter a valid email address.');
            isValid = false;
        }

        const passwordErrors = validatePassword(password);
        if (passwordErrors.length > 0) {
            passwordFeedback.html(passwordErrors.join('<br>'));
            isValid = false;
        }

        if (password !== confirmPassword) {
            confirmPasswordFeedback.text('Passwords do not match.');
            isValid = false;
        } else {
            confirmPasswordFeedback.text('');
        }

        if (!isValid) {
            event.preventDefault();
        }
    });
});