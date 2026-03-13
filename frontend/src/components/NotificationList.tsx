import React from 'react';
import { Notification } from '../App';
import './NotificationList.css';

interface Props {
  notifications: Notification[];
  onMarkRead: (id: string) => void;
}

const TYPE_COLORS: Record<string, string> = {
  INFO: '#1565c0',
  WARNING: '#e65100',
  ALERT: '#6a1b9a',
  ERROR: '#c62828'
};

function timeAgo(timestamp: string): string {
  const diff = Math.floor((Date.now() - new Date(timestamp).getTime()) / 1000);
  if (diff < 60) return `${diff}s ago`;
  if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
  return `${Math.floor(diff / 3600)}h ago`;
}

function NotificationList({ notifications, onMarkRead }: Props) {
  if (notifications.length === 0) {
    return (
      <div className="notif-list empty">
        <p>No notifications yet.</p>
        <p className="hint">Publish one from the form to see it appear here in real time.</p>
      </div>
    );
  }

  return (
    <div className="notif-list">
      <h2>Live Feed <span className="count">{notifications.length}</span></h2>
      {notifications.map(n => (
        <div
          key={n.id}
          className={`notif-card ${n.read ? 'read' : 'unread'}`}
          onClick={() => onMarkRead(n.id)}
        >
          <div className="notif-header">
            <span
              className="notif-type"
              style={{ color: TYPE_COLORS[n.type] || '#333' }}
            >
              {n.type}
            </span>
            <span className="notif-time">{timeAgo(n.timestamp)}</span>
          </div>
          <div className="notif-title">{n.title}</div>
          <div className="notif-message">{n.message}</div>
          {n.source && <div className="notif-source">from: {n.source}</div>}
          {!n.read && <div className="unread-dot" />}
        </div>
      ))}
    </div>
  );
}

export default NotificationList;