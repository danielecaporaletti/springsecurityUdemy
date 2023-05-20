import './App.css';
import Home from './pages/Home';
import LoginPage from './pages/LoginPage';
import Notices from './pages/Notices';
import MyApp from './pages/MyApp';
import Balance from './pages/Balance';
import Account from './pages/Account';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import store from './redux/store';
import { Provider } from 'react-redux';

function App() {
    return (
        <Provider store={store}>
            <Router>
                <Routes>
                    <Route path='/' element={<MyApp />} />
                    <Route path='/notices' element={<Notices />} />
                    <Route path='/login' element={<LoginPage />} />
                    <Route path='/home' element={<Home />} />
                    <Route path='/myAccount' element={<Account />} />
                    <Route path='/myBalance' element={<Balance />} />
                </Routes>
            </Router>
        </Provider>
    );
}


export default App;
