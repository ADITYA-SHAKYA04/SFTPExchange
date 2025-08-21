import React, { useEffect, useState } from 'react';
import matter from 'gray-matter';
import { marked } from 'marked';
import CommentSection from './CommentSection';

export default function Blog() {
  const [posts, setPosts] = useState([]);
  const [tag, setTag] = useState('All');

  useEffect(() => {
    // Dynamically import all markdown files in blog folder (Vite compatible)
    const importMarkdown = async () => {
      const modules = import.meta.glob('../blog/*.md');
      const files = await Promise.all(
        Object.entries(modules).map(async ([path, resolver]) => {
          const mod = await resolver();
          return { path, content: mod.default };
        })
      );
      const loadedPosts = files.map((file, idx) => {
        const { data, content } = matter(file.content);
        return { ...data, content: marked(content), id: data.title + idx };
      });
      setPosts(loadedPosts.sort((a, b) => new Date(b.date) - new Date(a.date)));
    };
    importMarkdown();
  }, []);

  // Collect all tags
  const allTags = Array.from(new Set(posts.flatMap(p => (p.tags ? (Array.isArray(p.tags) ? p.tags : typeof p.tags === 'string' ? JSON.parse(p.tags.replace(/'/g,'"')) : []) : [])))).filter(Boolean);
  const filtered = tag === 'All' ? posts : posts.filter(p => p.tags && (Array.isArray(p.tags) ? p.tags : typeof p.tags === 'string' ? JSON.parse(p.tags.replace(/'/g,'"')) : []).includes(tag));

  return (
    <section className="blog">
      <h2>Blog</h2>
      <div style={{ marginBottom: 16 }}>
        <b>Filter by tag:</b> {' '}
        <button onClick={() => setTag('All')} style={{ fontWeight: tag==='All'?'bold':'normal' }}>All</button>
        {allTags.map(t => (
          <button key={t} onClick={() => setTag(t)} style={{ fontWeight: tag===t?'bold':'normal', marginLeft: 8 }}>{t}</button>
        ))}
      </div>
      <ul className="blog-list">
        {filtered.map((post, i) => (
          <li key={i} className="blog-post">
            {post.featuredImage && <img src={post.featuredImage} alt="Featured" style={{maxWidth:'100%',borderRadius:'1rem'}} />}
            <h2>{post.title}</h2>
            <div style={{fontSize:'0.9em',color:'#888'}}>
              {post.date && <span>{post.date}</span>}
              {post.author && <span> &middot; {post.author}</span>}
              {post.tags && <span> &middot; {(Array.isArray(post.tags) ? post.tags : typeof post.tags === 'string' ? JSON.parse(post.tags.replace(/'/g,'"')) : []).map(t => <span key={t} style={{marginRight:4}}>{t}</span>)}</span>}
            </div>
            <div dangerouslySetInnerHTML={{ __html: post.content }} />
            <CommentSection postId={post.id} />
          </li>
        ))}
      </ul>
    </section>
  );
}
