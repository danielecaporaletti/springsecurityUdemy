import { createSlice } from '@reduxjs/toolkit';

const initialState = {
    id: null,
    name: '',
    email: '',
    mobileNumber: '',
    role: '',
    createDt: '',
    isAuthenticated: false,
};

const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        login(state, action) {
            state.id = action.payload.id;
            state.name = action.payload.name;
            state.email = action.payload.email;
            state.mobileNumber = action.payload.mobileNumber;
            state.role = action.payload.role;
            state.createDt = action.payload.createDt;
            state.isAuthenticated = true;
        },
        logout(state) {
            state.id = null;
            state.name = '';
            state.email = '';
            state.mobileNumber = '';
            state.role = '';
            state.createDt = '';
            state.isAuthenticated = false;
        },
    },
});
  
  export const { login, logout } = userSlice.actions;
  export default userSlice.reducer;
