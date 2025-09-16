import React, { useState, useEffect } from 'react'
import { useSelector } from 'react-redux'
import { selectCarsData, selectUsersData } from '../reducers/mobilitySlice'
import InteractiveMap from './map/InteractiveMap'

function AppContent() {
    const [windowDimensions, setWindowDimensions] = useState({
        height: window.innerHeight,
        width: window.innerWidth
    });

    const usersData = useSelector(selectUsersData);
    const carsData = useSelector(selectCarsData);

    useEffect(() => {
        const updateDimensions = () => {
            setWindowDimensions({
                height: window.innerHeight,
                width: window.innerWidth
            });
        };

        window.addEventListener("resize", updateDimensions);
        
        return () => {
            window.removeEventListener("resize", updateDimensions);
        };
    }, []);

    return (
        <section>
            <InteractiveMap
                carsData={carsData}
                usersData={usersData}
                height={windowDimensions.height}
                width={windowDimensions.width}
            />
        </section>
    )
}

export default AppContent
