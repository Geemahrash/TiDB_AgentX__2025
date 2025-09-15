import React, { useState, useEffect, useRef } from "react";
import "./App.css";
import TidbSearch from "./components/TidbSearch";


// Simple markdown-like formatter
function formatMessage(text) {
  if (!text) return "";
  
  // Convert bullet points
  let formatted = text.replace(/\* ([^\n]+)/g, '<li>$1</li>');
  formatted = formatted.replace(/(<li>[^<]+<\/li>)+/g, '<ul>$&</ul>');
  
  // Convert bold text (double asterisks)
  formatted = formatted.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
  
  // Convert headers
  formatted = formatted.replace(/#{3,6} ([^\n]+)/g, '<h4>$1</h4>');
  formatted = formatted.replace(/## ([^\n]+)/g, '<h3>$1</h3>');
  formatted = formatted.replace(/# ([^\n]+)/g, '<h2>$1</h2>');
  
  // Convert code blocks
  formatted = formatted.replace(/```([^`]+)```/g, '<pre><code>$1</code></pre>');
  
  // Convert inline code
  formatted = formatted.replace(/`([^`]+)`/g, '<code>$1</code>');
  
  // Convert paragraphs
  formatted = formatted.replace(/\n\n/g, '</p><p>');
  
  // Wrap in paragraph tags if not already wrapped
  if (!formatted.startsWith('<')) {
    formatted = '<p>' + formatted + '</p>';
  }
  
  return formatted;
}

function App() {
  const [sessionId, setSessionId] = useState(null);
  const [prompt, setPrompt] = useState("");
  const [prompts, setPrompts] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [connectionError, setConnectionError] = useState(false);
  const [conversations, setConversations] = useState([]);
  const [activeConversation, setActiveConversation] = useState(null);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [activeTab, setActiveTab] = useState('chat'); // 'chat' or 'tidb'
  const messagesEndRef = useRef(null);

  // helper to fetch prompt objects safely
  const fetchPromptObjects = (sid) => {
    if (!sid) return;
    setConnectionError(false);
    console.log("Fetching prompt objects for session:", sid);
    fetch(`http://localhost:8081/api/promptObjects/${sid}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
      },
      mode: "cors",
      credentials: "include"
    })
      .then((res) => {
        if (!res.ok) {
          console.error("Failed to fetch prompt objects:", res.status);
          setConnectionError(true);
          return [];
        }
        setConnectionError(false);
        return res.json();
      })
      .then((data) => {
        console.log("Received data:", data);
        if (Array.isArray(data)) setPrompts(data);
        else setPrompts([]);
      })
      .catch((err) => {
        console.error("Error fetching prompt objects:", err);
        setConnectionError(true);
        setPrompts([]);
      });
  };

  // Load conversation history from localStorage
  const loadConversations = () => {
    const savedConversations = localStorage.getItem("conversations");
    if (savedConversations) {
      return JSON.parse(savedConversations);
    }
    return [];
  };

  // Save conversation history to localStorage
  const saveConversations = (convs) => {
    localStorage.setItem("conversations", JSON.stringify(convs));
  };

  // Create a new conversation
  const createNewConversation = () => {
    let newSessionId;
    if (typeof crypto !== "undefined" && crypto.randomUUID) {
      newSessionId = crypto.randomUUID();
    } else {
      // fallback simple uuid v4-ish
      newSessionId = "s-" + Math.random().toString(36).slice(2, 10);
    }
    
    const newConversation = {
      id: newSessionId,
      title: "New Conversation",
      timestamp: new Date().toISOString(),
      lastMessage: ""
    };
    
    const updatedConversations = [newConversation, ...conversations];
    setConversations(updatedConversations);
    saveConversations(updatedConversations);
    setSessionId(newSessionId);
    setActiveConversation(newSessionId);
    setPrompts([]);
    // Due to this part of the code we are able to run multiple conversations at the same time in different tabs.<exclusive>
    // Update current session ID in localStorage
    localStorage.setItem("currentSessionId", newSessionId);
    
    return newSessionId;
  };

  // Update conversation title based on first message
  const updateConversationTitle = (sid, message) => {
    const updatedConversations = conversations.map(conv => {
      if (conv.id === sid) {
        // Use first 30 characters of first message as title
        const title = message.length > 30 ? message.substring(0, 30) + "..." : message;
        return { ...conv, title, lastMessage: message };
      }
      return conv;
    });
    
    setConversations(updatedConversations);
    saveConversations(updatedConversations);
  };

  // Switch to a different conversation
  const switchConversation = (sid) => {
    setSessionId(sid);
    setActiveConversation(sid);
    fetchPromptObjects(sid);
  };

  useEffect(() => {
    // Load existing conversations
    const loadedConversations = loadConversations();
    setConversations(loadedConversations);
    
    // Generate or reuse sessionId
    let existingSession = localStorage.getItem("currentSessionId");
    if (!existingSession || !loadedConversations.some(conv => conv.id === existingSession)) {
      // Create new conversation if no valid session exists
      existingSession = createNewConversation();
    } else {
      setActiveConversation(existingSession);
    }
    
    setSessionId(existingSession);
    localStorage.setItem("currentSessionId", existingSession);

    // initial fetch
    fetchPromptObjects(existingSession);
  }, []);  // Empty dependency array for initial load only
  
  // Set up auto-refresh for the active conversation
  useEffect(() => {
    if (!activeConversation) return;
    
    // auto-refresh every 2 seconds so processed answers appear without manual reload
    const interval = setInterval(() => {
      fetchPromptObjects(activeConversation);
    }, 2000);
    
    return () => clearInterval(interval);
  }, [activeConversation]);  // Re-run when active conversation changes

  // Scroll to bottom of messages when new messages arrive
  const scrollToBottom = () => {
    // Only auto-scroll if user is already near the bottom
    const chatMessages = document.querySelector('.chat-messages');
    if (chatMessages) {
      const isNearBottom = chatMessages.scrollHeight - chatMessages.scrollTop - chatMessages.clientHeight < 100;
      if (isNearBottom) {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
      }
    }
  };

  useEffect(() => {
    scrollToBottom();
  }, [prompts]);
  
  // Add a manual scroll button
  const scrollToBottomManually = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!sessionId) {
      console.error("No sessionId available");
      return;
    }
    if (!prompt || !prompt.trim()) return;

    const newPrompt = { sessionId: sessionId, prompt: prompt.trim() };
    setIsLoading(true);
    setConnectionError(false);

    // Add the user message immediately for better UX
    const userMessage = { sessionId: sessionId, prompt: prompt.trim(), answer: null };
    setPrompts(prev => [...prev, userMessage]);
    setPrompt(""); // clear input immediately for better UX

    // Update conversation title if this is the first message
    if (prompts.length === 0) {
      updateConversationTitle(sessionId, prompt.trim());
    }

    fetch("http://localhost:8081/api/prompt", {
      method: "POST",
      headers: { 
        "Content-Type": "application/json",
        "Accept": "application/json"
      },
      mode: "cors",
      credentials: "include",
      body: JSON.stringify(newPrompt),
    })
      .then((res) => {
        if (!res.ok) {
          console.error("Failed to save prompt:", res.status);
          setConnectionError(true);
        } else {
          setConnectionError(false);
        }
        // refresh objects list after saving
        fetchPromptObjects(sessionId);
        setIsLoading(false);
      })
      .catch((err) => {
        console.error("Error while saving prompt:", err);
        setConnectionError(true);
        setIsLoading(false);
      });
  };

  // Toggle sidebar visibility
  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  return (
    <div className="app-container">
      <div className={`sidebar ${sidebarOpen ? 'open' : 'closed'}`}>
        <div className="sidebar-header">
          <h2>Conversations</h2>
          <button className="new-chat-button" onClick={createNewConversation}>
            + New Chat
          </button>
        </div>
        <div className="conversation-list">
          {conversations.map((conv) => (
            <div 
              key={conv.id} 
              className={`conversation-item ${conv.id === activeConversation ? 'active' : ''}`}
              onClick={() => switchConversation(conv.id)}
            >
              <div className="conversation-title">{conv.title}</div>
              <div className="conversation-timestamp">
                {new Date(conv.timestamp).toLocaleDateString()}
              </div>
            </div>
          ))}
        </div>
      </div>
      
      <div className="chat-container">
        <div className="chat-header">
          <button className="toggle-sidebar" onClick={toggleSidebar}>
            ☰
          </button>
          <h1>AI Assistant</h1>
          <div className="tab-navigation">
            <button 
              className={`tab-button ${activeTab === 'chat' ? 'active' : ''}`}
              onClick={() => setActiveTab('chat')}
            >
              Chat
            </button>
            <button 
              className={`tab-button ${activeTab === 'tidb' ? 'active' : ''}`}
              onClick={() => setActiveTab('tidb')}
            >
              TiDB Search
            </button>
          </div>
          {connectionError && (
            <div className="connection-error">
              <span>⚠️ Connection error. Please check if the backend server is running.</span>
            </div>
          )}
        </div>
        
        {activeTab === 'chat' ? (
          <>
            <div className="chat-messages">
              {Array.isArray(prompts) && prompts.length > 0 ? (
                prompts.map((obj, idx) => (
                  <React.Fragment key={idx}>
                    <div className="message user-message">
                      <div className="message-content">{obj.prompt}</div>
                    </div>
                    {obj.answer !== undefined && (
                      <div className="message assistant-message">
                        <div className="message-content">
                          {obj.answer ? (
                            <div dangerouslySetInnerHTML={{ __html: formatMessage(obj.answer) }} />
                          ) : (
                            <div className="loading-container">
                              {isLoading && idx === prompts.length - 1 ? (
                                <div className="typing-indicator">
                                  <span></span>
                                  <span></span>
                                  <span></span>
                                </div>
                              ) : (
                                <em>Waiting for response...</em>
                              )}
                            </div>
                          )}
                        </div>
                      </div>
                    )}
                  </React.Fragment>
                ))
              ) : (
                <div className="empty-state">
                  <p>No conversation yet. Send a message to start chatting!</p>
                </div>
              )}
              <div ref={messagesEndRef} />
              {prompts.length > 3 && (
                <button 
                  className="scroll-bottom-button" 
                  onClick={scrollToBottomManually}
                  title="Scroll to bottom"
                >
                  ↓
                </button>
              )}
            </div>

            <div className="chat-input-container">
              <form onSubmit={handleSubmit} className="chat-form">
                <input
                  type="text"
                  value={prompt}
                  onChange={(e) => setPrompt(e.target.value)}
                  placeholder="Send a message..."
                  className="chat-input"
                  disabled={isLoading}
                />
                <button 
                  type="submit" 
                  className="send-button"
                  disabled={isLoading || !prompt.trim()}
                >
                  {isLoading ? "Sending..." : "Send"}
                </button>
              </form>
            </div>
          </>
        ) : (
          <TidbSearch sessionId={sessionId} />
        )}
      </div>
    </div>
  );
}

export default App;
