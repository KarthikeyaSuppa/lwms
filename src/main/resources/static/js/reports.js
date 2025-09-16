document.addEventListener('DOMContentLoaded', () => {
  // API endpoints
  const API_BASE = '/reports/api';
  
  // Chart data storage
  let chartData = {
    inventoryStatus: [],
    shipmentStatus: [],
    equipmentHeatmap: [],
    movementTrends: [],
    locationUtilization: [],
    supplierPerformance: [],
    activitySummary: []
  };

  // Initialize the page
  initializePage();

  async function initializePage() {
    try {
      await loadAllData();
      renderAllCharts();
      renderActivityTable();
      setupEventListeners();
    } catch (error) {
      window.toastManager.error('Failed to load reports data: ' + error.message);
    }
  }

  async function loadAllData() {
    const timeRange = document.getElementById('timeRangeFilter').value;
    
    try {
      // Load all data in parallel
      const [
        metrics,
        inventoryData,
        shipmentData,
        equipmentData,
        movementData,
        locationData,
        supplierData,
        activityData
      ] = await Promise.all([
        fetch(${API_BASE}/metrics?days=).then(r => r.json()),
        fetch(${API_BASE}/inventory-status?days=).then(r => r.json()),
        fetch(${API_BASE}/shipment-status?days=).then(r => r.json()),
        fetch(${API_BASE}/equipment-heatmap?days=).then(r => r.json()),
        fetch(${API_BASE}/movement-trends?days=).then(r => r.json()),
        fetch(${API_BASE}/location-utilization?days=).then(r => r.json()),
        fetch(${API_BASE}/supplier-performance?days=).then(r => r.json()),
        fetch(${API_BASE}/activity-summary?days=).then(r => r.json())
      ]);

      // Update metrics
      updateMetrics(metrics);
      
      // Store chart data
      chartData.inventoryStatus = inventoryData;
      chartData.shipmentStatus = shipmentData;
      chartData.equipmentHeatmap = equipmentData;
      chartData.movementTrends = movementData;
      chartData.locationUtilization = locationData;
      chartData.supplierPerformance = supplierData;
      chartData.activitySummary = activityData;
      
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
    chartData.supplierPerformance = generateMockSupplierData();
    chartData.activitySummary = generateMockActivityData();

    // Update metrics with mock data
    updateMetrics({
      totalInventory: 85,
      totalShipments: 58,
      activeEquipment: 24,
      totalValue: 125000
    });
  }

  function updateMetrics(metrics) {
    animateValue('totalInventory', metrics.totalInventory);
    animateValue('totalShipments', metrics.totalShipments);
    animateValue('activeEquipment', metrics.activeEquipment);
    animateValue('totalValue', metrics.totalValue, true);
  }

  function animateValue(elementId, value, isCurrency = false) {
    const element = document.getElementById(elementId);
    const start = 0;
    const end = value;
    const duration = 1000;
    const increment = end / (duration / 16);
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
    renderLineChart();
    renderLocationChart();
    renderRadarChart();
  }

  function renderDonutChart() {
    const chart = document.getElementById('inventoryStatusChart');
    const legend = document.getElementById('inventoryLegend');
    const percentageDisplay = document.getElementById('chart-percentage');
    const categoryDisplay = document.getElementById('chart-category');
    
    if (!chart || !chartData.inventoryStatus.length) return;

    let totalValue = 0;
    let cumulativePercentage = 0;

    const gradientParts = chartData.inventoryStatus.map(item => {
      totalValue += item.count;
      return item;
    }).map(item => {
      const percentage = (item.count / totalValue) * 100;
      const startAngle = cumulativePercentage;
      const endAngle = cumulativePercentage + percentage;
      item.startAngle = (startAngle / 100) * 360;
      item.endAngle = (endAngle / 100) * 360;
      const gradientString = ${item.color} % %;
      cumulativePercentage = endAngle;
      return gradientString;
    }).join(', ');

    chart.style.background = conic-gradient();

    // Create legend
    legend.innerHTML = '';
    chartData.inventoryStatus.forEach((item, index) => {
      const legendItem = document.createElement('div');
      legendItem.className = 'legend-item d-flex align-items-center p-2 rounded';
      legendItem.dataset.index = index;
      legendItem.innerHTML = 
        <div class="legend-color me-2" style="background-color: ;"></div>
        <span class="text-sm text-dark"></span>
      ;
      legend.appendChild(legendItem);
    });

    // Add click handlers
    setupDonutChartInteractions(chart, legend, percentageDisplay, categoryDisplay, chartData.inventoryStatus, totalValue);
  }

  function setupDonutChartInteractions(chart, legend, percentageDisplay, categoryDisplay, data, totalValue) {
    const legendItems = document.querySelectorAll('.legend-item');

    function updateDisplay(index) {
      if (index < 0 || index >= data.length) return;
      const selectedData = data[index];
      const percentage = (selectedData.count / totalValue) * 100;
      percentageDisplay.textContent = ${percentage.toFixed(0)}%;
      categoryDisplay.textContent = selectedData.status;
      legendItems.forEach((item, i) => {
        item.classList.toggle('active', i === index);
      });
    }

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

  function renderBarChart() {
    const chart = document.getElementById('shipmentStatusChart');
    if (!chart || !chartData.shipmentStatus.length) return;

    chart.innerHTML = '';
    const maxValue = Math.max(...chartData.shipmentStatus.map(item => item.count));

    chartData.shipmentStatus.forEach(item => {
      const barContainer = document.createElement('div');
      barContainer.className = 'd-flex flex-column align-items-center';
      
      const bar = document.createElement('div');
      bar.className = 'bar';
      const height = (item.count / maxValue) * 200;
      bar.style.height = ${height}px;
      
      const barValue = document.createElement('div');
      barValue.className = 'bar-value';
      barValue.textContent = item.count;
      
      const barLabel = document.createElement('div');
      barLabel.className = 'bar-label';
      barLabel.textContent = item.status;
      
      barContainer.appendChild(barValue);
      barContainer.appendChild(bar);
      barContainer.appendChild(barLabel);
      chart.appendChild(barContainer);
    });
  }

  function renderHeatmapChart() {
    const chart = document.getElementById('equipmentHeatmapChart');
    if (!chart || !chartData.equipmentHeatmap.length) return;

    chart.innerHTML = '';
    
    chartData.equipmentHeatmap.forEach((item, index) => {
      const cell = document.createElement('div');
      cell.className = 'heatmap-cell';
      cell.dataset.intensity = item.intensity;
      cell.title = ${item.equipment}: ;
      chart.appendChild(cell);
    });
  }

  function renderLineChart() {
    const chart = document.getElementById('movementTrendsChart');
    if (!chart || !chartData.movementTrends.length) return;

    chart.innerHTML = '';
    
    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svg.setAttribute('viewBox', '0 0 400 200');
    
    const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    const points = chartData.movementTrends.map((item, index) => {
      const x = (index / (chartData.movementTrends.length - 1)) * 350 + 25;
      const y = 175 - (item.value / Math.max(...chartData.movementTrends.map(d => d.value))) * 150;
      return ${x},;
    }).join(' L');
    
    path.setAttribute('d', M );
    path.setAttribute('class', 'line-path');
    svg.appendChild(path);
    
    chartData.movementTrends.forEach((item, index) => {
      const x = (index / (chartData.movementTrends.length - 1)) * 350 + 25;
      const y = 175 - (item.value / Math.max(...chartData.movementTrends.map(d => d.value))) * 150;
      
      const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
      circle.setAttribute('cx', x);
      circle.setAttribute('cy', y);
      circle.setAttribute('r', 4);
      circle.setAttribute('class', 'line-point');
      circle.setAttribute('title', ${item.date}: );
      svg.appendChild(circle);
    });
    
    chart.appendChild(svg);
  }

  function renderLocationChart() {
    const chart = document.getElementById('locationUtilizationChart');
    if (!chart || !chartData.locationUtilization.length) return;

    chart.innerHTML = '';
    const maxValue = Math.max(...chartData.locationUtilization.map(item => item.utilization));

    chartData.locationUtilization.forEach(item => {
      const barContainer = document.createElement('div');
      barContainer.className = 'd-flex flex-column align-items-center';
      
      const bar = document.createElement('div');
      bar.className = 'bar';
      const height = (item.utilization / maxValue) * 200;
      bar.style.height = ${height}px;
      
      const barValue = document.createElement('div');
      barValue.className = 'bar-value';
      barValue.textContent = ${item.utilization}%;
      
      const barLabel = document.createElement('div');
      barLabel.className = 'bar-label';
      barLabel.textContent = item.location;
      
      barContainer.appendChild(barValue);
      barContainer.appendChild(bar);
      barContainer.appendChild(barLabel);
      chart.appendChild(barContainer);
    });
  }

  function renderRadarChart() {
    const chart = document.getElementById('supplierPerformanceChart');
    if (!chart || !chartData.supplierPerformance.length) return;

    chart.innerHTML = '';
    
    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    svg.setAttribute('viewBox', '0 0 200 200');
    
    const centerX = 100;
    const centerY = 100;
    const radius = 80;
    
    // Draw grid circles
    for (let i = 1; i <= 5; i++) {
      const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
      circle.setAttribute('cx', centerX);
      circle.setAttribute('cy', centerY);
      circle.setAttribute('r', (radius / 5) * i);
      circle.setAttribute('class', 'radar-grid');
      circle.setAttribute('fill', 'none');
      svg.appendChild(circle);
    });
    
    // Draw axes
    const axes = ['Quality', 'Delivery', 'Price', 'Service', 'Reliability'];
    axes.forEach((axis, index) => {
      const angle = (index * 360 / axes.length) * Math.PI / 180;
      const x2 = centerX + Math.cos(angle) * radius;
      const y2 = centerY + Math.sin(angle) * radius;
      
      const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
      line.setAttribute('x1', centerX);
      line.setAttribute('y1', centerY);
      line.setAttribute('x2', x2);
      line.setAttribute('y2', y2);
      line.setAttribute('class', 'radar-axis');
      svg.appendChild(line);
    });
    
    chart.appendChild(svg);
  }

  function renderActivityTable() {
    const tbody = document.getElementById('activityTableBody');
    if (!tbody || !chartData.activitySummary.length) return;

    tbody.innerHTML = '';
    
    chartData.activitySummary.forEach(activity => {
      const row = document.createElement('tr');
      row.innerHTML = 
        <td><span class="label"></span></td>
        <td><span class="label"></span></td>
        <td><span class="label"></span></td>
        <td><span class="label"></span></td>
        <td><span class="label"></span></td>
      ;
      tbody.appendChild(row);
    });
  }

  function setupEventListeners() {
    // Time range filter
    document.getElementById('timeRangeFilter').addEventListener('change', () => {
      initializePage();
    });

    // Refresh button
    document.getElementById('refreshDataBtn').addEventListener('click', () => {
      initializePage();
    });

    // Chart type toggles
    document.querySelectorAll('.chart-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const container = e.target.closest('.chart-controls');
        container.querySelectorAll('.chart-btn').forEach(b => b.classList.remove('active'));
        e.target.classList.add('active');
        
        // Re-render chart with new type
        const chartType = e.target.dataset.chart;
        const chartCard = e.target.closest('.chart-card');
        const chartId = chartCard.querySelector('.chart-wrapper').id;
        
        // This would switch chart types - simplified for now
        console.log(Switching  to  chart);
      });
    });

    // Activity filter
    document.getElementById('activityFilter').addEventListener('change', (e) => {
      filterActivityTable(e.target.value);
    });
  }

  function filterActivityTable(filter) {
    const tbody = document.getElementById('activityTableBody');
    const rows = tbody.querySelectorAll('tr');
    
    rows.forEach(row => {
      const type = row.cells[0].textContent.toLowerCase();
      if (filter === 'all' || type.includes(filter.toLowerCase())) {
        row.style.display = '';
      } else {
        row.style.display = 'none';
      }
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
    const locations = ['Zone A', 'Zone B', 'Zone C', 'Zone D', 'Zone E'];
    return locations.map(location => ({
      location,
      utilization: Math.floor(Math.random() * 100)
    }));
  }

  function generateMockSupplierData() {
    return [
      { metric: 'Quality', value: 85 },
      { metric: 'Delivery', value: 90 },
      { metric: 'Price', value: 75 },
      { metric: 'Service', value: 88 },
      { metric: 'Reliability', value: 92 }
    ];
  }

  function generateMockActivityData() {
    const types = ['Inventory', 'Shipment', 'Equipment'];
    const descriptions = ['Item added', 'Shipment created', 'Equipment maintenance', 'Location updated'];
    const statuses = ['Completed', 'In Progress', 'Pending'];
    const users = ['admin123', 'manager123', 'operator1'];
    
    return Array.from({ length: 10 }, (_, i) => ({
      type: types[i % types.length],
      description: descriptions[i % descriptions.length],
      status: statuses[i % statuses.length],
      date: new Date(Date.now() - i * 24 * 60 * 60 * 1000).toLocaleDateString(),
      user: users[i % users.length]
    }));
  }
});
