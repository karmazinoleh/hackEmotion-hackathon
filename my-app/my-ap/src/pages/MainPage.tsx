import { useEffect, useState } from "react";
import Table from "../components/Table/Table.tsx";
import LevelBanner from "../components/LevelBanner/LevelBanner";
import NavigationMenu from "../components/NavigationMenu/NavigationMenu.tsx";
import WelcomeBack from "../components/WelcomeBack/WelcomeBack.tsx";
import {jwtDecode} from "jwt-decode";

type JwtPayload = {
    username: string;
};

const MainPage = () => {
    const [ratingData, setRatingData] = useState<any[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [username, setUsername] = useState<string>("Guest");

    // Функція для отримання імені користувача з токена
    const getUsernameFromToken = (): string => {
        const token = localStorage.getItem("token");
        if (!token) return "Guest";

        try {
            const decoded: JwtPayload = jwtDecode(token);
            return decoded.username || "Guest";
        } catch (error) {
            console.error("Invalid JWT token");
            return "Guest";
        }
    };

    useEffect(() => {
        setUsername(getUsernameFromToken()); // Зберігаємо ім'я користувача
    }, []);

    useEffect(() => {
        const fetchRatings = async () => {
            try {
                const response = await fetch("http://localhost:8088/api/rating");
                const data = await response.json();
                setRatingData(data);
            } catch (error) {
                console.error("Failed to load ratings:", error);
            } finally {
                setLoading(false);
            }
        };
        fetchRatings();
    }, []);

    const tableConfig = [
        { label: "Place", render: (row: any) => row.rank },
        { label: "User", render: (row: any) => row.user },
        { label: "Score", render: (row: any) => row.score },
    ];

    return (
        <div className="gigaDiv">
            <div>
                {loading ? (
                    <p>Loading...</p>
                ) : (
                    <Table config={tableConfig} data={ratingData} />
                )}
                <div style={{ display: "flex", justifyContent: "center", marginTop: "50px" }}>
                    <LevelBanner score={100} addedDatasets={10} ratedDatasets={5} />
                </div>
            </div>
            <NavigationMenu />
            <br />
            <WelcomeBack username={username} />
        </div>
    );
};

export default MainPage;
