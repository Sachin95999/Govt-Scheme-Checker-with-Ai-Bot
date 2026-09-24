// Shared Javascript for AI Government Schemes Assistant

const API_BASE = "/api";

// State management
const state = {
    token: localStorage.getItem("token") || null,
    username: localStorage.getItem("username") || null,
    role: localStorage.getItem("role") || null,
    sessionId: null
};

// Initialize app
document.addEventListener("DOMContentLoaded", () => {
    // Generate session ID for chatbot session if not present
    if (!state.sessionId) {
        state.sessionId = generateUUID();
    }

    // Apply active authentication layout changes
    updateAuthUI();

    // Route-specific setups
    const path = window.location.pathname;
    if (path.includes("dashboard.html")) {
        checkAuth();
        initDashboard();
    } else if (path.includes("eligibility.html")) {
        checkAuth();
        initEligibility();
    } else if (path.includes("admin.html")) {
        checkAuth();
        if (state.role !== "ADMIN") {
            alert("Access Denied: Administrator role required.");
            window.location.href = "dashboard.html";
        }
        initAdmin();
    } else if (path.includes("index.html") || path === "/") {
        initLanding();
    }
});

// Helper: UUID generator for sessions
function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

// Navigation guard
function checkAuth() {
    if (!state.token) {
        localStorage.clear();
        window.location.href = "index.html";
    }
}

// Refresh UI based on logged-in status
function updateAuthUI() {
    const userDisplay = document.getElementById("nav-username");
    if (userDisplay) {
        userDisplay.textContent = state.username || "Guest";
    }

    // Show admin link if user is admin
    const adminLink = document.getElementById("nav-admin-link");
    if (adminLink) {
        if (state.role === "ADMIN") {
            adminLink.classList.remove("hidden");
        } else {
            adminLink.classList.add("hidden");
        }
    }
}

// Global Logout
function logout() {
    localStorage.clear();
    window.location.href = "index.html";
}

// Formatter: Convert text with markdown, bolding, linebreaks, and source citations
function formatAiResponse(text) {
    if (!text) return "";
    
    // Escape HTML to prevent injection
    let formatted = text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");

    // Format code snippets
    formatted = formatted.replace(/```([\s\S]*?)```/g, '<pre class="bg-slate-900 text-slate-100 p-3 rounded-lg my-2 font-mono text-sm overflow-x-auto"><code>$1</code></pre>');

    // Format inline code
    formatted = formatted.replace(/`([^`]+)`/g, '<code class="bg-slate-800 text-pink-400 px-1 rounded font-mono text-sm">$1</code>');

    // Format bold text
    formatted = formatted.replace(/\*\*([^*]+)\*\*/g, '<strong class="font-semibold text-white">$1</strong>');

    // Format lists
    formatted = formatted.replace(/^\s*-\s+(.+)$/gm, '<li class="ml-4 list-disc text-slate-300">$1</li>');
    formatted = formatted.replace(/^\s*\*\s+(.+)$/gm, '<li class="ml-4 list-disc text-slate-300">$1</li>');
    
    // Group lists into ul blocks
    formatted = formatted.replace(/(<li.*<\/li>)/g, '<ul class="my-2 space-y-1">$1</ul>');
    // Clean up duplicate wrapping
    formatted = formatted.replace(/<\/ul>\s*<ul[^>]*>/g, "");

    // Format source citations [filename.pdf, Page X]
    // Matches patterns like [pm_awas.pdf, Page 2] or [doc.pdf, Page 12]
    const citationRegex = /\[([^,\]]+)\.pdf,\s*Page\s*(\d+)\]/gi;
    formatted = formatted.replace(citationRegex, (match, filename, page) => {
        return `<span class="inline-flex items-center gap-1 bg-violet-950/70 border border-violet-800 text-violet-300 px-2 py-0.5 rounded-full text-xs font-medium cursor-help" title="Verified source file: ${filename}.pdf (Page ${page})">
            <svg class="w-3 h-3 text-violet-400" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
            ${filename}.pdf (Pg ${page})
        </span>`;
    });

    // Format new lines
    formatted = formatted.replace(/\n/g, "<br>");

    return formatted;
}

// ----------------------------------------------------
// 1. LANDING & AUTH PAGE (`index.html`)
// ----------------------------------------------------
function initLanding() {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("register-form");

    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const username = document.getElementById("login-username").value;
            const password = document.getElementById("login-password").value;
            const errorEl = document.getElementById("login-error");

            try {
                const response = await fetch(`${API_BASE}/auth/login`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username, password })
                });

                const data = await response.json();
                if (response.ok) {
                    localStorage.setItem("token", data.token);
                    localStorage.setItem("username", data.username);
                    localStorage.setItem("role", data.role);
                    window.location.href = "eligibility.html";
                } else {
                    errorEl.textContent = data.message || "Invalid credentials. Try again.";
                    errorEl.classList.remove("hidden");
                }
            } catch (err) {
                errorEl.textContent = "Service unavailable. Please try again later.";
                errorEl.classList.remove("hidden");
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const username = document.getElementById("reg-username").value;
            const email = document.getElementById("reg-email").value;
            const password = document.getElementById("reg-password").value;
            const role = document.getElementById("reg-role").value;
            const errorEl = document.getElementById("reg-error");
            const successEl = document.getElementById("reg-success");

            try {
                const response = await fetch(`${API_BASE}/auth/register`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username, email, password, role })
                });

                const data = await response.json();
                if (response.ok) {
                    successEl.textContent = "Account created! You can now log in.";
                    successEl.classList.remove("hidden");
                    errorEl.classList.add("hidden");
                    registerForm.reset();
                } else {
                    errorEl.textContent = data.message || "Registration failed.";
                    errorEl.classList.remove("hidden");
                    successEl.classList.add("hidden");
                }
            } catch (err) {
                errorEl.textContent = "Service error. Please try again.";
                errorEl.classList.remove("hidden");
            }
        });
    }
}

// ----------------------------------------------------
// 2. USER DASHBOARD / CHAT INTERFACE (`dashboard.html`)
// ----------------------------------------------------
async function initDashboard() {
    const chatContainer = document.getElementById("chat-messages");
    const chatForm = document.getElementById("chat-form");
    const chatInput = document.getElementById("chat-input");
    const historySidebar = document.getElementById("chat-history-list");
    const schemeSelect = document.getElementById("chat-scheme-select");

    // Load Chat History list in sidebar
    await loadSidebarHistory(historySidebar);

    // Load schemes into Target Scheme dropdown
    if (schemeSelect) {
        try {
            const sRes = await fetch(`${API_BASE}/schemes`, {
                headers: { "Authorization": `Bearer ${state.token}` }
            });
            if (sRes.ok) {
                const schemes = await sRes.json();
                // Clear existing dynamic options (keep first option)
                schemeSelect.innerHTML = `<option value="">All Schemes (Auto-detect)</option>`;
                schemes.forEach(s => {
                    const opt = document.createElement("option");
                    opt.value = s.name;
                    opt.textContent = s.name;
                    schemeSelect.appendChild(opt);
                });
            }
        } catch (e) {
            System.err.println("Error loading schemes for dropdown: " + e);
        }
    }

    // Check if there is a target scheme redirected from scheme card or modal
    const targetScheme = localStorage.getItem("target_scheme_name");
    if (targetScheme && schemeSelect) {
        localStorage.removeItem("target_scheme_name");
        schemeSelect.value = targetScheme;
        if (chatInput) {
            chatInput.placeholder = `Ask a specific question about ${targetScheme}...`;
            chatInput.focus();
        }
    }

    // Initial Welcome Message
    const welcomeTrans = document.getElementById("welcome-message-translation");
    const welcomeMsg = welcomeTrans ? welcomeTrans.textContent : "Hello! I am your AI Schemes Assistant. Ask me anything about central or state welfare benefits.";
    appendMessage("system", welcomeMsg, null);

    // Check if there is an automated prefill query (e.g. from eligibility modal)
    const prefill = localStorage.getItem("prefill_query");
    if (prefill && chatInput && chatForm) {
        localStorage.removeItem("prefill_query");
        chatInput.value = prefill;
        setTimeout(() => {
            chatForm.dispatchEvent(new Event('submit'));
        }, 300);
    }

    // Send Message handler
    if (chatForm) {
        chatForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const message = chatInput.value.trim();
            if (!message) return;

            const selectedScheme = schemeSelect ? schemeSelect.value : "";

            // Render User Message
            appendMessage("user", message, null);
            chatInput.value = "";

            // Render Loader Message for AI response
            const loaderId = appendMessage("ai-loader", "", null);

            try {
                const response = await fetch(`${API_BASE}/chat`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${state.token}`
                    },
                    body: JSON.stringify({
                        message,
                        sessionId: state.sessionId,
                        schemeName: selectedScheme
                    })
                });

                const data = await response.json();
                
                // Remove loader message
                document.getElementById(loaderId).remove();

                if (response.ok) {
                    appendMessage("ai", data.aiResponse, data.id, data.sourcesJson);
                    // Refresh sidebar
                    loadSidebarHistory(historySidebar);
                } else {
                    appendMessage("system", "Sorry, I could not retrieve an answer due to an internal server issue.", null);
                }
            } catch (err) {
                document.getElementById(loaderId).remove();
                appendMessage("system", "Connection failed. Please verify the backend is running.", null);
            }
        });
    }
}

async function loadSidebarHistory(container) {
    if (!container) return;
    try {
        const response = await fetch(`${API_BASE}/chat/history`, {
            headers: { "Authorization": `Bearer ${state.token}` }
        });
        const data = await response.json();
        
        if (response.ok && data.length > 0) {
            container.innerHTML = "";
            
            // Deduplicate sessions or just list recent queries
            const recentChats = data.slice(0, 10); // get last 10 entries
            recentChats.forEach(chat => {
                const item = document.createElement("div");
                item.className = "p-3 rounded-lg bg-slate-800/40 border border-slate-700/30 hover:bg-slate-700/40 cursor-pointer transition text-sm flex flex-col gap-1";
                
                const messageText = chat.userMessage.length > 40 ? chat.userMessage.substring(0, 37) + "..." : chat.userMessage;
                item.innerHTML = `
                    <span class="text-slate-200 font-medium truncate">${messageText}</span>
                    <span class="text-xs text-slate-500">${new Date(chat.createdAt).toLocaleString()}</span>
                `;
                item.addEventListener("click", () => {
                    // Populate this chat in the window
                    const messagesArea = document.getElementById("chat-messages");
                    messagesArea.innerHTML = "";
                    appendMessage("user", chat.userMessage, null);
                    appendMessage("ai", chat.aiResponse, chat.id, chat.sourcesJson);
                });
                container.appendChild(item);
            });
        } else {
            const emptyTrans = document.getElementById("sidebar-empty-translation");
            const emptyMsg = emptyTrans ? emptyTrans.textContent : "No recent queries found.";
            container.innerHTML = `<div class="text-text-muted text-sm text-center py-4">${emptyMsg}</div>`;
        }
    } catch (err) {
        container.innerHTML = `<div class="text-red-500 text-xs py-4">Failed to load history.</div>`;
    }
}

function appendMessage(sender, text, chatId, sourcesJson = "[]") {
    const chatContainer = document.getElementById("chat-messages");
    if (!chatContainer) return null;

    const msgId = "msg_" + (chatId || Math.random().toString(36).substring(7));
    const messageWrapper = document.createElement("div");
    messageWrapper.id = msgId;
    messageWrapper.className = `flex w-full mb-5 ${sender === 'user' ? 'justify-end' : 'justify-start'}`;

    let avatarHtml = "";
    let contentClasses = "";
    
    if (sender === "user") {
        avatarHtml = `
            <div class="w-8 h-8 rounded-full bg-violet-600 flex items-center justify-center text-white text-xs font-semibold shrink-0 ml-3 order-2">
                U
            </div>
        `;
        contentClasses = "bg-violet-600 text-white rounded-2xl rounded-tr-none px-4 py-3 max-w-[75%] order-1 shadows";
    } else if (sender === "ai") {
        avatarHtml = `
            <div class="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-xs shrink-0 mr-3">
                🤖
            </div>
        `;
        contentClasses = "bg-slate-900/60 border border-slate-800 text-slate-200 rounded-2xl rounded-tl-none px-4 py-3 max-w-[75%] backdrop-blur-md";
    } else if (sender === "ai-loader") {
        avatarHtml = `
            <div class="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-xs shrink-0 mr-3">
                🤖
            </div>
        `;
        contentClasses = "bg-slate-900/60 border border-slate-800 text-slate-400 rounded-2xl rounded-tl-none px-4 py-3 backdrop-blur-md flex items-center gap-2";
    } else {
        // System
        contentClasses = "bg-slate-800/80 border border-slate-700 text-slate-300 rounded-lg px-4 py-2 w-full text-center text-xs font-medium";
    }

    let messageContentHtml = "";
    if (sender === "ai-loader") {
        messageContentHtml = `
            <div class="flex items-center gap-1.5 py-1">
                <span class="w-2 h-2 rounded-full bg-violet-400 animate-bounce"></span>
                <span class="w-2 h-2 rounded-full bg-violet-400 animate-bounce [animation-delay:0.2s]"></span>
                <span class="w-2 h-2 rounded-full bg-violet-400 animate-bounce [animation-delay:0.4s]"></span>
            </div>
        `;
    } else {
        messageContentHtml = `<div>${sender === 'ai' ? formatAiResponse(text) : text}</div>`;
        
        // Add sources footer for AI response
        if (sender === "ai" && sourcesJson && sourcesJson !== "[]") {
            try {
                const sources = typeof sourcesJson === 'string' ? JSON.parse(sourcesJson) : sourcesJson;
                if (sources && sources.length > 0) {
                    let sourceBadges = sources.map(src => 
                        `<span class="bg-slate-800 border border-slate-700 text-slate-400 px-2 py-0.5 rounded text-[10px]" title="${src.source}">${src.source} (Pg ${src.page})</span>`
                    ).join("");
                    messageContentHtml += `
                        <div class="mt-3 pt-2 border-t border-slate-800 flex flex-wrap items-center gap-1.5">
                            <span class="text-[10px] text-slate-500 font-medium">Citations:</span>
                            ${sourceBadges}
                        </div>
                    `;
                }
            } catch (e) {
                console.error("Error parsing sources json in template", e);
            }
        }

        // Add feedback rating buttons for AI messages
        if (sender === "ai" && chatId) {
            messageContentHtml += `
                <div class="mt-2 flex items-center justify-end gap-2 text-slate-500 text-xs">
                    <span>Helpful?</span>
                    <button onclick="submitMessageFeedback(${chatId}, 5, '${msgId}')" class="hover:text-emerald-400 transition" title="Thumbs Up">👍</button>
                    <button onclick="submitMessageFeedback(${chatId}, 1, '${msgId}')" class="hover:text-rose-400 transition" title="Thumbs Down">👎</button>
                </div>
            `;
        }
    }

    messageWrapper.innerHTML = `
        ${avatarHtml}
        <div class="${contentClasses}">
            ${messageContentHtml}
        </div>
    `;

    chatContainer.appendChild(messageWrapper);
    chatContainer.scrollTop = chatContainer.scrollHeight;
    
    return msgId;
}

// Submit thumbs feedback
async function submitMessageFeedback(chatId, rating, messageId) {
    try {
        const response = await fetch(`${API_BASE}/chat/feedback`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${state.token}`
            },
            body: JSON.stringify({ chatId, rating, comments: rating === 5 ? "Thumbs up feedback" : "Thumbs down feedback" })
        });
        if (response.ok) {
            // Find the feedback element inside the chat message bubble and replace it with thanks
            const messageBubble = document.getElementById(messageId);
            const feedbackContainer = messageBubble.querySelector(".mt-2.flex.items-center");
            if (feedbackContainer) {
                feedbackContainer.innerHTML = `<span class="text-emerald-400 font-medium text-[10px]">Thank you for your feedback!</span>`;
            }
        }
    } catch (err) {
        console.error("Failed to submit feedback", err);
    }
}

// ----------------------------------------------------
// 3. ELIGIBILITY CHECKER FORM (`eligibility.html`)
// ----------------------------------------------------
function initEligibility() {
    const form = document.getElementById("eligibility-form");
    const resultsContainer = document.getElementById("eligibility-results");

    // Restore saved form inputs & results if previously filled
    if (form) {
        const savedFormData = localStorage.getItem("eligibility_form_data");
        if (savedFormData) {
            try {
                const data = JSON.parse(savedFormData);
                if (data.age !== undefined && document.getElementById("age")) document.getElementById("age").value = data.age;
                if (data.gender && document.getElementById("gender")) document.getElementById("gender").value = data.gender;
                if (data.income !== undefined && document.getElementById("income")) document.getElementById("income").value = data.income;
                if (data.occupation && document.getElementById("occupation")) document.getElementById("occupation").value = data.occupation;
                if (data.state && document.getElementById("state")) document.getElementById("state").value = data.state;
                if (data.category && document.getElementById("category")) document.getElementById("category").value = data.category;
            } catch (e) {
                console.error("Error restoring saved eligibility form data", e);
            }
        }

        const savedResultsHtml = localStorage.getItem("eligibility_results_html");
        if (savedResultsHtml && resultsContainer) {
            resultsContainer.innerHTML = savedResultsHtml;
        }

        // Save inputs on change
        const inputs = form.querySelectorAll("input, select");
        inputs.forEach(input => {
            input.addEventListener("change", () => {
                const ageVal = document.getElementById("age")?.value;
                const incomeVal = document.getElementById("income")?.value;
                const occupationVal = document.getElementById("occupation")?.value;
                const stateVal = document.getElementById("state")?.value;
                const categoryVal = document.getElementById("category")?.value;
                const genderVal = document.getElementById("gender")?.value;
                localStorage.setItem("eligibility_form_data", JSON.stringify({
                    age: ageVal,
                    income: incomeVal,
                    occupation: occupationVal,
                    state: stateVal,
                    category: categoryVal,
                    gender: genderVal
                }));
            });
        });

        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            
            const age = parseInt(document.getElementById("age").value);
            const income = parseFloat(document.getElementById("income").value);
            const occupation = document.getElementById("occupation").value;
            const stateInput = document.getElementById("state").value;
            const category = document.getElementById("category").value;
            const gender = document.getElementById("gender").value;

            // Save form values to localStorage
            localStorage.setItem("eligibility_form_data", JSON.stringify({
                age, income, occupation, state: stateInput, category, gender
            }));

            resultsContainer.innerHTML = `
                <div class="flex flex-col items-center justify-center py-10 gap-3">
                    <span class="w-8 h-8 rounded-full border-4 border-t-violet-500 border-slate-700 animate-spin"></span>
                    <span class="text-sm text-slate-400 font-medium animate-pulse">Running demographic match algorithms...</span>
                </div>
            `;

            try {
                const response = await fetch(`${API_BASE}/schemes/check-eligibility`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${state.token}`
                    },
                    body: JSON.stringify({ age, income, occupation, state: stateInput, category, gender })
                });

                const schemes = await response.json();
                resultsContainer.innerHTML = "";

                if (response.ok) {
                    if (schemes.length === 0) {
                        resultsContainer.innerHTML = `
                            <div class="text-center p-8 bg-slate-900/40 rounded-xl border border-slate-800/80">
                                <span class="text-3xl">ℹ️</span>
                                <h3 class="text-lg font-semibold text-slate-300 mt-2">No Matching Schemes Found</h3>
                                <p class="text-slate-500 text-sm mt-1">Based on your age, income, and category, you do not meet the criteria of current logged welfare schemes. Try altering parameters or consult our RAG chatbot for details.</p>
                            </div>
                        `;
                    } else {
                        const grid = document.createElement("div");
                        grid.className = "grid grid-cols-1 md:grid-cols-2 gap-5";
                        
                        const transDept = document.getElementById("trans-dept")?.textContent || "Dept:";
                        const transRules = document.getElementById("trans-rules")?.textContent || "View Rules &rarr;";
                        
                        schemes.forEach(scheme => {
                            const card = document.createElement("div");
                            card.className = "bg-surface border border-border p-5 rounded-xl transition flex flex-col justify-between hover:border-primary shadow-sm";
                            card.innerHTML = `
                                <div>
                                    <div class="flex items-center justify-between mb-2">
                                        <span class="text-xs bg-surface border border-border text-text-muted px-2 py-0.5 rounded-full font-medium">${scheme.category}</span>
                                        <span class="text-xs bg-surface border border-border text-primary px-2 py-0.5 rounded-full font-semibold">${scheme.state}</span>
                                    </div>
                                    <h4 class="text-md font-semibold text-text-main mb-2">${scheme.name}</h4>
                                    <p class="text-xs text-text-muted line-clamp-3 mb-4 leading-relaxed">${scheme.description}</p>
                                </div>
                                <div class="pt-3 border-t border-border flex items-center justify-between text-xs text-text-muted">
                                    <span>${transDept} <strong class="text-text-main font-medium">${scheme.department}</strong></span>
                                    <div class="flex items-center gap-2">
                                        <button onclick="askAiAboutScheme('${scheme.name.replace(/'/g, "\\'")}')" class="bg-primary/10 text-primary hover:bg-primary hover:text-white px-2.5 py-1 rounded-lg font-semibold transition text-xs flex items-center gap-1">
                                            <span>💬</span> Ask AI
                                        </button>
                                        <button onclick="viewSchemeDetails(${scheme.id})" class="text-primary font-semibold hover:underline transition">${transRules}</button>
                                    </div>
                                </div>
                            `;
                            grid.appendChild(card);
                        });
                        
                        resultsContainer.appendChild(grid);
                    }
                    // Save rendered results HTML to localStorage
                    localStorage.setItem("eligibility_results_html", resultsContainer.innerHTML);
                } else {
                    resultsContainer.innerHTML = `<div class="text-red-500 text-sm text-center py-6">Could not process evaluation. Please review values.</div>`;
                }
            } catch (err) {
                resultsContainer.innerHTML = `<div class="text-red-500 text-sm text-center py-6">Server request failed. Is the database active?</div>`;
            }
        });
    }
}

// Modal for Scheme details
async function viewSchemeDetails(id) {
    try {
        const response = await fetch(`${API_BASE}/schemes/${id}`, {
            headers: { "Authorization": `Bearer ${state.token}` }
        });
        const scheme = await response.json();
        
        if (response.ok) {
            // Create a modal element dynamically
            const modal = document.createElement("div");
            modal.className = "fixed inset-0 z-50 flex items-center justify-center bg-slate-950/80 backdrop-blur-sm p-4";
            
            // Grab Translations
            const tDesc = document.getElementById("trans-modal-desc")?.textContent || "Description";
            const tAge = document.getElementById("trans-modal-age")?.textContent || "Age Limits";
            const tIncome = document.getElementById("trans-modal-income")?.textContent || "Income Cap";
            const tOccupations = document.getElementById("trans-modal-occupations")?.textContent || "Eligible Occupations";
            const tCategories = document.getElementById("trans-modal-categories")?.textContent || "Social Categories";
            const tGenders = document.getElementById("trans-modal-genders")?.textContent || "Eligible Genders";
            const tDept = document.getElementById("trans-modal-dept")?.textContent || "Ministry / Department";
            const tDocs = document.getElementById("trans-modal-docs")?.textContent || "Documents Required";
            const tMissingTitle = document.getElementById("trans-modal-missing-title")?.textContent || "Missing a required document?";
            const tMissingDesc = document.getElementById("trans-modal-missing-desc")?.textContent || "Ask our AI chatbot if there are concessions...";
            const tBtnAsk = document.getElementById("trans-modal-btn-ask")?.textContent || "Ask AI Assistant &rarr;";
            const tBtnApply = document.getElementById("trans-modal-btn-apply")?.textContent || "Apply Online &rarr;";
            const tBtnDownload = document.getElementById("trans-modal-btn-download")?.textContent || "Download Guidelines PDF 📄";
            const tBtnClose = document.getElementById("trans-modal-btn-close")?.textContent || "Close";

            // Format documents list
            let docsHtml = "";
            if (scheme.documentsRequired) {
                const docs = scheme.documentsRequired.split(",");
                docsHtml = docs.map(doc => `<li class="flex items-center gap-2 text-text-main py-0.5"><span class="text-success">✓</span> ${doc.trim()}</li>`).join("");
            } else {
                docsHtml = `<span class="text-text-muted">None specified.</span>`;
            }

            // Mappings for links
            let linksHtml = "";
            if (scheme.applyLink) {
                linksHtml += `<a href="${scheme.applyLink}" target="_blank" class="bg-primary hover:bg-primary-hover text-white px-5 py-2 rounded-lg font-semibold text-xs text-center shadow-sm transition duration-150">${tBtnApply}</a>`;
            }
            if (scheme.pdfUrl) {
                linksHtml += `<a href="${scheme.pdfUrl}" target="_blank" class="bg-surface border border-border text-text-main px-5 py-2 rounded-lg font-semibold text-xs text-center hover:bg-surface-sec transition">${tBtnDownload}</a>`;
            }

            modal.innerHTML = `
                <div class="bg-surface border border-border rounded-xl w-full max-w-2xl max-h-[85vh] overflow-y-auto p-6 shadow-md">
                    <div class="flex justify-between items-start mb-4 border-b border-border pb-3">
                        <div>
                            <span class="text-xs bg-surface border border-border text-text-muted px-2.5 py-0.5 rounded-full font-medium mr-2">${scheme.category}</span>
                            <span class="text-xs bg-surface border border-border text-primary px-2.5 py-0.5 rounded-full font-semibold">${scheme.state}</span>
                            <h3 class="text-xl font-bold text-text-main mt-2">${scheme.name}</h3>
                        </div>
                        <button onclick="this.closest('.fixed').remove()" class="text-text-muted hover:text-text-main transition text-xl">&times;</button>
                    </div>
                    <div class="space-y-4 text-sm text-text-main leading-relaxed">
                        <div>
                            <h5 class="text-xs font-semibold uppercase tracking-wider text-text-muted mb-1">${tDesc}</h5>
                            <p class="bg-surface-sec p-3 rounded-lg border border-border">${scheme.description}</p>
                        </div>
                        <div class="grid grid-cols-2 gap-4">
                            <div>
                                <h5 class="text-xs font-semibold uppercase tracking-wider text-text-muted mb-1">${tAge}</h5>
                                <p class="text-text-main font-medium">${scheme.minAge || 0} to ${scheme.maxAge || "No Limit"} Years</p>
                            </div>
                            <div>
                                <h5 class="text-xs font-semibold uppercase tracking-wider text-text-muted mb-1">${tIncome}</h5>
                                <p class="text-text-main font-medium">${scheme.maxIncome ? "₹ " + scheme.maxIncome.toLocaleString() : "No Cap"}</p>
                            </div>
                            <div>
                                <h5 class="text-xs font-semibold uppercase tracking-wider text-text-muted mb-1">${tOccupations}</h5>
                                <p class="text-text-main font-medium">${scheme.eligibleOccupations || "All"}</p>
                            </div>
                            <div>
                                <h5 class="text-xs font-semibold uppercase tracking-wider text-text-muted mb-1">${tCategories}</h5>
                                <p class="text-text-main font-medium">${scheme.eligibleCategories || "All"}</p>
                            </div>
                        </div>
                        <div class="grid grid-cols-2 gap-4">
                            <div>
                                <h5 class="text-xs font-semibold uppercase tracking-wider text-text-muted mb-1">${tGenders}</h5>
                                <p class="text-text-main font-medium">${scheme.eligibleGenders || "All"}</p>
                            </div>
                            <div>
                                <h5 class="text-xs font-semibold uppercase tracking-wider text-text-muted mb-1">${tDept}</h5>
                                <p class="text-text-main font-medium">${scheme.department}</p>
                            </div>
                        </div>
                        <div>
                            <h5 class="text-xs font-semibold uppercase tracking-wider text-text-muted mb-1.5">${tDocs}</h5>
                            <ul class="grid grid-cols-1 md:grid-cols-2 gap-x-4 gap-y-1 bg-surface-sec border border-border p-4 rounded-lg">
                                ${docsHtml}
                            </ul>
                            
                            <!-- Ask AI alternative documents section -->
                            <div class="mt-4 bg-surface-sec border border-border p-4 rounded-lg flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                                <div class="space-y-0.5">
                                    <h6 class="text-xs font-bold text-primary">${tMissingTitle}</h6>
                                    <p class="text-[10px] text-text-muted leading-normal">${tMissingDesc}</p>
                                </div>
                                <button onclick="askAiAboutDocuments('${scheme.name.replace(/'/g, "\\'")}')" class="bg-primary hover:bg-primary-hover text-white px-4 py-1.5 rounded-lg font-semibold text-xs transition duration-150 shrink-0 self-start sm:self-auto">${tBtnAsk}</button>
                            </div>
                        </div>
                    </div>
                    <div class="mt-6 pt-4 border-t border-border flex flex-col sm:flex-row justify-between items-center gap-3">
                        <div class="flex flex-wrap gap-2">
                            ${linksHtml}
                        </div>
                        <button onclick="this.closest('.fixed').remove()" class="bg-surface border border-border text-text-main px-5 py-2 rounded-lg font-medium hover:bg-surface-sec transition text-xs shrink-0 self-end sm:self-auto">${tBtnClose}</button>
                    </div>
                </div>
            `;
            document.body.appendChild(modal);
        }
    } catch (e) {
        alert("Failed to fetch scheme rules.");
    }
}

function askAiAboutScheme(schemeName, customQuery = "") {
    localStorage.setItem("target_scheme_name", schemeName);
    if (customQuery) {
        localStorage.setItem("prefill_query", customQuery);
    }
    const activeModals = document.querySelectorAll(".fixed.inset-0");
    activeModals.forEach(m => m.remove());
    window.location.href = "dashboard.html";
}

function askAiAboutDocuments(schemeName) {
    askAiAboutScheme(
        schemeName,
        `I want to apply for ${schemeName}. I do not have all the required documents. What alternative documents can I use as substitutes, or are there concessions?`
    );
}

// ----------------------------------------------------
// 4. ADMIN DASHBOARD PANEL (`admin.html`)
// ----------------------------------------------------
async function initAdmin() {
    const metricsContainer = document.getElementById("admin-metrics");
    const uploadForm = document.getElementById("admin-upload-form");
    const docList = document.getElementById("admin-docs-list");
    const feedbackList = document.getElementById("admin-feedback-list");
    const schemeForm = document.getElementById("admin-scheme-form");

    // Fetch Metrics
    await refreshAdminMetrics(metricsContainer);

    // Fetch Document Index List
    await refreshAdminDocuments(docList);

    // Fetch Feedback Logs
    await refreshAdminFeedback(feedbackList);

    // Document Upload handler
    if (uploadForm) {
        uploadForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const fileInput = document.getElementById("pdf-file");
            if (fileInput.files.length === 0) return;

            const formData = new FormData();
            formData.append("file", fileInput.files[0]);

            const progressEl = document.getElementById("upload-progress-text");
            progressEl.textContent = "Processing PDF. Extracting pages and vectorizing text embeddings...";
            progressEl.classList.remove("hidden");

            try {
                const response = await fetch(`${API_BASE}/admin/documents/upload`, {
                    method: "POST",
                    headers: { "Authorization": `Bearer ${state.token}` },
                    body: formData
                });

                const data = await response.json();
                progressEl.classList.add("hidden");
                uploadForm.reset();

                if (response.ok) {
                    alert("Document vectorized and added to Spring AI vector store successfully!");
                    refreshAdminDocuments(docList);
                    refreshAdminMetrics(metricsContainer);
                } else {
                    alert(data.message || "Failed to process PDF.");
                }
            } catch (err) {
                progressEl.classList.add("hidden");
                alert("Server upload request failed.");
            }
        });
    }

    // Add Scheme handler
    if (schemeForm) {
        schemeForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const newScheme = {
                name: document.getElementById("s-name").value,
                category: document.getElementById("s-category").value,
                state: document.getElementById("s-state").value,
                department: document.getElementById("s-dept").value,
                description: document.getElementById("s-desc").value,
                minAge: document.getElementById("s-minage").value ? parseInt(document.getElementById("s-minage").value) : null,
                maxAge: document.getElementById("s-maxage").value ? parseInt(document.getElementById("s-maxage").value) : null,
                maxIncome: document.getElementById("s-maxincome").value ? parseFloat(document.getElementById("s-maxincome").value) : null,
                eligibleOccupations: document.getElementById("s-occupations").value || "All",
                eligibleCategories: document.getElementById("s-social").value || "All",
                eligibleGenders: document.getElementById("s-gender").value || "All",
                metadata: "{}"
            };

            try {
                const response = await fetch(`${API_BASE}/admin/schemes`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${state.token}`
                    },
                    body: JSON.stringify(newScheme)
                });

                if (response.ok) {
                    alert("Scheme created successfully!");
                    schemeForm.reset();
                    refreshAdminMetrics(metricsContainer);
                } else {
                    const errData = await response.json();
                    alert(errData.message || "Failed to save scheme.");
                }
            } catch (err) {
                alert("Network request failed.");
            }
        });
    }
}

async function refreshAdminMetrics(container) {
    if (!container) return;
    try {
        const response = await fetch(`${API_BASE}/admin/analytics`, {
            headers: { "Authorization": `Bearer ${state.token}` }
        });
        const data = await response.json();
        
        if (response.ok) {
            const mUsers = document.getElementById("trans-metric-users")?.textContent || "Registered Users";
            const mSchemes = document.getElementById("trans-metric-schemes")?.textContent || "Welfare Schemes";
            const mDocs = document.getElementById("trans-metric-docs")?.textContent || "Indexed PDFs";
            const mChats = document.getElementById("trans-metric-chats")?.textContent || "Queries Handled";

            container.innerHTML = `
                <div class="bg-surface border border-border p-4 rounded-xl text-center">
                    <span class="text-2xl text-primary font-semibold">${data.users}</span>
                    <h5 class="text-xs text-text-muted uppercase tracking-wide mt-1 font-semibold">${mUsers}</h5>
                </div>
                <div class="bg-surface border border-border p-4 rounded-xl text-center">
                    <span class="text-2xl text-primary font-semibold">${data.schemes}</span>
                    <h5 class="text-xs text-text-muted uppercase tracking-wide mt-1 font-semibold">${mSchemes}</h5>
                </div>
                <div class="bg-surface border border-border p-4 rounded-xl text-center">
                    <span class="text-2xl text-primary font-semibold">${data.documents}</span>
                    <h5 class="text-xs text-text-muted uppercase tracking-wide mt-1 font-semibold">${mDocs}</h5>
                </div>
                <div class="bg-surface border border-border p-4 rounded-xl text-center">
                    <span class="text-2xl text-primary font-semibold">${data.chats}</span>
                    <h5 class="text-xs text-text-muted uppercase tracking-wide mt-1 font-semibold">${mChats}</h5>
                </div>
            `;
        }
    } catch (e) {
        container.innerHTML = `<div class="text-red-500 text-xs">Failed to load analytics metrics.</div>`;
    }
}

async function refreshAdminDocuments(container) {
    if (!container) return;
    try {
        const response = await fetch(`${API_BASE}/admin/documents`, {
            headers: { "Authorization": `Bearer ${state.token}` }
        });
        const docs = await response.json();
        
        if (response.ok) {
            container.innerHTML = "";
            if (docs.length === 0) {
                container.innerHTML = `<div class="text-slate-500 text-xs text-center py-4">No PDF documents indexed.</div>`;
            } else {
                const list = document.createElement("ul");
                list.className = "divide-y divide-slate-800";
                
                docs.forEach(doc => {
                    const li = document.createElement("li");
                    li.className = "py-3 flex justify-between items-center text-xs";
                    li.innerHTML = `
                        <div class="flex flex-col gap-0.5">
                            <span class="text-slate-200 font-medium">${doc.fileName}</span>
                            <span class="text-slate-500">Uploaded by ${doc.uploadedBy} on ${new Date(doc.uploadedAt).toLocaleString()}</span>
                        </div>
                        <span class="text-[10px] bg-emerald-950 text-emerald-400 px-2 py-0.5 rounded border border-emerald-800 font-semibold">ACTIVE</span>
                    `;
                    list.appendChild(li);
                });
                container.appendChild(list);
            }
        }
    } catch (e) {
        container.innerHTML = `<div class="text-red-500 text-xs">Failed to load document logs.</div>`;
    }
}

async function refreshAdminFeedback(container) {
    if (!container) return;
    try {
        const response = await fetch(`${API_BASE}/admin/feedback`, {
            headers: { "Authorization": `Bearer ${state.token}` }
        });
        const logs = await response.json();
        
        if (response.ok) {
            container.innerHTML = "";
            if (logs.length === 0) {
                container.innerHTML = `<div class="text-slate-500 text-xs text-center py-4">No feedback logs found.</div>`;
            } else {
                const list = document.createElement("ul");
                list.className = "divide-y divide-slate-800";
                
                logs.forEach(log => {
                    const li = document.createElement("li");
                    li.className = "py-3 text-xs flex flex-col gap-1";
                    
                    const stars = "⭐".repeat(log.rating) + "☆".repeat(5 - log.rating);
                    li.innerHTML = `
                        <div class="flex justify-between items-center font-medium">
                            <span class="text-yellow-500">${stars} <span class="text-slate-400">(${log.rating}/5)</span></span>
                            <span class="text-slate-500">${new Date(log.createdAt).toLocaleString()}</span>
                        </div>
                        <p class="text-slate-300 italic">"${log.comments || 'No comment'}"</p>
                        <span class="text-[10px] text-slate-500 font-semibold">For Chat Entry ID: ${log.chatId}</span>
                    `;
                    list.appendChild(li);
                });
                container.appendChild(list);
            }
        }
    } catch (e) {
        container.innerHTML = `<div class="text-red-500 text-xs">Failed to load feedback logs.</div>`;
    }
}
