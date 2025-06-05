// Main JavaScript file for Gender Healthcare Service Management System

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Form validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Handle appointment booking validation
    const appointmentForm = document.getElementById('appointmentForm');
    if (appointmentForm) {
        appointmentForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            if (appointmentForm.checkValidity()) {
                // Show success message
                const successAlert = document.getElementById('appointmentSuccess');
                if (successAlert) {
                    successAlert.classList.remove('d-none');
                    appointmentForm.reset();
                    appointmentForm.classList.remove('was-validated');
                    
                    // Hide success message after 5 seconds
                    setTimeout(() => {
                        successAlert.classList.add('d-none');
                    }, 5000);
                }
            } else {
                appointmentForm.classList.add('was-validated');
            }
        });
    }

    // Handle contact form submission
    const contactForm = document.getElementById('contactForm');
    if (contactForm) {
        contactForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            if (contactForm.checkValidity()) {
                // Show success message
                const successAlert = document.getElementById('contactSuccess');
                if (successAlert) {
                    successAlert.classList.remove('d-none');
                    contactForm.reset();
                    contactForm.classList.remove('was-validated');
                    
                    // Hide success message after 5 seconds
                    setTimeout(() => {
                        successAlert.classList.add('d-none');
                    }, 5000);
                }
            } else {
                contactForm.classList.add('was-validated');
            }
        });
    }

    // Handle login
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            // Demo accounts
            const accounts = {
                'guest@example.com': {password: 'guest123', role: 'guest'},
                'customer@example.com': {password: 'customer123', role: 'customer'},
                'consultant@example.com': {password: 'consultant123', role: 'consultant'},
                'staff@example.com': {password: 'staff123', role: 'staff'},
                'manager@example.com': {password: 'manager123', role: 'manager'},
                'admin@example.com': {password: 'admin123', role: 'admin'}
            };
            
            // Check credentials
            if (accounts[email] && accounts[email].password === password) {
                // Store user info in localStorage for demo purposes
                localStorage.setItem('currentUser', JSON.stringify({
                    email: email,
                    role: accounts[email].role,
                    name: email.split('@')[0]
                }));
                
                // Redirect based on role
                window.location.href = `dashboard-${accounts[email].role}.html`;
            } else {
                // Show error message
                const errorAlert = document.getElementById('loginError');
                if (errorAlert) {
                    errorAlert.classList.remove('d-none');
                    
                    // Hide error message after 3 seconds
                    setTimeout(() => {
                        errorAlert.classList.add('d-none');
                    }, 3000);
                }
            }
        });
    }

    // Handle logout
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            localStorage.removeItem('currentUser');
            window.location.href = 'login.html';
        });
    }

    // Check if user is logged in and display user info
    const userInfoElement = document.getElementById('userInfo');
    if (userInfoElement) {
        const currentUser = JSON.parse(localStorage.getItem('currentUser'));
        if (currentUser) {
            userInfoElement.textContent = `${currentUser.name} (${currentUser.role})`;
        } else {
            // Redirect to login if not logged in and trying to access dashboard
            if (window.location.href.includes('dashboard')) {
                window.location.href = 'login.html';
            }
        }
    }

    // Initialize datepickers
    const datepickers = document.querySelectorAll('.datepicker');
    datepickers.forEach(datepicker => {
        if (window.Litepicker) {
            new Litepicker({
                element: datepicker,
                format: 'YYYY-MM-DD',
                minDate: new Date()
            });
        }
    });

    // Handle search functionality
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keyup', function() {
            const searchTerm = this.value.toLowerCase();
            const searchableElements = document.querySelectorAll('.searchable');
            
            searchableElements.forEach(element => {
                const text = element.textContent.toLowerCase();
                if (text.includes(searchTerm)) {
                    element.style.display = '';
                } else {
                    element.style.display = 'none';
                }
            });

            // Advanced search for specific fields (patient name, ID, etc.)
            if (document.querySelector('.advanced-search')) {
                const patientRows = document.querySelectorAll('tbody tr');
                patientRows.forEach(row => {
                    const patientName = row.querySelector('[data-patient-name]')?.textContent.toLowerCase() || '';
                    const patientId = row.querySelector('[data-patient-id]')?.textContent.toLowerCase() || '';
                    const patientCondition = row.querySelector('[data-condition]')?.textContent.toLowerCase() || '';
                    
                    if (patientName.includes(searchTerm) || 
                        patientId.includes(searchTerm) || 
                        patientCondition.includes(searchTerm)) {
                        row.style.display = '';
                    } else {
                        row.style.display = 'none';
                    }
                });
            }

            // Show no results message if needed
            const noResultsMessage = document.getElementById('noSearchResults');
            if (noResultsMessage) {
                const visibleElements = Array.from(searchableElements).filter(el => el.style.display !== 'none');
                if (visibleElements.length === 0 && searchTerm !== '') {
                    noResultsMessage.classList.remove('d-none');
                } else {
                    noResultsMessage.classList.add('d-none');
                }
            }
        });
    }

    // Task completion functionality
    const taskCompleteButtons = document.querySelectorAll('.btn-outline-success');
    taskCompleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Get the parent task item
            const taskItem = this.closest('.list-group-item');
            
            // Add a completed class with strikethrough effect
            taskItem.classList.add('task-completed');
            
            // Change button to show completed state
            this.innerHTML = '<i class="fas fa-check"></i> Done';
            this.classList.remove('btn-outline-success');
            this.classList.add('btn-success');
            this.disabled = true;
            
            // Optional: Add animation or fade effect
            taskItem.style.opacity = '0.7';
            
            // Show success toast or notification
            showNotification('Task marked as completed!', 'success');
            
            // In a real app, would send to server
            console.log('Task completed:', taskItem.querySelector('h6').textContent);
        });
    });

    // Initialize charts if Chart.js is available
    if (typeof Chart !== 'undefined') {
        initializeCharts();
    }
});

// Notification system
function showNotification(message, type = 'info') {
    // Create notification container if it doesn't exist
    let notificationContainer = document.getElementById('notification-container');
    if (!notificationContainer) {
        notificationContainer = document.createElement('div');
        notificationContainer.id = 'notification-container';
        notificationContainer.className = 'position-fixed top-0 end-0 p-3';
        notificationContainer.style.zIndex = '1060';
        document.body.appendChild(notificationContainer);
    }

    // Create toast element
    const toastId = 'toast-' + Date.now();
    const toast = document.createElement('div');
    toast.id = toastId;
    toast.className = `toast align-items-center text-white bg-${type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    // Toast content
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;

    // Add to container
    notificationContainer.appendChild(toast);

    // Initialize and show the toast
    const bsToast = new bootstrap.Toast(toast, { delay: 3000 });
    bsToast.show();

    // Remove from DOM after hiding
    toast.addEventListener('hidden.bs.toast', function() {
        this.remove();
    });
}

// Charts initialization
function initializeCharts() {
    // Appointments Overview Chart
    const appointmentsCtx = document.getElementById('appointmentsChart');
    if (appointmentsCtx) {
        new Chart(appointmentsCtx, {
            type: 'bar',
            data: {
                labels: ['January', 'February', 'March', 'April', 'May', 'June'],
                datasets: [{
                    label: 'Appointments',
                    data: [65, 59, 80, 81, 56, 75],
                    backgroundColor: 'rgba(44, 107, 172, 0.7)',
                    borderColor: 'rgba(44, 107, 172, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    // Services Distribution Chart
    const servicesCtx = document.getElementById('servicesChart');
    if (servicesCtx) {
        new Chart(servicesCtx, {
            type: 'doughnut',
            data: {
                labels: ['Consultations', 'Health Screenings', 'Treatments', 'Support Programs'],
                datasets: [{
                    data: [35, 25, 22, 18],
                    backgroundColor: [
                        'rgba(44, 107, 172, 0.7)',
                        'rgba(40, 167, 69, 0.7)',
                        'rgba(23, 162, 184, 0.7)',
                        'rgba(255, 193, 7, 0.7)'
                    ],
                    borderColor: [
                        'rgba(44, 107, 172, 1)',
                        'rgba(40, 167, 69, 1)',
                        'rgba(23, 162, 184, 1)',
                        'rgba(255, 193, 7, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true
            }
        });
    }
    
    // Staff Dashboard - Appointments by Type Chart
    const appointmentTypeCtx = document.getElementById('appointmentTypeChart');
    if (appointmentTypeCtx) {
        new Chart(appointmentTypeCtx, {
            type: 'pie',
            data: {
                labels: ['Initial Consultation', 'Follow-up', 'Hormone Therapy', 'General Check-up', 'Mental Health'],
                datasets: [{
                    data: [12, 19, 8, 15, 6],
                    backgroundColor: [
                        'rgba(44, 107, 172, 0.7)',
                        'rgba(40, 167, 69, 0.7)',
                        'rgba(23, 162, 184, 0.7)',
                        'rgba(255, 193, 7, 0.7)',
                        'rgba(220, 53, 69, 0.7)'
                    ],
                    borderColor: [
                        'rgba(44, 107, 172, 1)',
                        'rgba(40, 167, 69, 1)',
                        'rgba(23, 162, 184, 1)',
                        'rgba(255, 193, 7, 1)',
                        'rgba(220, 53, 69, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }
}

// Function to handle appointment status changes
function changeAppointmentStatus(id, status) {
    // In a real application, this would make an API call
    console.log(`Changing appointment ${id} status to ${status}`);
    
    // For demo purposes, update UI directly
    const statusElement = document.getElementById(`status-${id}`);
    if (statusElement) {
        // Remove all existing status classes
        statusElement.classList.remove('status-pending', 'status-active', 'status-completed', 'status-cancelled');
        
        // Add new status class
        statusElement.classList.add(`status-${status.toLowerCase()}`);
        
        // Update text
        statusElement.textContent = status;
        
        // Show success message
        showNotification(`Appointment status updated to ${status}`, 'success');
    }
}

// Function to check in a patient
function checkInPatient(patientId, patientName) {
    // In a real application, this would make an API call
    console.log(`Checking in patient ${patientName} with ID ${patientId}`);
    
    // Update UI to show checked-in status
    const patientRow = document.querySelector(`[data-patient-id="${patientId}"]`)?.closest('tr');
    if (patientRow) {
        const statusCell = patientRow.querySelector('[id^="status-"]');
        if (statusCell) {
            statusCell.innerHTML = '<span class="status-badge status-active">Checked In</span>';
        }
    }
    
    // Show success notification
    showNotification(`Patient ${patientName} has been checked in`, 'success');
}

// Function to create a new task (for the Create Task button)
function createNewTask() {
    // Get modal elements (would be in the HTML)
    const modal = document.getElementById('createTaskModal');
    if (modal) {
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
    } else {
        console.error('Create Task modal not found in the DOM');
    }
}

// Function to save a new task from the modal form
function saveNewTask(form) {
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return false;
    }
    
    const taskTitle = form.querySelector('#taskTitle').value;
    const taskDescription = form.querySelector('#taskDescription').value;
    const taskPriority = form.querySelector('#taskPriority').value;
    const taskAssignee = form.querySelector('#taskAssignee').value;
    
    // In a real app, save to server
    console.log('New task created:', { 
        title: taskTitle, 
        description: taskDescription,
        priority: taskPriority,
        assignee: taskAssignee
    });
    
    // Create new task UI element
    const tasksList = document.querySelector('.list-group');
    if (tasksList) {
        const newTask = document.createElement('div');
        newTask.className = 'list-group-item list-group-item-action d-flex justify-content-between align-items-center';
        
        // Set priority badge class based on selection
        let priorityClass = 'bg-secondary';
        let priorityText = 'Low Priority';
        
        if (taskPriority === 'high') {
            priorityClass = 'bg-warning text-dark';
            priorityText = 'High Priority';
        } else if (taskPriority === 'medium') {
            priorityClass = 'bg-info text-dark';
            priorityText = 'Medium Priority';
        }
        
        // Set HTML content
        newTask.innerHTML = `
            <div>
                <h6 class="mb-1">${taskTitle}</h6>
                <p class="text-muted mb-0 small">${taskDescription}</p>
            </div>
            <div>
                <span class="badge ${priorityClass}">${priorityText}</span>
                <button class="btn btn-sm btn-outline-success ms-2">Complete</button>
            </div>
        `;
        
        // Add event listener to the Complete button
        const completeButton = newTask.querySelector('.btn-outline-success');
        completeButton.addEventListener('click', function(e) {
            e.preventDefault();
            this.innerHTML = '<i class="fas fa-check"></i> Done';
            this.classList.remove('btn-outline-success');
            this.classList.add('btn-success');
            this.disabled = true;
            newTask.classList.add('task-completed');
            newTask.style.opacity = '0.7';
            
            showNotification('Task marked as completed!', 'success');
        });
        
        // Add to the list
        tasksList.prepend(newTask);
    }
    
    // Reset form and hide modal
    form.reset();
    form.classList.remove('was-validated');
    
    // Close modal
    const modal = bootstrap.Modal.getInstance(document.getElementById('createTaskModal'));
    if (modal) {
        modal.hide();
    }
    
    // Show success message
    showNotification('New task created successfully!', 'success');
    
    return false; // Prevent form submission
}
