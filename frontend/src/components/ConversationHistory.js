import React from 'react';
import './ConversationHistory.css';

function ConversationHistory({ 
  conversations, 
  activeConversation, 
  switchConversation, 
  createNewConversation,
  sidebarOpen
}) {
  return (
    <div className={`conversation-sidebar ${sidebarOpen ? 'open' : 'closed'}`}>
      <div className="sidebar-header">
        <h2>Conversations</h2>
      </div>
      <button className="new-chat-button" onClick={createNewConversation}>
        + New Chat
      </button>
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
  );
}

export default ConversationHistory;