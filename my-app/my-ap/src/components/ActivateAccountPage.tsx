import React from 'react';
import { useNavigate } from 'react-router-dom';

const ActivateAccountPage: React.FC = () => {
    const navigate = useNavigate();

    const loginRedirect = () => {
        navigate('/login');
    };

    return (
        <div style={{ textAlign: 'center', padding: '20px' }}>
            <h1>Account Activated Successfully!</h1>
            <p>Your account has been successfully activated. You can now log in and start using our services.</p>
            <button
                onClick={loginRedirect}
                style={{
                    padding: '10px 20px',
                    backgroundColor: '#007bff',
                    color: 'white',
                    border: 'none',
                    borderRadius: '5px',
                    cursor: 'pointer'
                }}
            >
                Go to Login Page
            </button>
        </div>
    );
};

export default ActivateAccountPage;
