import React, { useState, useEffect } from 'react';

export default function Profile() {
  const headline = "ADITYA SHAKYA";
  const [displayed, setDisplayed] = useState("");

  useEffect(() => {
    let i = 0;
    const interval = setInterval(() => {
      setDisplayed(headline.slice(0, i + 1));
      i++;
      if (i === headline.length) clearInterval(interval);
    }, 120);
    return () => clearInterval(interval);
  }, []);

  return (
    <section className="hero">
      <div className="hero-content">
        <h1 className="typewriter">{displayed}<span className="typewriter-cursor">|</span></h1>
        <h2>C++ / Android Developer</h2>
        <p style={{marginTop: '4rem', paddingLeft: '2rem', paddingRight: '2rem'}}>
          Experienced C++ and Android Developer with 3+ years in Android development and system-level programming. Skilled in
          building scalable automotive and mobile solutions using C++, Android SDK/NDK, and Java. Strong in cross-platform optimization,
          cloud integration, and leading development efforts. Seeking a role focused on practical solutions and continuous growth.
        </p>
        <a
          href="/AdityaShakya_3yrsC++Android.pdf"
          download
          className="download-resume-btn"
        >
          Download Resume
        </a>
      </div>
    </section>
  );
}
