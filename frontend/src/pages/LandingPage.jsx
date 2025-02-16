import { motion } from 'framer-motion';
import { FaWarehouse, FaChartBar, FaLock } from 'react-icons/fa';
import {
  Container,
  Header,
  Logo,
  Nav,
  NavItem,
  HeroSection,
  HeroText,
  HeroButton,
  FeaturesSection,
  Feature,
  Footer
} from './LandingPageStyles';

const LandingPage = () => {
  return (
    <Container>
      <Header>
        <Logo>Gestión de Inventario</Logo>
        <Nav>
          <NavItem href="#">
            <FaWarehouse style={{ marginRight: '8px' }} />
            Inicio
          </NavItem>
          <NavItem href="#">
            <FaLock style={{ marginRight: '8px' }} />
            Iniciar Sesión
          </NavItem>
          <NavItem href="#">
            <FaChartBar style={{ marginRight: '8px' }} />
            Registrarse
          </NavItem>
        </Nav>
      </Header>

      <HeroSection>
        <motion.div initial={{ opacity: 0, y: 50 }} animate={{ opacity: 1, y: 0 }} transition={{ duration: 1 }}>
          <HeroText>
            <h1>Gestiona tu inventario de manera profesional</h1>
            <p>La mejor solución para el control de inventarios de tu negocio.</p>
            <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.95 }}>
              <HeroButton href="#features">Descubre más</HeroButton>
            </motion.div>
          </HeroText>
        </motion.div>
      </HeroSection>

      <FeaturesSection id="features">
        <Feature>
          <motion.div whileHover={{ scale: 1.05 }}>
            <FaWarehouse size={40} style={{ color: '#007bff', marginBottom: '10px' }} />
            <h3>Control Completo</h3>
            <p>Administra cada detalle de tu inventario.</p>
          </motion.div>
        </Feature>
        <Feature>
          <motion.div whileHover={{ scale: 1.05 }}>
            <FaChartBar size={40} style={{ color: '#28a745', marginBottom: '10px' }} />
            <h3>Informes Precisos</h3>
            <p>Obtén reportes detallados en tiempo real.</p>
          </motion.div>
        </Feature>
        <Feature>
          <motion.div whileHover={{ scale: 1.05 }}>
            <FaLock size={40} style={{ color: '#dc3545', marginBottom: '10px' }} />
            <h3>Seguridad</h3>
            <p>Tus datos siempre protegidos y seguros.</p>
          </motion.div>
        </Feature>
      </FeaturesSection>

      <Footer>
        <p>&copy; 2025 Gestión de Inventario. Todos los derechos reservados.</p>
      </Footer>
    </Container>
  );
};

export default LandingPage;
