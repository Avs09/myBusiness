import { useState } from 'react';
import styled from 'styled-components';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  FaUser, 
  FaUserAlt, 
  FaIdCard, 
  FaEnvelope, 
  FaLock, 
  FaBuilding, 
  FaBarcode, 
 
} from 'react-icons/fa';

const Container = styled.div`
  max-width: 400px;
  margin: 2rem auto;
  padding: 2rem;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
`;

const Title = styled.h1`
  text-align: center;
  margin-bottom: 2rem;
  color: #2c3e50;
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
`;

const FieldContainer = styled.div`
  display: flex;
  flex-direction: column;
  margin-bottom: 1rem;
`;

const FieldLabel = styled.label`
  margin-bottom: 0.25rem;
  font-size: 0.95rem;
  color: #333;
`;

const InputWrapper = styled.div`
  display: flex;
  align-items: center;
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 0.5rem;
  background: #fff;
`;

const IconContainer = styled.div`
  margin-right: 0.5rem;
  color: #2c3e50;
  font-size: 1.2rem;
`;

const StyledInput = styled.input`
  border: none;
  outline: none;
  flex: 1;
  font-size: 1rem;
  background: transparent;
`;

const StyledSelect = styled.select`
  border: none;
  outline: none;
  flex: 1;
  font-size: 1rem;
  background: transparent;
`;

const ButtonContainer = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 1.5rem;
`;

const Button = styled.button`
  padding: 0.75rem 1.5rem;
  background-color: #2c3e50;
  border: none;
  color: #fff;
  font-size: 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s ease;
  
  &:hover {
    background-color: #34495e;
  }
`;

const LogoUploadContainer = styled.label`
  width: 150px;
  height: 150px;
  border: 1px dashed #ccc;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  background-color: #f9f9f9;
  margin-top: 0.5rem;
  color: #666;
  font-size: 0.9rem;
  text-align: center;
`;

const HiddenInput = styled.input`
  display: none;
`;

const LogoImage = styled.img`
  width: 100%;
  height: 100%;
  object-fit: contain;
`;

const ErrorMessage = styled.span`
  color: red;
  font-size: 0.85rem;
  margin-top: 0.25rem;
`;

const motionVariants = {
  initial: { x: 100, opacity: 0 },
  animate: { x: 0, opacity: 1 },
  exit: { x: -100, opacity: 0 },
};

const RegisterPage = () => {
  const [step, setStep] = useState(1);
  const [errors, setErrors] = useState({});
  const [formData, setFormData] = useState({
    nombre: '',
    apellidos: '',
    tipoDocumento: '',
    numeroDocumento: '',
    email: '',
    password: '',
    nombreEmpresa: '',
    nit: '',
    logo: null,
  });
  const [logoPreviewUrl, setLogoPreviewUrl] = useState(null);

  const handleChange = (e) => {
    const { name, value, type, files } = e.target;
    if (type === 'file') {
      const file = files[0];
      setFormData({ ...formData, [name]: file });
      if (file) {
        const reader = new FileReader();
        reader.onloadend = () => {
          setLogoPreviewUrl(reader.result);
        };
        reader.readAsDataURL(file);
      }
    } else {
      setFormData({ ...formData, [name]: value });
    }
  };

  // Validaciones para la sección 1 (Datos Personales)
  const validateStep1 = () => {
    let newErrors = {};

    // Solo letras, mínimo 4 caracteres
    if (!formData.nombre.trim() || !/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{4,}$/.test(formData.nombre.trim())) {
      newErrors.nombre = "El nombre debe tener al menos 4 letras y solo letras.";
    }
    if (!formData.apellidos.trim() || !/^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{4,}$/.test(formData.apellidos.trim())) {
      newErrors.apellidos = "El apellido debe tener al menos 4 letras y solo letras.";
    }
    if (!formData.tipoDocumento) {
      newErrors.tipoDocumento = "Seleccione un tipo de documento.";
    }
    if (!formData.numeroDocumento.trim() || !/^\d{8,10}$/.test(formData.numeroDocumento.trim())) {
      newErrors.numeroDocumento = "El número de documento debe tener entre 8 y 10 dígitos.";
    }
    if (!formData.email.trim() || !/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.com$/.test(formData.email.trim())) {
      newErrors.email = "Ingrese un correo válido (ejemplo@dominio.com).";
    }
    if (!formData.password || !/^(?=(?:.*[A-Za-z]){8,})(?=(?:.*\d){3,})(?=.*[!@#$%^&*(),.?":{}|<>]).{12,}$/.test(formData.password)) {
      newErrors.password = "La contraseña debe tener al menos 8 letras, 3 números y 1 carácter especial (mínimo 12 caracteres en total).";
    }

    return newErrors;
  };

  // Validaciones para la sección 2 (Datos de la Empresa)
  const validateStep2 = () => {
    let newErrors = {};
    if (!formData.nombreEmpresa.trim() || formData.nombreEmpresa.trim().length < 3) {
      newErrors.nombreEmpresa = "El nombre de la empresa debe tener al menos 3 caracteres.";
    }
    // Para el NIT, asumimos entre 8 y 12 dígitos
    if (!formData.nit.trim() || !/^\d{8,12}$/.test(formData.nit.trim())) {
      newErrors.nit = "El NIT debe tener entre 8 y 12 dígitos.";
    }
    if (!formData.logo) {
      newErrors.logo = "Debe cargar el logo de la empresa.";
    }
    return newErrors;
  };

  const handleNext = (e) => {
    e.preventDefault();
    const errorsStep1 = validateStep1();
    if (Object.keys(errorsStep1).length > 0) {
      setErrors(errorsStep1);
      return;
    }
    setErrors({});
    setStep(2);
  };

  const handleBack = (e) => {
    e.preventDefault();
    setErrors({});
    setStep(1);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const errorsStep2 = validateStep2();
    if (Object.keys(errorsStep2).length > 0) {
      setErrors(errorsStep2);
      return;
    }
    setErrors({});
    // Aquí enviarías los datos al servidor o ejecutarías la lógica de registro.
    console.log('Datos de registro:', formData);
  };

  return (
    <Container>
      <Title>Registro</Title>
      <Form onSubmit={step === 1 ? handleNext : handleSubmit}>
        <AnimatePresence exitBeforeEnter>
          {step === 1 && (
            <motion.div
              key="step1"
              variants={motionVariants}
              initial="initial"
              animate="animate"
              exit="exit"
              transition={{ duration: 0.5 }}
            >
              {/* Datos Personales */}
              <FieldContainer>
                <FieldLabel htmlFor="nombre">Nombre Completo:</FieldLabel>
                <InputWrapper>
                  <IconContainer><FaUser /></IconContainer>
                  <StyledInput type="text" id="nombre" name="nombre" value={formData.nombre} onChange={handleChange} />
                </InputWrapper>
                {errors.nombre && <ErrorMessage>{errors.nombre}</ErrorMessage>}
              </FieldContainer>

              <FieldContainer>
                <FieldLabel htmlFor="apellidos">Apellidos:</FieldLabel>
                <InputWrapper>
                  <IconContainer><FaUserAlt /></IconContainer>
                  <StyledInput type="text" id="apellidos" name="apellidos" value={formData.apellidos} onChange={handleChange} />
                </InputWrapper>
                {errors.apellidos && <ErrorMessage>{errors.apellidos}</ErrorMessage>}
              </FieldContainer>

              <FieldContainer>
                <FieldLabel htmlFor="tipoDocumento">Tipo de Documento:</FieldLabel>
                <InputWrapper>
                  <IconContainer><FaIdCard /></IconContainer>
                  <StyledSelect id="tipoDocumento" name="tipoDocumento" value={formData.tipoDocumento} onChange={handleChange}>
                    <option value="">Selecciona</option>
                    <option value="cedula">Cédula</option>
                    <option value="pasaporte">Pasaporte</option>
                    <option value="otro">Otro</option>
                  </StyledSelect>
                </InputWrapper>
                {errors.tipoDocumento && <ErrorMessage>{errors.tipoDocumento}</ErrorMessage>}
              </FieldContainer>

              <FieldContainer>
                <FieldLabel htmlFor="numeroDocumento">Número de Documento:</FieldLabel>
                <InputWrapper>
                  <IconContainer><FaIdCard /></IconContainer>
                  <StyledInput type="text" id="numeroDocumento" name="numeroDocumento" value={formData.numeroDocumento} onChange={handleChange} />
                </InputWrapper>
                {errors.numeroDocumento && <ErrorMessage>{errors.numeroDocumento}</ErrorMessage>}
              </FieldContainer>

              <FieldContainer>
                <FieldLabel htmlFor="email">Correo Electrónico:</FieldLabel>
                <InputWrapper>
                  <IconContainer><FaEnvelope /></IconContainer>
                  <StyledInput type="email" id="email" name="email" value={formData.email} onChange={handleChange} />
                </InputWrapper>
                {errors.email && <ErrorMessage>{errors.email}</ErrorMessage>}
              </FieldContainer>

              <FieldContainer>
                <FieldLabel htmlFor="password">Contraseña:</FieldLabel>
                <InputWrapper>
                  <IconContainer><FaLock /></IconContainer>
                  <StyledInput type="password" id="password" name="password" value={formData.password} onChange={handleChange} />
                </InputWrapper>
                {errors.password && <ErrorMessage>{errors.password}</ErrorMessage>}
              </FieldContainer>

              <ButtonContainer>
                <div></div>
                <Button type="submit">Siguiente</Button>
              </ButtonContainer>
            </motion.div>
          )}

          {step === 2 && (
            <motion.div
              key="step2"
              variants={motionVariants}
              initial={{ x: -100, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              exit={{ x: 100, opacity: 0 }}
              transition={{ duration: 0.5 }}
            >
              {/* Datos de la Empresa */}
              <FieldContainer>
                <FieldLabel htmlFor="nombreEmpresa">Nombre de la Empresa o Negocio:</FieldLabel>
                <InputWrapper>
                  <IconContainer><FaBuilding /></IconContainer>
                  <StyledInput type="text" id="nombreEmpresa" name="nombreEmpresa" value={formData.nombreEmpresa} onChange={handleChange} />
                </InputWrapper>
                {errors.nombreEmpresa && <ErrorMessage>{errors.nombreEmpresa}</ErrorMessage>}
              </FieldContainer>

              <FieldContainer>
                <FieldLabel htmlFor="nit">NIT:</FieldLabel>
                <InputWrapper>
                  <IconContainer><FaBarcode /></IconContainer>
                  <StyledInput type="text" id="nit" name="nit" value={formData.nit} onChange={handleChange} />
                </InputWrapper>
                {errors.nit && <ErrorMessage>{errors.nit}</ErrorMessage>}
              </FieldContainer>

              <FieldContainer>
                <FieldLabel htmlFor="logo">Logo:</FieldLabel>
                <LogoUploadContainer htmlFor="logo">
                  {logoPreviewUrl ? (
                    <LogoImage src={logoPreviewUrl} alt="Vista previa del logo" />
                  ) : (
                    "Cargar logo"
                  )}
                  <HiddenInput 
                    type="file" 
                    id="logo" 
                    name="logo" 
                    accept="image/*" 
                    onChange={handleChange} 
                  />
                </LogoUploadContainer>
                {errors.logo && <ErrorMessage>{errors.logo}</ErrorMessage>}
              </FieldContainer>

              <ButtonContainer>
                <Button type="button" onClick={handleBack}>Volver</Button>
                <Button type="submit">Registrarme</Button>
              </ButtonContainer>
            </motion.div>
          )}
        </AnimatePresence>
      </Form>
    </Container>
  );
};

export default RegisterPage;
