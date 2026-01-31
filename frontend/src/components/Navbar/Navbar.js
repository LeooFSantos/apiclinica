import React from 'react';
import { useNavigate } from 'react-router-dom';
import { getAuthUser, clearAuth } from '../../config';
import './Navbar.css';

export default function Navbar() {
  const user = getAuthUser();
  const navigate = useNavigate();

  const handleLogout = () => {
    clearAuth();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <h2>🏥 API Clínica</h2>
        </div>

        <div className="navbar-menu">
          <span className="user-info">
            👤 {user?.nomeUsuario}
          </span>
          <button className="btn btn-outline btn-small" onClick={handleLogout}>
            Sair
          </button>
        </div>
      </div>
    </nav>
  );
}
