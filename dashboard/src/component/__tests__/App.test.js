import React from 'react';
import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { configureStore } from '@reduxjs/toolkit';
import { mobilitySlice } from '../../reducers/mobilitySlice';
import App from '../app';

// Mock the InteractiveMap component since it requires Mapbox
jest.mock('../map/InteractiveMap', () => {
  return function MockInteractiveMap() {
    return <div data-testid="interactive-map">Mocked Interactive Map</div>;
  };
});

// Create a test store
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

describe('App Component', () => {
  it('renders without crashing', () => {
    const store = createTestStore();
    render(
      <Provider store={store}>
        <App />
      </Provider>
    );
    
    expect(screen.getByTestId('interactive-map')).toBeInTheDocument();
  });

  it('renders the main app structure', () => {
    const store = createTestStore();
    const { container } = render(
      <Provider store={store}>
        <App />
      </Provider>
    );
    
    expect(container.querySelector('.App')).toBeInTheDocument();
  });

  it('provides store to child components', () => {
    const store = createTestStore({
      carsData: { id: 'test-car', x: 7.06064, y: 48.092971 },
      usersData: { id: 'test-user', x: 7.1, y: 48.1 },
    });
    
    render(
      <Provider store={store}>
        <App />
      </Provider>
    );
    
    // The InteractiveMap should receive the data from the store
    expect(screen.getByTestId('interactive-map')).toBeInTheDocument();
  });
});