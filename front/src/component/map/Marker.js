import React from "react";
import { Marker as RMarker } from "react-map-gl";

import "./Marker.css";

export const MarkerSvg = ({ color }) => (
    <svg height="20" width="20">
        <circle cx="10" cy="10" r="5" stroke="black" strokeWidth="0" fill={color} />
    </svg>
);

const Marker = ({ color, text, xy }) => (
    <RMarker 
        latitude={xy.x} 
        longitude={xy.y} 
        anchor="center"
    >
        <div style={{ transform: 'translate(-10px, -10px)' }}>
            <MarkerSvg color={color} />
            <div className="marker">{text}</div>
        </div>
    </RMarker>
);

export default Marker;