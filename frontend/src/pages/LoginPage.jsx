import { useState } from "react";
import styled from "styled-components";

const Container = styled.div`
  max-width: 400px;
  margin: 50px auto;
  padding: 2rem;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
`;

const Title = styled.h1`
  text-align: center;
  margin-bottom: 1.5rem;
  font-size: 1.8rem;
  color: #333;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
`;

const Label = styled.label`
  margin-bottom: 0.5rem;
  font-weight: bold;
`;

const Input = styled.input`
  padding: 0.8rem;
  margin-bottom: 1rem;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1rem;
`;

const Button = styled.button`
  padding: 0.8rem;
  border: none;
  background-color: #007bff;
  color: white;
  font-size: 1rem;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;

  &:hover {
    background-color: #0056b3;
  }
`;

const ErrorMessage = styled.p`
  color: red;
  font-size: 0.9rem;
  text-align: center;
`;

const LoginPage = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!email || !password) {
      setError("Todos los campos son obligatorios");
      return;
    }
    setError("");
    console.log("Iniciar sesión con:", { email, password });
  };

  return (
    <Container>
      <Title>Iniciar Sesión</Title>
      {error && <ErrorMessage>{error}</ErrorMessage>}
      <Form onSubmit={handleSubmit}>
        <Label>Correo Electrónico:</Label>
        <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />

        <Label>Contraseña:</Label>
        <Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />

        <Button type="submit">Ingresar</Button>
      </Form>
    </Container>
  );
};

export default LoginPage;
