# DocMind Home Page - Setup & Documentation

## Overview

A beautiful, modern ChatGPT-like home page has been created for your DocMind application. The page features a professional chat interface with conversation history, profile section, and real-time API integration.

## 📁 Files Created

### 1. **home.html** - Main Template
**Location**: `src/main/resources/templates/home.html`

The Thymeleaf template containing the complete page structure:
- Sidebar with conversation list
- Profile/Settings/Logout buttons
- Main chat area with welcome screen
- Message display area
- Input controls with file attachment
- Toast notification container

### 2. **home-style.css** - Styling
**Location**: `src/main/resources/static/css/home-style.css`

Comprehensive CSS (1000+ lines) providing:
- Modern, clean ChatGPT-like design
- Dark mode support (toggle in settings)
- Responsive layout (mobile, tablet, desktop)
- Smooth animations and transitions
- Custom scrollbars
- CSS variables for easy theme customization

### 3. **home-app.js** - Application Logic
**Location**: `src/main/resources/static/js/home-app.js`

Complete JavaScript application (~600 lines) handling:
- State management
- API communication
- Message handling
- File uploads
- Dark mode persistence
- Toast notifications
- Connection monitoring

### 4. **HomePageController.java** - Backend Routes
**Location**: `src/main/java/com/kamran/Docmind/controller/HomePageController.java`

Spring Controller providing:
- `/dashboard` endpoint → serves home.html
- `/chat` endpoint → serves home.html

## 🚀 How to Use

### Access the Page

```
http://localhost:8080/dashboard
# or
http://localhost:8080/chat
```

### Features Walkthrough

#### Welcome Screen
- Shows when starting a new chat
- Quick prompt cards for common actions
- Click any card to auto-fill the message input

#### Sending Messages
1. Type your message in the input field
2. Optionally attach files by clicking the paperclip icon
3. Press Enter or click the send button
4. View the assistant's response in real-time

#### Conversation Management
- **New Chat**: Click "New chat" button to start fresh
- **Conversation History**: Left sidebar shows past conversations (when endpoint is ready)
- **Load Conversation**: Click any conversation to load its history

#### User Controls
- **Profile**: Opens profile menu (expandable feature)
- **Settings**: Toggles dark/light mode
- **Logout**: Signs you out and redirects to login

## 🔧 Configuration & Customization

### Colors & Theming

Edit CSS variables in `home-style.css`:

```css
:root {
    --primary-color: #10a37f;        /* Main brand color */
    --secondary-color: #40414f;      /* Secondary color */
    --background-color: #ffffff;     /* Background */
    --sidebar-bg: #ffffff;           /* Sidebar background */
    --message-user-bg: #10a37f;      /* User message color */
    --message-assistant-bg: #f0f0f0; /* Assistant message color */
    --text-primary: #0d0d0d;         /* Main text */
    --text-secondary: #565869;       /* Secondary text */
}
```

### API Endpoints

The app communicates with these endpoints (defined in `home-app.js`):

```javascript
POST /c/
// Request: userQuery, conversationId, attachments
// Response: { conversationId, response, message, content }

GET /c/{conversationId}
// Returns: Array of messages with structure: 
// { messageType/type, content/message/response }
```

Ensure your controller returns JSON in the expected format.

### Message Display Format

The JavaScript expects messages in one of these formats:

```javascript
// Option 1 - Full format
{
    messageType: "ASSISTANT",
    content: "Your message here"
}

// Option 2 - Flexible format
{
    type: "USER",
    message: "Alternative message field",
    response: "Alternative response field"
}
```

### Modify Styling

#### Font Family
```css
body {
    font-family: 'Your Font', sans-serif;
}
```

#### Button Radius & Spacing
```css
.new-chat-btn {
    border-radius: 12px;  /* Adjust roundness */
    padding: 12px 16px;   /* Adjust spacing */
}
```

#### Dark Mode Colors
```css
body.dark-mode {
    --primary-color: #10a37f;
    --background-color: #1a1a1a;
    --sidebar-bg: #191919;
}
```

## 📱 Responsive Design

The page is fully responsive with breakpoints at:
- **Desktop**: Full layout
- **Tablet (≤768px)**: Sidebar toggles via hamburger menu
- **Mobile (≤480px)**: Optimized touch interface

## 🔌 API Integration

### Sending a Message

The app automatically handles:

```javascript
const formData = new FormData();
formData.append('userQuery', messageText);
formData.append('conversationId', conversationId);
attachments.forEach(file => formData.append('attachment', file));

fetch('/c/', {
    method: 'POST',
    body: formData
})
```

### Loading Conversations

```javascript
fetch(`/c/${conversationId}`)
    .then(r => r.json())
    .then(messages => /* render messages */)
```

## 🎨 Customization Examples

### Change Primary Color to Blue

**home-style.css:**
```css
:root {
    --primary-color: #0066cc;      /* Changed to blue */
    --message-user-bg: #0066cc;
}
```

### Increase Input Area Height

**home-style.css:**
```css
.message-input {
    max-height: 300px;  /* Was 200px */
}
```

### Modify Welcome Message

**home.html:**
```html
<h2>How can I help you today?</h2>
<p class="welcome-subtitle">Your custom subtitle here</p>
```

### Change Message Bubble Styling

**home-style.css:**
```css
.message-bubble {
    border-radius: 20px;  /* More rounded */
    font-size: 15px;      /* Larger text */
    line-height: 1.6;     /* More spacing */
}
```

## 🔒 Security Considerations

1. **File Upload**: Current code uploads any file - add validation
2. **Message Escaping**: HTML is properly escaped (via `.textContent`)
3. **API Endpoints**: Ensure proper authentication on backend
4. **CORS**: Configure if frontend and backend are on different origins

### Add File Validation

**In home-app.js, modify `handleFileSelect()`:**
```javascript
const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
const ALLOWED_TYPES = ['application/pdf', 'text/plain', 'text/markdown'];

function handleFileSelect(event) {
    const files = Array.from(event.target.files).filter(file => {
        if (file.size > MAX_FILE_SIZE) {
            showToast(`File too large: ${file.name}`, 'danger');
            return false;
        }
        if (!ALLOWED_TYPES.includes(file.type)) {
            showToast(`File type not allowed: ${file.name}`, 'danger');
            return false;
        }
        return true;
    });
    appState.attachments.push(...files);
    renderFilePreview();
}
```

## 🐛 Troubleshooting

### Messages not appearing
- Check browser console for errors
- Verify API endpoint returns JSON
- Check CORS headers if backend is on different origin

### Files not uploading
- Check file size limits
- Verify multipart/form-data handling on backend
- Ensure file input accepts correct file types

### Styling looks broken
- Clear browser cache (Ctrl+Shift+Delete)
- Verify CSS file is loaded (check Network tab)
- Check for CSS conflicts with Bootstrap

### Dark mode not persisting
- Check localStorage permissions
- Verify browser privacy settings

## 📊 Performance Tips

1. **Lazy load images** if adding media support
2. **Limit message history** displayed at once
3. **Debounce input** resizing for better performance
4. **Use compression** for large files
5. **Implement pagination** for conversation list

## 🚀 Next Steps

1. **Authentication**: Integrate with your login system
2. **Conversation Persistence**: Create backend endpoint to fetch saved conversations
3. **User Profiles**: Implement profile page
4. **Settings**: Add theme, language, notification settings
5. **Rich Messages**: Support markdown, code highlighting, images
6. **Message Search**: Add search functionality to conversations
7. **Export**: Add ability to export conversations as PDF/text

## 📝 License & Credits

Created as part of the DocMind AI Chat Assistant project.
Uses Bootstrap 5.3 and Font Awesome 6.4 for UI components.

## 📞 Support

For issues or feature requests, refer to the main project documentation.
