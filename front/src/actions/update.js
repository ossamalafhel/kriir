import store from '../reducers/store'

const userActions = {
    update: (newUsersPosition) => {
        store.dispatch({type: "USERS_UPDATE", id:newUsersPosition.id, x: newUsersPosition.x, y: newUsersPosition.y})
    },
};

const carActions = {
    update: (newCarsPosition) => {
        store.dispatch({type: "CARS_UPDATE", id: newCarsPosition.id, x: newCarsPosition.x, y: newCarsPosition.y})
    },
};

export  {userActions, carActions}
