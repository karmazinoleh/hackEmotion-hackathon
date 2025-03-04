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
import MyAssetsPage from "./pages/MyAssetsPage.tsx";
import RatePage from "./pages/RatePage.tsx";


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
                                <MainPage/>
                            </AuthWrapper>
                       </PrivateRoute>
                    }
                />

                <Route
                    path="/my-assets"
                    element={
                        <PrivateRoute>
                            <AuthWrapper>
                                <MyAssetsPage/>
                            </AuthWrapper>
                        </PrivateRoute>
                    }
                />

                <Route
                    path="/rate"
                    element={
                        <PrivateRoute>
                            <AuthWrapper>
                                <RatePage/>
                            </AuthWrapper>
                        </PrivateRoute>
                    }
                />

                <Route
                    path="/upload-emotions"
                    element={
                        <PrivateRoute>
                            <AuthWrapper>
                                <UploadPage/>
                            </AuthWrapper>
                        </PrivateRoute>
                    }
                />

            </Routes>
        </BrowserRouter>
    );
};

export default App;
