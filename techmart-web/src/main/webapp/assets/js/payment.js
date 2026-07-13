const PAYMENTS_API_ENDPOINT = "api/payments/get-all";

document.addEventListener("DOMContentLoaded", () => {
    lucide.createIcons();
    fetchUserPaymentLedgerStream();
});

window.fetchUserPaymentLedgerStream = async function() {
    const syncButtonNode = document.getElementById('btnSyncPayment');
    const refreshIconNode = document.getElementById('iconPaymentRefresh');
    const tableBodyNode = document.getElementById('tblPaymentHistoryRows');
    const statusLabel = document.getElementById('lblPaymentNetworkStatus');

    if (syncButtonNode && refreshIconNode) {
        syncButtonNode.classList.add("pointer-events-none", "opacity-60");
        refreshIconNode.classList.add("animate-spin", "text-indigo-400");
    }
    if (statusLabel) {
        statusLabel.className = "text-xs font-black rounded-sm px-2 py-0.5 bg-amber-500/10 text-amber-400 border border-amber-500/20 w-fit mt-1.5";
        statusLabel.innerText = "REST_STREAM_ACTIVE";
    }

    try {
        const response = await fetch(PAYMENTS_API_ENDPOINT, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });

        if (response.status === 401) {
            showPaymentToast("Session context dropped or missing. Returning to auth block...", true);
            setTimeout(() => { window.location.href = "sign-in.html"; }, 200);
            return;
        }

        const jsonResponse = await response.json();

        if (response.ok && jsonResponse.success && jsonResponse.data) {
            renderPaymentLedgerRows(jsonResponse.data);
        } else {
            tableBodyNode.innerHTML = `<tr><td colspan="6" class="text-center py-8 text-rose-400 font-bold">${jsonResponse.message || 'Server dropped reading context parameters.'}</td></tr>`;
        }
    } catch (error) {
        console.error("Payment pipeline network routing exception context caught:", error);
        tableBodyNode.innerHTML = `<tr><td colspan="6" class="text-center py-8 text-rose-500 font-bold">No records now.</td></tr>`;
        showPaymentToast("Please login again", true);
    } finally {
        setTimeout(() => {
            if (syncButtonNode && refreshIconNode) {
                syncButtonNode.classList.remove("pointer-events-none", "opacity-60");
                refreshIconNode.classList.remove("animate-spin", "text-indigo-400");
            }
            if (statusLabel) {
                statusLabel.className = "text-xs font-black rounded-sm px-2 py-0.5 bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 w-fit mt-1.5";
                statusLabel.innerText = "FETCH_IDLE";
            }
            lucide.createIcons();
        }, 400);
    }
};

function renderPaymentLedgerRows(historyDataList) {
    const tableBodyNode = document.getElementById('tblPaymentHistoryRows');

    if (!historyDataList || historyDataList.length === 0) {
        tableBodyNode.innerHTML = `<tr><td colspan="6" class="text-center py-10 text-slate-500">No confirmed payment transactions linked to this profile node.</td></tr>`;
        document.getElementById('lblTotalSpent').innerText = "Rs 0.00";
        document.getElementById('lblTotalUnits').innerText = "0 Items";
        return;
    }

    let dynamicRowsHTML = "";
    let calculatedGrossTotal = 0;
    let calculatedItemsTotal = 0;

    historyDataList.forEach(item => {
        const itemUnitPrice = item.unitPrice || 0;
        const itemQuantity = item.qty || 0;
        const rowNetTotal = itemUnitPrice * itemQuantity;

        calculatedGrossTotal += rowNetTotal;
        calculatedItemsTotal += itemQuantity;

        const dateFormatted = item.createdAt
            ? new Date(item.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' })
            : 'N/A';

        const stateBadgeClass = item.status === "COMPLETED"
            ? "bg-emerald-500/10 text-emerald-400 border-emerald-500/20"
            : "bg-slate-800 text-slate-400 border-slate-700";

        dynamicRowsHTML += `
            <tr class="hover:bg-slate-900/40 transition-colors duration-150">
                <td class="py-4 px-6">
                    <div class="font-bold text-slate-100">${item.productTitle || 'Untitled Catalog Item'}</div>
                </td>
                <td class="py-4 px-6 text-xs font-mono tracking-tight text-slate-400">${dateFormatted}</td>
                <td class="py-4 px-6 font-mono font-bold text-slate-300">Rs ${itemUnitPrice.toFixed(2)}</td>
                <td class="py-4 px-6 font-mono text-slate-400">${itemQuantity} Units</td>
                <td class="py-4 px-6 font-mono font-black text-indigo-400">Rs ${rowNetTotal.toFixed(2)}</td>
                <td class="py-4 px-6">
                    <span class="px-2 py-0.5 border text-[11px] font-black rounded-md tracking-wider uppercase ${stateBadgeClass}">
                        ${item.status || 'PENDING'}
                    </span>
                </td>
            </tr>
        `;
    });

    document.getElementById('lblTotalSpent').innerText = `Rs ${calculatedGrossTotal.toFixed(2)}`;
    document.getElementById('lblTotalUnits').innerText = `${calculatedItemsTotal} Items`;

    tableBodyNode.innerHTML = dynamicRowsHTML;
    lucide.createIcons();
}

function showPaymentToast(msg, isErr = false) {
    const element = document.getElementById('paymentToast');
    if (!element) return;

    element.className = `fixed top-5 right-5 z-50 px-4 py-3 rounded-xl shadow-xl flex items-center gap-2 border transition-all duration-300 ${
        isErr ? 'bg-red-950 border-red-800 text-red-400' : 'bg-slate-900 border-slate-800 text-white'
    }`;

    element.innerHTML = `${
        isErr ? '<i data-lucide="alert-circle" class="w-5 h-5 text-red-400"></i>' : '<i data-lucide="check-circle" class="w-5 h-5 text-indigo-400"></i>'
    } <span class="text-sm font-semibold">${msg}</span>`;

    lucide.createIcons();
    element.classList.remove('hidden');
    setTimeout(() => element.classList.add('hidden'), 4000);
}