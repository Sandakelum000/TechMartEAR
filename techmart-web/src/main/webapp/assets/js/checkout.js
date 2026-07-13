const CHECKOUT_API_BASE = "api/checkouts";
let savedProfileAddress = null;
let isCurrentAddressSelected = true;


document.addEventListener("DOMContentLoaded", () => {
    loadCheckoutDetails();
});

async function loadCheckoutDetails() {
    try {
        const response = await fetch(`${CHECKOUT_API_BASE}/user-checkout-data`);
        if (response.redirected) {
            redirectToLogin();
            return;
        }
        const jsonResponse = await response.json();

        if (!jsonResponse.success && (jsonResponse.message === "Please login first!" || response.status === 401)) {
            redirectToLogin(jsonResponse.message);
            return;
        }

        if (response.ok && jsonResponse.success && jsonResponse.data) {
            const dataContext = jsonResponse.data;

            if (dataContext.addressDTO) {
                savedProfileAddress = dataContext.addressDTO;
                populateAddressFormFields(savedProfileAddress);
            }

            renderProductSummary(dataContext.cartItemDTOList || []);
        } else {
            showToastNotice(jsonResponse.message || "Failed to load checkout details.", true);
        }
    } catch (error) {
        console.error("Checkout Read Error:", error);
        showToastNotice("Server connection fault loading checkout arrays.", true);
    }
}
function redirectToLogin(message = "Session expired. Redirecting to login...") {
    showToastNotice(message, true);
    setTimeout(() => {
        window.location.href = "sign-in.html";
    }, 2000);
}

function populateAddressFormFields(addr) {
    document.getElementById('txtFirstName').value = addr.firstName || '';
    document.getElementById('txtLastName').value = addr.lastName || '';
    document.getElementById('txtLineOne').value = addr.lineOne || '';
    document.getElementById('txtLineTwo').value = addr.lineTwo || '';
    document.getElementById('txtPostalCode').value = addr.postalCode || '';
    document.getElementById('txtMobile').value = addr.mobile || '';
}

window.toggleAddressView = function(useSaved) {
    isCurrentAddressSelected = useSaved;
    const form = document.getElementById('checkoutAddressForm');
    const labelSaved = document.getElementById('labelSavedAddress');
    const labelNew = document.getElementById('labelNewAddress');

    if (useSaved) {
        form.classList.add('opacity-60', 'pointer-events-none');
        labelSaved.className = "border-2 border-indigo-600 p-4 rounded-xl flex items-start gap-3 cursor-pointer bg-indigo-50/40";
        labelNew.className = "border border-gray-200 p-4 rounded-xl flex items-start gap-3 cursor-pointer hover:bg-gray-50 transition-colors";
        if (savedProfileAddress) populateAddressFormFields(savedProfileAddress);
    } else {
        form.classList.remove('opacity-60', 'pointer-events-none');
        labelSaved.className = "border border-gray-200 p-4 rounded-xl flex items-start gap-3 cursor-pointer hover:bg-gray-50 transition-colors";
        labelNew.className = "border-2 border-indigo-600 p-4 rounded-xl flex items-start gap-3 cursor-pointer bg-indigo-50/40";
        form.reset();
    }
}

function renderProductSummary(items) {
    const container = document.getElementById('checkoutSummaryItems');
    document.getElementById('txtSummaryCount').innerText = items.length;
    container.innerHTML = '';

    if (items.length === 0) {
        container.innerHTML = `<p class="text-sm text-gray-400 py-4 text-center">Your basket is empty.</p>`;
        return;
    }

    let computationTotal = 0;

    container.innerHTML = items.map(item => {
        const productRowCost = item.price * item.qty;
        computationTotal += productRowCost;

        const imgFallback = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=80&q=80';
        const displayImg = item.images && item.images.length > 0 ? item.images[0] : imgFallback;

        return `
            <div class="flex items-center gap-3 py-3 text-sm">
                <div class="w-12 h-12 bg-gray-50 border border-gray-100 rounded-lg overflow-hidden shrink-0">
                    <img src="${displayImg}" alt="${item.productTitle}" class="w-full h-full object-cover">
                </div>
                <div class="flex-1 min-w-0">
                    <h4 class="font-semibold text-gray-900 line-clamp-1">${item.productTitle}</h4>
                    <p class="text-xs text-gray-400 mt-0.5">Qty: ${item.qty} × Rs ${item.price}</p>
                </div>
                <span class="font-bold text-gray-900 shrink-0">Rs ${productRowCost}</span>
            </div>
        `;
    }).join('');

    document.getElementById('checkoutSubtotal').innerText = `Rs ${computationTotal.toFixed(2)}`;
    document.getElementById('checkoutTotalAmount').innerText = `Rs ${computationTotal.toFixed(2)}`;
    lucide.createIcons();
}

window.executePaymentVerificationPipeline = async function() {
    const checkoutRequestPayload = {
        currentAddress: isCurrentAddressSelected,
        firstName: document.getElementById('txtFirstName').value.trim(),
        lastName: document.getElementById('txtLastName').value.trim(),
        lineOne: document.getElementById('txtLineOne').value.trim(),
        lineTwo: document.getElementById('txtLineTwo').value.trim(),
        postalCode: document.getElementById('txtPostalCode').value.trim(),
        mobile: document.getElementById('txtMobile').value.trim()
    };

    try {
        const response = await fetch(`${CHECKOUT_API_BASE}/user-checkout`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(checkoutRequestPayload)
        });

        const jsonResponse = await response.json();

        if (response.ok && jsonResponse.success && jsonResponse.data) {
            showToastNotice("Routing to PayHere secure gateway...");
            payhere.startPayment(jsonResponse.data);
        } else {
            showToastNotice(jsonResponse.message || "Checkout rejected by server.", true);
        }
    } catch (error) {
        console.error("Checkout Exception Context:", error);
        showToastNotice("System error processing payment pipeline.", true);
    }
}

payhere.onCompleted = function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);
    verifyOrder(orderId);
};

payhere.onDismissed = function onDismissed() {
    showToastNotice("Payment processing panel was closed.", true);
};

payhere.onError = function onError(error) {
    showToastNotice("Gateway error occurred: " + error, true);
};

async function verifyOrder(orderId) {
    let attempts = 0;
    const maxAttempts = 20;
    const pollingIntervalMs = 1500;

    showToastNotice("Payment authorized! Synchronizing order via message queues...", false);

    const interval = setInterval(async () => {
        attempts++;

        try {
            const parsedOrderId = orderId.replace(/^0+/, '');

            const response = await fetch(`api/orders/verify-order?orderId=${parsedOrderId}`);

            if (!response.ok) {
                throw new Error(`HTTP Error Status: ${response.status}`);
            }

            const data = await response.json();
            console.log(`Polling attempt ${attempts}/${maxAttempts}:`, data);

            if (data.success) {
                clearInterval(interval);
                showToastNotice("Order successfully verified! Redirecting...", false);

                setTimeout(() => {
                    window.location.href = `payment-history.html`;
                }, 1500);
                return;
            }

            showToastNotice(`Verifying processing queues... (Attempt ${attempts}/${maxAttempts})`, false);

            if (attempts >= maxAttempts) {
                clearInterval(interval);
                showToastNotice("Verification is taking longer than expected. Please check your profile order history shortly.", true);
            }

        } catch (e) {
            clearInterval(interval);
            console.error("Polling Pipeline Error:", e);
            showToastNotice("Network synchronization error occurred during verification.", true);
        }
    }, pollingIntervalMs);
}

function showToastNotice(msg, isErr = false) {
    const element = document.getElementById('toastNotification');
    element.className = `fixed bottom-5 right-5 z-50 px-4 py-3 rounded-xl shadow-lg flex items-center gap-2 border transition-all duration-300 ${
        isErr ? 'bg-red-50 border-red-200 text-red-800' : 'bg-gray-900 border-transparent text-white'
    }`;

    const iconTag = isErr ? '<i data-lucide="alert-circle" class="w-5 h-5"></i>' : '<i data-lucide="check-circle" class="w-5 h-5"></i>';
    element.innerHTML = `${iconTag} <span class="text-sm font-semibold">${msg}</span>`;

    lucide.createIcons();
    element.classList.remove('hidden');

    setTimeout(() => element.classList.add('hidden'), 4000);
}