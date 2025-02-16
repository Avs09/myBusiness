import styled from 'styled-components';

const HomeContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  box-sizing: border-box;
  width: 100%;
  overflow-x: hidden;
`;

const Title = styled.h2`
  font-size: 2rem;
  color: #333;
  margin-bottom: 1rem;
`;

const Description = styled.p`
  font-size: 1.2rem;
  color: #555;
  text-align: center;
  max-width: 600px;
  margin-bottom: 2rem;
`;

const CallToAction = styled.button`
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;

  &:hover {
    background-color: #0056b3;
  }
`;

const Home = () => (
  <HomeContainer>
    <Title>Bienvenido a Gestión de Inventario</Title>
    <Description>
      Optimiza y gestiona eficientemente el inventario de tu negocio con nuestra
      aplicación profesional, robusta y escalable. ¡Empieza hoy mismo y lleva tu
      empresa al siguiente nivel!
    </Description>
    <CallToAction>Comienza Ahora</CallToAction>
  </HomeContainer>
);

export default Home;