
import { motion } from 'framer-motion';
import { 
  FaWarehouse, 
  FaChartBar, 
  FaLock, 
  FaBoxOpen, 
  FaSearch, 
  FaChartLine 
} from 'react-icons/fa';
import {
  Container,
  HeroSection,
  HeroText,
  HeroButton,
  FeaturesSection,
  Feature,
  HowItWorksSection,
  StepsContainer,
  Step,
  StepIcon,
  StepTitle,
  StepDescription,
  SectionBanner,
  StepsBanner
} from './LandingPageStyles';

const LandingPage = () => {
  return (
    <Container>
      <HeroSection>
        <motion.div
          initial={{ opacity: 0, y: 40 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1.2, ease: 'easeOut' }}
        >
          <HeroText>
            <h1>Gestiona tu inventario con elegancia</h1>
            <p>
              La solución clásica y profesional para el control de inventarios. 
              Optimiza tus procesos con estilo y precisión.
            </p>
            <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
              <HeroButton href="#features">Descubre más</HeroButton>
            </motion.div>
          </HeroText>
        </motion.div>
      </HeroSection>

      {/* Banner para la sección de features */}
      <SectionBanner />

      <FeaturesSection id="features">
        <Feature>
          <motion.div whileHover={{ scale: 1.03 }} transition={{ duration: 0.3 }}>
            <FaWarehouse size={50} style={{ color: '#2c3e50', marginBottom: '15px' }} />
            <h3>Control Completo</h3>
            <p>Administra cada detalle de tu inventario de forma sencilla y precisa.</p>
          </motion.div>
        </Feature>
        <Feature>
          <motion.div whileHover={{ scale: 1.03 }} transition={{ duration: 0.3 }}>
            <FaChartBar size={50} style={{ color: '#2c3e50', marginBottom: '15px' }} />
            <h3>Informes Precisos</h3>
            <p>Obtén reportes detallados que te permitan tomar decisiones informadas.</p>
          </motion.div>
        </Feature>
        <Feature>
          <motion.div whileHover={{ scale: 1.03 }} transition={{ duration: 0.3 }}>
            <FaLock size={50} style={{ color: '#2c3e50', marginBottom: '15px' }} />
            <h3>Seguridad Garantizada</h3>
            <p>Tus datos están protegidos con la máxima seguridad y confidencialidad.</p>
          </motion.div>
        </Feature>
      </FeaturesSection>

      <HowItWorksSection>
        {/* Banner para la sección de "Cómo Funciona" */}
        <StepsBanner />
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 1 }}>
          <h2>¿Cómo Funciona?</h2>
          <StepsContainer>
            <Step>
              <StepIcon>
                <FaBoxOpen />
              </StepIcon>
              <StepTitle>Agrega tus productos</StepTitle>
              <StepDescription>
                Registra cada artículo de tu inventario con detalles precisos.
              </StepDescription>
            </Step>
            <Step>
              <StepIcon>
                <FaSearch />
              </StepIcon>
              <StepTitle>Monitorea en tiempo real</StepTitle>
              <StepDescription>
                Visualiza y controla el estado de tu inventario en todo momento.
              </StepDescription>
            </Step>
            <Step>
              <StepIcon>
                <FaChartLine />
              </StepIcon>
              <StepTitle>Analiza y optimiza</StepTitle>
              <StepDescription>
                Genera reportes y toma decisiones informadas para mejorar tu negocio.
              </StepDescription>
            </Step>
          </StepsContainer>
        </motion.div>
      </HowItWorksSection>
    </Container>
  );
};

export default LandingPage;
