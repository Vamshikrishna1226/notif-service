import React, { useState } from 'react';
import axios from 'axios';
import './PublishForm.css';

const API = 'http://localhost:8081/api/notifications/publish';

const TYPES = ['INFO', 'WARNING', 'ALERT', 'ERROR'];

function PublishForm() {
  const [form, setForm] = useState({
    type: 'INFO',
    title: '',
    message: '',
    source: ''
  });
  const [status, setStatus] = useState<'idle' | 'sending' | 'sent' | 'error'>('idle');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.title || !form.message) return;

    setStatus('sending');
    try {
      await axios.post(API, form);
      setStatus('sent');
      setForm(prev => ({ ...prev, title: '', message: '' }));
      setTimeout(() => setStatus('idle'), 2000);
    } catch {
      setStatus('error');
      setTimeout(() => setStatus('idle'), 3000);
    }
  };

  return (
    <div className="publish-form">
      <h2>Publish Notification</h2>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label>Type</label>
          <select name="type" value={form.type} onChange={handleChange}>
            {TYPES.map(t => <option key={t} value={t}>{t}</option>)}
          </select>
        </div>

        <div className="field">
          <label>Title</label>
          <input
            name="title"
            value={form.title}
            onChange={handleChange}
            placeholder="Notification title"
            required
          />
        </div>

        <div className="field">
          <label>Message</label>
          <textarea
            name="message"
            value={form.message}
            onChange={handleChange}
            placeholder="What happened?"
            rows={3}
            required
          />
        </div>

        <div className="field">
          <label>Source</label>
          <input
            name="source"
            value={form.source}
            onChange={handleChange}
            placeholder="e.g. monitoring, api, cron"
          />
        </div>

        <button type="submit" disabled={status === 'sending'}>
          {status === 'sending' ? 'Sending...' : 'Publish'}
        </button>

        {status === 'sent' && <p className="msg success">Published successfully</p>}
        {status === 'error' && <p className="msg error">Something went wrong</p>}
      </form>
    </div>
  );
}

export default PublishForm;