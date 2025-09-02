import React, { useState, useEffect } from "react";

function App() {
  const [sessionId, setSessionId] = useState(null);
  const [prompt, setPrompt] = useState("");
  const [prompts, setPrompts] = useState([]);

  // helper to fetch prompt objects safely
  const fetchPromptObjects = (sid) => {
    if (!sid) return;
    fetch(`http://localhost:8080/api/promptObjects/${sid}`)
      .then((res) => {
        if (!res.ok) {
          console.error("Failed to fetch prompt objects:", res.status);
          return [];
        }
        return res.json();
      })
      .then((data) => {
        if (Array.isArray(data)) setPrompts(data);
        else setPrompts([]);
      })
      .catch((err) => {
        console.error("Error fetching prompt objects:", err);
        setPrompts([]);
      });
  };

  useEffect(() => {
    // Generate or reuse sessionId
    let existingSession = localStorage.getItem("sessionId");
    if (!existingSession) {
      if (typeof crypto !== "undefined" && crypto.randomUUID) {
        existingSession = crypto.randomUUID();
      } else {
        // fallback simple uuid v4-ish
        existingSession = "s-" + Math.random().toString(36).slice(2, 10);
      }
      localStorage.setItem("sessionId", existingSession);
    }
    setSessionId(existingSession);

    // initial fetch
    fetchPromptObjects(existingSession);

    // auto-refresh every 2 seconds so processed answers appear without manual reload
    const interval = setInterval(() => fetchPromptObjects(existingSession), 2000);
    return () => clearInterval(interval);
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();

    if (!sessionId) {
      console.error("No sessionId available");
      return;
    }
    if (!prompt || !prompt.trim()) return;

    const newPrompt = { sessionId: sessionId, prompt: prompt.trim() };

    fetch("http://localhost:8080/api/prompt", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newPrompt),
    })
      .then((res) => {
        if (!res.ok) {
          console.error("Failed to save prompt:", res.status);
        }
        // refresh objects list after saving
        fetchPromptObjects(sessionId);
      })
      .catch((err) => {
        console.error("Error while saving prompt:", err);
      });

    setPrompt(""); // clear input
  };

  return (
    <div style={{ padding: "20px" }}>
      <h1>AI Prompt Chat</h1>

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={prompt}
          onChange={(e) => setPrompt(e.target.value)}
          placeholder="Enter your prompt"
          style={{ width: "300px", marginRight: "10px" }}
        />
        <button type="submit">Send</button>
      </form>

      <h2>Chat History</h2>

      {Array.isArray(prompts) && prompts.length > 0 ? (
        <ul>
          {prompts.map((obj, idx) => (
            <li key={idx} style={{ marginBottom: "10px", border: "1px solid #ddd", padding: 8 }}>
              <p><strong>Prompt:</strong> {obj.prompt}</p>
              <p><strong>Required Data:</strong> {obj.requiredData || "None"}</p>
              <p><strong>Answer:</strong> {obj.answer || "Not generated yet"}</p>
            </li>
          ))}
        </ul>
      ) : (
        <p>No prompts yet.</p>
      )}
    </div>
  );
}

export default App;
