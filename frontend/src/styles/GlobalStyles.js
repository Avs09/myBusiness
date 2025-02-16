import { createGlobalStyle } from 'styled-components';

const GlobalStyles = createGlobalStyle`
  * {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }

  body {
    font-family: 'Arial', sans-serif;
    min-height: 100vh;
    overflow-x: hidden;
    background-color: #f5f5f5;
  }

  #root {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
  }
`;

export default GlobalStyles;
