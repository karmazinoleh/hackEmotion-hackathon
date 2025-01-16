// RegisterPage.tsx
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const RegisterPage: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [username, setUsername] = useState('');
    const [fullName, setFullName] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const navigate = useNavigate();

    const handleRegister = async () => {
        if (password !== confirmPassword) {
            alert('Passwords do not match!');
            return;
        }

        try {
            await axios.post('http://localhost:8088/auth/register', {
                username,
                fullName,
                email,
                password
            });
            alert('Registration successful! Please log in.');
            navigate('/login');
        } catch (error) {
            alert('Registration failed!');
        }
    };

    return (
        <div>
            <h1>Register</h1>
            <input
                type="text"
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="Email"
            />
            <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="Password"
            />
            <input
                type="username"
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="Username"
            />
            <input
                type="fullName"
                value={fullName}
                onChange={e => setFullName(e.target.value)}
                placeholder="Full Name"
            />
            <input
                type="password"
                value={confirmPassword}
                onChange={e => setConfirmPassword(e.target.value)}
                placeholder="Confirm Password"
            />
            <button onClick={handleRegister}>Register</button>
        </div>
    );
};

export default RegisterPage;
