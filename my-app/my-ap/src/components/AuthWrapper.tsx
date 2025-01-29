import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

interface JwtPayload {
    exp: number;
}

function isTokenExpired(token: string): boolean {
    try {
        const decoded: JwtPayload = jwtDecode(token);
        const currentTime = Math.floor(Date.now() / 1000);
        return decoded.exp < currentTime;
    } catch (error) {
        return true;
    }
}

const AuthWrapper = ({ children }: { children: React.ReactNode }) => {
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    useEffect(() => {
        if (!token || isTokenExpired(token)) {
            localStorage.removeItem("token");
            navigate("/login");
        }
    }, [token, navigate]);

    return <>{children}</>;
};

export default AuthWrapper;