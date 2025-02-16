// src/components/Header.js
import styled from 'styled-components';
import { Link } from 'react-router-dom';

const Nav = styled.nav`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #f8f9fa;
  box-sizing: border-box;
  width: 100%;
`;

const Logo = styled.h1`
  font-size: 1.5rem;
  color: #333;
  margin: 0;
`;

const Menu = styled.ul`
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
`;

const MenuItem = styled.li`
  margin-left: 1.5rem;
`;

const MenuLink = styled(Link)`
  text-decoration: none;
  color: #333;
  font-size: 1rem;
  transition: color 0.3s;

  &:hover {
    color: #007bff;
  }
`;

const Header = () => (
  <Nav>
    <Logo>Gestión de Inventario</Logo>
    <Menu>
      <MenuItem>
        <MenuLink to="/">Inicio</MenuLink>
      </MenuItem>
      <MenuItem>
        <MenuLink to="/login">Iniciar Sesión</MenuLink>
      </MenuItem>
      <MenuItem>
        <MenuLink to="/register">Registrarse</MenuLink>
      </MenuItem>
    </Menu>
  </Nav>
);

export default Header;