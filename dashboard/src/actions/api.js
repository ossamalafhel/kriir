const c = console;
import config from '../config'

const host = config.hostFull

const CarsEventSourceApi = {

  _init: () => {
    const eventSource = new EventSource(`${host}/cars`);
    return eventSource
  },

  onMessage: (callback) => {
    const connection = CarsEventSourceApi._init()

    connection.addEventListener('message', (sseMessage) => {
      var data = sseMessage.data;
      data = JSON.parse(data);
      callback(data)
    });

    connection.addEventListener('error', () => {
      c.log('server disconnected');
      connection.close()
    })
  }

};

const UsersEventSourceApi = {

    _init: () => {
        const eventSource = new EventSource(`${host}/users`);
        return eventSource
    },

    onMessage: (callback) => {
        const connection = UsersEventSourceApi._init();

        connection.addEventListener('message', (sseMessage) => {
            var data = sseMessage.data;
            data = JSON.parse(data);
            callback(data)
        });

        connection.addEventListener('error', () => {
            c.log('server disconnected');
            connection.close()
        })
    }

};

const CarsES = CarsEventSourceApi;
const UsersES = UsersEventSourceApi;

export {CarsES, UsersES}

