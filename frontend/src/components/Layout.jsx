
import PropTypes from 'prop-types';
import Header from './Header';
import Footer from './Footer';
import styled from 'styled-components';

const Content = styled.main`
  flex: 1;
  padding: 1rem;
`;

const Layout = ({ children }) => {
  return (
    <>
      <Header />
      <Content>{children}</Content>
      <Footer />
    </>
  );
};

Layout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default Layout;
