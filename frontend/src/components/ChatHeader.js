import React, { useEffect } from 'react';
import './ChatHeader.css';

function ChatHeader({ 
  toggleSidebar, 
  autoRefresh, 
  setAutoRefresh, 
  manualRefresh, 
  autoScroll, 
  setAutoScroll, 
  lastRefreshTime,
  connectionError
}) {
  // Prevent page refresh with beforeunload event
  useEffect(() => {
    const handleBeforeUnload = (e) => {
      // Cancel the event
      e.preventDefault();
      // Chrome requires returnValue to be set
      e.returnValue = '';
      // Display confirmation dialog
      return '';
    };

    // Add event listener
    window.addEventListener('beforeunload', handleBeforeUnload);

    // Clean up event listener
    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, []);

  return (
    <div className="chat-header">
      <button className="toggle-sidebar" onClick={toggleSidebar}>
        ☰
      </button>
      <h1>AgentX Chat</h1>
      <div className="header-controls">
        <button 
          className={`refresh-toggle ${autoRefresh ? 'active' : 'inactive'}`}
          onClick={() => setAutoRefresh(!autoRefresh)}
          title={autoRefresh ? "Auto-refresh is ON" : "Auto-refresh is OFF"}
        >
          {autoRefresh ? "Auto 🔄 ON" : "Auto 🔄 OFF"}
        </button>
        <button 
          className="manual-refresh" 
          onClick={manualRefresh}
          title="Manually refresh conversation"
        >
          🔄 Refresh Now
        </button>
        <button 
          className={`scroll-toggle ${autoScroll ? 'active' : 'inactive'}`}
          onClick={() => setAutoScroll(!autoScroll)}
          title={autoScroll ? "Auto-scroll is ON" : "Auto-scroll is OFF"}
        >
          {autoScroll ? "Auto-scroll ON" : "Auto-scroll OFF"}
        </button>
        <div className="last-refresh">
          Last updated: {new Date(lastRefreshTime).toLocaleTimeString()}
        </div>
      </div>
      {connectionError && (
        <div className="connection-error">
          <span>⚠️ Connection error. Please check if the backend server is running.</span>
        </div>
      )}
    </div>
  );
}

export default ChatHeader;