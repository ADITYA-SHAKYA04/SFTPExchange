import React, { useState } from 'react';

export default function CommentSection({ postId }) {
  const [comments, setComments] = useState([]);
  const [input, setInput] = useState('');

  const handleAdd = () => {
    if (input.trim()) {
      setComments([...comments, { text: input, date: new Date().toLocaleString() }]);
      setInput('');
    }
  };

  return (
    <div className="comment-section">
      <h4>Comments</h4>
      <ul>
        {comments.map((c, i) => (
          <li key={i}><b>{c.date}:</b> {c.text}</li>
        ))}
      </ul>
      <input
        value={input}
        onChange={e => setInput(e.target.value)}
        placeholder="Add a comment..."
      />
      <button onClick={handleAdd}>Post</button>
    </div>
  );
}
