import React, { useState, useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import { getAuthToken } from '../../config';
import Navbar from '../Navbar/Navbar';
import AdminDashboard from '../AdminDashboard/AdminDashboard';
import PacienteDashboard from '../PacienteDashboard/PacienteDashboard';
import MedicoDashboard from '../MedicoDashboard/MedicoDashboard';
import './Dashboard.css';

export default function Dashboard() {
  const [userType, setUserType] = useState(null);
  const token = getAuthToken();

  useEffect(() => {
    const type = localStorage.getItem('userType') || 'USER';
    setUserType(type);
  }, []);

  if (!token) {
    return <Navigate to="/" />;
  }

  if (!userType) {
    return <div className="loading"><div className="spinner"></div></div>;
  }

  return (
    <div className="dashboard-layout">
      <Navbar />
      <main className="dashboard-content">
        {userType === 'ADMIN' && <AdminDashboard />}
        {userType === 'MEDICO' && <MedicoDashboard />}
        {userType === 'USER' && <PacienteDashboard />}
      </main>
    </div>
  );
}
