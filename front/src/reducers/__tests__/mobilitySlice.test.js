import { configureStore } from '@reduxjs/toolkit';
import { mobilitySlice, updateCars, updateUsers, setLoading, setError, clearError } from '../mobilitySlice';

describe('mobilitySlice', () => {
  let store;

  beforeEach(() => {
    store = configureStore({
      reducer: {
        mobility: mobilitySlice.reducer,
      },
    });
  });

  it('should have correct initial state', () => {
    const state = store.getState().mobility;
    expect(state).toEqual({
      usersData: null,
      carsData: null,
      loading: false,
      error: null,
    });
  });

  it('should update cars data', () => {
    const carsData = { id: 'test-car', x: 7.06064, y: 48.092971 };
    
    store.dispatch(updateCars(carsData));
    
    const state = store.getState().mobility;
    expect(state.carsData).toEqual(carsData);
    expect(state.loading).toBe(false);
    expect(state.error).toBe(null);
  });

  it('should update users data', () => {
    const usersData = { id: 'test-user', x: 7.1, y: 48.1 };
    
    store.dispatch(updateUsers(usersData));
    
    const state = store.getState().mobility;
    expect(state.usersData).toEqual(usersData);
    expect(state.loading).toBe(false);
    expect(state.error).toBe(null);
  });

  it('should set loading state', () => {
    store.dispatch(setLoading(true));
    
    const state = store.getState().mobility;
    expect(state.loading).toBe(true);
  });

  it('should set error state', () => {
    const error = 'Something went wrong';
    
    store.dispatch(setError(error));
    
    const state = store.getState().mobility;
    expect(state.error).toBe(error);
    expect(state.loading).toBe(false);
  });

  it('should clear error state', () => {
    // First set an error
    store.dispatch(setError('Some error'));
    expect(store.getState().mobility.error).toBe('Some error');
    
    // Then clear it
    store.dispatch(clearError());
    
    const state = store.getState().mobility;
    expect(state.error).toBe(null);
  });

  it('should handle multiple updates correctly', () => {
    const carsData = { id: 'car-1', x: 7.06064, y: 48.092971 };
    const usersData = { id: 'user-1', x: 7.1, y: 48.1 };
    
    store.dispatch(updateCars(carsData));
    store.dispatch(updateUsers(usersData));
    
    const state = store.getState().mobility;
    expect(state.carsData).toEqual(carsData);
    expect(state.usersData).toEqual(usersData);
    expect(state.loading).toBe(false);
    expect(state.error).toBe(null);
  });

  it('should override previous data when updating', () => {
    const firstCarsData = { id: 'car-1', x: 7.0, y: 48.0 };
    const secondCarsData = { id: 'car-2', x: 7.1, y: 48.1 };
    
    store.dispatch(updateCars(firstCarsData));
    expect(store.getState().mobility.carsData).toEqual(firstCarsData);
    
    store.dispatch(updateCars(secondCarsData));
    expect(store.getState().mobility.carsData).toEqual(secondCarsData);
  });
});