import { configureStore } from '@reduxjs/toolkit'
import { mobilitySlice } from './mobilitySlice'

const store = configureStore({
  reducer: {
    mobility: mobilitySlice.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST'],
      },
    }),
  devTools: process.env.NODE_ENV !== 'production',
})

export default store
export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
