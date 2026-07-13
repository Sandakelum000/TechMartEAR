const ADMIN_AUTH_ENDPOINT = "api/admin/login";

document.addEventListener("DOMContentLoaded", () => {
    lucide.createIcons();
});

window.executeAdminAuthPipeline = async function(event) {
    event.preventDefault();

    const usernameInput = document.getElementById('txtAuthUser');
    const passwordInput = document.getElementById('txtAuthPass');
    const actionButton = document.getElementById('btnSubmitAuth');

    const usernameValue = usernameInput.value.trim();
    const passwordValue = passwordInput.value;

    if (!usernameValue || !passwordValue) {
        showAuthToast("Please enter both parameters to authenticate.", true);
        return;
    }

    actionButton.classList.add("pointer-events-none", "opacity-60");
    actionButton.innerHTML = `<span>Verifying Credentials...</span><i data-lucide="loader" class="w-4 h-4 animate-spin"></i>`;
    lucide.createIcons();

    const authDataPayload = {
        username: usernameValue,
        password: passwordValue
    };

    try {
        const response = await fetch(ADMIN_AUTH_ENDPOINT, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(authDataPayload)
        });

        const jsonResponse = await response.json();

        if (response.ok && jsonResponse.success) {
            showAuthToast(jsonResponse.message || "Identity confirmed. Handshaking secure cookies...");

            setTimeout(() => {
                window.location.href = "admin-dashboard.html";
            }, 1000);
        } else {
            showAuthToast(jsonResponse.message || "Invalid credential parameters. Access denied.", true);
            resetAuthControlElements(actionButton);
        }
    } catch (error) {
        console.error("Networking transport layer failure caught:", error);
        showAuthToast("Server pipeline unreachable. Check deployment containers.", true);
        resetAuthControlElements(actionButton);
    }
};

function resetAuthControlElements(buttonNode) {
    buttonNode.classList.remove("pointer-events-none", "opacity-60");
    buttonNode.innerHTML = `<span>Establish Session Matrix</span><i data-lucide="arrow-right" class="w-4 h-4"></i>`;
    lucide.createIcons();
}

function showAuthToast(msg, isErr = false) {
    const element = document.getElementById('authToast');
    if (!element) return;

    element.className = `fixed top-5 right-5 z-50 px-4 py-3 rounded-xl shadow-xl flex items-center gap-2 border transition-all duration-300 ${
        isErr ? 'bg-red-50 border-red-200 text-red-800' : 'bg-slate-900 border-transparent text-white'
    }`;

    element.innerHTML = `${
        isErr ? '<i data-lucide="alert-circle" class="w-5 h-5"></i>' : '<i data-lucide="check-circle" class="w-5 h-5"></i>'
    } <span class="text-sm font-semibold">${msg}</span>`;

    lucide.createIcons();
    element.classList.remove('hidden');

    setTimeout(() => element.classList.add('hidden'), 4000);
}