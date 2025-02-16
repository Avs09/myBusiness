
const LoginPage = () => {
  return (
    <div>
      <h1>Iniciar Sesión</h1>
      <form>
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
        <button type="submit">Ingresar</button>
      </form>
    </div>
  );
};

export default LoginPage;
