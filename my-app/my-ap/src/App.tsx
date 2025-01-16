import React from "react";
import UploadPage from "./UploadPage.tsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import PrivateRoute from "./PrivateRoute.tsx";
import LoginPage from "./components/LoginPage.tsx";
import RegisterPage from "./components/RegisterPage.tsx";
import ActivateAccountPage from "./components/ActivateAccountPage.tsx";
import Hello from "./components/hello/Hello.tsx";


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
                            <UploadPage />
                        </PrivateRoute>
                    }
                />
                <Route path="/hello" element={<Hello userName={"Oleh"} />} />
            </Routes>
        </BrowserRouter>
    );
};

export default App;
