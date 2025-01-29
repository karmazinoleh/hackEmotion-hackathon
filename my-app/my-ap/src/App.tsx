import React from "react";
import UploadPage from "./pages/UploadPage.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import PrivateRoute from "./PrivateRoute.tsx";
import LoginPage from "./pages/LoginPage.tsx";
import RegisterPage from "./pages/RegisterPage.tsx";
import ActivateAccountPage from "./pages/ActivateAccountPage.tsx";
import Hello from "./components/hello/Hello.tsx";
import AuthWrapper from "./components/AuthWrapper.tsx";


const App: React.FC = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/activate-account" element={<ActivateAccountPage />} />
                <Route
                    path="/"
                    element={
                        <PrivateRoute>
                            <AuthWrapper>
                                <UploadPage />
                            </AuthWrapper>
                        </PrivateRoute>
                    }
                />
                <Route path="/hello" element={<Hello userName={"Oleh"} />} />
            </Routes>
        </BrowserRouter>
    );
};

export default App;
