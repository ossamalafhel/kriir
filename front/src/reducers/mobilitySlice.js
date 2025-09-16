import { createSlice } from '@reduxjs/toolkit'

const initialState = {
  usersData: null,
  carsData: null,
  loading: false,
  error: null,
}

export const mobilitySlice = createSlice({
  name: 'mobility',
  initialState,
  reducers: {
    updateCars: (state, action) => {
      state.carsData = action.payload
      state.loading = false
      state.error = null
    },
    updateUsers: (state, action) => {
      state.usersData = action.payload
      state.loading = false
      state.error = null
    },
    setLoading: (state, action) => {
      state.loading = action.payload
    },
    setError: (state, action) => {
      state.error = action.payload
      state.loading = false
    },
    clearError: (state) => {
      state.error = null
    }
  },
})

// Action creators are generated for each case reducer function
export const { updateCars, updateUsers, setLoading, setError, clearError } = mobilitySlice.actions

// Selectors
export const selectCarsData = (state) => state.mobility.carsData
export const selectUsersData = (state) => state.mobility.usersData
export const selectLoading = (state) => state.mobility.loading
export const selectError = (state) => state.mobility.error

export default mobilitySlice.reducer