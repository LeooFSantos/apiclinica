import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login/Login.jsx';
import Dashboard from './components/Dashboard/Dashboard.jsx';
import RegisterPaciente from './components/RegisterPaciente/RegisterPaciente.jsx';
import RegisterMedico from './components/RegisterMedico/RegisterMedico.jsx';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/registrar-paciente" element={<RegisterPaciente />} />
        <Route path="/registrar-medico" element={<RegisterMedico />} />
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
