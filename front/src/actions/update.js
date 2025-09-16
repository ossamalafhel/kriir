import store from '../reducers/store'
import { updateCars, updateUsers } from '../reducers/mobilitySlice'

const userActions = {
    update: (newUsersPosition) => {
        store.dispatch(updateUsers(newUsersPosition))
    },
};

const carActions = {
    update: (newCarsPosition) => {
        store.dispatch(updateCars(newCarsPosition))
    },
};

export { userActions, carActions }
