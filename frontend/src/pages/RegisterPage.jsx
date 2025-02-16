

const RegisterPage = () => {
  return (
    <div>
      <h1>Registro</h1>
      <form>
        <label>
          Nombre Completo:
          <input type="text" name="fullName" />
        </label>
        <br />
        <label>
          Correo Electrónico:
          <input type="email" name="email" />
        </label>
        <br />
        <label>
          Contraseña:
          <input type="password" name="password" />
        </label>
        <br />
        <button type="submit">Registrarse</button>
      </form>
    </div>
  );
};

export default RegisterPage;
