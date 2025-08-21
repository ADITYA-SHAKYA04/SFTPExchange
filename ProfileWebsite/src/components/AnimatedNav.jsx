import { motion, AnimatePresence } from 'framer-motion';
import React from 'react';

export function AnimatedNav({ items, current, onSelect }) {
  return (
    <nav className="animated-nav">
      {items.map(item => (
        <motion.button
          key={item}
          className={current === item ? 'active' : ''}
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => onSelect(item)}
        >
          {item}
        </motion.button>
      ))}
    </nav>
  );
}

export function SectionTransition({ children, keyProp }) {
  return (
    <AnimatePresence mode="wait">
      <motion.div
        key={keyProp}
        initial={{ opacity: 0, y: 40 }}
        animate={{ opacity: 1, y: 0 }}
        exit={{ opacity: 0, y: -40 }}
        transition={{ duration: 0.5 }}
      >
        {children}
      </motion.div>
    </AnimatePresence>
  );
}
