import React, { useState } from "react";

function App() {
  const [prompt, setPrompt] = useState("");
  const [response, setResponse] = useState("");

  const handleSubmit = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/save", {
        method: "POST",
        headers: { "Content-Type": "text/plain" },
        body: prompt,
      });
      const text = await res.text();
      setResponse(text);
    } catch (error) {
      setResponse("Error: " + error.message);
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>Enter your prompt:</h2>
      <textarea
        rows="6"
        cols="50"
        value={prompt}
        onChange={(e) => setPrompt(e.target.value)}
      />
      <br />
      <button onClick={handleSubmit}>Submit</button>
      <p>{response}</p>
    </div>
  );
}

export default App;
