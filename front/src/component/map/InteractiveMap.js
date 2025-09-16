import React, { useState, useEffect, useRef, useCallback } from 'react'
import 'mapbox-gl/dist/mapbox-gl.css';
import Map from "react-map-gl";
import Marker from "./Marker.js";

function InteractiveMap({ width, height, carsData, usersData }) {
    const mapRef = useRef();
    const [viewport, setViewport] = useState({
        latitude: 43.6536025,
        longitude: -79.4004877,
        zoom: 13,
        width: width,
        height: height,
    });

    const mapStyle = "mapbox://styles/mapbox/dark-v9";

    useEffect(() => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(position => {
                const { latitude, longitude } = position.coords;
                setViewport(prev => ({
                    ...prev,
                    latitude,
                    longitude
                }));
            });
        } else {
            console.log("Geolocation is not supported by this browser.");
        }
    }, []);

    useEffect(() => {
        setViewport(prev => ({
            ...prev,
            width: width,
            height: height
        }));
    }, [width, height]);

    const renderMarkers = useCallback((data, color) => {
        if (!data?.x) return null;
        return (
            <Marker
                xy={{ x: data.x, y: data.y }}
                color={color}
                key={data.id}
                text={""}
            />
        );
    }, []);

    const getBounds = useCallback(() => {
        if (!mapRef.current) return null;
        const rawBounds = mapRef.current.getMap().getBounds();
        return {
            lat: {
                high: rawBounds._ne.lat,
                low: rawBounds._sw.lat
            },
            lon: {
                high: rawBounds._ne.lng,
                low: rawBounds._sw.lng
            }
        };
    }, []);

    const withinBounds = useCallback((latLon) => {
        const bounds = getBounds();
        if (!bounds) return false;
        return (
            latLon.lat >= bounds.lat.low &&
            latLon.lat <= bounds.lat.high &&
            latLon.lon >= bounds.lon.low &&
            latLon.lon <= bounds.lon.high
        );
    }, [getBounds]);

    const onViewportChange = useCallback((newViewport) => {
        setViewport(prev => ({ ...prev, ...newViewport }));
    }, []);

    return (
        <Map
            ref={mapRef}
            mapboxAccessToken="pk.eyJ1IjoiZ2Vqb3NlIiwiYSI6ImNqMm8xZTg5ZjAyNHYzM3FieW14eGxvaGMifQ.DlQAXVocu-c7yXDxdTQ-tA"
            onMove={evt => setViewport(evt.viewState)}
            mapStyle={mapStyle}
            {...viewport}
        >
            {carsData && renderMarkers(carsData, "blue")}
            {usersData && renderMarkers(usersData, "red")}
        </Map>
    );
}

export default InteractiveMap
