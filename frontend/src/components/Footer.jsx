import styled from 'styled-components';

const FooterContainer = styled.footer`
  text-align: center;
  padding: 1rem 0;
  background-color: #f8f9fa;
  width: 100%;
  position: relative;
  bottom: 0;
`;

const FooterText = styled.p`
  color: #666;
  font-size: 0.9rem;
  margin: 0.5rem 0;
`;

const Footer = () => (
  <FooterContainer>
    <FooterText>&copy; 2025 Gestión de Inventario. Todos los derechos reservados.</FooterText>
    <FooterText>Contacto: info@gestioninventario.com</FooterText>
  </FooterContainer>
);

export default Footer;