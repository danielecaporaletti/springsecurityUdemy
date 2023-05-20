import React, { useEffect } from 'react';
import axios from 'axios';

const Notices = () => {

    useEffect(() => {
      const fetchData = async () => {
        try {
          const result = await axios('http://localhost:8080/notices');
          console.log(result.data);
        } catch (error) {
          console.error("Errore durante il fetch dei dati: ", error);
        }
      };
  
      fetchData();
    }, []);

  return (
    <div>Notices in console</div>
  )
}

export default Notices;