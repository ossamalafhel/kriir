import React from 'react'
import Comp from './base/comp'
import InteractiveMap from './map/InteractiveMap'

class AppContent extends Comp {

    constructor(props) {
        super(props);
        this.state = {
            windowDimensions: {
                height: 100,
                width: 100
            }
        };
    }

    componentDidMount() {
        this._updateDimensions();
        window.addEventListener("resize", this._updateDimensions);
    }

    _updateDimensions = () => {
        this.setState({
            windowDimensions: {
                height: window.innerHeight,
                width: window.innerWidth
            },
        });
    };

    render() {
        return (
            <section>
                {this._content()}
            </section>
        )
    }

    _content() {
        const state = this.context.store.getState();
        // console.log("state:", state);
        const usersData = state.usersData;
        const carsData = state.carsData;
        return (
            <section>
                <InteractiveMap
                    carsData={carsData}
                    usersData={usersData}
                    height={this.state.windowDimensions.height}
                    width={this.state.windowDimensions.width}
                />
            </section>
        )
    }
}

export default AppContent
