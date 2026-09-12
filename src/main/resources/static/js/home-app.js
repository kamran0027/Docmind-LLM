/* ==================== State Management ==================== */
const appState = {
    conversationId: null,
    attachments: [],
    isLoading: false,
    messages: [],
    isDarkMode: localStorage.getItem('docmind-dark-mode') === 'true'
};

/* ==================== DOM Elements ==================== */
const elements = {
    newChatBtn: document.getElementById('newChatBtn'),
    attachBtn: document.getElementById('attachBtn'),
    fileInput: document.getElementById('fileInput'),
    sendBtn: document.getElementById('sendBtn'),
    messageInput: document.getElementById('messageInput'),
    messagesList: document.getElementById('messagesList'),
    messagesArea: document.getElementById('messagesArea'),
    welcomeScreen: document.getElementById('welcomeScreen'),
    conversationsList: document.getElementById('conversationsList'),
    filePreview: document.getElementById('filePreview'),
    toastContainer: document.getElementById('toastContainer'),
    statusDot: document.getElementById('statusIndicator'),
    statusText: document.getElementById('statusText'),
    profileBtn: document.getElementById('profileBtn'),
    settingsBtn: document.getElementById('settingsBtn'),
    logoutBtn: document.getElementById('logoutBtn'),
    chatTitle: document.getElementById('chatTitle')
};

/* ==================== Initialize App ==================== */
document.addEventListener('DOMContentLoaded', () => {
    console.log('App initializing...');
    
    // Apply saved theme
    if (appState.isDarkMode) {
        document.body.classList.add('dark-mode');
    }
    
    // Setup event listeners
    setupEventListeners();
    
    // Load conversations
    loadConversations();
    
    // Check API connection
    checkConnection();
    
    // Auto resize textarea
    autoResizeInput();
});

/* ==================== Event Listeners ==================== */
function setupEventListeners() {
    // Chat actions
    elements.newChatBtn.addEventListener('click', startNewChat);
    elements.attachBtn.addEventListener('click', () => elements.fileInput.click());
    elements.fileInput.addEventListener('change', handleFileSelect);
    elements.sendBtn.addEventListener('click', sendMessage);
    elements.messageInput.addEventListener('keydown', handleInputKeydown);
    elements.messageInput.addEventListener('input', autoResizeInput);
    
    // Quick prompts
    document.querySelectorAll('.prompt-card').forEach(card => {
        card.addEventListener('click', () => {
            elements.messageInput.value = card.dataset.prompt;
            elements.messageInput.focus();
            autoResizeInput();
        });
    });
    
    // Profile buttons
    elements.profileBtn.addEventListener('click', () => showToast('Profile feature coming soon!', 'info'));
    elements.settingsBtn.addEventListener('click', () => {
        appState.isDarkMode = !appState.isDarkMode;
        document.body.classList.toggle('dark-mode');
        localStorage.setItem('docmind-dark-mode', appState.isDarkMode);
        showToast(`${appState.isDarkMode ? 'Dark' : 'Light'} mode enabled`, 'info');
    });
    elements.logoutBtn.addEventListener('click', () => {
        if (confirm('Are you sure you want to logout?')) {
            // Use form submission to properly trigger Spring Security logout
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '/logout';
            document.body.appendChild(form);
            form.submit();
        }
    });
}

/* ==================== File Handling ==================== */
function handleFileSelect(event) {
    const files = Array.from(event.target.files);
    appState.attachments.push(...files);
    renderFilePreview();
}

function renderFilePreview() {
    if (appState.attachments.length === 0) {
        elements.filePreview.classList.add('d-none');
        elements.filePreview.innerHTML = '';
        return;
    }
    
    elements.filePreview.classList.remove('d-none');
    elements.filePreview.innerHTML = appState.attachments
        .map((file, idx) => `
            <div class="file-chip">
                <i class="fas fa-file"></i>
                <span>${truncateFileName(file.name, 20)}</span>
                <button type="button" data-idx="${idx}">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        `)
        .join('');
    
    // Remove file handlers
    elements.filePreview.querySelectorAll('button').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const idx = parseInt(btn.dataset.idx);
            appState.attachments.splice(idx, 1);
            renderFilePreview();
        });
    });
}

function truncateFileName(name, length) {
    return name.length > length ? name.substring(0, length) + '...' : name;
}

/* ==================== Message Handling ==================== */
function appendMessage(type, content, timestamp = new Date()) {
    const messageEl = document.createElement('div');
    messageEl.className = `message ${type.toLowerCase()}`;
    
    const avatar = document.createElement('div');
    avatar.className = 'message-avatar';
    avatar.innerHTML = type.toUpperCase() === 'USER' 
        ? '<i class="fas fa-user"></i>' 
        : '<i class="fas fa-robot"></i>';
    
    const bubble = document.createElement('div');
    bubble.className = 'message-bubble';
    bubble.textContent = content;
    
    messageEl.appendChild(avatar);
    messageEl.appendChild(bubble);
    
    elements.messagesList.appendChild(messageEl);
    scrollToBottom();
    
    // Store message
    appState.messages.push({ type, content, timestamp });
}

function showTypingIndicator() {
    const messageEl = document.createElement('div');
    messageEl.id = 'typingIndicator';
    messageEl.className = 'message assistant';
    messageEl.innerHTML = `
        <div class="message-avatar">
            <i class="fas fa-robot"></i>
        </div>
        <div class="message-bubble">
            <div class="typing-indicator">
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
            </div>
        </div>
    `;
    elements.messagesList.appendChild(messageEl);
    scrollToBottom();
}

function removeTypingIndicator() {
    const typingEl = document.getElementById('typingIndicator');
    if (typingEl) typingEl.remove();
}

function scrollToBottom() {
    elements.messagesArea.scrollTop = elements.messagesArea.scrollHeight;
}

/* ==================== Message Sending ==================== */
// async function sendMessage() {
//     const message = elements.messageInput.value.trim();
    
//     if (!message && appState.attachments.length === 0) {
//         showToast('Please enter a message or attach a file', 'warning');
//         return;
//     }
    
//     if (appState.isLoading) return;
    
//     // Show user message
//     appendMessage('USER', message || '(File attached)');
    
//     // Clear input
//     elements.messageInput.value = '';
//     autoResizeInput();
    
//     // Show loading state
//     setBusy(true);
//     showTypingIndicator();
    
//     try {
//         // Show messages area
//         showMessagesArea();
        
//         // Prepare request
//         const formData = new FormData();
//         formData.append('userQuery', message || '');
        
//         // Only add conversationId if it exists (not for first message)
//         if (appState.conversationId) {
//             formData.append('conversationId', appState.conversationId);
//         }
        
//         // Add attachments - CRITICAL: Properly append all files
//         console.log('Total attachments to send:', appState.attachments.length);
//         appState.attachments.forEach((file, index) => {
//             console.log(`Appending file ${index}:`, file.name, 'Size:', file.size, 'Type:', file.type);
//             formData.append('attachment', file);
//         });
        
//         // Log FormData contents for debugging
//         console.log('=== FormData Contents ===');
//         for (let [key, value] of formData.entries()) {
//             if (value instanceof File) {
//                 console.log(`  ${key}: File(${value.name}, ${value.size} bytes, ${value.type})`);
//             } else {
//                 console.log(`  ${key}: ${value}`);
//             }
//         }
//         console.log('========================');
        
//         console.log('Sending request to /c/ endpoint with:');
//         console.log('  ConversationId:', appState.conversationId);
//         console.log('  Message:', message);
//         console.log('  Attachments count:', appState.attachments.length);
        
//         // Send request - DO NOT set Content-Type, browser will set it with proper boundary
//         const response = await fetch('/c/', {
//             method: 'POST',
//             body: formData
//         });
        
//         if (!response.ok) {
//             const errorText = await response.text();
//             console.error('Server error:', errorText);
//             throw new Error(errorText || `HTTP ${response.status}`);
//         }
        
//         const data = await response.json();
//         console.log('Response received:', data);
        
//         // Remove typing indicator
//         removeTypingIndicator();
        
//         // Show response
//         const assistantMessage = data.response || data.message || data.content || 'No response received';
//         appendMessage('ASSISTANT', assistantMessage);
        
//         // Update conversation ID from response (critical for first message)
//         if (data.conversationId) {
//             console.log('Updated conversationId to:', data.conversationId);
//             appState.conversationId = data.conversationId;
//         }
        
//         // Clear attachments
//         appState.attachments = [];
//         elements.fileInput.value = '';
//         renderFilePreview();
        
//         // Reload conversations
//         await loadConversations();
        
//     } catch (error) {
//         console.error('Error:', error);
//         removeTypingIndicator();
        
//         const errorMsg = error.message || 'An error occurred. Please check your connection and try again.';
//         appendMessage('ASSISTANT', `Sorry, I encountered an error: ${errorMsg}`);
//         showToast(errorMsg, 'danger');
//     } finally {
//         setBusy(false);
//     }
// }
async function sendMessage() {

    const message =
        elements.messageInput.value.trim();


    /*
     * Validate input
     */
    if (!message && appState.attachments.length === 0) {

        showToast(
            'Please enter a message or attach a file',
            'warning'
        );

        return;
    }


    /*
     * Prevent multiple simultaneous requests
     */
    if (appState.isLoading) {
        return;
    }


    /*
     * Show message area
     */
    showMessagesArea();


    /*
     * Display user message
     */
    appendMessage(
        'USER',
        message || '📎 Document uploaded'
    );


    /*
     * Clear input
     */
    elements.messageInput.value = '';

    autoResizeInput();


    /*
     * Set loading state
     */
    setBusy(true);

    showTypingIndicator();


    try {

        /*
         * Create multipart request
         */
        const formData = new FormData();


        /*
         * User query
         */
        formData.append(
            'userQuery',
            message
        );


        /*
         * Conversation ID
         */
        if (appState.conversationId) {

            formData.append(
                'conversationId',
                appState.conversationId
            );
        }


        /*
         * Attach all selected files
         */
        appState.attachments.forEach(file => {

            formData.append(
                'attachment',
                file
            );
        });


        /*
         * CSRF token
         */
        const csrfToken =
            document
                .querySelector(
                    'meta[name="_csrf"]'
                )
                ?.getAttribute('content');


        const csrfHeader =
            document
                .querySelector(
                    'meta[name="_csrf_header"]'
                )
                ?.getAttribute('content');


        /*
         * Request headers
         */
        const headers = {};

        if (csrfToken && csrfHeader) {

            headers[csrfHeader] =
                csrfToken;
        }


        console.log(
            'Sending message to server...'
        );


        /*
         * API request
         */
        const response =
            await fetch('/c/', {

                method: 'POST',

                headers: headers,

                body: formData

            });


        /*
         * Handle errors
         */
        if (!response.ok) {

            const errorText =
                await response.text();

            throw new Error(
                errorText ||
                `Server error: ${response.status}`
            );
        }


        /*
         * Parse response
         */
        const data =
            await response.json();


        console.log(
            'Server response:',
            data
        );


        /*
         * Remove typing animation
         */
        removeTypingIndicator();


        /*
         * Extract response
         *
         * Adjust according to MessageDto
         */
        const assistantMessage =

            data.content || 'No response received';


        /*
         * Display AI response
         */
        appendMessage(
            'ASSISTANT',
            assistantMessage
        );


        /*
         * Store conversation ID
         */
        if (data.conversationId) {

            appState.conversationId =
                data.conversationId;

            console.log(
                'Conversation ID updated:',
                appState.conversationId
            );
        }


        /*
         * Clear attachments
         */
        appState.attachments = [];

        elements.fileInput.value = '';

        renderFilePreview();


        /*
         * Reload sidebar
         */
        await loadConversations();


    } catch (error) {

        console.error(
            'Chat request failed:',
            error
        );


        removeTypingIndicator();


        appendMessage(
            'ASSISTANT',
            'Sorry, something went wrong while processing your request.'
        );


        showToast(
            error.message ||
            'Failed to communicate with server',
            'danger'
        );


    } finally {

        setBusy(false);
    }
}

function handleInputKeydown(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        sendMessage();
    }
}

function autoResizeInput() {
    elements.messageInput.style.height = 'auto';
    const maxHeight = 200;
    const scrollHeight = elements.messageInput.scrollHeight;
    elements.messageInput.style.height = Math.min(scrollHeight, maxHeight) + 'px';
}

function setBusy(busy) {
    appState.isLoading = busy;
    elements.sendBtn.disabled = busy;
    elements.attachBtn.disabled = busy;
    elements.messageInput.disabled = busy;
}

/* ==================== Chat Management ==================== */
function startNewChat() {
    appState.conversationId = null;
    appState.attachments = [];
    appState.messages = [];
    
    elements.messageInput.value = '';
    elements.fileInput.value = '';
    autoResizeInput();
    renderFilePreview();
    
    elements.messagesArea.classList.add('d-none');
    elements.welcomeScreen.classList.remove('d-none');
    elements.messagesList.innerHTML = '';
    
    // Deselect all conversations
    document.querySelectorAll('.conversation-item').forEach(item => {
        item.classList.remove('active');
    });
    
    elements.messageInput.focus();
}

function showMessagesArea() {
    elements.welcomeScreen.classList.add('d-none');
    elements.messagesArea.classList.remove('d-none');
}

/* ==================== Conversations Management ==================== */
// async function loadConversations() {
//     try {
//         console.log('Loading conversations...');
        
//         // Fetch conversations from /chat endpoint
//         const response = await fetch('/chat', {
//             method: 'GET',
//             headers: {
//                 'Content-Type': 'application/json'
//             }
//         });
        
//         if (!response.ok) {
//             throw new Error(`Failed to load conversations: ${response.status}`);
//         }
        
//         const conversations = await response.json();
//         console.log('Conversations loaded:', conversations);
        
//         // If no conversations
//         if (!Array.isArray(conversations) || conversations.length === 0) {
//             elements.conversationsList.innerHTML = `
//                 <div class="text-secondary small p-2">
//                     <i class="fas fa-inbox"></i> No conversations yet
//                 </div>
//             `;
//             return;
//         }
        
//         // Render conversations
//         elements.conversationsList.innerHTML = '';
//         conversations.forEach(conv => {
//             const convItem = document.createElement('button');
//             convItem.className = 'conversation-item';
//             convItem.dataset.id = conv.id;
            
//             // Use title or create one from timestamp
//             const title = conv.title || new Date(conv.createdAt).toLocaleString();
//             const tempBadge = conv.temporary ? ' <span class="badge bg-warning text-dark ms-1" style="font-size: 10px;">temp</span>' : '';
            
//             convItem.innerHTML = `
//                 <i class="fas fa-comment-dots me-2"></i>
//                 <span title="${escapeHtml(title)}">${escapeHtml(title)}</span>
//                 ${tempBadge}
//             `;
            
//             convItem.addEventListener('click', () => openConversation(conv.id));
//             elements.conversationsList.appendChild(convItem);
//         });
        
//     } catch (error) {
//         console.error('Error loading conversations:', error);
//         elements.conversationsList.innerHTML = `
//             <div class="text-danger small p-2">
//                 <i class="fas fa-exclamation-triangle"></i> Error loading conversations
//             </div>
//         `;
//         showToast(`Error loading conversations: ${error.message}`, 'danger');
//     }
// }
async function loadConversations() {

    try {

        const response =
            await fetch('/chat', {
                method: 'GET'
            });


        if (!response.ok) {

            throw new Error(
                `Failed to load conversations: ${response.status}`
            );
        }


        const conversations =
            await response.json();


        console.log(
            'Loaded conversations:',
            conversations
        );


        elements.conversationsList.innerHTML = '';


        /*
         * No conversations
         */

        if (
            !Array.isArray(conversations)
            || conversations.length === 0
        ) {

            elements.conversationsList.innerHTML = `

                <div class="empty-conversations">

                    <i class="fas fa-message"></i>

                    <span>
                        No conversations yet
                    </span>

                </div>

            `;

            return;
        }


        /*
         * Render conversations
         */

        conversations.forEach(conv => {

            const conversationWrapper =
                document.createElement('div');

            conversationWrapper.className =
                'conversation-wrapper';


            const conversationItem =
                document.createElement('button');

            conversationItem.type = 'button';

            conversationItem.className =
                'conversation-item';

            conversationItem.dataset.id =
                conv.id;


            const title =
                conv.title || 'New Conversation';


            conversationItem.innerHTML = `

                <div class="conversation-content">

                    <i class="fas fa-comment-dots"></i>

                    <span class="conversation-title">
                        ${escapeHtml(title)}
                    </span>

                </div>

            `;


            /*
             * Open conversation
             */

            conversationItem.addEventListener(
                'click',
                () => openConversation(conv.id)
            );


            /*
             * Action container
             */

            const actions =
                document.createElement('div');

            actions.className =
                'conversation-actions';


            /*
             * Three dot button
             */

            const menuButton =
                document.createElement('button');

            menuButton.type = 'button';

            menuButton.className =
                'conversation-menu-btn';

            menuButton.title =
                'Conversation options';

            menuButton.innerHTML =
                '<i class="fas fa-ellipsis-vertical"></i>';


            /*
             * Dropdown
             */

            const dropdown =
                document.createElement('div');

            dropdown.className =
                'conversation-dropdown';


            /*
             * Rename
             */

            const renameButton =
                document.createElement('button');

            renameButton.type = 'button';

            renameButton.className =
                'conversation-dropdown-item';

            renameButton.innerHTML = `
                <i class="fas fa-pen"></i>
                <span>Rename</span>
            `;


            /*
             * Delete
             */

            const deleteButton =
                document.createElement('button');

            deleteButton.type = 'button';

            deleteButton.className =
                'conversation-dropdown-item delete-option';

            deleteButton.innerHTML = `
                <i class="fas fa-trash"></i>
                <span>Delete</span>
            `;


            dropdown.appendChild(renameButton);

            dropdown.appendChild(deleteButton);


            actions.appendChild(menuButton);

            actions.appendChild(dropdown);


            conversationWrapper.appendChild(
                conversationItem
            );

            conversationWrapper.appendChild(
                actions
            );


            /*
             * Toggle menu
             */

            menuButton.addEventListener(
                'click',
                (event) => {

                    event.stopPropagation();


                    document
                        .querySelectorAll(
                            '.conversation-dropdown.show'
                        )
                        .forEach(menu => {

                            if (menu !== dropdown) {

                                menu.classList.remove('show');

                            }

                        });


                    dropdown.classList.toggle('show');

                }
            );


            /*
             * Rename
             */

            renameButton.addEventListener(
                'click',
                (event) => {

                    event.stopPropagation();

                    dropdown.classList.remove('show');

                    renameConversation(
                        conv.id,
                        title
                    );

                }
            );


            /*
             * Delete
             */

            deleteButton.addEventListener(
                'click',
                (event) => {

                    event.stopPropagation();

                    dropdown.classList.remove('show');

                    deleteConversation(
                        conv.id,
                        title
                    );

                }
            );


            elements.conversationsList.appendChild(
                conversationWrapper
            );

        });


    } catch (error) {

        console.error(
            'Error loading conversations:',
            error
        );


        elements.conversationsList.innerHTML = `

            <div class="error-conversations">

                <i class="fas fa-triangle-exclamation"></i>

                <span>
                    Failed to load conversations
                </span>

            </div>

        `;

    }
}

async function renameConversation(
    conversationId,
    currentTitle
) {

    const newTitle =
        prompt(
            'Enter new conversation name:',
            currentTitle
        );


    if (!newTitle) {
        return;
    }


    const trimmedTitle =
        newTitle.trim();


    if (!trimmedTitle) {

        showToast(
            'Conversation name cannot be empty',
            'warning'
        );

        return;
    }


    try {

        const response =
            await fetch(
                `/c/${conversationId}/rename`,
                {

                    method: 'PUT',

                    headers: {
                        'Content-Type':
                            'application/json'
                    },

                    body: JSON.stringify({
                        title: trimmedTitle
                    })

                }
            );


        if (!response.ok) {

            throw new Error(
                'Failed to rename conversation'
            );
        }


        await loadConversations();


        showToast(
            'Conversation renamed successfully',
            'success'
        );


    } catch (error) {

        console.error(
            'Rename error:',
            error
        );


        showToast(
            'Failed to rename conversation',
            'danger'
        );

    }
}

async function deleteConversation(
    conversationId,
    conversationTitle
) {

    const confirmed =
        confirm(
            `Delete "${conversationTitle}"?\n\nThis conversation will be removed from your chat history.`
        );


    if (!confirmed) {
        return;
    }


    try {

        const response =
            await fetch(
                `/c/${conversationId}`,
                {

                    method: 'DELETE'

                }
            );


        if (!response.ok) {

            throw new Error(
                'Failed to delete conversation'
            );
        }


        /*
         * If currently open conversation
         * was deleted
         */

        if (
            appState.conversationId ===
            conversationId
        ) {

            startNewChat();

        }


        /*
         * Refresh sidebar
         */

        await loadConversations();


        showToast(
            'Conversation deleted successfully',
            'success'
        );


    } catch (error) {

        console.error(
            'Delete conversation error:',
            error
        );


        showToast(
            'Failed to delete conversation',
            'danger'
        );

    }
}

async function openConversation(id) {
    appState.conversationId = id;
    appState.messages = [];
    
    showMessagesArea();
    elements.messagesList.innerHTML = '<div class="text-secondary small p-2">Loading conversation...</div>';
    
    // Update active state
    document.querySelectorAll('.conversation-item').forEach(item => {
        item.classList.toggle('active', item.dataset.id === id);
    });
    
    try {
        const response = await fetch(`/c/${encodeURIComponent(id)}`);
        
        if (!response.ok) throw new Error('Failed to load conversation');
        
        const messages = await response.json();
        elements.messagesList.innerHTML = '';
        
        messages.forEach(msg => {
            const type = msg.messageType || msg.type || 'ASSISTANT';
            const content = msg.content || msg.message || msg.response || '';
            appendMessage(type, content);
        });
        
        scrollToBottom();
    } catch (error) {
        console.error('Error:', error);
        showToast('Failed to load conversation', 'danger');
    }
}

/* ==================== Connection Check ==================== */
// async function checkConnection() {
//     try {
//         const response = await fetch('/', { method: 'HEAD', timeout: 5000 });
        
//         if (response.ok) {
//             elements.statusDot.classList.add('connected');
//             elements.statusText.textContent = 'Connected';
//         } else {
//             throw new Error('Not connected');
//         }
//     } catch (error) {
//         console.error('Connection error:', error);
//         elements.statusDot.classList.remove('connected');
//         elements.statusText.textContent = 'Disconnected';
//         showToast('Connection error. Please check your server.', 'danger');
//     }
// }
async function checkConnection() {

    try {

        const controller =
            new AbortController();


        const timeoutId =
            setTimeout(() => {

                controller.abort();

            }, 5000);


        const response =
            await fetch('/chat', {

                method: 'GET',

                signal: controller.signal

            });


        clearTimeout(timeoutId);


        if (!response.ok) {

            throw new Error(
                'Server is not responding'
            );
        }


        elements.statusDot
            .classList
            .add('connected');


        elements.statusText.textContent =
            'Connected';


    } catch (error) {

        console.error(
            'Connection error:',
            error
        );


        elements.statusDot
            .classList
            .remove('connected');


        elements.statusText.textContent =
            'Disconnected';
    }
}

/* ==================== Utility Functions ==================== */
function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

function showToast(message, type = 'info') {
    const toastEl = document.createElement('div');
    toastEl.className = `toast align-items-center text-bg-${type} border-0`;
    toastEl.setAttribute('role', 'alert');
    
    const bgClass = {
        'info': 'primary',
        'success': 'success',
        'warning': 'warning',
        'danger': 'danger'
    }[type] || 'info';
    
    toastEl.className = `toast align-items-center text-bg-${bgClass} border-0`;
    toastEl.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${escapeHtml(message)}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;
    
    elements.toastContainer.appendChild(toastEl);
    
    const bsToast = new bootstrap.Toast(toastEl, { delay: 3500 });
    bsToast.show();
    
    // Remove from DOM after hide
    toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/* ==================== Advanced Features ==================== */
// Auto-connect status checker
setInterval(() => {
    // You can add periodic connection checks here
}, 30000);

// Prevent accidental page close with unsent messages
window.addEventListener('beforeunload', (e) => {
    if (elements.messageInput.value.trim() || appState.attachments.length > 0) {
        e.preventDefault();
        e.returnValue = '';
    }
});


document.addEventListener(
    'click',
    () => {

        document
            .querySelectorAll(
                '.conversation-dropdown.show'
            )
            .forEach(menu => {

                menu.classList.remove('show');

            });

    }
);

console.log('DocMind Chat App Loaded Successfully!');

