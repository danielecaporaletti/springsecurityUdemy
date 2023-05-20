import React, { useEffect } from 'react';
import api from '../axios/api';
import { useSelector } from 'react-redux';

const Balance = () => {

    const userId = useSelector(state => state.user.id);

    useEffect(() => {
        const fetchAccountData = async () => {
            try {
                const response = await api.get(`/myBalance?id=${userId}`);
                // Print the response data to the console
                console.log(response.data);
            } catch (error) {
                console.error("Something went wrong!", error);
            }
        };

        fetchAccountData();
    }, [userId]);
  
    return (
        <div>Balance in console</div>
    )
}

export default Balance