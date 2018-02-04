import React, {Component} from 'react'
import 'mapbox-gl/dist/mapbox-gl.css';
import MapGL from "react-map-gl";
import Marker from "./Marker.js";


class InteractiveMap extends Component {
    constructor(props) {
        super(props);
        this.state = {
            viewport: {
                latitude: 43.6536025,
                longitude: -79.4004877,
                zoom: 13,
                width: this.props.width,
                height: this.props.height,
                startDragLngLat: null,
                isDragging: null
            },
            mapStyle: "mapbox://styles/mapbox/dark-v9",
            carsData: {},
            usersData: {}
        };
    }

    componentDidMount() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position =>
                this._recenterIfToronto(position.coords)
            );
        } else {
            console.log("nope");
        }
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.carsData) {
            const newState = this.state;
            newState.carsData = nextProps.carsData;
            this.setState(newState);
        }
        if (nextProps.usersData) {
            const newState = this.state;
            newState.usersData = nextProps.usersData;
            this.setState(newState);
        }
        if (nextProps.height) {
            const newState = this.state;
            newState.viewport.height = nextProps.height;
            this.setState(newState);
        }
        if (nextProps.width) {
            const newState = this.state;
            newState.viewport.width = nextProps.width;
            this.setState(newState);
        }
    }

    render() {
        const {mapStyle, viewport, carsData, usersData} = this.state;
        return (
            <MapGL
                mapboxApiAccessToken="pk.eyJ1IjoiZ2Vqb3NlIiwiYSI6ImNqMm8xZTg5ZjAyNHYzM3FieW14eGxvaGMifQ.DlQAXVocu-c7yXDxdTQ-tA"
                onChangeViewport={this._onChangeViewport}
                mapStyle={mapStyle}
                ref={map => (this.map = map)}
                {...viewport}
            >
                {carsData ? this._markers(carsData, "blue") : null}
                {usersData ? this._markers(usersData, "red") : null}
                {/**<Legend/>**/}
            </MapGL>
        );
    }

    _markers(data, color) {
        if(!data.x) return;
        const rows =
            <Marker
                xy={{ x: data.x, y: data.y }}
                color={color}
                key={data.id}
                text={""}
            />;
        const isEmpty = Array.isArray(rows) && rows.length === 0
        return (isEmpty ? null : rows)
    }

    _recenter = coordinates => {
        const {latitude, longitude} = coordinates;
        const newViewport = {latitude, longitude};
        const viewport = Object.assign({}, this.state.viewport, newViewport);
        this.setState({viewport});
    };

    _recenterIfToronto = coordinates => {
        this._recenter(coordinates);
    };

    _getBounds = () => {
        const rawBounds = this.map._getMap().getBounds();
        const bounds = {
            lat: {
                high: rawBounds._ne.lat,
                low: rawBounds._sw.lat
            },
            lon: {
                high: rawBounds._ne.lng,
                low: rawBounds._sw.lng
            }
        };
        return bounds;
    };

    _withinBounds = latLon => {
        return (
            latLon.lat >= this._getBounds().lat.low &&
            latLon.lat <= this._getBounds().lat.high &&
            latLon.lon >= this._getBounds().lon.low &&
            latLon.lon <= this._getBounds().lon.high
        );
    };

    _onChangeViewport = newViewport => {
        const viewport = Object.assign({}, this.state.viewport, newViewport);
        this.setState({viewport});
    };

}

export default InteractiveMap
