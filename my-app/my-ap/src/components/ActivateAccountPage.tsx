import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import axios from 'axios';

const ActivateAccountPage: React.FC = () => {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const [token, setToken] = useState(searchParams.get('token') || '');
    const [message, setMessage] = useState('');

    const handleTokenChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setToken(e.target.value);
    };

    const activateAccount = async () => {
        if (!token) {
            setMessage('Please enter a valid activation token.');
            return;
        }

        try {
            await axios.get(`http://localhost:8088/auth/activate-account?token=${token}`);
            setMessage('Account activated successfully! Redirecting to login...');
            setTimeout(() => navigate('/login'), 3000);
        } catch (error) {
            setMessage('Failed to activate account. Please try again.');
        }
    };

    return (
        <div style={{ textAlign: 'center', padding: '20px' }}>
            <h1>Activate Your Account</h1>
            <p>Enter your activation token below and activate your account.</p>

            <input
                type="text"
                value={token}
                onChange={handleTokenChange}
                placeholder="Enter activation token"
                style={{
                    padding: '10px',
                    width: '80%',
                    maxWidth: '400px',
                    marginBottom: '10px',
                    border: '1px solid #ccc',
                    borderRadius: '5px'
                }}
            />
            <br />

            <button
                onClick={activateAccount}
                style={{
                    padding: '10px 20px',
                    backgroundColor: '#28a745',
                    color: 'white',
                    border: 'none',
                    borderRadius: '5px',
                    cursor: 'pointer',
                    margin: '10px'
                }}
            >
                Activate Account
            </button>

            <button
                onClick={() => navigate('/login')}
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

            {message && <p style={{ marginTop: '20px', color: 'red' }}>{message}</p>}
        </div>
    );
};

export default ActivateAccountPage;
