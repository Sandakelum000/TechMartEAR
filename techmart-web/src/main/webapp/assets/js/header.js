document.addEventListener("DOMContentLoaded", () => {
    injectGlobalHeaderComponent();
});

async function injectGlobalHeaderComponent() {
    const targetPlaceholder = document.getElementById('header-slot');
    if (!targetPlaceholder) return;

    try {
        const response = await fetch("header.html");
        if (!response.ok) throw new Error(`Status: ${response.status}`);

        targetPlaceholder.innerHTML = await response.text();
        evaluateUserNavigationState();
    } catch (error) {
        console.error("Layout engine failure rendering header component:", error);
    }
}

function evaluateUserNavigationState() {
    const guestGroupNode = document.getElementById('navGroupGuest');
    const userGroupNode = document.getElementById('navGroupUser');
    const usernameLabel = document.getElementById('navUsernameLabel');
    const userInitialNode = document.getElementById('navUserInitial');

    let runningCustomerSession = null;
    try {
        const customerCachedData = localStorage.getItem("user");
        if (customerCachedData) runningCustomerSession = JSON.parse(customerCachedData);
    } catch (e) {
        console.error(e);
    }

    if (runningCustomerSession && runningCustomerSession.username) {
        if (guestGroupNode) guestGroupNode.classList.add('hidden');
        if (userGroupNode) userGroupNode.classList.remove('hidden');

        if (usernameLabel) usernameLabel.innerText = runningCustomerSession.username;
        if (userInitialNode) userInitialNode.innerText = runningCustomerSession.username.charAt(0).toUpperCase();
    } else {
        if (userGroupNode) userGroupNode.classList.add('hidden');
        if (guestGroupNode) guestGroupNode.classList.remove('hidden');
    }

    if (window.lucide && typeof lucide.createIcons === "function") {
        lucide.createIcons();
    }
}

window.executeUserLogoutPipeline = async function() {
    console.log("Initiating server-side session invalidation sequence...");

    try {
        const response = await fetch("api/users/logout", {
            method: "POST",
            headers: {
                "Accept": "application/json"
            }
        });

        if (response.ok) {
            const data = await response.json();
            if (data.success) {
                console.log("Server session cleared successfully.");
            }
        } else {
            console.warn("Backend rejected structural session invalidation or was already closed.");
        }
    } catch (error) {
        console.error("Network infrastructure exception during logout pipeline dispatch:", error);
    } finally {
        localStorage.removeItem("user");

        if (window.iziToast) {
            iziToast.info({
                timeout: 800,
                message: "Session ended securely.",
                position: 'topRight',
                onClosing: () => {
                    window.location.href = "sign-in.html";
                }
            });
        } else {
            window.location.href = "sign-in.html";
        }
    }
};