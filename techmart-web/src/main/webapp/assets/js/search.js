
let currentCategories = [];
let currentBrands = [];
let totalProductCount = 0;
let currentPage = 1;
const itemsPerPage = 10;

const BASE_URL = "api/advanced-search";

document.addEventListener("DOMContentLoaded", () => {
    loadInitialData();
});

async function loadInitialData() {
    try {
        const response = await fetch(`${BASE_URL}/all-data`);
        if (!response.ok) throw new Error("Failed to load initial data context.");

        const jsonResponse = await response.json();

        if (jsonResponse.success && jsonResponse.data) {
            const serverData = jsonResponse.data;
            console.log(serverData);
            totalProductCount = serverData.allProductCount || 0;

            renderFilterCheckboxes('categoryContainer', serverData.categoryList, 'category');
            renderFilterCheckboxes('brandContainer', serverData.brandList, 'brand');

            setupPriceSlider(serverData.minPrice, serverData.maxPrice);

            processReceivedProducts(serverData.productList);
        }
    } catch (error) {
        console.error("Initialization Error:", error);
        displayGridMessage("Error connecting to server data pipelines.");
    }
}

function renderFilterCheckboxes(containerId, list, type) {
    const container = document.getElementById(containerId);
    if (!list || list.length === 0) {
        container.innerHTML = `<p class="text-xs text-gray-400">None available</p>`;
        return;
    }

    container.innerHTML = list.map(item => `
        <label class="flex items-center gap-3 text-sm font-medium text-gray-600 cursor-pointer hover:text-indigo-600">
            <input type="checkbox" name="${type}" value="${item.name}" onchange="applyFilters()" class="w-4 h-4 text-indigo-600 border-gray-300 rounded-sm focus:ring-indigo-500">
            <span>${item.name}</span>
        </label>
    `).join('');
}

function setupPriceSlider(min, max) {
    const slider = document.getElementById('priceSlider');
    slider.min = min || 0;
    slider.max = max || 1000;
    slider.value = max || 1000;

    document.getElementById('priceValue').innerText = `$${slider.value}`;
    slider.nextElementSibling.innerHTML = `<span>$${min}</span><span>$${max}</span>`;
}

function updatePriceLabel(value) {
    document.getElementById('priceValue').innerText = `$${value}`;
}


async function applyFilters() {
    const searchString = document.getElementById('searchInput').value.trim();
    const maxPriceThreshold = parseFloat(document.getElementById('priceSlider').value);
    const selectedSort = document.getElementById('sortSelect').value;

    const selectedCategoryElement = document.querySelector('input[name="category"]:checked');
    const selectedBrandElement = document.querySelector('input[name="brand"]:checked');

    const currentOffset = (currentPage - 1) * itemsPerPage;

    const requestPayload = {
        searchText:searchString !== ""?searchString:null,
        brandName: selectedBrandElement ? selectedBrandElement.value : null,
        categoryName: selectedCategoryElement ? selectedCategoryElement.value : null,
        priceStart: parseFloat(document.getElementById('priceSlider').min) || 0.0,
        priceEnd: maxPriceThreshold,
        sortBy: selectedSort,
        offset: currentOffset
    };

    try {
        const response = await fetch(`${BASE_URL}/filter`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestPayload)
        });

        if (!response.ok) throw new Error("Search execution failed.");
        const jsonResponse = await response.json();

        if (jsonResponse.success && jsonResponse.data) {
            totalProductCount = jsonResponse.data.allProductCount;
            processReceivedProducts(jsonResponse.data.productList);
        } else {
            displayGridMessage(jsonResponse.message || "No records retrieved.");
        }
    } catch (error) {
        console.error("Filter Pipeline Processing Error:", error);
        displayGridMessage("Error updating matching collections.");
    }
}

function handleSearch() {
    currentPage = 1;
    applyFilters();
}

function processReceivedProducts(productList) {
    const grid = document.getElementById('productGrid');
    document.getElementById('resultCount').innerText = `Showing ${productList ? productList.length : 0} of ${totalProductCount} products`;

    grid.innerHTML = '';

    if (!productList || productList.length === 0) {
        grid.innerHTML = `
            <div class="col-span-full py-12 flex flex-col items-center justify-center text-gray-400">
                <i data-lucide="package-x" class="w-12 h-12 mb-2 stroke-1"></i>
                <p class="text-base font-medium">No items found matching criteria parameters.</p>
            </div>
        `;
        renderPaginationControls();
        lucide.createIcons();
        return;
    }

    grid.innerHTML = productList.map(product => {
        const imageSrc = product.images && product.images.length > 0
            ? product.images[0]
            : 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=400&q=80';

        return `
            <div class="group bg-white rounded-2xl border border-gray-200 overflow-hidden shadow-xs hover:shadow-md transition-all flex flex-col">
                <div class="relative bg-gray-100 aspect-square overflow-hidden">
                    <img src="${imageSrc}" alt="${product.title}" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300">
                    <span class="absolute top-3 left-3 bg-white/90 backdrop-blur-xs text-[10px] font-bold text-gray-700 px-2.5 py-1 rounded-full uppercase tracking-wider shadow-xs">
                        ${product.categoryName}
                    </span>
                </div>
                <div class="p-5 flex flex-col flex-1 justify-between gap-4">
                    <div class="space-y-1">
                        <span class="text-xs font-semibold text-indigo-600 uppercase tracking-wide">${product.brandName}</span>
                        <h4 class="font-bold text-gray-900 group-hover:text-indigo-600 transition-colors line-clamp-2">${product.title}</h4>
                        <p class="text-xs text-gray-400">Stock Qty: ${product.qty}</p>
                    </div>
                    <div class="flex items-center justify-between pt-2">
                        <span class="text-xl font-black text-gray-900">Rs ${product.price}</span>
                        <button onclick="qtyUpdate(${product.stockId}, 1)" class="bg-gray-900 hover:bg-indigo-600 text-white p-2.5 rounded-xl transition-colors cursor-pointer border-none">
                            <i data-lucide="plus" class="w-4 h-4"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');

    renderPaginationControls();
    lucide.createIcons();
}


async function qtyUpdate(stockId, numericDelta) {
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
        showToast("Server transmission fault while adjusting quantity.", true);
    }
}


function renderPaginationControls() {
    const container = document.getElementById('paginationContainer');
    container.innerHTML = '';

    const totalPages = Math.ceil(totalProductCount / itemsPerPage);
    if (totalPages <= 1) return;

    const prevDisabled = currentPage === 1;
    container.insertAdjacentHTML('beforeend', `
        <button onclick="changePage(${currentPage - 1})" ${prevDisabled ? 'disabled' : ''} class="p-2 border border-gray-200 rounded-lg text-gray-500 hover:bg-gray-50 disabled:opacity-40 cursor-pointer">
            <i data-lucide="chevron-left" class="w-4 h-4"></i>
        </button>
    `);

    for (let i = 1; i <= totalPages; i++) {
        const isActive = i === currentPage;
        container.insertAdjacentHTML('beforeend', `
            <button onclick="changePage(${i})" class="w-9 h-9 font-semibold text-sm rounded-lg border border-gray-200 cursor-pointer ${isActive ? 'bg-indigo-600 border-indigo-600 text-white' : 'text-gray-600 hover:bg-gray-50'}">
                ${i}
            </button>
        `);
    }

    const nextDisabled = currentPage === totalPages;
    container.insertAdjacentHTML('beforeend', `
        <button onclick="changePage(${currentPage + 1})" ${nextDisabled ? 'disabled' : ''} class="p-2 border border-gray-200 rounded-lg text-gray-500 hover:bg-gray-50 disabled:opacity-40 cursor-pointer">
            <i data-lucide="chevron-right" class="w-4 h-4"></i>
        </button>
    `);
}

function changePage(page) {
    currentPage = page;
    applyFilters();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function resetFilters() {
    document.getElementById('searchForm').reset();
    document.querySelectorAll('input[type="checkbox"]').forEach(el => el.checked = false);
    currentPage = 1;
    loadInitialData();
}

function displayGridMessage(message) {
    document.getElementById('productGrid').innerHTML = `
        <div class="col-span-full py-12 text-center text-gray-500">
            <p>${message}</p>
        </div>
    `;
}


