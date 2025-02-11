import "./NavigationMenu.css";
import React from "react";
import {useNavigate} from "react-router-dom";


const LevelBanner: React.FC = () => {
    const navigate = useNavigate();

    return (
        <div className="navigation">
            <button className="rate" onClick={() => navigate('/rate')}>Rate</button>
            <button onClick={() => navigate('/my-profile')}>My profile</button>
            <button onClick={() => navigate('/settings')}>Settings</button>
            <button onClick={() => navigate('/my-assets')}>My assets</button>
            <button onClick={() => navigate('/')}>Home</button>
        </div>
    );
};

export default LevelBanner;
