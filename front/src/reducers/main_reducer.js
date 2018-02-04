import defaultState from './main_state'

const mainReducer = (state = defaultState, action) => {
    switch (action.type) {
        case "CARS_UPDATE":
            state.carsData = action;
            return state;
        case "USERS_UPDATE":
            state.usersData = action;
            return state;
        default:
            return state;
    }
}

export default mainReducer
