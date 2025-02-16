import styled from 'styled-components';

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  min-height: 100vh; /* Ocupa al menos el 100% del viewport */
`;


export const Header = styled.header`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 40px;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

export const Logo = styled.h1`
  font-size: 24px;
  font-weight: bold;
`;

export const Nav = styled.nav`
  display: flex;
  gap: 20px;
`;

export const NavItem = styled.a`
  text-decoration: none;
  color: #333;
  font-size: 16px;
  transition: color 0.3s;

  &:hover {
    color: #007bff;
  }
`;

export const HeroSection = styled.section`
  padding: 100px 20px;
  text-align: center;
  background: #e6e6e6;
`;

export const HeroText = styled.div`
  h1 {
    font-size: 48px;
    margin-bottom: 20px;
  }

  p {
    font-size: 18px;
    margin-bottom: 30px;
  }
`;

export const HeroButton = styled.a`
  display: inline-block;
  padding: 12px 30px;
  background: #007bff;
  color: #fff;
  font-weight: bold;
  border-radius: 8px;
  text-decoration: none;
  transition: background 0.3s;

  &:hover {
    background: #0056b3;
  }
`;

export const FeaturesSection = styled.section`
  padding: 60px 20px;
  display: flex;
  justify-content: space-around;
  background: #fff;
`;

export const Feature = styled.div`
  text-align: center;
  width: 30%;

  h3 {
    font-size: 24px;
    margin-bottom: 10px;
  }

  p {
    font-size: 16px;
    color: #666;
  }
`;


export const Footer = styled.footer`
  padding: 20px;
  text-align: center;
  background: #333;
  color: #fff;
  font-size: 14px;
  margin-top: auto; /* Empuja el footer hacia el final del contenedor */
`;

export const Content = styled.div`
  max-width: 100%;  /* Elimina la restricción de 1200px */
  width: 100%;
  text-align: center;
  padding: 20px;
  box-sizing: border-box;
  flex: 1;
`;