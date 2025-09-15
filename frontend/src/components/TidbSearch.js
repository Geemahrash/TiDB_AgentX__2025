import React, { useState } from 'react';
import './TidbSearch.css';

function TidbSearch({ sessionId }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState(null);
  const [isSearching, setIsSearching] = useState(false);
  const [error, setError] = useState(null);

  const handleSearch = async () => {
    if (!searchQuery.trim()) return;
    
    setIsSearching(true);
    setError(null);
    
    try {
      // Send the search query to the backend
      const response = await fetch(`/api/prompt`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          sessionId: sessionId,
          prompt: searchQuery
        }),
      });
      
      if (!response.ok) {
        throw new Error(`Search failed with status: ${response.status}`);
      }
      
      // After sending the query, poll for results
      pollForResults(sessionId);
    } catch (err) {
      setError(`Error: ${err.message}`);
      setIsSearching(false);
    }
  };
  
  const pollForResults = async (sid) => {
    try {
      const response = await fetch(`/api/promptObjects/${sid}`);
      if (!response.ok) {
        throw new Error(`Failed to fetch results: ${response.status}`);
      }
      
      const data = await response.json();
      
      // Find the prompt object that matches our search query
      const resultObj = data.find(obj => obj.prompt === searchQuery);
      
      if (resultObj && resultObj.answer && resultObj.answer !== '') {
        // We have a result
        setSearchResults(resultObj.answer);
        setIsSearching(false);
      } else {
        // No result yet, poll again after a delay
        setTimeout(() => pollForResults(sid), 1000);
      }
    } catch (err) {
      setError(`Error polling for results: ${err.message}`);
      setIsSearching(false);
    }
  };

  const formatResults = (results) => {
    // Split the results by double newlines to separate entries
    const sections = results.split('\n\n');
    
    return sections.map((section, index) => {
      // For each section, split by newlines to get individual lines
      const lines = section.split('\n');
      
      return (
        <div key={index} className="search-result-item">
          {lines.map((line, lineIndex) => {
            // Check if the line starts with "Question:", "Answer:", or "Relevance:"
            if (line.startsWith('Question:')) {
              return <h4 key={lineIndex}>{line}</h4>;
            } else if (line.startsWith('Answer:')) {
              return <p key={lineIndex} className="answer-text">{line}</p>;
            } else if (line.startsWith('Relevance:')) {
              return <p key={lineIndex} className="relevance-score">{line}</p>;
            } else if (line.startsWith('Found')) {
              return <p key={lineIndex} className="result-count">{line}</p>;
            } else {
              return <p key={lineIndex}>{line}</p>;
            }
          })}
        </div>
      );
    });
  };

  return (
    <div className="tidb-search-container">
      <h3>TiDB Search</h3>
      <p className="search-description">
        Search our knowledge base using TiDB's B+ tree indexing for fast and accurate results.
      </p>
      
      <div className="search-input-container">
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Enter your search query..."
          className="search-input"
          onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
        />
        <button 
          onClick={handleSearch} 
          className="search-button"
          disabled={isSearching}
        >
          {isSearching ? 'Searching...' : 'Search'}
        </button>
      </div>
      
      {error && <div className="search-error">{error}</div>}
      
      <div className="search-results">
        {isSearching ? (
          <div className="searching-indicator">
            <div className="spinner"></div>
            <p>Searching TiDB database...</p>
          </div>
        ) : searchResults ? (
          <div className="results-container">
            {formatResults(searchResults)}
          </div>
        ) : null}
      </div>
    </div>
  );
}

export default TidbSearch;