import React, { useState, useEffect, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import NotificationList from './components/NotificationList';
import PublishForm from './components/PublishForm';
import './App.css';

export interface Notification {
  id: string;
  type: string;
  title: string;
  message: string;
  source: string;
  timestamp: string;
  read: boolean;
}

function App() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8081/ws'),
      onConnect: () => {
        setConnected(true);
        client.subscribe('/topic/notifications', (message) => {
          const notif: Notification = JSON.parse(message.body);
          setNotifications(prev => [notif, ...prev]);
        });
      },
      onDisconnect: () => setConnected(false),
      reconnectDelay: 3000
    });

    client.activate();
    return () => { client.deactivate(); };
  }, []);

  const markRead = useCallback((id: string) => {
    setNotifications(prev =>
      prev.map(n => n.id === id ? { ...n, read: true } : n)
    );
  }, []);

  const unreadCount = notifications.filter(n => !n.read).length;

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-left">
          <span className="header-icon">🔔</span>
          <h1>Notification Service</h1>
          {unreadCount > 0 && <span className="badge">{unreadCount}</span>}
        </div>
        <span className={`status ${connected ? 'online' : 'offline'}`}>
          {connected ? '● connected' : '○ disconnected'}
        </span>
      </header>

      <main className="app-body">
        <PublishForm />
        <NotificationList notifications={notifications} onMarkRead={markRead} />
      </main>
    </div>
  );
}

export default App;