import React from 'react';
import { Link } from 'react-router-dom';

const MyApp = () => {
  return (
    <div className="App">
        <header className="App-header">
            <Link to='/notices'>Notizie</Link>
            <Link to='/login'>Login</Link>
        </header>
    </div>
  )
}

export default MyApp