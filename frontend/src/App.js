import React, { useState, useEffect, useRef } from "react";
import "./App.css";
import ConversationHistory from './components/ConversationHistory';
import MessageDisplay from './components/MessageDisplay';
import MessageInput from './components/MessageInput';
import ChatHeader from './components/ChatHeader';

// Simple markdown-like formatter
function formatMessage(text) {
  if (!text) return "";
  
  // Convert bullet points
  let formatted = text.replace(/\* ([^\n]+)/g, '<li>$1</li>');
  formatted = formatted.replace(/(<li>[^<]+<\/li>)+/g, '<ul>$&</ul>');
  
  // Convert headers
  formatted = formatted.replace(/#{3,6} ([^\n]+)/g, '<h4>$1</h4>');
  formatted = formatted.replace(/## ([^\n]+)/g, '<h3>$1</h3>');
  formatted = formatted.replace(/# ([^\n]+)/g, '<h2>$1</h2>');
  
  // Convert bold text (double asterisks)
  formatted = formatted.replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>');
  
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
  const messagesEndRef = useRef(null);

  // helper to fetch prompt objects safely
  const fetchPromptObjects = (sid) => {
    if (!sid) return;
    setConnectionError(false);
    fetch(`http://localhost:8080/api/promptObjects/${sid}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      mode: 'cors',
      cache: 'no-cache'
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
    // Save current session ID to localStorage
    localStorage.setItem("currentSessionId", sid);
    
    // Update state
    setSessionId(sid);
    setActiveConversation(sid);
    
    // Fetch conversation data
    fetchPromptObjects(sid);
    
    // Reset auto-refresh for the new conversation
    if (autoRefresh) {
      startAutoRefresh(sid);
    }
  };

  // State to track if we should auto-refresh
  const [autoRefresh, setAutoRefresh] = useState(true);
  const [lastRefreshTime, setLastRefreshTime] = useState(Date.now());
  const refreshIntervalRef = useRef(null);

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

    // Set up auto-refresh but with a way to disable it
    startAutoRefresh(existingSession);
    
    return () => {
      if (refreshIntervalRef.current) {
        clearInterval(refreshIntervalRef.current);
      }
    };
  }, []);
  
  // Function to start auto-refresh
  const startAutoRefresh = (sid) => {
    if (refreshIntervalRef.current) {
      clearInterval(refreshIntervalRef.current);
    }
    
    refreshIntervalRef.current = setInterval(() => {
      if (autoRefresh) {
        fetchPromptObjects(sid);
        setLastRefreshTime(Date.now());
      }
    }, 5000); // Changed from 2000ms to 5000ms for less frequent refreshes
  };
  
  // Function to stop auto-refresh
  const stopAutoRefresh = () => {
    setAutoRefresh(false);
    if (refreshIntervalRef.current) {
      clearInterval(refreshIntervalRef.current);
      refreshIntervalRef.current = null;
    }
  };
  
  // Function to manually refresh
  const manualRefresh = () => {
    if (sessionId) {
      fetchPromptObjects(sessionId);
      setLastRefreshTime(Date.now());
    }
  };

  // State to track if auto-scrolling is enabled
  const [autoScroll, setAutoScroll] = useState(true);
  
  // Scroll to bottom of messages when new messages arrive
  const scrollToBottom = () => {
    if (autoScroll) {
      messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }
  };

  useEffect(() => {
    scrollToBottom();
  }, [prompts, autoScroll]);
  
  // Detect manual scrolling to disable auto-scroll
  const handleScroll = (e) => {
    const element = e.target;
    const isScrolledToBottom = Math.abs(element.scrollHeight - element.scrollTop - element.clientHeight) < 50;
    setAutoScroll(isScrolledToBottom);
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
    } else {
      // Update the last message in conversation history
      const updatedConversations = conversations.map(conv => {
        if (conv.id === sessionId) {
          return { ...conv, lastMessage: prompt.trim() };
        }
        return conv;
      });
      setConversations(updatedConversations);
      saveConversations(updatedConversations);
    }
    
    // Ensure auto-refresh is active
    if (!autoRefresh) {
      setAutoRefresh(true);
      startAutoRefresh(sessionId);
    }

    fetch("http://localhost:8080/api/prompt", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
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
      <ConversationHistory 
        conversations={conversations}
        activeConversation={activeConversation}
        switchConversation={switchConversation}
        createNewConversation={createNewConversation}
        sidebarOpen={sidebarOpen}
      />
      
      <div className="chat-container">
        <ChatHeader 
          toggleSidebar={toggleSidebar}
          autoRefresh={autoRefresh}
          setAutoRefresh={setAutoRefresh}
          manualRefresh={manualRefresh}
          autoScroll={autoScroll}
          setAutoScroll={setAutoScroll}
          lastRefreshTime={lastRefreshTime}
          connectionError={connectionError}
        />
        
        <MessageDisplay 
          prompts={prompts}
          isLoading={isLoading}
          formatMessage={formatMessage}
          autoScroll={autoScroll}
          handleScroll={handleScroll}
        />

        <MessageInput 
          onSubmit={handleSubmit}
          isLoading={isLoading}
        />
      </div>
    </div>
  );
}

export default App;
