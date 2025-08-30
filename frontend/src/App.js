import React, { useState, useEffect } from "react";

function App() {
  const [sessionId, setSessionId] = useState(null);
  const [prompt, setPrompt] = useState("");
  const [prompts, setPrompts] = useState([]);

  useEffect(() => {
    // Generate or reuse sessionId
    let existingSession = localStorage.getItem("sessionId");
    if (!existingSession) {
      existingSession = crypto.randomUUID(); // generate unique id
      localStorage.setItem("sessionId", existingSession);
    }
    setSessionId(existingSession);

    // Fetch existing prompts for this session
    fetch(`http://localhost:8080/api/prompts/${existingSession}`)
      .then((res) => res.json())
      .then((data) => setPrompts(data))
      .catch((err) => console.error(err));
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();

    const newPrompt = {
      sessionId: sessionId,
      prompt: prompt,
    };

    fetch("http://localhost:8080/api/prompt", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newPrompt),
    })
      .then(() =>
        fetch(`http://localhost:8080/api/prompts/${sessionId}`)
          .then((res) => res.json())
          .then((data) => setPrompts(data))
      )
      .catch((err) => console.error(err));

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
      <ul>
        {prompts.map((p, idx) => (
          <li key={idx}>{p}</li>
        ))}
      </ul>
    </div>
  );
}

export default App;
