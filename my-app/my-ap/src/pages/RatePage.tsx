import {useEffect, useState} from "react";
import {jwtDecode} from "jwt-decode";

type JwtPayload = {
    username: string;
};

const emotions: EmotionOption[] = [
    { id: 0, value: "HAPPINESS", label: "Happiness" },
    { id: 1, value: "SADNESS", label: "Sadness" },
    { id: 2, value: "FEAR", label: "Fear" },
    { id: 3, value: "ANGER", label: "Anger" },
    { id: 4, value: "REVULSION", label: "Revulsion" },
    { id: 5, value: "SURPRISE", label: "Surprise" },
];

const RatePage = () => {
    const [assets, setAssets] = useState([]);
    const [selectedEmotion, setSelectedEmotion] = useState<number | null>(null);
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
        fetch(`http://localhost:8088/rate/${username}`)
            .then(response => response.json())
            .then(data => setAssets(data));
    }, []);


    const vote = (assetId: number) => {
        if (!selectedEmotion) return;

        fetch(`http://localhost:8088/rate/${username}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ assetId, emotionId: selectedEmotion }),
        }).then(() => alert("Vote recorded"));
    };

    return (
        <div>
            <h1>Rate Images</h1>
            {assets.map(asset => (
                <div key={asset.id}>
                    <img src={`/uploads/${asset.name}`} alt="asset" />
                    <select onChange={(e) => setSelectedEmotion(parseInt(e.target.value))}>
                        <option value="">Choose emotion</option>
                        <option value="0">Happy</option>
                        <option value="1">Sad</option>
                    </select>
                    <button onClick={() => vote(asset.id)}>Submit</button>
                </div>
            ))}
        </div>
    );
};

export default RatePage;
