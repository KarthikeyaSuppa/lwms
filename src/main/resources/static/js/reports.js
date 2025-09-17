document.addEventListener('DOMContentLoaded', () => {
  // API endpoints
  const API_BASE = '/reports/api';
  
  // Chart/data storage
  let chartData = {
    inventoryStatus: [],
    shipmentStatus: [],
    equipmentHeatmap: [],
    movementTrends: [],
    locationUtilization: []
  };

  // Initialize the page
  initializePage();

  async function initializePage() {
    try {
      await loadAllData();
      renderAllCharts();
      setupEventListeners();
    } catch (error) {
      window.toastManager && window.toastManager.error('Failed to load reports data: ' + error.message);
    }
  }

  async function loadAllData() {
    const timeRange = 30;
    try {
          const [
      metrics,
      inventoryData,
      shipmentData,
      equipmentData,
      locationData
    ] = await Promise.all([
      fetch(`${API_BASE}/metrics?days=${timeRange}`).then(r => r.json()),
      fetch(`${API_BASE}/inventory-status?days=${timeRange}`).then(r => r.json()),
      fetch(`${API_BASE}/shipment-status?days=${timeRange}`).then(r => r.json()),
      fetch(`${API_BASE}/equipment-heatmap?days=${timeRange}`).then(r => r.json()),
      fetch(`${API_BASE}/location-utilization?days=${timeRange}`).then(r => r.json())
    ]);

    // Update metrics
    updateMetrics(metrics);
    
    // Store chart data
    chartData.inventoryStatus = inventoryData;
    chartData.shipmentStatus = shipmentData;
    chartData.equipmentHeatmap = equipmentData;
    chartData.locationUtilization = locationData;
      
    } catch (error) {
      console.error('Error loading data:', error);
      // Use mock data if API fails
      loadMockData();
    }
  }

  function loadMockData() {
    // Mock data for demonstration
    chartData.inventoryStatus = [
      { status: 'Optimal', count: 45, color: '#4A90E2' },
      { status: 'Low Stock', count: 20, color: '#F5A623' },
      { status: 'Out of Stock', count: 8, color: '#E04439' },
      { status: 'Overstock', count: 12, color: '#BD10E0' }
    ];

    chartData.shipmentStatus = [
      { status: 'Planned', count: 15 },
      { status: 'In Transit', count: 8 },
      { status: 'Delivered', count: 32 },
      { status: 'Cancelled', count: 3 }
    ];

    chartData.equipmentHeatmap = generateMockHeatmapData();
    chartData.movementTrends = generateMockTrendData();
    chartData.locationUtilization = generateMockLocationData();

    // Update metrics with mock data
    updateMetrics({
      totalInventory: 85,
      totalShipments: 58,
      activeEquipment: 24,
      totalValue: 125000
    });
  }

  function updateMetrics(metrics) {
    animateValue('totalInventory', metrics.totalInventory || 0);
    animateValue('totalShipments', metrics.totalShipments || 0);
    animateValue('activeEquipment', metrics.activeEquipment || 0);
    animateValue('totalValue', Math.round(metrics.totalValue || 0), true);
  }

  function animateValue(elementId, value, isCurrency = false) {
    const element = document.getElementById(elementId);
    if (!element) return;
    const start = 0;
    const end = value;
    const duration = 1000;
    const steps = Math.max(1, Math.floor(duration / 16));
    const increment = end / steps;
    let current = start;

    const timer = setInterval(() => {
      current += increment;
      if (current >= end) {
        current = end;
        clearInterval(timer);
      }
      
      if (isCurrency) {
        element.textContent = '$' + Math.floor(current).toLocaleString();
      } else {
        element.textContent = Math.floor(current).toLocaleString();
      }
    }, 16);
  }

  function renderAllCharts() {
    renderDonutChart();
    renderBarChart();
    renderHeatmapChart();
    renderLocationGrid();
  }

  // Donut Chart (only)
  function renderDonutChart() {
    const chart = document.getElementById('inventoryStatusChart');
    const legend = document.getElementById('inventoryLegend');
    const percentageDisplay = document.getElementById('chart-percentage');
    const categoryDisplay = document.getElementById('chart-category');
    
    if (!chart || !chartData.inventoryStatus.length) return;

    let totalValue = chartData.inventoryStatus.reduce((sum, item) => sum + (item.count || 0), 0);
    let cumulativePercentage = 0;

    const gradientParts = chartData.inventoryStatus.map(item => {
      const percentage = totalValue > 0 ? (item.count / totalValue) * 100 : 0;
      const startAngle = cumulativePercentage;
      const endAngle = cumulativePercentage + percentage;
      item.startAngle = (startAngle / 100) * 360;
      item.endAngle = (endAngle / 100) * 360;
      const start = startAngle.toFixed(2);
      const end = endAngle.toFixed(2);
      cumulativePercentage = endAngle;
      return `${item.color} ${start}% ${end}%`;
    }).join(', ');

    chart.style.background = `conic-gradient(${gradientParts})`;

    // Create legend
    legend.innerHTML = '';
    chartData.inventoryStatus.forEach((item, index) => {
      const legendItem = document.createElement('div');
      legendItem.className = 'legend-item d-flex align-items-center p-2 rounded';
      legendItem.dataset.index = String(index);
      legendItem.innerHTML = `
        <div class="legend-color me-2" style="background-color: ${item.color};"></div>
        <span class="text-sm text-dark">${item.status} (${item.count})</span>
      `;
      legend.appendChild(legendItem);
    });

    // Add click handlers
    setupDonutChartInteractions(chart, legend, percentageDisplay, categoryDisplay, chartData.inventoryStatus, totalValue);
  }

  function setupDonutChartInteractions(chart, legend, percentageDisplay, categoryDisplay, data, totalValue) {
    const legendItems = legend.querySelectorAll('.legend-item');

    function updateDisplay(index) {
      if (index < 0 || index >= data.length) return;
      const selectedData = data[index];
      const percentage = totalValue > 0 ? (selectedData.count / totalValue) * 100 : 0;
      percentageDisplay.textContent = `${percentage.toFixed(0)}%`;
      categoryDisplay.textContent = selectedData.status;
      legendItems.forEach((item, i) => {
        item.classList.toggle('active', i === index);
      });
    }

    // Default to first category if exists
    if (data.length > 0) updateDisplay(0);

    legend.addEventListener('click', (e) => {
      const legendItem = e.target.closest('.legend-item');
      if (legendItem) {
        const index = parseInt(legendItem.dataset.index, 10);
        updateDisplay(index);
      }
    });

    chart.addEventListener('click', (e) => {
      const rect = chart.getBoundingClientRect();
      const x = e.clientX - rect.left - rect.width / 2;
      const y = e.clientY - rect.top - rect.height / 2;
      let angle = Math.atan2(y, x) * (180 / Math.PI) + 90;
      if (angle < 0) {
        angle += 360;
      }
      const clickedSegment = data.findIndex(item => angle >= item.startAngle && angle < item.endAngle);
      if (clickedSegment !== -1) {
        updateDisplay(clickedSegment);
      }
    });
  }

  // Shipment Status Bar
  function renderBarChart() {
    const chart = document.getElementById('shipmentStatusChart');
    if (!chart || !chartData.shipmentStatus.length) return;

    chart.innerHTML = '';
    const maxValue = Math.max(...chartData.shipmentStatus.map(item => item.count || 0), 1);

    chartData.shipmentStatus.forEach(item => {
      const barContainer = document.createElement('div');
      barContainer.className = 'd-flex flex-column align-items-center position-relative';
      
      const bar = document.createElement('div');
      bar.className = 'bar';
      const height = (item.count / maxValue) * 200;
      bar.style.height = `${height}px`;
      
      const barValue = document.createElement('div');
      barValue.className = 'bar-value';
      barValue.textContent = String(item.count);
      
      const barLabel = document.createElement('div');
      barLabel.className = 'bar-label';
      barLabel.textContent = item.status;
      
      barContainer.appendChild(barValue);
      barContainer.appendChild(bar);
      barContainer.appendChild(barLabel);
      chart.appendChild(barContainer);
    });
  }

  // Equipment Heatmap (only heatmap)
  function renderHeatmapChart() {
    const chart = document.getElementById('equipmentHeatmapChart');
    const info = document.getElementById('equipmentHeatmapInfo');
    if (!chart || !chartData.equipmentHeatmap.length) return;

    chart.innerHTML = '';
    if (info) info.innerHTML = '<div class="placeholder text-muted">Select a cell to view info</div>';
    
    chartData.equipmentHeatmap.forEach((item) => {
      const cell = document.createElement('div');
      cell.className = 'heatmap-cell';
      cell.dataset.intensity = item.intensity;
      cell.title = `${item.equipment}: ${item.status}`;
      cell.addEventListener('click', () => {
        if (info) {
          info.innerHTML = `<div class="detail-item"><span class="label">Equipment:</span> ${item.equipment}</div>
            <div class=\"detail-item\"><span class=\"label\">Status:</span> ${item.status}</div>
            <div class=\"detail-item\"><span class=\"label\">Intensity:</span> ${item.intensity}</div>`;
        }
      });
      chart.appendChild(cell);
    });
  }


  // Location Utilization Grid + Details panel
  function renderLocationGrid() {
    const grid = document.getElementById('locationGrid');
    const details = document.getElementById('locationDetails');
    if (!grid || !chartData.locationUtilization.length) return;

    grid.innerHTML = '';

    chartData.locationUtilization.forEach((loc, index) => {
      const box = document.createElement('div');
      box.className = 'location-box';
      box.dataset.index = String(index);

      const utilization = Math.max(0, Math.min(100, loc.utilization || 0));
      let level = 'low';
      if (utilization >= 75) level = 'high';
      else if (utilization >= 50) level = 'medium';

      box.dataset.utilizationLevel = level;
      box.innerHTML = `
        <div class="location-code">${loc.location}</div>
        <div class="location-capacity">Cap: ${loc.capacity ?? '-'} | Load: ${loc.currentLoad ?? '-'}</div>
        <div class="utilization-bar"><span style="width:${utilization}%"></span></div>
      `;

      box.addEventListener('click', () => {
        renderLocationDetails(details, loc);
        grid.querySelectorAll('.location-box').forEach(el => el.classList.remove('active'));
        box.classList.add('active');
      });

      grid.appendChild(box);
    });

    // Select first by default
    if (chartData.locationUtilization.length) {
      renderLocationDetails(details, chartData.locationUtilization[0]);
      const firstBox = grid.querySelector('.location-box');
      if (firstBox) firstBox.classList.add('active');
    }
  }

  function renderLocationDetails(container, loc) {
    if (!container || !loc) return;
    container.innerHTML = `
      <div class="detail-item"><span class="label">Location:</span> ${loc.location}</div>
      <div class="detail-item"><span class="label">Capacity:</span> ${loc.capacity ?? '-'}</div>
      <div class="detail-item"><span class="label">Current Load:</span> ${loc.currentLoad ?? '-'}</div>
      <div class="detail-item"><span class="label">Utilization:</span> ${Math.max(0, Math.min(100, loc.utilization || 0))}%</div>
    `;
  }

  function setupEventListeners() {
    // Time range filter
    const timeFilter = document.getElementById('timeRangeFilter');
    if (timeFilter) {
      timeFilter.addEventListener('change', () => {
        initializePage();
      });
    }

    // Refresh button
    const refreshBtn = document.getElementById('refreshDataBtn');
    if (refreshBtn) {
      refreshBtn.addEventListener('click', () => {
        initializePage();
      });
    }

    // Chart type toggles (simplified to active-only buttons)
    document.querySelectorAll('.chart-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const container = e.target.closest('.chart-controls');
        container && container.querySelectorAll('.chart-btn').forEach(b => b.classList.remove('active'));
        e.target.classList.add('active');
        // No alternate chart rendering required per spec
      });
    });
  }

  // Mock data generators
  function generateMockHeatmapData() {
    const intensities = ['low', 'medium', 'high', 'critical'];
    const statuses = ['Active', 'Maintenance', 'Idle', 'Broken'];
    const equipment = ['Forklift', 'Conveyor', 'Crane', 'Scanner', 'Pallet Jack'];
    
    return Array.from({ length: 20 }, (_, i) => ({
      equipment: equipment[i % equipment.length],
      status: statuses[i % statuses.length],
      intensity: intensities[Math.floor(Math.random() * intensities.length)]
    }));
  }

  function generateMockTrendData() {
    const days = 30;
    return Array.from({ length: days }, (_, i) => ({
      date: new Date(Date.now() - (days - i) * 24 * 60 * 60 * 1000).toLocaleDateString(),
      value: Math.floor(Math.random() * 100) + 20
    }));
  }

  function generateMockLocationData() {
    const locations = ['A-1-1-1', 'A-1-1-2', 'A-1-2-1', 'B-1-1-1', 'B-1-1-2'];
    return locations.map(location => ({
      location,
      utilization: Math.floor(Math.random() * 100),
      capacity: 100,
      currentLoad: Math.floor(Math.random() * 100)
    }));
  }
});
