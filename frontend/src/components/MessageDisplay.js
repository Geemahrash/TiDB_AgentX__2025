import React, { useRef, useEffect } from 'react';
import './MessageDisplay.css';

function MessageDisplay({ 
  prompts, 
  isLoading, 
  formatMessage, 
  autoScroll,
  handleScroll 
}) {
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    if (autoScroll && messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  };

  useEffect(() => {
    scrollToBottom();
  }, [prompts, autoScroll]);

  return (
    <div className="message-display" onScroll={handleScroll}>
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
    </div>
  );
}

export default MessageDisplay;