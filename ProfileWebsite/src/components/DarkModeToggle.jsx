import React from 'react';

export default function DarkModeToggle({ dark, setDark }) {
  return (
    <button
      className={`dark-toggle modern-toggle${dark ? ' dark' : ''}`}
      onClick={() => setDark((d) => !d)}
      aria-label="Toggle dark mode"
    >
      <span className="toggle-track">
        <span className="toggle-thumb">
          {dark ? '🌙' : '☀️'}
        </span>
      </span>
    </button>
  );
}
