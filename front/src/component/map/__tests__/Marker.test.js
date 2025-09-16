import React from 'react';
import { render, screen } from '@testing-library/react';
import Marker, { MarkerSvg } from '../Marker';

// Mock react-map-gl Marker component
jest.mock('react-map-gl', () => ({
  Marker: ({ children, latitude, longitude, anchor }) => (
    <div 
      data-testid="map-marker" 
      data-latitude={latitude} 
      data-longitude={longitude}
      data-anchor={anchor}
    >
      {children}
    </div>
  ),
}));

describe('MarkerSvg Component', () => {
  it('renders SVG with correct color', () => {
    const { container } = render(<MarkerSvg color="red" />);
    const circle = container.querySelector('circle');
    
    expect(circle).toBeInTheDocument();
    expect(circle).toHaveAttribute('fill', 'red');
    expect(circle).toHaveAttribute('cx', '10');
    expect(circle).toHaveAttribute('cy', '10');
    expect(circle).toHaveAttribute('r', '5');
  });

  it('renders SVG with blue color', () => {
    const { container } = render(<MarkerSvg color="blue" />);
    const circle = container.querySelector('circle');
    
    expect(circle).toHaveAttribute('fill', 'blue');
  });
});

describe('Marker Component', () => {
  const mockProps = {
    color: 'red',
    text: 'Test Marker',
    xy: { x: 7.06064, y: 48.092971 },
  };

  it('renders marker with correct coordinates', () => {
    render(<Marker {...mockProps} />);
    
    const marker = screen.getByTestId('map-marker');
    expect(marker).toBeInTheDocument();
    expect(marker).toHaveAttribute('data-latitude', '7.06064');
    expect(marker).toHaveAttribute('data-longitude', '48.092971');
    expect(marker).toHaveAttribute('data-anchor', 'center');
  });

  it('renders marker with text', () => {
    render(<Marker {...mockProps} />);
    
    expect(screen.getByText('Test Marker')).toBeInTheDocument();
  });

  it('renders marker with correct color', () => {
    const { container } = render(<Marker {...mockProps} />);
    const circle = container.querySelector('circle');
    
    expect(circle).toHaveAttribute('fill', 'red');
  });

  it('handles empty text', () => {
    const propsWithEmptyText = { ...mockProps, text: '' };
    render(<Marker {...propsWithEmptyText} />);
    
    const marker = screen.getByTestId('map-marker');
    expect(marker).toBeInTheDocument();
  });

  it('handles different coordinates', () => {
    const propsWithDifferentCoords = {
      ...mockProps,
      xy: { x: -74.006, y: 40.7128 }, // New York coordinates
    };
    
    render(<Marker {...propsWithDifferentCoords} />);
    
    const marker = screen.getByTestId('map-marker');
    expect(marker).toHaveAttribute('data-latitude', '-74.006');
    expect(marker).toHaveAttribute('data-longitude', '40.7128');
  });
});