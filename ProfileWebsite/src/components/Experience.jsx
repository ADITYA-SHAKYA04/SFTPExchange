import React from 'react';

// Detailed professional experience
const experience = [
  {
    company: 'Harman Connected Services Pvt. Ltd. (Bangalore)',
    role: 'Associate Engineer II, Software Engineering',
    period: 'Feb 2024 – Present',
    project: 'PSA – STL (Automotive Security Team)',
    details: [
      'Designed and implemented functionalities for Android-based Automotive systems, improving integrity and reliability across deployments.',
      'Contributed to the integration process of the TimeSecure protocol by analyzing HAL service behavior and assisting in flow validation across system components.',
      'Optimized integrity-checking logic, resolving SOP-critical issues and enhancing security compliance.',
      'Led defect triaging and resolution cycles, collaborating with cross-functional teams to unblock features and deliver timely fixes.'
    ],
    environment: 'C++, STL, AOSP, HAL, Java, Linux, Android Native, Git, Multithreading, Code Sonar, Valgrind'
  },
  {
    company: 'The Hi-Tech Robotic Systemz Ltd. (Gurugram)',
    role: 'Research Engineer',
    period: 'Jun 2022 – Jan 2024',
    project: 'Novus Aware – Attention & Behavior Monitoring (ADAS)',
    details: [
      'Heading ADAS R&D on Android platform, integrating innovative tech for enhanced driver behavior through pioneering Driver Monitoring State application. Committed to preemptively alerting drivers about risky behaviors for safer road.',
      'Developed FOTA upgrade protocol for ADAS using UART communication protocol, ensuring seamless and efficient updates for connected devices.',
      'Integrated AWS IoT services with ADAS IoT-enabled devices, enabling secure and scalable communication between devices and the cloud platform.'
    ],
    environment: 'C, C++, STL, Java, Linux, Shell Scripting, Android SDK, Android NDK, IPC, SFTP, CAN/FOTA, AWS IoT, Git, Python, Multithreading, MQTT, Docker, S3 bucket, Valgrind, Data Structures and Algorithms',
    intern: {
      role: 'Intern',
      period: 'Mar 2021 – Jun 2022',
      details: [
        'Implemented driver image upload, critical warning notifications, power tampering alerts, and automated warning video features. Facilitated seamless transfer of corresponding metadata between device and Linux host over Wi-Fi utilizing the secure SFTP protocol with RSA authentication.',
        'Designed and developed advanced ADAS application for Android platform and modules from technical requirement with cross-functional teams to define, design, and launch new features. (Enhancing Production Efficiency with 500%).',
        'Developed the Real-time data uploading solution for the data to cloud and Leveraged the JITR of AWS-IoT.'
      ],
      environment: 'C, C++, STL, Java, Linux, Shell Scripting, Android SDK, TCP, SFTP, CAN/FOTA, Docker, AWS IoT, Git, Python, Doxygen, Valgrind, S3 bucket, Data Structures and Algorithms'
    }
  },
  {
    company: 'New Software Solution LLP (Bhopal)',
    role: 'Android Intern',
  period: 'Aug 2020 – Jan 2021',
    details: [
      'Chalo Pandit Ji - Built a location-based Android app enabling users to book verified Hindu priests for local religious services.',
      'Self-help Audio - Redesigned and enhanced a mobile app for streaming audiobooks authored by independent writers.',
      'Photo-Copy - Developed a native app for placing online print orders and filling applications via nearby trusted vendors.'
    ],
    environment: 'Java, Android Studio, Rest APIs, Postman, Adobe XD, Firebase, Jira, Agile, Data Structures and Algorithms'
  }
];

export default function Experience() {
  return (
    <section className="experience">
      <h2 style={{textAlign:'center', marginBottom:'1.2rem'}}>Experience</h2>
      <div className="timeline-container">
        {experience.map((exp, i) => (
          <div key={i} className={`timeline-item fade-in${i === experience.length ? ' last-timeline-item' : ''}`}>
            <div className="timeline-dot"></div>
            <div className="timeline-line"></div>
            <div className="timeline-content experience-card">
              <h3 className="exp-role-heading" style={{marginBottom:'0.2rem'}}>{exp.role}</h3>
              <div className="exp-company exp-white" style={{marginBottom:'0.2rem',fontWeight:'bold'}}>{exp.company}</div>
              <div className="exp-tenure" style={{marginBottom:'0.5rem'}}>{exp.period}</div>
              {exp.project && <div className="exp-project exp-white" style={{marginBottom:'0.5rem', fontWeight:'bold'}}><b>Project:</b> {exp.project}</div>}
              <ul style={{marginBottom:'0.5rem'}}>
                {exp.details.map((d, idx) => <li key={idx} style={{listStyleType:'disc', marginLeft:'1.2em'}}>&#8226; {d}</li>)}
              </ul>
              <div className="exp-environment">{exp.environment}</div>
              {exp.intern && (
                <div style={{marginTop:'1.5rem'}}>
                  <h4 className="exp-role-heading" style={{marginBottom:'0.2rem'}}>Intern</h4>
                  <div className="exp-tenure" style={{marginBottom:'0.5rem'}}>{exp.intern.period}</div>
                  <ul style={{marginBottom:'0.5rem'}}>
                    {exp.intern.details.map((d, idx) => <li key={idx}>{d}</li>)}
                  </ul>
                  <div className="exp-environment">{exp.intern.environment}</div>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </section>
  );
}
