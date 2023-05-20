import React, { useState } from 'react';
import api from '../axios/api';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { login } from '../redux/userSlice';

const LoginPage = () => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const dispatch = useDispatch();

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response =await api.get("/user", {
                auth: {
                    username: username,
                    password: password,
                },
            });

            // Salva l'Authorization Header in Session Storage
            const authHeader = response.headers.authorization;
            if (authHeader) {
                window.sessionStorage.setItem("Authorization", authHeader);
            }
        
            dispatch(login(response.data));
            console.log(response.data);

            navigate("/home");
        } catch (error) {
            console.error("Something went wrong!", error);
        }
    };


    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                <label>
                Username:
                <input type="text" value={username} onChange={e => setUsername(e.target.value)} required />
                </label>
                <label>
                Password:
                <input type="password" value={password} onChange={e => setPassword(e.target.value)} required />
                </label>
                <button type="submit">Submit</button>
            </form>
        </div>
    );
    
}

export default LoginPage;