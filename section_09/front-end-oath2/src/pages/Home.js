import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div className="App">
            <header className="App-header">
                <Link to='/myAccount'>myAccount</Link>
                <Link to='/myBalance'>myBalance</Link>
            </header>
        </div>  
    )
}

export default Home;