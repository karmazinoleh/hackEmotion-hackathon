import { useEffect, useState } from "react";
import Table from "../components/Table/Table.tsx";
import LevelBanner from "../components/LevelBanner/LevelBanner";
import NavigationMenu from "../components/NavigationMenu/NavigationMenu.tsx";
import WelcomeBack from "../components/WelcomeBack/WelcomeBack.tsx";

const MainPage = () => {
    const [ratingData, setRatingData] = useState<any[]>([]);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        const fetchRatings = async () => {
            try {
                const response = await fetch("http://localhost:8088/api/rating"); // замініть на реальний API URL
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
                    <Table config={tableConfig} data={ratingData}/>
                )}
                <div style={{display: "flex", justifyContent: "center", marginTop: "50px"}}>
                    <LevelBanner score={100} addedDatasets={10} ratedDatasets={5}/>
                </div>
            </div>
            <NavigationMenu/>
            <br/>
            <WelcomeBack username={ratingData.length > 0 ? ratingData[0].user : "Guest"} />
        </div>
    );
};

export default MainPage;
