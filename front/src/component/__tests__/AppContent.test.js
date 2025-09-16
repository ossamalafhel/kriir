import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import { mobilitySlice } from '../../reducers/mobilitySlice';
import AppContent from '../app_content';

// Mock the InteractiveMap component
jest.mock('../map/InteractiveMap', () => {
  return function MockInteractiveMap({ carsData, usersData, height, width }) {
    return (
      <div data-testid="interactive-map">
        <div data-testid="cars-data">{JSON.stringify(carsData)}</div>
        <div data-testid="users-data">{JSON.stringify(usersData)}</div>
        <div data-testid="dimensions">{`${width}x${height}`}</div>
      </div>
    );
  };
});

const createTestStore = (initialState = {}) => {
  return configureStore({
    reducer: {
      mobility: mobilitySlice.reducer,
    },
    preloadedState: {
      mobility: {
        usersData: null,
        carsData: null,
        loading: false,
        error: null,
        ...initialState,
      },
    },
  });
};

// Mock window dimensions
Object.defineProperty(window, 'innerHeight', {
  writable: true,
  configurable: true,
  value: 768,
});

Object.defineProperty(window, 'innerWidth', {
  writable: true,
  configurable: true,
  value: 1024,
});

describe('AppContent Component', () => {
  it('renders without crashing', () => {
    const store = createTestStore();
    render(
      <Provider store={store}>
        <AppContent />
      </Provider>
    );
    
    expect(screen.getByTestId('interactive-map')).toBeInTheDocument();
  });

  it('passes window dimensions to InteractiveMap', () => {
    const store = createTestStore();
    render(
      <Provider store={store}>
        <AppContent />
      </Provider>
    );
    
    expect(screen.getByTestId('dimensions')).toHaveTextContent('1024x768');
  });

  it('passes cars and users data to InteractiveMap', () => {
    const carsData = { id: 'test-car', x: 7.06064, y: 48.092971 };
    const usersData = { id: 'test-user', x: 7.1, y: 48.1 };
    
    const store = createTestStore({
      carsData,
      usersData,
    });
    
    render(
      <Provider store={store}>
        <AppContent />
      </Provider>
    );
    
    expect(screen.getByTestId('cars-data')).toHaveTextContent(JSON.stringify(carsData));
    expect(screen.getByTestId('users-data')).toHaveTextContent(JSON.stringify(usersData));
  });

  it('updates dimensions on window resize', () => {
    const store = createTestStore();
    render(
      <Provider store={store}>
        <AppContent />
      </Provider>
    );
    
    // Change window dimensions
    window.innerWidth = 1200;
    window.innerHeight = 800;
    
    // Trigger resize event
    fireEvent(window, new Event('resize'));
    
    // Check if dimensions updated
    expect(screen.getByTestId('dimensions')).toHaveTextContent('1200x800');
  });

  it('handles null data gracefully', () => {
    const store = createTestStore({
      carsData: null,
      usersData: null,
    });
    
    render(
      <Provider store={store}>
        <AppContent />
      </Provider>
    );
    
    expect(screen.getByTestId('cars-data')).toHaveTextContent('null');
    expect(screen.getByTestId('users-data')).toHaveTextContent('null');
  });
});