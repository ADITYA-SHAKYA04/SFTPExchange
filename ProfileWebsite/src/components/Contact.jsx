import React from 'react';

export default function Contact() {
  return (
    <section className="contact">
      <h2 style={{textAlign: 'center'}}>Contact</h2>
      <div className="contact-content">
        <table style={{width:'100%', borderCollapse:'separate', borderSpacing:'0 12px', fontSize:'1.08em', margin:'0 auto'}}>
          <tbody>
            <tr>
              <td className="contact-label">Email</td>
              <td className="contact-value"><a href="mailto:aditya.shakya@email.com" className="contact-link">aditya.shakya@email.com</a></td>
            </tr>
            <tr>
              <td className="contact-label">LinkedIn</td>
              <td className="contact-value"><a href="https://www.linkedin.com/in/aditya-shakya/" target="_blank" rel="noopener noreferrer" className="contact-link">aditya-shakya</a></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  );
}
