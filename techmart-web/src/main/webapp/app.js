const PRODUCTS_REST_ENDPOINT = "api/home/products";

document.addEventListener("DOMContentLoaded", () => {
    fetchActiveStoreCatalog();
});

async function fetchActiveStoreCatalog() {
    const carouselContainer = document.getElementById('product-carousel');
    if (!carouselContainer) return;

    try {
        const response = await fetch(PRODUCTS_REST_ENDPOINT, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });

        const jsonResponse = await response.json();

        if (response.ok && jsonResponse.success && jsonResponse.data) {
            renderStoreCarousel(jsonResponse.data);
        } else {
            carouselContainer.innerHTML = `
                <div class="w-full text-center py-12 text-rose-400 font-bold border border-rose-950/40 rounded-xl bg-rose-950/10">
                    <i data-lucide="alert-octagon" class="w-6 h-6 mx-auto mb-1"></i>
                    <span>${jsonResponse.message || 'Server dropped reading current data parameters.'}</span>
                </div>`;
        }
    } catch (error) {
        console.error("Catalog stream processing error exception caught:", error);
        carouselContainer.innerHTML = `
            <div class="w-full text-center py-12 text-rose-500 font-bold border border-rose-950/40 rounded-xl bg-rose-950/10">
                <i data-lucide="wifi-off" class="w-6 h-6 mx-auto mb-1"></i>
                <span>Data transport grid unreachable. Check backend container deployment.</span>
            </div>`;
    } finally {
        if (window.lucide && typeof lucide.createIcons === "function") {
            lucide.createIcons();
        }
    }
}

function renderStoreCarousel(stockList) {
    const carouselContainer = document.getElementById('product-carousel');
    if (stockList.length === 0) {
        carouselContainer.innerHTML = `<p class="w-full text-center py-12 text-slate-500 font-medium">No active products tracked inside store grids currently.</p>`;
        return;
    }

    let trackHtml = "";

    stockList.forEach(item => {
        const displayThumbnail = (item.images && item.images.length > 0)
            ? item.images[0]
            : "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=400&q=80";

        trackHtml += `
            <div class="w-[280px] sm:w-[320px] shrink-0 snap-start bg-slate-900 rounded-2xl border border-slate-800/80 overflow-hidden group hover:border-slate-700 transition-all flex flex-col justify-between">
                
                <div class="relative bg-slate-950 aspect-square overflow-hidden border-b border-slate-800/60">
                    <img src="${displayThumbnail}" alt="${item.title || 'Techmart Product'}" class="w-full h-full object-cover group-hover:scale-105 transition-all duration-300">
                    <span class="absolute top-3 left-3 bg-indigo-600/90 text-white text-[10px] font-black tracking-wider uppercase px-2.5 py-1 rounded-md shadow-md backdrop-blur-xs">
                        ${item.categoryName || 'Unassigned'}
                    </span>
                    <span class="absolute top-3 right-3 bg-slate-950/80 text-slate-400 text-[10px] font-bold px-2 py-1 rounded border border-slate-800">
                        Qty: ${item.qty}
                    </span>
                </div>

                <div class="p-5 flex flex-col flex-grow justify-between gap-4">
                    <div>
                        <span class="text-[10px] text-indigo-400 font-bold uppercase tracking-wider">${item.brandName || 'Generic'}</span>
                        <h3 class="font-bold text-slate-200 mt-0.5 group-hover:text-indigo-400 transition truncate" title="${item.title}">${item.title}</h3>
                        <p class="text-xl font-black text-slate-100 font-mono mt-1">Rs ${(item.price || 0).toFixed(2)}</p>
                    </div>

                    <button onclick="addToCart(${item.stockId}, 1)" 
                            class="w-full bg-slate-950 hover:bg-indigo-600 border border-slate-800 hover:border-transparent text-slate-200 hover:text-white text-xs font-bold py-3 rounded-xl transition-all flex items-center justify-center gap-2 cursor-pointer shadow-lg shadow-slate-950/40">
                        <i data-lucide="plus-circle" class="w-4 h-4"></i>
                        <span>Add to Cart</span>
                    </button>
                </div>
            </div>
        `;
    });

    carouselContainer.innerHTML = trackHtml;
}

window.slideCarouselContainer = function(direction) {
    const container = document.getElementById('product-carousel');
    if (!container) return;

    const slideOffsetWidth = direction === 'left' ? -340 : 340;
    container.scrollBy({ left: slideOffsetWidth, behavior: 'smooth' });
};

window.executeBasketAdditionPipeline = function(stockId, productTitle) {
    console.log(`Intercepted cart registration request for target index stockId: ${stockId}`);

    if (typeof showPaymentToast === "function") {
        showPaymentToast(`Added "${productTitle}" safely onto active checkout pipeline context buffers.`);
    } else {
        alert(`Success! Bound "${productTitle}" onto active order framework (Stock Index ID: ${stockId}).`);
    }
};

function escapeHtml(text) {
    return text.replace(/'/g, "\\'").replace(/"/g, '&quot;');
}

async function addToCart(stockId, numericDelta) {
    const qtyRequest = {
        stockId: String(stockId),
        qty: String(numericDelta)
    };

    try {
        const response = await fetch(`api/carts/add-to-cart`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(qtyRequest)
        });

        const jsonResponse = await response.json();

        if (response.ok && jsonResponse.success) {
            iziToast.success(
                {
                    timeout:800,
                    message:jsonResponse.message,
                    position:'topRight',
                    onClosing: ()=>{
                        window.location = "cart.html"
                    }
                }
            );
        } else {
            showToast(jsonResponse.message || "Failed to alter inventory quantity allocation.", true);
        }
    } catch (error) {
        console.error("Mutation Error:", error);
      //  showToast("Server transmission fault while adjusting quantity.", true);
    }
}

