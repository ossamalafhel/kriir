import React from "react";
import Comp from './base/comp.js'
import AppContent from './app_content'
import storeInstance from '../reducers/store'
import Provider from './provider'

class App extends Comp {

    render() {
        return (
            <div className="App">
                <Provider store={storeInstance}>
                    <AppContent />
                </Provider>
            </div>
        );
    }
}

export default App;
