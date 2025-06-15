import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';


const Navbar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav className="bg-white shadow p-4 flex justify-between items-center w-full">
      <div className="flex space-x-4">
        <Link to="/dashboard" className="text-blue-600 hover:underline">
          Dashboard
        </Link>
        <Link to="/products" className="text-blue-600 hover:underline">
          Productos
        </Link>
        <Link to="/movements" className="text-blue-600 hover:underline">
          Movimientos
        </Link>
        <Link to="/reports" className="text-blue-600 hover:underline">
          Reportes
        </Link>
        <Link to="/alerts" className="text-blue-600 hover:underline">
          Alertas
        </Link>
      </div>
      <div className="flex items-center space-x-4">
        {user && (
          <button onClick={handleLogout} className="text-red-500 hover:underline">
            Cerrar Sesión
          </button>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
