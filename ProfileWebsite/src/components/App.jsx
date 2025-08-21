
import React, { useState } from 'react';
import ParticleBackground from './ParticleBackground';
import Profile from './Profile';
import Experience from './Experience';
import Projects from './Projects';
import Blog from './Blog';
import Contact from './Contact';
import AnimatedSection from './AnimatedSection';
import DarkModeToggle from './DarkModeToggle';
import { AnimatedNav, SectionTransition } from './AnimatedNav';
import Resume from './Resume';

export default function App() {

  const [dark, setDark] = useState(true);
  const [section, setSection] = useState('Profile');
  React.useEffect(() => {
    document.body.className = dark ? 'dark' : '';
  }, [dark]);
  const sections = [
  { name: 'Profile', comp: <Profile /> },
  { name: 'Skills', comp: <Resume /> },
    { name: 'Experience', comp: <Experience /> },
    { name: 'Projects', comp: <Projects /> },
    { name: 'Blog', comp: <Blog /> },
    { name: 'Contact', comp: <Contact /> },
  ];
  const current = sections.find(s => s.name === section);
  return (
    <>
      <ParticleBackground />
      <div>
        <div style={{display:'flex', alignItems:'center', justifyContent:'center', gap:'1.5rem', marginBottom:'2.5rem'}}>
          <AnimatedNav items={sections.map(s => s.name)} current={section} onSelect={setSection} />
          <DarkModeToggle dark={dark} setDark={setDark} />
        </div>
        <SectionTransition keyProp={section}>
          <AnimatedSection>
            <div className="fade-in">{current.comp}</div>
          </AnimatedSection>
        </SectionTransition>
      </div>
    </>
  );
}
