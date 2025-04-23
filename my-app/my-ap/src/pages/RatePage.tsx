import { useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";

type JwtPayload = {
    username: string;
};

type Asset = {
    id: number;
    name: string;
    url: string;
};

type EmotionOption = {
    id: number;
    value: string;
    label: string;
};

const emotions: EmotionOption[] = [
    { id: 0, value: "HAPPINESS", label: "Happiness" },
    { id: 1, value: "SADNESS",   label: "Sadness" },
    { id: 2, value: "FEAR", label: "Fear" },
    { id: 3, value: "ANGER", label: "Anger" },
    { id: 4, value: "REVULSION", label: "Revulsion" },
    { id: 5, value: "SURPRISE", label: "Surprise" },
];

const RatePage = () => {
    const [assets, setAssets] = useState<Asset[]>([]);
    const [selectedEmotion, setSelectedEmotion] = useState<number | null>(null);
    const [username, setUsername] = useState<string>("Guest");

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            try {
                const decoded: JwtPayload = jwtDecode(token);
                setUsername(decoded.username || "Guest");
            } catch (error) {
                console.error("Invalid JWT token");
            }
        }
    }, []);

    useEffect(() => {
        if (username !== "Guest") {
            fetch(`http://localhost:8088/asset/rate/${username}`)
                .then(response => response.json())
                .then(data => setAssets(data))
                .catch(error => console.error("Error fetching assets:", error));
        }
    }, [username]);

    const vote = (assetId: number) => {
        if (selectedEmotion === null) {
            alert("Please select an emotion");
            return;
        }

        fetch(`http://localhost:8088/asset/rate/${username}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ assetId, emotionId: selectedEmotion }),
        })
            .then(response => {
                if (response.ok) {
                    alert("Vote recorded");
                } else {
                    alert("Failed to record vote");
                }
            })
            .catch(error => console.error("Error submitting vote:", error));
    };

    return (
        <div>
            <h1>Rate Images</h1>
            {assets.length === 0 ? <p>No assets to rate</p> : null}
            {assets.map(asset => (
                <div key={asset.id} style={{ marginBottom: "20px" }}>
                    <img src={asset.url} alt="asset" style={{ width: "300px", height: "auto" }} />
                    <select onChange={(e) => setSelectedEmotion(parseInt(e.target.value, 10))}>
                        <option value="">Choose emotion</option>
                        {emotions.map(emotion => (
                            <option key={emotion.id} value={emotion.id}>
                                {emotion.label}
                            </option>
                        ))}
                    </select>
                    <button onClick={() => vote(asset.id)}>Submit</button>
                </div>
            ))}
        </div>
    );
};

export default RatePage;
