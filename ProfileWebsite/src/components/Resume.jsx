import React from 'react';

export default function Resume() {
  return (
    <section className="resume">
  <h2 style={{textAlign:'center', marginBottom:'0.5rem'}}>Skills</h2>
      <div className="resume-content">
  {/* Removed name and experience as requested */}
          <table style={{width:'100%', borderCollapse:'separate', borderSpacing:'0 12px', fontSize:'1.08em', margin:'0 auto'}}>
            <tbody>
              <tr>
                <td style={{padding:'8px 0', fontWeight:'bold'}}>Languages</td>
                <td style={{padding:'8px 0'}}>C, C++, Java</td>
              </tr>
              <tr>
                <td style={{padding:'8px 0', fontWeight:'bold'}}>Scripting</td>
                <td style={{padding:'8px 0'}}>Bash, Python</td>
              </tr>
              <tr>
                <td style={{padding:'8px 0', fontWeight:'bold'}}>Mobile Development</td>
                <td style={{padding:'8px 0'}}>Android SDK/NDK, AOSP, HAL, Jetpack</td>
              </tr>
              <tr>
                <td style={{padding:'8px 0', fontWeight:'bold'}}>Cloud & Protocols</td>
                <td style={{padding:'8px 0'}}>AWS IoT, MQTT, S3, JITR, SFTP</td>
              </tr>
              <tr>
                <td style={{padding:'8px 0', fontWeight:'bold'}}>Operating Systems</td>
                <td style={{padding:'8px 0'}}>Linux (Ubuntu), Android</td>
              </tr>
              <tr>
                <td style={{padding:'8px 0', fontWeight:'bold'}}>Tools & Frameworks</td>
                <td style={{padding:'8px 0'}}>Docker, CodeSonar, JIRA, Git, G-Unit, GDB, Valgrind</td>
              </tr>
              <tr>
                <td style={{padding:'8px 0', fontWeight:'bold'}}>Core Interests</td>
                <td style={{padding:'8px 0'}}>Data Structures, Algorithms, Blockchain, Linux System Programming</td>
              </tr>
            </tbody>
          </table>
      </div>
    </section>
  );
}
