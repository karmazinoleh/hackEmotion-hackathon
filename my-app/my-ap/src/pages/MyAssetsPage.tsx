import { useEffect, useState } from "react";
import Table from "../components/Table/Table.tsx";
import NavigationMenu from "../components/NavigationMenu/NavigationMenu.tsx";
import {jwtDecode} from "jwt-decode";

type JwtPayload = {
    username: string;
};

type Asset = {
    id: number;
    name: string;
    emotionNames: string[];
    //value: number;
};

const MyAssetsPage = () => {
    const [assets, setAssets] = useState<Asset[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [username, setUsername] = useState<string>("Guest");

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
        setUsername(getUsernameFromToken());
    }, []);

    useEffect(() => {
        const fetchUserAssets = async () => {
            try {
                const response = await fetch(`http://localhost:8088/asset/${username}`);
                const data = await response.json();
                setAssets(data);
            } catch (error) {
                console.error("Failed to load assets:", error);
            } finally {
                setLoading(false);
            }
        };

        if (username !== "Guest") {
            fetchUserAssets();
        }
    }, [username]);

    const tableConfig = [
        { label: "Asset ID", render: (row: Asset) => row.id },
        { label: "Name", render: (row: Asset) => row.name },
        { label: "Emotions", render: (row: Asset) => row.emotionNames.join(", ") },
        //{ label: "Value", render: (row: Asset) => `$${row.value.toFixed(2)}` },
    ];

    return (
        <div className="assetsPage">
            <NavigationMenu />
            <h1>My Assets</h1>
            {loading ? (
                <p>Loading assets...</p>
            ) : assets.length > 0 ? (
                <Table config={tableConfig} data={assets} />
            ) : (
                <p>No assets found for {username}.</p>
            )}
        </div>
    );
};

export default MyAssetsPage;
