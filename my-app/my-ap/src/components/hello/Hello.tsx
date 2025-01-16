import React from 'react';
import './Hello.css';

interface HelloProps {
    userName: string;
}

const Hello: React.FC<HelloProps> = ({ userName }) => {
    return (
        <div className="hello-container">
            <span className="welcome-text">Welcome back,</span>
            <span className="user-name">{userName}</span>
        </div>
    );
}

export default Hello;
