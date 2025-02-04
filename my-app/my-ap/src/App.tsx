import React from "react";
//import UploadPage from "./pages/UploadPage.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import PrivateRoute from "./PrivateRoute.tsx";
import LoginPage from "./pages/LoginPage.tsx";
import RegisterPage from "./pages/RegisterPage.tsx";
import ActivateAccountPage from "./pages/ActivateAccountPage.tsx";
import AuthWrapper from "./components/AuthWrapper.tsx";
import MainPage from "./pages/MainPage.tsx";
import UploadPage from "./pages/UploadPage.tsx";


const App: React.FC = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/activate-account" element={<ActivateAccountPage />} />
                <Route path="/upload-emotions" element={<UploadPage />} />
                <Route
                    path="/"
                    element={
                        <PrivateRoute>
                            <AuthWrapper>
                                {/*<UploadPage />*/}
                                <MainPage/>
                            </AuthWrapper>
                       </PrivateRoute>
                    }
                />
            </Routes>
        </BrowserRouter>
    );
};

export default App;
