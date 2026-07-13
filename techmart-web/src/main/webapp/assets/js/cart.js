const CART_API_BASE = "api/carts";
let activeCartItems = [];

document.addEventListener("DOMContentLoaded", () => {
    fetchUserCartData();
});

async function fetchUserCartData() {
    try {
        const response = await fetch(`${CART_API_BASE}/all-carts`);
        if (!response.ok) throw new Error("Could not pull downstream cart configurations.");

        const jsonResponse = await response.json();

        if (jsonResponse.success) {
            activeCartItems = jsonResponse.data || [];
            renderCartUI();
        } else {
            showToast(jsonResponse.message || "Failed to load session cart items.", true);
        }
    } catch (error) {
        console.error("Cart Loading Error:", error);
        showToast("Error connecting to inventory context.", true);
    }
}

async function modifyItemQuantity(stockId, numericDelta) {
    const payload = {
        stockId: String(stockId),
        qty: String(numericDelta)
    };

    try {
        const response = await fetch(`${CART_API_BASE}/add-to-cart`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const jsonResponse = await response.json();

        if (response.ok && jsonResponse.success) {
            showToast(jsonResponse.message || "Cart updated successfully.");
            fetchUserCartData();
        } else {
            showToast(jsonResponse.message || "Failed to alter inventory quantity allocation.", true);
        }
    } catch (error) {
        console.error("Mutation Error:", error);
        showToast("Server transmission fault while adjusting quantity.", true);
    }
}

function handleQuantityDecrement(cartId, stockId, currentQty) {
    if (currentQty <= 1) {
        removeCartItemFromServer(cartId);
    } else {
        modifyItemQuantity(stockId, -1);
    }
}

async function removeCartItemFromServer(cartId) {
    try {
        const response = await fetch(`${CART_API_BASE}/remove-cart/${cartId}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        });

        const jsonResponse = await response.json();

        if (response.ok && jsonResponse.success) {
            showToast("Item removed from cart layout.");
            fetchUserCartData();
        } else {
            showToast(jsonResponse.message || "Could not complete drop task.", true);
        }
    } catch (error) {
        console.error("Deletion Pipeline Error:", error);
        showToast("Error processing removal request pipelines.", true);
    }
}

function renderCartUI() {
    const container = document.getElementById('cartItemsContainer');
    const badge = document.getElementById('cartCountBadge');

    badge.innerText = `${activeCartItems.length} Item${activeCartItems.length === 1 ? '' : 's'}`;
    container.innerHTML = '';

    if (activeCartItems.length === 0) {
        container.innerHTML = `
            <div class="bg-white rounded-2xl border border-gray-200 p-12 flex flex-col items-center justify-center text-gray-400 text-center">
                <div class="p-4 bg-gray-50 rounded-full text-gray-300 mb-3">
                    <i data-lucide="shopping-cart" class="w-12 h-12 stroke-1"></i>
                </div>
                <p class="text-base font-semibold text-gray-600">Your basket is currently empty.</p>
                <p class="text-xs text-gray-400 max-w-xs mt-1">Add items from our inventory search collections to complete an active order profile.</p>
                <a href="index.html" class="mt-4 bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs py-2 px-4 rounded-xl shadow-xs transition-colors no-underline">Browse Catalog</a>
            </div>
        `;
        updateFinancialSummary(0);
        lucide.createIcons();
        return;
    }

    let dynamicSubtotal = 0;

    container.innerHTML = activeCartItems.map(item => {
        const rowItemTotal = item.price * item.qty;
        dynamicSubtotal += rowItemTotal;

        const fallbackImg = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=120&q=80';
        const displayImage = item.images && item.images.length > 0 ? item.images[0] : fallbackImg;

        return `
            <div class="cart-item-row group bg-white p-4 sm:p-5 rounded-2xl border border-gray-200 shadow-xs hover:border-gray-300 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                <div class="flex items-center gap-4 flex-1">
                    <div class="w-20 h-20 bg-gray-100 rounded-xl overflow-hidden shrink-0 border border-gray-100">
                        <img src="${displayImage}" alt="${item.productTitle}" class="w-full h-full object-cover">
                    </div>
                    <div class="space-y-1">
                        <h4 class="font-bold text-gray-900 group-hover:text-indigo-600 transition-colors line-clamp-2 text-sm sm:text-base">${item.productTitle}</h4>
                        <p class="text-xs font-semibold text-indigo-600 tracking-wide">Unit Price: Rs ${item.price}</p>
                    </div>
                </div>

                <div class="flex items-center justify-between sm:justify-end gap-6 w-full sm:w-auto border-t sm:border-transparent pt-3 sm:pt-0">
                    <div class="flex items-center border border-gray-200 rounded-xl p-1 bg-gray-50/50">
                        <button onclick="handleQuantityDecrement(${item.cartId}, ${item.stockId}, ${item.qty})" class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-500 hover:bg-white active:bg-gray-100 hover:text-indigo-600 transition-colors cursor-pointer border-none">
                            <i data-lucide="minus" class="w-4 h-4"></i>
                        </button>
                        <span class="w-10 text-center font-bold text-sm text-gray-800">${item.qty}</span>
                        <button onclick="modifyItemQuantity(${item.stockId}, 1)" class="w-8 h-8 rounded-lg flex items-center justify-center text-gray-500 hover:bg-white active:bg-gray-100 hover:text-indigo-600 transition-colors cursor-pointer border-none">
                            <i data-lucide="plus" class="w-4 h-4"></i>
                        </button>
                    </div>

                    <div class="text-right min-w-[70px]">
                        <span class="font-black text-gray-900 text-base block">Rs ${rowItemTotal}</span>
                        <button onclick="removeCartItemFromServer(${item.cartId})" class="text-xs text-red-500 hover:text-red-600 font-semibold hover:underline bg-transparent border-none cursor-pointer mt-0.5">
                            Remove
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');

    updateFinancialSummary(dynamicSubtotal);
    lucide.createIcons();
}

function updateFinancialSummary(subtotalValue) {
    document.getElementById('summarySubtotal').innerText = `Rs ${subtotalValue.toFixed(2)}`;
    document.getElementById('summaryTotal').innerText = `Rs ${subtotalValue.toFixed(2)}`;
}

function showToast(message, isError = false) {
    const toast = document.getElementById('toastNotification');
    toast.className = `fixed bottom-5 right-5 z-50 px-4 py-3 rounded-xl shadow-lg flex items-center gap-2 transition-all duration-300 transform border ${
        isError ? 'bg-red-50 border-red-200 text-red-800' : 'bg-gray-900 border-transparent text-white'
    }`;

    const icon = isError ? '<i data-lucide="alert-circle" class="w-5 h-5"></i>' : '<i data-lucide="check-circle" class="w-5 h-5"></i>';
    toast.innerHTML = `${icon} <span class="text-sm font-semibold">${message}</span>`;

    lucide.createIcons({ attrs: { class: ['w-5', 'h-5'] } });

    toast.classList.add('show');
    toast.style.transform = 'translateY(0)';
    toast.style.opacity = '1';

    setTimeout(() => {
        toast.style.transform = 'translateY(10px)';
        toast.style.opacity = '0';
        setTimeout(() => toast.classList.remove('show'), 300);
    }, 3500);
}