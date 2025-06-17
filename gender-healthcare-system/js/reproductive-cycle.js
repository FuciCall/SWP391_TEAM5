/**
 * Reproductive Cycle Tracking System
 * Handles menstrual cycle declaration, ovulation predictions, 
 * pregnancy likelihood, and contraceptive reminders
 */

class ReproductiveCycleTracker {
    constructor() {
        this.cycles = this.loadCycles();
        this.reminderSettings = this.loadReminderSettings();
        this.pillTaken = this.loadPillTaken();
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.updateOverviewCards();
        this.renderCycleHistory();
        this.updateReminderInfo();
        this.setupNotifications();
        this.renderInsights();
        this.startReminderTimer();
    }

    setupEventListeners() {
        // Tab switching
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const tabId = e.target.dataset.tab;
                this.switchTab(tabId);
            });
        });

        // Cycle declaration form
        const cycleForm = document.getElementById('cycle-form');
        if (cycleForm) {
            cycleForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleCycleDeclaration(e);
            });
        }

        // Pill reminder form
        const pillForm = document.getElementById('pill-reminder-form');
        if (pillForm) {
            pillForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handlePillReminderSetup(e);
            });
        }

        // Mark pill taken button
        const markPillBtn = document.getElementById('mark-pill-taken');
        if (markPillBtn) {
            markPillBtn.addEventListener('click', () => {
                this.markPillTaken();
            });
        }

        // Toast close button
        const closeToast = document.getElementById('close-toast');
        if (closeToast) {
            closeToast.addEventListener('click', () => {
                this.hideToast();
            });
        }

        // Request notification permission
        document.getElementById('enable-notifications')?.addEventListener('change', (e) => {
            if (e.target.checked) {
                this.requestNotificationPermission();
            }
        });
    }

    switchTab(tabId) {
        // Remove active class from all tabs and buttons
        document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

        // Add active class to selected tab and button
        document.querySelector(`[data-tab="${tabId}"]`).classList.add('active');
        document.getElementById(tabId).classList.add('active');
    }

    handleCycleDeclaration(event) {
        const formData = new FormData(event.target);
        const symptoms = Array.from(formData.getAll('symptoms'));
        
        const cycleData = {
            id: Date.now(),
            periodStart: formData.get('periodStart'),
            periodEnd: formData.get('periodEnd'),
            cycleLength: parseInt(formData.get('cycleLength')) || 28,
            periodLength: parseInt(formData.get('periodLength')) || 5,
            symptoms: symptoms,
            flowIntensity: formData.get('flowIntensity'),
            dateCreated: new Date().toISOString()
        };

        // Validate dates
        if (!this.validateCycleData(cycleData)) {
            return;
        }

        // Calculate ovulation date
        cycleData.ovulationDate = this.calculateOvulationDate(cycleData.periodStart, cycleData.cycleLength);
        cycleData.nextPeriodDate = this.calculateNextPeriodDate(cycleData.periodStart, cycleData.cycleLength);

        // Save cycle data
        this.cycles.unshift(cycleData);
        this.saveCycles();

        // Update UI
        this.updateOverviewCards();
        this.renderCycleHistory();
        this.renderInsights();

        // Show success message
        this.showToast('Cycle data saved successfully!', 'success');

        // Reset form
        event.target.reset();

        // Schedule notifications
        this.scheduleOvulationNotification(cycleData);
    }

    validateCycleData(data) {
        const startDate = new Date(data.periodStart);
        const endDate = data.periodEnd ? new Date(data.periodEnd) : null;
        const today = new Date();

        if (startDate > today) {
            this.showToast('Period start date cannot be in the future!', 'error');
            return false;
        }

        if (endDate && endDate < startDate) {
            this.showToast('Period end date cannot be before start date!', 'error');
            return false;
        }

        if (endDate && endDate > today) {
            this.showToast('Period end date cannot be in the future!', 'error');
            return false;
        }

        return true;
    }

    calculateOvulationDate(periodStart, cycleLength) {
        const startDate = new Date(periodStart);
        const ovulationDate = new Date(startDate);
        ovulationDate.setDate(startDate.getDate() + cycleLength - 14);
        return ovulationDate.toISOString().split('T')[0];
    }

    calculateNextPeriodDate(periodStart, cycleLength) {
        const startDate = new Date(periodStart);
        const nextDate = new Date(startDate);
        nextDate.setDate(startDate.getDate() + cycleLength);
        return nextDate.toISOString().split('T')[0];
    }

    updateOverviewCards() {
        if (this.cycles.length === 0) {
            this.resetOverviewCards();
            return;
        }

        const latestCycle = this.cycles[0];
        const today = new Date();
        
        // Next period date
        const nextPeriodDate = new Date(latestCycle.nextPeriodDate);
        const daysUntilPeriod = Math.ceil((nextPeriodDate - today) / (1000 * 60 * 60 * 24));
        
        document.getElementById('next-period-date').textContent = this.formatDate(nextPeriodDate);
        document.getElementById('days-until-period').textContent = daysUntilPeriod > 0 
            ? `${daysUntilPeriod} days` 
            : daysUntilPeriod === 0 
                ? 'Today!' 
                : 'Overdue';

        // Next ovulation date
        const ovulationDate = new Date(latestCycle.ovulationDate);
        const daysUntilOvulation = Math.ceil((ovulationDate - today) / (1000 * 60 * 60 * 24));
        
        document.getElementById('next-ovulation-date').textContent = this.formatDate(ovulationDate);
        document.getElementById('days-until-ovulation').textContent = daysUntilOvulation > 0 
            ? `${daysUntilOvulation} days` 
            : daysUntilOvulation === 0 
                ? 'Today!' 
                : 'Passed';

        // Pregnancy likelihood
        const pregnancyData = this.calculatePregnancyLikelihood(ovulationDate, today);
        document.getElementById('pregnancy-likelihood').textContent = pregnancyData.likelihood;
        document.getElementById('pregnancy-percentage').textContent = `${pregnancyData.percentage}%`;
        
        // Update class for styling
        const pregnancyElement = document.getElementById('pregnancy-likelihood');
        pregnancyElement.className = `status-${pregnancyData.likelihood.toLowerCase()}`;

        // Pill reminder status
        if (this.reminderSettings.isActive) {
            const pillStatus = this.getPillStatus();
            document.getElementById('pill-status').textContent = pillStatus.status;
            document.getElementById('next-pill-time').textContent = this.reminderSettings.reminderTime;
        }
    }

    resetOverviewCards() {
        document.getElementById('next-period-date').textContent = '--';
        document.getElementById('days-until-period').textContent = '-- days';
        document.getElementById('next-ovulation-date').textContent = '--';
        document.getElementById('days-until-ovulation').textContent = '-- days';
        document.getElementById('pregnancy-likelihood').textContent = '--';
        document.getElementById('pregnancy-percentage').textContent = '--%';
    }

    calculatePregnancyLikelihood(ovulationDate, currentDate) {
        const daysDiff = Math.abs((ovulationDate - currentDate) / (1000 * 60 * 60 * 24));
        
        if (daysDiff <= 1) {
            return { likelihood: 'HIGH', percentage: 25 };
        } else if (daysDiff <= 2) {
            return { likelihood: 'HIGH', percentage: 20 };
        } else if (daysDiff <= 3) {
            return { likelihood: 'MEDIUM', percentage: 15 };
        } else if (daysDiff <= 5) {
            return { likelihood: 'MEDIUM', percentage: 10 };
        } else {
            return { likelihood: 'LOW', percentage: 2 };
        }
    }

    renderCycleHistory() {
        const historyContainer = document.getElementById('cycle-history-list');
        if (!historyContainer) return;

        if (this.cycles.length === 0) {
            historyContainer.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-calendar-alt" style="font-size: 3rem; color: #ccc; margin-bottom: 1rem;"></i>
                    <h3>No cycle data yet</h3>
                    <p>Start by declaring your first menstrual cycle in the "Declare Cycle" tab.</p>
                </div>
            `;
            return;
        }

        const historyHTML = this.cycles.map(cycle => `
            <div class="cycle-history-item">
                <h4>
                    <i class="fas fa-calendar-check"></i>
                    Cycle from ${this.formatDate(new Date(cycle.periodStart))}
                </h4>
                <div class="cycle-details">
                    <div class="cycle-detail">
                        <i class="fas fa-play"></i>
                        <span>Start: ${this.formatDate(new Date(cycle.periodStart))}</span>
                    </div>
                    ${cycle.periodEnd ? `
                        <div class="cycle-detail">
                            <i class="fas fa-stop"></i>
                            <span>End: ${this.formatDate(new Date(cycle.periodEnd))}</span>
                        </div>
                    ` : ''}
                    <div class="cycle-detail">
                        <i class="fas fa-clock"></i>
                        <span>Cycle Length: ${cycle.cycleLength} days</span>
                    </div>
                    <div class="cycle-detail">
                        <i class="fas fa-tint"></i>
                        <span>Period Length: ${cycle.periodLength} days</span>
                    </div>
                    <div class="cycle-detail">
                        <i class="fas fa-seedling"></i>
                        <span>Ovulation: ${this.formatDate(new Date(cycle.ovulationDate))}</span>
                    </div>
                    ${cycle.flowIntensity ? `
                        <div class="cycle-detail">
                            <i class="fas fa-chart-bar"></i>
                            <span>Flow: ${cycle.flowIntensity}</span>
                        </div>
                    ` : ''}
                </div>
                ${cycle.symptoms && cycle.symptoms.length > 0 ? `
                    <div class="symptoms">
                        <strong>Symptoms:</strong> ${cycle.symptoms.join(', ')}
                    </div>
                ` : ''}
            </div>
        `).join('');

        historyContainer.innerHTML = historyHTML;
    }

    handlePillReminderSetup(event) {
        const formData = new FormData(event.target);
        
        this.reminderSettings = {
            pillName: formData.get('pillName') || 'Contraceptive Pill',
            reminderTime: formData.get('reminderTime'),
            enableNotifications: formData.get('enableNotifications') === 'on',
            isActive: true,
            dateCreated: new Date().toISOString()
        };

        this.saveReminderSettings();
        this.updateReminderInfo();
        this.updateOverviewCards();
        this.startReminderTimer();

        this.showToast('Pill reminder set successfully!', 'success');

        if (this.reminderSettings.enableNotifications) {
            this.requestNotificationPermission();
        }
    }

    updateReminderInfo() {
        const reminderInfo = document.getElementById('current-reminder-info');
        if (!reminderInfo) return;

        if (!this.reminderSettings.isActive) {
            reminderInfo.innerHTML = `
                <p><i class="fas fa-info-circle"></i> No reminder set. Use the form above to set your daily pill reminder.</p>
            `;
            return;
        }

        reminderInfo.innerHTML = `
            <h4><i class="fas fa-bell"></i> Current Reminder Settings</h4>
            <div class="reminder-details">
                <p><strong>Pill:</strong> ${this.reminderSettings.pillName}</p>
                <p><strong>Time:</strong> ${this.reminderSettings.reminderTime}</p>
                <p><strong>Notifications:</strong> ${this.reminderSettings.enableNotifications ? 'Enabled' : 'Disabled'}</p>
                <p><strong>Status:</strong> <span class="status-active">Active</span></p>
            </div>
        `;

        // Update pill status for today
        this.updatePillStatusToday();
    }

    updatePillStatusToday() {
        const pillStatusToday = document.getElementById('pill-status-today');
        if (!pillStatusToday) return;

        const today = new Date().toDateString();
        const takenToday = this.pillTaken[today];

        if (takenToday) {
            pillStatusToday.innerHTML = `
                <p class="status-success">
                    <i class="fas fa-check-circle"></i> 
                    Taken at ${takenToday.time}
                </p>
            `;
            document.getElementById('mark-pill-taken').style.display = 'none';
        } else {
            pillStatusToday.innerHTML = `
                <p class="status-warning">
                    <i class="fas fa-exclamation-circle"></i> 
                    Not taken yet today
                </p>
            `;
            document.getElementById('mark-pill-taken').style.display = 'inline-flex';
        }
    }

    markPillTaken() {
        const today = new Date().toDateString();
        const currentTime = new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
        
        this.pillTaken[today] = {
            time: currentTime,
            timestamp: new Date().toISOString()
        };
        
        this.savePillTaken();
        this.updatePillStatusToday();
        this.showToast('Pill marked as taken!', 'success');
    }

    getPillStatus() {
        const today = new Date().toDateString();
        const takenToday = this.pillTaken[today];
        
        return {
            status: takenToday ? 'Taken Today' : 'Not Taken',
            taken: !!takenToday
        };
    }

    renderInsights() {
        this.renderFertilityWindow();
        this.renderCycleRegularity();
        this.renderHealthAlerts();
        this.renderCycleCalendar();
    }

    renderFertilityWindow() {
        const fertilityWindow = document.getElementById('fertility-window');
        if (!fertilityWindow) return;

        if (this.cycles.length === 0) {
            fertilityWindow.innerHTML = '<p>No cycle data available for fertility predictions.</p>';
            return;
        }

        const latestCycle = this.cycles[0];
        const ovulationDate = new Date(latestCycle.ovulationDate);
        const fertileStart = new Date(ovulationDate);
        fertileStart.setDate(ovulationDate.getDate() - 5);
        const fertileEnd = new Date(ovulationDate);
        fertileEnd.setDate(ovulationDate.getDate() + 1);

        const today = new Date();
        const isInFertileWindow = today >= fertileStart && today <= fertileEnd;

        fertilityWindow.innerHTML = `
            <div class="fertility-info">
                <p><strong>Fertile Window:</strong> ${this.formatDate(fertileStart)} - ${this.formatDate(fertileEnd)}</p>
                <p><strong>Peak Fertility:</strong> ${this.formatDate(ovulationDate)}</p>
                <div class="fertility-status ${isInFertileWindow ? 'fertile-now' : ''}">
                    <i class="fas ${isInFertileWindow ? 'fa-seedling' : 'fa-calendar'}"></i>
                    ${isInFertileWindow ? 'You are currently in your fertile window' : 'Not currently in fertile window'}
                </div>
            </div>
        `;
    }

    renderCycleRegularity() {
        const cycleRegularity = document.getElementById('cycle-regularity');
        if (!cycleRegularity) return;

        if (this.cycles.length < 3) {
            cycleRegularity.innerHTML = '<p>Need at least 3 cycles to analyze regularity.</p>';
            return;
        }

        const cycleLengths = this.cycles.slice(0, 6).map(cycle => cycle.cycleLength);
        const averageLength = cycleLengths.reduce((a, b) => a + b, 0) / cycleLengths.length;
        const variation = Math.max(...cycleLengths) - Math.min(...cycleLengths);

        let regularity = 'Regular';
        if (variation > 7) {
            regularity = 'Irregular';
        } else if (variation > 3) {
            regularity = 'Somewhat Irregular';
        }

        cycleRegularity.innerHTML = `
            <div class="regularity-info">
                <p><strong>Average Cycle Length:</strong> ${averageLength.toFixed(1)} days</p>
                <p><strong>Variation:</strong> ${variation} days</p>
                <p><strong>Regularity:</strong> <span class="status-${regularity.toLowerCase().replace(' ', '-')}">${regularity}</span></p>
                <div class="cycle-chart-mini">
                    ${cycleLengths.map((length, index) => `
                        <div class="cycle-bar" style="height: ${(length / 45) * 100}%">
                            <span>${length}</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }

    renderHealthAlerts() {
        const healthAlerts = document.getElementById('health-alerts');
        if (!healthAlerts) return;

        const alerts = [];

        // Check for irregular cycles
        if (this.cycles.length >= 3) {
            const cycleLengths = this.cycles.slice(0, 6).map(cycle => cycle.cycleLength);
            const variation = Math.max(...cycleLengths) - Math.min(...cycleLengths);
            
            if (variation > 7) {
                alerts.push({
                    type: 'warning',
                    message: 'Your cycles show significant variation. Consider consulting a healthcare provider.',
                    icon: 'fa-exclamation-triangle'
                });
            }
        }

        // Check for missed pills
        const today = new Date();
        const yesterday = new Date(today);
        yesterday.setDate(today.getDate() - 1);
        
        if (this.reminderSettings.isActive && !this.pillTaken[yesterday.toDateString()]) {
            alerts.push({
                type: 'error',
                message: 'You may have missed your contraceptive pill yesterday.',
                icon: 'fa-pills'
            });
        }

        // Check for overdue period
        if (this.cycles.length > 0) {
            const latestCycle = this.cycles[0];
            const nextPeriodDate = new Date(latestCycle.nextPeriodDate);
            const daysOverdue = Math.floor((today - nextPeriodDate) / (1000 * 60 * 60 * 24));
            
            if (daysOverdue > 7) {
                alerts.push({
                    type: 'info',
                    message: `Your period is ${daysOverdue} days late. Consider taking a pregnancy test or consulting a healthcare provider.`,
                    icon: 'fa-calendar-times'
                });
            }
        }

        if (alerts.length === 0) {
            healthAlerts.innerHTML = `
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i>
                    No health alerts at this time. Keep tracking your cycle regularly!
                </div>
            `;
        } else {
            healthAlerts.innerHTML = alerts.map(alert => `
                <div class="alert alert-${alert.type}">
                    <i class="fas ${alert.icon}"></i>
                    ${alert.message}
                </div>
            `).join('');
        }
    }

    renderCycleCalendar() {
        const cycleCalendar = document.getElementById('cycle-calendar');
        if (!cycleCalendar) return;

        // Simple calendar implementation
        const today = new Date();
        const currentMonth = today.getMonth();
        const currentYear = today.getFullYear();
        
        // Get first day of month and number of days
        const firstDay = new Date(currentYear, currentMonth, 1);
        const lastDay = new Date(currentYear, currentMonth + 1, 0);
        const daysInMonth = lastDay.getDate();
        const startingDayOfWeek = firstDay.getDay();

        // Calendar header
        const monthNames = ["January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"];
        
        let calendarHTML = `
            <div class="calendar-header">
                <h4>${monthNames[currentMonth]} ${currentYear}</h4>
            </div>
            <div class="calendar-days-header">
                <div>Sun</div><div>Mon</div><div>Tue</div><div>Wed</div><div>Thu</div><div>Fri</div><div>Sat</div>
            </div>
        `;

        // Empty cells for days before month starts
        for (let i = 0; i < startingDayOfWeek; i++) {
            calendarHTML += '<div class="calendar-day empty"></div>';
        }

        // Days of the month
        for (let day = 1; day <= daysInMonth; day++) {
            const currentDate = new Date(currentYear, currentMonth, day);
            const dateString = currentDate.toISOString().split('T')[0];
            
            let classes = ['calendar-day'];
            let dayInfo = day;

            // Check if today
            if (currentDate.toDateString() === today.toDateString()) {
                classes.push('today');
            }

            // Check cycle data
            if (this.cycles.length > 0) {
                const latestCycle = this.cycles[0];
                
                // Period days
                const periodStart = new Date(latestCycle.periodStart);
                const periodEnd = latestCycle.periodEnd ? new Date(latestCycle.periodEnd) : null;
                
                if (currentDate >= periodStart && (!periodEnd || currentDate <= periodEnd)) {
                    classes.push('period');
                }
                
                // Ovulation day
                const ovulationDate = new Date(latestCycle.ovulationDate);
                if (currentDate.toDateString() === ovulationDate.toDateString()) {
                    classes.push('ovulation');
                    dayInfo += ' 🥚';
                }
                
                // Fertile window
                const fertileStart = new Date(ovulationDate);
                fertileStart.setDate(ovulationDate.getDate() - 5);
                const fertileEnd = new Date(ovulationDate);
                fertileEnd.setDate(ovulationDate.getDate() + 1);
                
                if (currentDate >= fertileStart && currentDate <= fertileEnd && !classes.includes('ovulation')) {
                    classes.push('fertile');
                }
            }

            calendarHTML += `<div class="${classes.join(' ')}" data-date="${dateString}">${dayInfo}</div>`;
        }

        cycleCalendar.innerHTML = calendarHTML;
    }

    // Notification System
    setupNotifications() {
        if ('Notification' in window) {
            if (Notification.permission === 'default') {
                // Permission will be requested when user enables notifications
            }
        }
    }

    requestNotificationPermission() {
        if ('Notification' in window) {
            Notification.requestPermission().then(permission => {
                if (permission === 'granted') {
                    this.showToast('Notifications enabled!', 'success');
                } else {
                    this.showToast('Notifications denied. You won\'t receive pill reminders.', 'warning');
                    document.getElementById('enable-notifications').checked = false;
                }
            });
        }
    }

    scheduleOvulationNotification(cycle) {
        if (!this.reminderSettings.enableNotifications) return;

        const ovulationDate = new Date(cycle.ovulationDate);
        const notificationDate = new Date(ovulationDate);
        notificationDate.setDate(ovulationDate.getDate() - 1); // Notify 1 day before

        const now = new Date();
        const timeUntilNotification = notificationDate.getTime() - now.getTime();

        if (timeUntilNotification > 0) {
            setTimeout(() => {
                this.sendNotification(
                    'Ovulation Reminder',
                    'Your ovulation window is approaching. This is your most fertile time.',
                    'ovulation'
                );
            }, timeUntilNotification);
        }
    }

    startReminderTimer() {
        if (!this.reminderSettings.isActive) return;

        // Check every minute for pill reminder
        setInterval(() => {
            this.checkPillReminder();
        }, 60000); // 1 minute
    }

    checkPillReminder() {
        if (!this.reminderSettings.isActive || !this.reminderSettings.enableNotifications) return;

        const now = new Date();
        const currentTime = now.toTimeString().slice(0, 5); // HH:MM format
        const today = now.toDateString();

        // Check if it's time for pill reminder and not taken yet
        if (currentTime === this.reminderSettings.reminderTime && !this.pillTaken[today]) {
            this.sendNotification(
                'Contraceptive Pill Reminder',
                `Time to take your ${this.reminderSettings.pillName}!`,
                'pill'
            );
        }
    }

    sendNotification(title, body, type) {
        if ('Notification' in window && Notification.permission === 'granted') {
            const notification = new Notification(title, {
                body: body,
                icon: '/favicon.ico',
                badge: '/favicon.ico',
                tag: type,
                requireInteraction: true
            });

            notification.onclick = () => {
                window.focus();
                notification.close();
                
                if (type === 'pill') {
                    // Switch to pill reminder tab
                    this.switchTab('pill-reminders');
                }
            };

            // Auto close after 10 seconds
            setTimeout(() => {
                notification.close();
            }, 10000);
        }
    }

    // Utility Methods
    formatDate(date) {
        return date.toLocaleDateString('en-US', {
            weekday: 'short',
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    }

    showToast(message, type = 'info') {
        const toast = document.getElementById('notification-toast');
        const toastMessage = document.getElementById('toast-message');
        
        toastMessage.textContent = message;
        toast.className = `toast ${type}`;
        toast.classList.add('show');

        // Auto hide after 5 seconds
        setTimeout(() => {
            this.hideToast();
        }, 5000);
    }

    hideToast() {
        const toast = document.getElementById('notification-toast');
        toast.classList.remove('show');
    }

    // Data Persistence Methods
    loadCycles() {
        try {
            const stored = localStorage.getItem('reproductive_cycles');
            return stored ? JSON.parse(stored) : [];
        } catch (error) {
            console.error('Error loading cycles:', error);
            return [];
        }
    }

    saveCycles() {
        try {
            localStorage.setItem('reproductive_cycles', JSON.stringify(this.cycles));
        } catch (error) {
            console.error('Error saving cycles:', error);
        }
    }

    loadReminderSettings() {
        try {
            const stored = localStorage.getItem('pill_reminder_settings');
            return stored ? JSON.parse(stored) : { isActive: false };
        } catch (error) {
            console.error('Error loading reminder settings:', error);
            return { isActive: false };
        }
    }

    saveReminderSettings() {
        try {
            localStorage.setItem('pill_reminder_settings', JSON.stringify(this.reminderSettings));
        } catch (error) {
            console.error('Error saving reminder settings:', error);
        }
    }

    loadPillTaken() {
        try {
            const stored = localStorage.getItem('pill_taken_log');
            return stored ? JSON.parse(stored) : {};
        } catch (error) {
            console.error('Error loading pill taken log:', error);
            return {};
        }
    }

    savePillTaken() {
        try {
            localStorage.setItem('pill_taken_log', JSON.stringify(this.pillTaken));
        } catch (error) {
            console.error('Error saving pill taken log:', error);
        }
    }
}

// Initialize the reproductive cycle tracker when the page loads
document.addEventListener('DOMContentLoaded', () => {
    // Only initialize if we're on the reproductive cycle page
    if (document.getElementById('cycle-form')) {
        window.reproductiveCycleTracker = new ReproductiveCycleTracker();
    }
});

// Export for potential module use
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ReproductiveCycleTracker;
}
