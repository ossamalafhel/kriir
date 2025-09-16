import {CarsES, UsersES} from './api'
import {userActions, carActions} from './update'

const startup = () => {

    CarsES.onMessage((message) => {
        carActions.update(message)
    });

    UsersES.onMessage((message) => {
        userActions.update(message)
    });

};

export default startup
