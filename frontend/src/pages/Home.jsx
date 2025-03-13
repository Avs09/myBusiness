import styled from "styled-components";

const HomeContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem;
  text-align: center;
  background-color: #f9f9f9;
  height: 100vh;
`;

const Title = styled.h2`
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 1rem;
`;

const Description = styled.p`
  font-size: 1.2rem;
  color: #555;
  max-width: 600px;
  margin-bottom: 2rem;
`;

const CallToAction = styled.button`
  padding: 0.8rem 1.5rem;
  font-size: 1rem;
  color: white;
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
      Controla eficientemente el inventario de tu negocio con nuestra aplicación profesional.
    </Description>
    <CallToAction>Comienza Ahora</CallToAction>
  </HomeContainer>
);

export default Home;
