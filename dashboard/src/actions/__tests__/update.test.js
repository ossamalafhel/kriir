import { configureStore } from '@reduxjs/toolkit';
import { mobilitySlice } from '../../reducers/mobilitySlice';
import { userActions, carActions } from '../update';

// Mock the store import in update.js
jest.mock('../../reducers/store', () => {
  const { configureStore } = require('@reduxjs/toolkit');
  const { mobilitySlice } = require('../../reducers/mobilitySlice');
  
  return configureStore({
    reducer: {
      mobility: mobilitySlice.reducer,
    },
  });
});

describe('Update Actions', () => {
  let mockStore;

  beforeEach(() => {
    // Create a fresh store for each test
    mockStore = configureStore({
      reducer: {
        mobility: mobilitySlice.reducer,
      },
    });
    
    // Replace the store in the actions module
    jest.doMock('../../reducers/store', () => mockStore);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('userActions', () => {
    it('should dispatch updateUsers action', () => {
      const userData = { id: 'user-1', x: 7.06064, y: 48.092971 };
      
      userActions.update(userData);
      
      const state = mockStore.getState().mobility;
      expect(state.usersData).toEqual(userData);
    });

    it('should handle multiple user updates', () => {
      const userData1 = { id: 'user-1', x: 7.0, y: 48.0 };
      const userData2 = { id: 'user-2', x: 7.1, y: 48.1 };
      
      userActions.update(userData1);
      expect(mockStore.getState().mobility.usersData).toEqual(userData1);
      
      userActions.update(userData2);
      expect(mockStore.getState().mobility.usersData).toEqual(userData2);
    });
  });

  describe('carActions', () => {
    it('should dispatch updateCars action', () => {
      const carData = { id: 'car-1', x: 7.06064, y: 48.092971 };
      
      carActions.update(carData);
      
      const state = mockStore.getState().mobility;
      expect(state.carsData).toEqual(carData);
    });

    it('should handle multiple car updates', () => {
      const carData1 = { id: 'car-1', x: 7.0, y: 48.0 };
      const carData2 = { id: 'car-2', x: 7.1, y: 48.1 };
      
      carActions.update(carData1);
      expect(mockStore.getState().mobility.carsData).toEqual(carData1);
      
      carActions.update(carData2);
      expect(mockStore.getState().mobility.carsData).toEqual(carData2);
    });
  });

  describe('combined actions', () => {
    it('should handle both user and car updates', () => {
      const userData = { id: 'user-1', x: 7.06064, y: 48.092971 };
      const carData = { id: 'car-1', x: 7.1, y: 48.1 };
      
      userActions.update(userData);
      carActions.update(carData);
      
      const state = mockStore.getState().mobility;
      expect(state.usersData).toEqual(userData);
      expect(state.carsData).toEqual(carData);
    });
  });
});