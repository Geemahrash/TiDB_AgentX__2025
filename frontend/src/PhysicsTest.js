import React, { useState } from 'react';

function PhysicsTest() {
  const [physicsData, setPhysicsData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [connectionStatus, setConnectionStatus] = useState(null);

  const fetchPhysicsData = async () => {
    setLoading(true);
    setError(null);
    try {
      // First check connection
      const verifyResponse = await fetch('http://localhost:8080/api/physics/verify-connection', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        mode: 'cors',
        cache: 'no-cache'
      });
      
      const verifyData = await verifyResponse.json();
      setConnectionStatus(verifyData);
      
      // Then fetch all chapters
      const response = await fetch('http://localhost:8080/api/physics/chapters', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        mode: 'cors',
        cache: 'no-cache'
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      
      const data = await response.json();
      setPhysicsData(data);
    } catch (err) {
      console.error('Error fetching physics data:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto' }}>
      <h2>Physics Data Test</h2>
      <button 
        onClick={fetchPhysicsData} 
        disabled={loading}
        style={{
          padding: '10px 15px',
          backgroundColor: '#4CAF50',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: loading ? 'not-allowed' : 'pointer',
          opacity: loading ? 0.7 : 1
        }}
      >
        {loading ? 'Loading...' : 'Test TiDB Connection'}
      </button>
      
      {connectionStatus && (
        <div style={{ margin: '20px 0', padding: '15px', backgroundColor: connectionStatus.success ? '#e8f5e9' : '#ffebee', borderRadius: '4px' }}>
          <h3>Connection Status:</h3>
          <p><strong>Success:</strong> {connectionStatus.success ? 'Yes' : 'No'}</p>
          <p><strong>Message:</strong> {connectionStatus.message}</p>
          {connectionStatus.totalChapters && (
            <p><strong>Total Chapters:</strong> {connectionStatus.totalChapters}</p>
          )}
          {connectionStatus.sampleChapters && connectionStatus.sampleChapters.length > 0 && (
            <div>
              <h4>Sample Chapters:</h4>
              <ul>
                {connectionStatus.sampleChapters.map(chapter => (
                  <li key={chapter.chapterNo}>
                    <strong>{chapter.chapterNo}. {chapter.title}</strong>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}
      
      {error && (
        <div style={{ margin: '20px 0', padding: '15px', backgroundColor: '#ffebee', borderRadius: '4px' }}>
          <h3>Error:</h3>
          <p>{error}</p>
        </div>
      )}
      
      {physicsData.length > 0 && (
        <div style={{ margin: '20px 0' }}>
          <h3>Physics Chapters:</h3>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                <th style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'left' }}>Chapter No</th>
                <th style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'left' }}>Title</th>
                <th style={{ border: '1px solid #ddd', padding: '8px', textAlign: 'left' }}>Summary</th>
              </tr>
            </thead>
            <tbody>
              {physicsData.map(chapter => (
                <tr key={chapter.chapterNo}>
                  <td style={{ border: '1px solid #ddd', padding: '8px' }}>{chapter.chapterNo}</td>
                  <td style={{ border: '1px solid #ddd', padding: '8px' }}>{chapter.title}</td>
                  <td style={{ border: '1px solid #ddd', padding: '8px' }}>{chapter.summary}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default PhysicsTest;