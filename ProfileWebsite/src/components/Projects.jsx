import React from 'react';

const projects = [
  {
    name: 'FaceTiltWarningApp',
    description: 'Android app using ML Kit and C++ NDK to detect face tilt and warn the user.',
    link: '#'
  },
  {
    name: 'NetScannerApp',
    description: 'Android network scanner to find devices, open ports, and vulnerabilities.',
    link: '#'
  },
  {
    name: 'GraphAlgoVisualizer',
    description: 'Interactive Android app to visualize graph algorithms and data structures.',
    link: '#'
  }
];

export default function Projects() {
  return (
    <section className="projects">
      <h2>Projects</h2>
      <ul>
        {projects.map((proj, i) => (
          <li key={i}>
            <div className="project-card">
              <div className="card-inner">
                <div className="card-front">
                  <h3>{proj.name}</h3>
                  <span role="img" aria-label="project" style={{fontSize:'2rem'}}>🚀</span>
                </div>
                <div className="card-back">
                  <p>{proj.description}</p>
                  {proj.link && <a href={proj.link}>View Project</a>}
                </div>
              </div>
            </div>
          </li>
        ))}
      </ul>
    </section>
  );
}
