const PRODUCTS_API_BASE = "api/products";
const INVENTORY_API_BASE = "api/inventory";
const ADMIN_LOGIN = "admin-login.html"

let liveTelemetryChartInstance = null;
let currentCapturedProductId = null;

let currentSortColumn = "qty";
let currentSortDirection = "asc";

let globalInventorySocket = null;
let transactionStreamCount = 0;

let orderStatusChartInstance = null;

document.addEventListener("DOMContentLoaded", () => {
    lucide.createIcons();
    setDefaultDashboardDateFrame();

    initializeTelemetryCharts();
    loadDashboardMetrics();
    loadProductAttributes();
    syncMasterInventoryViewports();
    connectLiveInventorySocket();
});

function setDefaultDashboardDateFrame() {
    const endInput = document.getElementById('dashEndDate');
    const startInput = document.getElementById('dashStartDate');

    if (endInput && startInput) {
        const today = new Date();
        const sevenDaysAgo = new Date();
        sevenDaysAgo.setDate(today.getDate() - 7);

        // Convert cleanly to yyyy-MM-dd HTML format standard strings
        endInput.value = today.toISOString().split('T')[0];
        startInput.value = sevenDaysAgo.toISOString().split('T')[0];
    }
}



window.triggerGlobalDashboardSync = async function() {
    const syncButtonNode = document.querySelector("button[onclick='triggerGlobalDashboardSync()']");
    const refreshIconNode = document.getElementById("iconGlobalRefresh");

    if (syncButtonNode && refreshIconNode) {
        syncButtonNode.classList.add("pointer-events-none", "opacity-70");
        refreshIconNode.classList.add("animate-spin", "text-indigo-600");
    }

    showAdminToast("Synchronizing system states across data planes...");

    try {
        await Promise.all([
            loadDashboardMetrics(),
            loadProductAttributes(),
            syncMasterInventoryViewports()
        ]);

        showAdminToast("System clusters reporting healthy synchronized data grids.");
    } catch (error) {
        console.error("Global cluster synchronization error exception:", error);
        showAdminToast("Data grid sync dropped parameters or failed mapping pipelines.", true);
    } finally {
        setTimeout(() => {
            const freshIconNode = document.getElementById("iconGlobalRefresh");
            const freshButtonNode = document.querySelector("button[onclick='triggerGlobalDashboardSync()']");

            if (freshIconNode && freshButtonNode) {
                freshIconNode.classList.remove("animate-spin", "text-indigo-600");
                freshButtonNode.classList.remove("pointer-events-none", "opacity-70");
            }

            lucide.createIcons();
        }, 300);
    }
}

window.switchAdminTab = function(targetViewId) {
    const tabs = ['overview', 'products', 'inventory', 'live-feed', 'orders'];

    tabs.forEach(view => {
        const componentNode = document.getElementById(`viewSegment-${view}`);
        const actionButton = document.getElementById(`btnTab-${view}`);

        if (view === targetViewId) {
            if (componentNode) componentNode.classList.remove('hidden');
            if (actionButton) actionButton.className = "w-full flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm font-semibold transition-all cursor-pointer bg-slate-800 text-white";
        } else {
            if (componentNode) componentNode.classList.add('hidden');
            if (actionButton) actionButton.className = "w-full flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm font-semibold transition-all cursor-pointer hover:bg-slate-800 hover:text-white text-slate-400";
        }
    });

    if (targetViewId === 'overview') document.getElementById('txtViewHeaderTitle').innerText = 'Overview Dashboard';
    if (targetViewId === 'products') document.getElementById('txtViewHeaderTitle').innerText = 'Product Catalog Management';
    if (targetViewId === 'inventory') document.getElementById('txtViewHeaderTitle').innerText = 'Inventory Management System';
    if (targetViewId === 'live-feed') document.getElementById('txtViewHeaderTitle').innerText = 'Live WebSocket Checkout Feeds';
    if (targetViewId === 'orders') {
        document.getElementById('txtViewHeaderTitle').innerText = 'Order Lifecycle Tracking';
        loadAdminOrderSummary();
    }
}

async function loadProductAttributes() {
    try {
        const response = await fetch(PRODUCTS_API_BASE);
        const jsonResponse = await response.json();

        if (response.status === 401) {
            window.location.href = ADMIN_LOGIN;
        }

        if (response.ok && jsonResponse.success && jsonResponse.data) {
            const attributes = jsonResponse.data;
            document.getElementById('selProductBrand').innerHTML = '<option value="">Select Brand</option>' +
                attributes.brandList.map(b => `<option value="${b.id}">${b.name}</option>`).join('');
            document.getElementById('selProductCategory').innerHTML = '<option value="">Select Category</option>' +
                attributes.categoryList.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
            document.getElementById('selProductWarehouse').innerHTML = '<option value="">Select Warehouse Link</option>' +
                attributes.warehouseList.map(w => `<option value="${w.id}">${w.name}</option>`).join('');
        }
    } catch (error) {
        console.error("Attributes metadata layout load fault:", error);
    }
}

window.executeProductSavePipeline = async function(event) {
    event.preventDefault();

    const productPayload = {
        title: document.getElementById('txtProductTitle').value.trim(),
        brandId: parseInt(document.getElementById('selProductBrand').value),
        categoryId: parseInt(document.getElementById('selProductCategory').value),
        warehouseId: parseInt(document.getElementById('selProductWarehouse').value),
        price: parseFloat(document.getElementById('numProductPrice').value),
        qty: parseInt(document.getElementById('numProductQty').value),
        description: document.getElementById('txtProductDesc').value.trim()
    };

    try {
        const response = await fetch(`${PRODUCTS_API_BASE}/save-product`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(productPayload)
        });

        const jsonResponse = await response.json();

        if (response.status === 401) {
            window.location.href = ADMIN_LOGIN;
        }
        if (response.ok && jsonResponse.success && jsonResponse.data) {
            currentCapturedProductId = jsonResponse.data;
            document.getElementById('txtUploadProductId').value = currentCapturedProductId;

            const uploadPanel = document.getElementById('panelImageUpload');
            uploadPanel.classList.remove('opacity-40', 'pointer-events-none');

            showAdminToast(`Product metadata saved. Entity Reference ID: ${currentCapturedProductId}`);
            document.getElementById('frmSaveProduct').reset();
        } else {
            showAdminToast(jsonResponse.message || "EJB persistence operation rejected parameters.", true);
        }
    } catch (error) {
        showAdminToast("Connection drop writing text parameters.", true);
    }
}

window.executeMultipartUploadPipeline = async function() {
    const filesInput = document.getElementById('fileImageParts');

    if (!currentCapturedProductId) {
        showAdminToast("No active validation identity reference matched.", true);
        return;
    }
    if (filesInput.files.length === 0) {
        showAdminToast("Please pick at least one product thumbnail file entry.", true);
        return;
    }

    const imageMultipartForm = new FormData();
    for (let i = 0; i < filesInput.files.length; i++) {
        imageMultipartForm.append("images[]", filesInput.files[i]);
    }

    try {
        showAdminToast("Streaming image buffers to persistence micro-node...");
        const response = await fetch(`${PRODUCTS_API_BASE}/${currentCapturedProductId}/upload-images`, {
            method: 'PUT',
            body: imageMultipartForm
        });

        const jsonResponse = await response.json();

        if (response.status === 401) {
            window.location.href = ADMIN_LOGIN;
        }

        if (response.ok && jsonResponse.success) {
            showAdminToast("All image components bound onto catalog item successfully!");
            filesInput.value = "";
            document.getElementById('txtUploadProductId').value = "";
            document.getElementById('panelImageUpload').classList.add('opacity-40', 'pointer-events-none');
            currentCapturedProductId = null;
            syncMasterInventoryViewports();
        } else {
            showAdminToast(jsonResponse.message || "Media processor dropped upload stream data payload.", true);
        }
    } catch (error) {
        showAdminToast("Server error streaming multipart media binary arrays.", true);
    }
}

window.toggleInventorySort = function(targetColumnField) {
    if (currentSortColumn === targetColumnField) {
        currentSortDirection = currentSortDirection === "asc" ? "desc" : "asc";
    } else {
        currentSortColumn = targetColumnField;
        currentSortDirection = "asc";
    }

    ['id', 'price', 'qty', 'createdAt', 'updatedAt'].forEach(col => {
        const handleNode = document.getElementById(`sortIcon-${col}`);
        if (!handleNode) return;

        if (col === currentSortColumn) {
            handleNode.innerHTML = currentSortDirection === "asc"
                ? `<i data-lucide="arrow-up-narrow-wide" class="w-3.5 h-3.5 text-indigo-600"></i>`
                : `<i data-lucide="arrow-down-wide-narrow" class="w-3.5 h-3.5 text-indigo-600"></i>`;
        } else {
            handleNode.innerHTML = `<i data-lucide="chevrons-up-down" class="w-3.5 h-3.5 text-slate-300"></i>`;
        }
    });

    syncMasterInventoryViewports();
}

window.syncMasterInventoryViewports = async function() {
    try {
        const queryUrl = `${INVENTORY_API_BASE}/get-all?sort=${currentSortColumn}&order=${currentSortDirection}`;
        const response = await fetch(queryUrl);
        const jsonResponse = await response.json();

        if (response.status === 401) {
            window.location.href = ADMIN_LOGIN;
        }

        if (!response.ok || !jsonResponse.success || !jsonResponse.data) {
            document.getElementById('tblInventoryLogLines').innerHTML = `<tr><td colspan="5" class="text-center py-4 text-red-500">Failed to load real-time ledger records.</td></tr>`;
            document.getElementById('tblMasterStockLines').innerHTML = `<tr><td colspan="8" class="text-center py-4 text-red-500">Failed to pull database warehouse stock entities.</td></tr>`;
            return;
        }

        const stockDataList = jsonResponse.data;
        let unifiedTransactionLogsHTML = "";
        let masterStockRowsHTML = "";

        stockDataList.forEach(stockItem => {
            const prod = stockItem.product;
            const wh = stockItem.warehouse;

            let recordCreationString = "N/A";
            if (stockItem.inventoryTransactionDTOList && stockItem.inventoryTransactionDTOList.length > 0) {
                const creationDates = stockItem.inventoryTransactionDTOList
                    .map(t => new Date(t.createdAt).getTime())
                    .filter(t => !isNaN(t));
                if (creationDates.length > 0) {
                    recordCreationString = new Date(Math.min(...creationDates)).toLocaleDateString(undefined, {
                        month: 'short', day: 'numeric', year: 'numeric'
                    });
                }
            }

            let recordUpdatedString = "N/A";
            if (stockItem.inventoryTransactionDTOList && stockItem.inventoryTransactionDTOList.length > 0) {
                const updateDates = stockItem.inventoryTransactionDTOList
                    .map(t => new Date(t.updatedAt || t.createdAt).getTime())
                    .filter(t => !isNaN(t));
                if (updateDates.length > 0) {
                    recordUpdatedString = new Date(Math.max(...updateDates)).toLocaleDateString(undefined, {
                        month: 'short', day: 'numeric', year: 'numeric'
                    });
                }
            }

            const stockStatusBadge = stockItem.status
                ? `<span class="px-2 py-0.5 text-xs font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 rounded-md flex items-center gap-1 w-fit"><span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>ACTIVE</span>`
                : `<span class="px-2 py-0.5 text-xs font-bold text-gray-600 bg-gray-50 border border-gray-200 rounded-md flex items-center gap-1 w-fit"><span class="w-1.5 h-1.5 rounded-full bg-gray-400"></span>INACTIVE</span>`;

            const countWarningLabelClass = stockItem.qty <= 5 ? "text-red-600 font-black bg-red-50/60 px-2 py-1 rounded-lg border border-red-100 flex items-center gap-1 w-fit" : "text-gray-900";

            let rawLastTimestamp = null;
            if (stockItem.inventoryTransactionDTOList && stockItem.inventoryTransactionDTOList.length > 0) {
                const timestamps = stockItem.inventoryTransactionDTOList.map(t => new Date(t.updatedAt || t.createdAt).getTime());
                rawLastTimestamp = Math.max(...timestamps);
            }

            const lastActivityString = rawLastTimestamp && isFinite(rawLastTimestamp)
                ? new Date(rawLastTimestamp).toLocaleDateString(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
                : 'No active logs';

            // Fully structured 8-column layout mapping row
            masterStockRowsHTML += `
                <tr class="hover:bg-gray-50/50 transition-colors">
                    <td class="py-3.5 px-6 font-mono text-xs font-bold text-indigo-600">STK-${stockItem.stockId}</td>
                    <td class="py-3.5 px-6">
                        <div class="font-bold text-gray-900">${prod.title || 'Untitled'}</div>
                        <div class="text-xs text-gray-400 font-medium">${prod.brandName} • ${prod.categoryName}</div>
                    </td>
                    <td class="py-3.5 px-6">
                        <div class="font-semibold text-gray-800">${wh.name}</div>
                        <div class="text-xs text-gray-400">${wh.location}</div>
                    </td>
                    <td class="py-3.5 px-6 font-bold text-gray-900">$${stockItem.price.toFixed(2)}</td>
                    <td class="py-3.5 px-6">
                        <div class="${countWarningLabelClass}">
                            ${stockItem.qty <= 5 ? '<i data-lucide="alert-triangle" class="w-3.5 h-3.5 text-red-500"></i>' : ''}
                            ${stockItem.qty} Units
                        </div>
                    </td>
                    <td class="py-3.5 px-6 text-xs text-gray-600 font-semibold font-mono">${recordCreationString}</td>
                    <td class="py-3.5 px-6 text-xs text-gray-600 font-semibold font-mono">${recordUpdatedString}</td>
                    <td class="py-3.5 px-6">
                        <div class="flex flex-col gap-1 items-start">
                            ${stockStatusBadge}
                            <span class="text-[10px] font-semibold text-slate-400 tracking-tight flex items-center gap-0.5" title="Latest Database Audit Sync Timestamp">
                                <i data-lucide="clock" class="w-2.5 h-2.5 text-indigo-400"></i> Upd: ${lastActivityString}
                            </span>
                        </div>
                    </td>
                </tr>
            `;

            if (stockItem.inventoryTransactionDTOList && stockItem.inventoryTransactionDTOList.length > 0) {
                stockItem.inventoryTransactionDTOList.forEach(tx => {
                    let badgeTheme = "bg-gray-50 text-gray-700 border-gray-200";
                    let prefixSign = "";

                    if (tx.type === "RECEIVE") { badgeTheme = "bg-emerald-50 text-emerald-700 border-emerald-200"; prefixSign = "+"; }
                    else if (tx.type === "SALE") { badgeTheme = "bg-blue-50 text-blue-700 border-blue-200"; prefixSign = "-"; }
                    else if (tx.type === "DAMAGED") { badgeTheme = "bg-red-50 text-red-700 border-red-200"; prefixSign = "-"; }
                    else if (tx.type === "ADJUSTMENT") { badgeTheme = "bg-amber-50 text-amber-700 border-amber-200"; prefixSign = "="; }

                    const offsetQtyColorClass = prefixSign === "+" ? "text-emerald-600" : prefixSign === "-" ? "text-red-600" : "text-amber-600";

                    const formattedTxTime = tx.createdAt ? new Date(tx.createdAt).toLocaleDateString(undefined, {
                        month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
                    }) : 'N/A';

                    unifiedTransactionLogsHTML += `
                        <tr class="hover:bg-gray-50/70 transition-colors">
                            <td class="py-2.5 px-6">
                                <div class="font-bold text-gray-900">#${tx.id}</div>
                                <div class="text-[10px] text-gray-400 font-mono tracking-tight flex items-center gap-1">
                                    <i data-lucide="calendar" class="w-2.5 h-2.5"></i> ${formattedTxTime}
                                </div>
                            </td>
                            <td class="py-2.5 px-6 font-mono text-xs font-bold text-slate-500">STK-${stockItem.stockId}</td>
                            <td class="py-2.5 px-6"><span class="px-2 py-0.5 border text-xs font-bold rounded-md uppercase ${badgeTheme}">${tx.type}</span></td>
                            <td class="py-2.5 px-6 font-black text-sm ${offsetQtyColorClass}">${prefixSign}${tx.qty}</td>
                            <td class="py-2.5 px-6 text-xs text-gray-400 max-w-xs truncate" title="${tx.reference || ''}">${tx.reference || 'N/A'}</td>
                        </tr>
                    `;
                });
            }
        });

        document.getElementById('tblMasterStockLines').innerHTML = masterStockRowsHTML || `<tr><td colspan="8" class="text-center py-6 text-gray-400">No active stock records tracked inside data grids.</td></tr>`;
        document.getElementById('tblInventoryLogLines').innerHTML = unifiedTransactionLogsHTML || `<tr><td colspan="5" class="text-center py-6 text-gray-400">No transaction records logged.</td></tr>`;
        lucide.createIcons();

    } catch (error) {
        console.error("Data pipeline load mapping error context:", error);
    }
}

window.dispatchStockEvent = async function(event) {
    event.preventDefault();

    const transactionEventPayload = {
        stockId: parseInt(document.getElementById('numStockId').value),
        qty: parseInt(document.getElementById('numQty').value),
        type: document.getElementById('selType').value,
        reason: document.getElementById('txtReason').value.trim()
    };

    try {
        showAdminToast("Queuing inventory payload to JAX-RS JMS pipeline...");
        const response = await fetch(`${INVENTORY_API_BASE}/receive`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(transactionEventPayload)
        });

        const jsonResponse = await response.json();

        if (response.status === 401) {
            window.location.href = ADMIN_LOGIN;
        }

        if (response.ok && jsonResponse.success) {
            showAdminToast("Event successfully queued for processing context.");
            document.getElementById('frmInventoryTransaction').reset();

            setTimeout(() => { syncMasterInventoryViewports(); }, 300);
            setTimeout(() => { syncMasterInventoryViewports(); }, 1200);
        } else {
            showAdminToast(jsonResponse.message || "Queue message target rejected transaction parameters.", true);
        }
    } catch (error) {
        showAdminToast("Connection error writing payload arrays.", true);
    }
}

window.lookupStockById = async function() {
    const idInput = document.getElementById('numLookupId').value.trim();
    if (!idInput) {
        showAdminToast("Please enter a valid stock identification number.", true);
        return;
    }

    try {
        showAdminToast(`Querying target profile node STK-${idInput}...`);
        const response = await fetch(`${INVENTORY_API_BASE}/get${idInput}`);
        const jsonResponse = await response.json();

        if (response.status === 401) {
            window.location.href = ADMIN_LOGIN;
        }

        if (!response.ok || !jsonResponse.success || !jsonResponse.data) {
            showAdminToast(jsonResponse.message || `Stock location profile STK-${idInput} missing from servers.`, true);
            return;
        }

        const stockItem = jsonResponse.data;
        const prod = stockItem.product;
        const wh = stockItem.warehouse;

        let recordCreationString = "N/A";
        if (stockItem.inventoryTransactionDTOList && stockItem.inventoryTransactionDTOList.length > 0) {
            const creationDates = stockItem.inventoryTransactionDTOList
                .map(t => new Date(t.createdAt).getTime())
                .filter(t => !isNaN(t));
            if (creationDates.length > 0) {
                recordCreationString = new Date(Math.min(...creationDates)).toLocaleDateString(undefined, {
                    month: 'short', day: 'numeric', year: 'numeric'
                });
            }
        }

        let recordUpdatedString = "N/A";
        if (stockItem.inventoryTransactionDTOList && stockItem.inventoryTransactionDTOList.length > 0) {
            const updateDates = stockItem.inventoryTransactionDTOList
                .map(t => new Date(t.updatedAt || t.createdAt).getTime())
                .filter(t => !isNaN(t));
            if (updateDates.length > 0) {
                recordUpdatedString = new Date(Math.max(...updateDates)).toLocaleDateString(undefined, {
                    month: 'short', day: 'numeric', year: 'numeric'
                });
            }
        }

        const stockStatusBadge = stockItem.status
            ? `<span class="px-2 py-0.5 text-xs font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 rounded-md flex items-center gap-1 w-fit"><span class="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>ACTIVE</span>`
            : `<span class="px-2 py-0.5 text-xs font-bold text-gray-600 bg-gray-50 border border-gray-200 rounded-md flex items-center gap-1 w-fit"><span class="w-1.5 h-1.5 rounded-full bg-gray-400"></span>INACTIVE</span>`;

        const countWarningLabelClass = stockItem.qty <= 5 ? "text-red-600 font-black bg-red-50/60 px-2 py-1 rounded-lg border border-red-100 flex items-center gap-1 w-fit" : "text-gray-900";

        let rawLastTimestamp = null;
        if (stockItem.inventoryTransactionDTOList && stockItem.inventoryTransactionDTOList.length > 0) {
            const timestamps = stockItem.inventoryTransactionDTOList.map(t => new Date(t.updatedAt || t.createdAt).getTime());
            rawLastTimestamp = Math.max(...timestamps);
        }
        const lastActivityString = rawLastTimestamp && isFinite(rawLastTimestamp)
            ? new Date(rawLastTimestamp).toLocaleDateString(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
            : 'No active logs';

        document.getElementById('tblMasterStockLines').innerHTML = `
            <tr class="bg-indigo-50/40 hover:bg-indigo-50/60 transition-colors duration-150 animate-fade-in">
                <td class="py-3.5 px-6 font-mono text-xs font-bold text-indigo-600">STK-${stockItem.stockId}</td>
                <td class="py-3.5 px-6">
                    <div class="font-bold text-gray-900">${prod.title || 'Untitled'}</div>
                    <div class="text-xs text-gray-400 font-medium">${prod.brandName} • ${prod.categoryName}</div>
                </td>
                <td class="py-3.5 px-6">
                    <div class="font-semibold text-gray-800">${wh.name}</div>
                    <div class="text-xs text-gray-400">${wh.location}</div>
                </td>
                <td class="py-3.5 px-6 font-bold text-gray-900">$${stockItem.price.toFixed(2)}</td>
                <td class="py-3.5 px-6">
                    <div class="${countWarningLabelClass}">
                        ${stockItem.qty <= 5 ? '<i data-lucide="alert-triangle" class="w-3.5 h-3.5 text-red-500"></i>' : ''}
                        ${stockItem.qty} Units
                    </div>
                </td>
                <td class="py-3.5 px-6 text-xs text-gray-600 font-semibold font-mono">${recordCreationString}</td>
                <td class="py-3.5 px-6 text-xs text-gray-600 font-semibold font-mono">${recordUpdatedString}</td>
                <td class="py-3.5 px-6">
                    <div class="flex flex-col gap-1 items-start">
                        ${stockStatusBadge}
                        <span class="text-[10px] font-semibold text-slate-400 tracking-tight flex items-center gap-0.5">
                            <i data-lucide="clock" class="w-2.5 h-2.5 text-indigo-400"></i> Upd: ${lastActivityString}
                        </span>
                    </div>
                </td>
            </tr>
        `;

        document.getElementById('lookupResetBadge').classList.remove('hidden');
        showAdminToast("Target stock profile snapshot parsed cleanly.");
        lucide.createIcons();

    } catch (error) {
        console.error("Single item lookup search processing error exception:", error);
        showAdminToast("Server error reading single stock item metadata maps.", true);
    }
}

window.clearStockLookup = function() {
    document.getElementById('numLookupId').value = "";
    document.getElementById('lookupResetBadge').classList.add('hidden');
    syncMasterInventoryViewports();
}

function connectLiveInventorySocket() {
    const socketUri = `${window.location.protocol === 'https:' ? 'wss:' : 'ws:'}//${window.location.host}/${window.location.pathname.split('/')[1]}/inventory`;

    console.log("Establishing backend WebSocket link at:", socketUri);
    globalInventorySocket = new WebSocket(socketUri);

    globalInventorySocket.onopen = () => {
        console.log("JAX-RS WebSocket tunnel verified active.");
        const statusNode = document.getElementById('socketStatusBadge');
        if (statusNode) {
            statusNode.className = "px-2 py-0.5 text-[10px] font-black rounded-sm bg-emerald-500/20 text-emerald-400 border border-emerald-500/30";
            statusNode.innerText = "LIVE STREAM ACTIVE";
        }
    };

    globalInventorySocket.onmessage = (messageEvent) => {
        try {
            const eventData = JSON.parse(messageEvent.data);
            renderIncomingTransactionCard(eventData);
        } catch (error) {
            console.error("Payload structural serialization failure:", error);
        }
    };

    globalInventorySocket.onclose = () => {
        console.warn("WebSocket stream broken. Reconnection retry queued...");
        const statusNode = document.getElementById('socketStatusBadge');
        if (statusNode) {
            statusNode.className = "px-2 py-0.5 text-[10px] font-black rounded-sm bg-red-500/20 text-red-400 border border-red-500/30";
            statusNode.innerText = "STREAM CLOSED - RECONNECTING";
        }
        setTimeout(() => { connectLiveInventorySocket(); }, 5000);
    };
}

function renderIncomingTransactionCard(event) {
    const streamContainer = document.getElementById('divLiveStreamBox');
    const placeholder = document.getElementById('divLiveStreamPlaceholder');

    if (placeholder) placeholder.remove();

    transactionStreamCount++;
    document.getElementById('txtLiveVolumeMetric').innerText = transactionStreamCount;

    const netTotalValue = (event.soldQty || 0) * (event.unitPrice || 0);
    const timestampString = new Date().toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit', second: '2-digit' });

    const payloadCardHTML = `
        <div class="bg-white p-4 rounded-xl border border-gray-200 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-4 transition-all duration-300 hover:shadow-md border-l-4 border-l-rose-500 animate-slide-in">
            <div class="flex items-start gap-3">
                <div class="p-2.5 bg-rose-50 text-rose-600 rounded-xl mt-0.5">
                    <i data-lucide="shopping-cart" class="w-4 h-4"></i>
                </div>
                <div>
                    <div class="text-xs text-gray-400 font-semibold tracking-tight uppercase flex items-center gap-1.5">
                        <span class="text-slate-800 font-bold">User: ${event.userName || 'Guest User'} (ID: #${event.userId || '0'})</span>
                        • <span class="font-mono">${timestampString}</span>
                    </div>
                    <h4 class="text-sm font-bold text-gray-900 mt-0.5">${event.productTitle || 'Unknown Product Catalog Title'}</h4>
                    <p class="text-xs text-gray-400 font-medium mt-0.5">${event.productBrand || 'Generic'} • ${event.productCategory || 'Unassigned'} • Stock Key: STK-${event.stockId}</p>
                </div>
            </div>
            <div class="flex items-center md:items-end justify-between md:flex-col shrink-0 border-t md:border-t-0 pt-2 md:pt-0 border-gray-100">
                <div class="text-right">
                    <div class="text-xs text-gray-400 font-bold">Volume Handled</div>
                    <div class="text-sm font-black text-rose-600 font-mono">-${event.soldQty || 0} Units</div>
                </div>
                <div class="text-right md:mt-1">
                    <div class="text-xs text-gray-400 font-bold md:hidden">Total Cost</div>
                    <div class="text-xs font-black text-slate-800 bg-slate-100 px-2 py-0.5 rounded-md border border-slate-200 font-mono">$${netTotalValue.toFixed(2)}</div>
                </div>
            </div>
        </div>
    `;

    streamContainer.insertAdjacentHTML('afterbegin', payloadCardHTML);

    if (typeof syncMasterInventoryViewports === "function") {
        syncMasterInventoryViewports();
    }

    lucide.createIcons();
}

function initializeTelemetryCharts() {
    const ctx = document.getElementById('telemetryChartNode');
    if (!ctx) return;

    liveTelemetryChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [
                {
                    label: 'Gross Revenue Sales (Rs)',
                    data: [],
                    borderColor: '#4f46e5',
                    backgroundColor: 'rgba(79, 70, 229, 0.05)',
                    borderWidth: 2.5,
                    tension: 0.3,
                    fill: true,
                    yAxisID: 'y'
                },
                {
                    label: 'orders count',
                    data: [],
                    borderColor: '#10b981',
                    backgroundColor: 'transparent',
                    borderWidth: 2,
                    borderDash: [4, 4],
                    tension: 0.1,
                    yAxisID: 'y1'
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: { type: 'linear', display: true, position: 'left', grid: { color: '#f1f5f9' } },
                y1: { type: 'linear', display: true, position: 'right', grid: { drawOnChartArea: false } }
            }
        }
    });
}

async function loadDashboardMetrics() {
    const refreshIcon = document.getElementById('iconGlobalRefresh');
    if (refreshIcon) refreshIcon.classList.add('animate-spin');

    const startVal = document.getElementById('dashStartDate')?.value;
    const endVal = document.getElementById('dashEndDate')?.value;

    if (!startVal || !endVal) {
        showAdminToast("Select valid timeline parameters before matching operations.", "error");
        return;
    }

    try {
        const targetUrl = `api/admin/dashboard?startDate=${startVal}&endDate=${endVal}`;
        const response = await fetch(targetUrl, {
            method: "GET",
            headers: { "Accept": "application/json" }
        });

        if (!response.ok) throw new Error(`HTTP Status Code: ${response.status}`);
        const restResult = await response.json();

        if (restResult.success && restResult.data) {
            const metrics = restResult.data;

            document.getElementById('metricUsers').innerText = (metrics.activeUsers ?? 0).toLocaleString();
            document.getElementById('metricSales').innerText = (metrics.totalSales ?? 0).toLocaleString();
            document.getElementById('metricProducts').innerText = (metrics.totalProductUnits ?? 0).toLocaleString();

            if (metrics.salesChart && liveTelemetryChartInstance) {
                const chartLabels = metrics.salesChart.map(point => point.label || point.date);
                const salesValues = metrics.salesChart.map(point => point.salesSum);
                const orderValues = metrics.salesChart.map(point => point.orderCount);

                liveTelemetryChartInstance.data.labels = chartLabels;
                liveTelemetryChartInstance.data.datasets[0].data = salesValues;
                liveTelemetryChartInstance.data.datasets[1].data = orderValues;

                liveTelemetryChartInstance.update();
            }
        } else {
            showAdminToast(restResult.message || "Failed syncing system data arrays.", "error");
        }
    } catch (err) {
        console.error("Dashboard thread synchronization loop broken down:", err);
        showAdminToast("Connection failure tracing metrics from JAX-RS containers.", "error");
    } finally {
        if (refreshIcon) refreshIcon.classList.remove('animate-spin');
    }
}


window.loadAdminOrderSummary = async function() {
    try {
        const response = await fetch("api/admin/order-summery");

        if (response.status === 401) {
            window.location.href = ADMIN_LOGIN;
            return;
        }

        const jsonResponse = await response.json();

        if (!response.ok || !jsonResponse.success || !jsonResponse.data) {
            document.getElementById('tblAdminOrderLines').innerHTML = `<tr><td colspan="5" class="text-center py-4 text-red-500">Failed to sync systemic order logs context.</td></tr>`;
            return;
        }

        const ordersList = jsonResponse.data;
        let ordersHTML = "";

        let totalCount = ordersList.length;
        let completedCount = 0;
        let pendingCount = 0;
        let cumulativePerformanceTime = 0;
        let calculatedPerformanceInstancesCount = 0;

        const statusMapData = {
            "PENDING": 0,
            "COMPLETED": 0,
            "REJECTED": 0,
            "FAILED_OUT_OF_STOCK": 0,
            "INACTIVE": 0
        };

        ordersList.forEach(orderItem => {
            if (orderItem.status in statusMapData) {
                statusMapData[orderItem.status]++;
            }
            if (orderItem.status === "COMPLETED") completedCount++;
            if (orderItem.status === "PENDING") pendingCount++;

            if (orderItem.performance > 0) {
                cumulativePerformanceTime += orderItem.performance;
                calculatedPerformanceInstancesCount++;
            }

            let statusBadgeClass = "bg-gray-50 text-gray-700 border-gray-200";
            if (orderItem.status === "PENDING") statusBadgeClass = "bg-amber-50 text-amber-700 border-amber-200";
            else if (orderItem.status === "COMPLETED") statusBadgeClass = "bg-emerald-50 text-emerald-700 border-emerald-200";
            else if (orderItem.status === "REJECTED") statusBadgeClass = "bg-rose-50 text-rose-700 border-rose-200";
            else if (orderItem.status === "FAILED_OUT_OF_STOCK") statusBadgeClass = "bg-red-50 text-red-700 border-red-200";
            else if (orderItem.status === "INACTIVE") statusBadgeClass = "bg-slate-100 text-slate-600 border-slate-300";

            const formattedDate = orderItem.createdAt
                ? new Date(orderItem.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
                : 'N/A';

            const executionSeconds = orderItem.performance > 0 ? `${(orderItem.performance / 1000).toFixed(1)}s` : 'Instant/Pending';

            ordersHTML += `
                <tr class="hover:bg-gray-50/50 transition-colors">
                    <td class="py-3.5 px-6 font-mono text-xs font-bold text-indigo-600">ORD-${orderItem.orderId}</td>
                    <td class="py-3.5 px-6 font-semibold text-slate-700">USR-#${orderItem.userId}</td>
                    <td class="py-3.5 px-6 text-xs text-gray-500 font-mono">${formattedDate}</td>
                    <td class="py-3.5 px-6">
                        <div class="flex items-center gap-1 text-xs text-slate-600 font-medium font-mono">
                            <i data-lucide="zap" class="w-3.5 h-3.5 text-amber-500"></i> ${executionSeconds}
                        </div>
                    </td>
                    <td class="py-3.5 px-6">
                        <span class="px-2 py-0.5 border text-[11px] font-bold rounded-md tracking-wide uppercase ${statusBadgeClass}">
                            ${orderItem.status || 'UNASSIGNED'}
                        </span>
                    </td>
                </tr>
            `;
        });

        document.getElementById('statTotalOrders').innerText = totalCount.toLocaleString();
        document.getElementById('statCompletedOrders').innerText = completedCount.toLocaleString();
        document.getElementById('statPendingOrders').innerText = pendingCount.toLocaleString();

        const runtimeAvgSeconds = calculatedPerformanceInstancesCount > 0
            ? (cumulativePerformanceTime / calculatedPerformanceInstancesCount / 1000).toFixed(2)
            : "0.00";
        document.getElementById('statAvgPerformance').innerText = `${runtimeAvgSeconds}s`;

        document.getElementById('tblAdminOrderLines').innerHTML = ordersHTML || `<tr><td colspan="5" class="text-center py-6 text-gray-400">No recent client checkout pipeline runs found.</td></tr>`;

        renderOrderStatusChart(statusMapData);
        lucide.createIcons();

    } catch (error) {
        console.error("Order metrics synchronization layout exception:", error);
        showAdminToast("Connection drop reading order summary arrays.", true);
    }
}

function renderOrderStatusChart(dataSpread) {
    const chartContextNode = document.getElementById('orderStatusMetricChart');
    if (!chartContextNode) return;

    const labels = Object.keys(dataSpread);
    const dataValues = Object.values(dataSpread);

    if (orderStatusChartInstance) {
        orderStatusChartInstance.data.datasets[0].data = dataValues;
        orderStatusChartInstance.update();
    } else {
        orderStatusChartInstance = new Chart(chartContextNode, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Active Order Units',
                    data: dataValues,
                    backgroundColor: [
                        'rgba(245, 158, 11, 0.7)',
                        'rgba(16, 185, 129, 0.7)',
                        'rgba(244, 63, 94, 0.7)',
                        'rgba(239, 68, 68, 0.7)',
                        'rgba(100, 116, 139, 0.7)'
                    ],
                    borderColor: [
                        '#d97706', '#059669', '#e11d48', '#dc2626', '#475569'
                    ],
                    borderWidth: 1.5,
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { precision: 0 },
                        grid: { color: '#f1f5f9' }
                    },
                    x: {
                        grid: { display: false }
                    }
                }
            }
        });
    }
}

window.executeAdminLogout = async function() {
    if (!confirm("Are you sure you want to terminate your administrative session context?")) {
        return;
    }

    try {
        const response = await fetch("api/admin/logout", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            }
        });

        const jsonResponse = await response.json();

        if (response.ok && jsonResponse.success) {
            showAdminToast("Session expired cleanly. Diverting interface...");

            localStorage.removeItem("adminToken");
            sessionStorage.clear();

            setTimeout(() => {
                window.location.href = ADMIN_LOGIN;
            }, 1000);

        } else {
            showAdminToast(jsonResponse.message || "Failed to drop active session metadata cleanly.", true);
        }

    } catch (error) {
        console.error("Session logout transmission fault drop matrix exception:", error);
        showAdminToast("Server error during logout routine orchestration pipelines.", true);
    }
}

function showAdminToast(message, type = "info") {
    const toast = document.getElementById('adminToast');
    if (!toast) return;

    toast.innerText = message;
    toast.className = `fixed bottom-5 right-5 z-50 px-4 py-3 rounded-xl shadow-xl flex items-center gap-2 border text-xs font-bold transition-all duration-300 ${
        type === 'error' ? 'bg-rose-50 text-rose-700 border-rose-200' : 'bg-emerald-50 text-emerald-700 border-emerald-200'
    }`;
    toast.classList.remove('hidden');
    setTimeout(() => toast.classList.add('hidden'), 3500);
}

