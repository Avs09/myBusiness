import styled from 'styled-components';
import bannerControl from '../assets/1.jpg';
import bannerSteps from '../assets/1.jpg';

export const Container = styled.div`
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #fdfdfd;
`;

export const HeroSection = styled.section`
  padding: 120px 20px;
  text-align: center;
  background: linear-gradient(135deg, #ffffff, #f8f8f8);
`;

export const HeroText = styled.div`
  h1 {
    font-size: 3.5rem;
    margin-bottom: 20px;
    color: #2c3e50;
    font-family: 'Playfair Display', serif;
  }
  p {
    font-size: 1.25rem;
    margin-bottom: 40px;
    color: #555;
    font-family: 'Georgia', serif;
    line-height: 1.5;
  }
`;

export const HeroButton = styled.a`
  display: inline-block;
  padding: 14px 32px;
  background: #2c3e50;
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 30px;
  text-decoration: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  &:hover {
    background: #34495e;
    transform: translateY(-3px);
  }
`;

export const FeaturesSection = styled.section`
  padding: 80px 20px;
  display: flex;
  justify-content: space-around;
  background-color: #fff;
  flex-wrap: wrap;
`;

export const Feature = styled.div`
  text-align: center;
  width: 280px;
  margin: 20px;
  h3 {
    font-size: 1.5rem;
    margin: 20px 0 10px;
    color: #2c3e50;
    font-family: 'Georgia', serif;
  }
  p {
    font-size: 1rem;
    color: #555;
    line-height: 1.6;
  }
`;

// Sección banner para features
export const SectionBanner = styled.div`
  width: 100%;
  height: 300px;
  background-image: url(${bannerControl});
  background-position: center;
  background-size: cover;
  margin-bottom: 40px;
  position: relative;
  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: rgba(44, 62, 80, 0.5);
  }
`;

// Sección banner para "Cómo Funciona"
export const StepsBanner = styled.div`
  width: 100%;
  height: 300px;
  background-image: url(${bannerSteps});
  background-position: center;
  background-size: cover;
  margin-bottom: 40px;
  position: relative;
  &::after {
    content: '';
    position: absolute;
    inset: 0;
    background: rgba(44, 62, 80, 0.5);
  }
`;

export const HowItWorksSection = styled.section`
  padding: 80px 20px;
  background-color: #f7f7f7;
  text-align: center;
`;

export const StepsContainer = styled.div`
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 40px;
  margin-top: 40px;
`;

export const Step = styled.div`
  width: 280px;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

export const StepIcon = styled.div`
  font-size: 50px;
  color: #2c3e50;
  margin-bottom: 20px;
`;

export const StepTitle = styled.h3`
  font-size: 1.5rem;
  margin-bottom: 10px;
  color: #2c3e50;
  font-family: 'Georgia', serif;
`;

export const StepDescription = styled.p`
  font-size: 1rem;
  color: #555;
  line-height: 1.6;
`;
