import React, { useState, ChangeEvent, FormEvent } from "react";
import axios from "axios";
import Select, { MultiValue } from "react-select";
import {jwtDecode} from "jwt-decode";

// Типи даних
type EmotionOption = {
    id: number;
    value: string;
    label: string;
};

type FileWithEmotions = {
    file: File;
    previewUrl: string;
    selectedEmotions: EmotionOption[];
};

type JwtPayload = {
    sub: string; // Ім'я користувача або інший ідентифікатор
};

// Список емоцій
const emotions: EmotionOption[] = [
    { id: 0, value: "HAPPINESS", label: "Happiness" },
    { id: 1, value: "SADNESS", label: "Sadness" },
    { id: 2, value: "FEAR", label: "Fear" },
    { id: 3, value: "ANGER", label: "Anger" },
    { id: 4, value: "REVULSION", label: "Revulsion" },
    { id: 5, value: "SURPRISE", label: "Surprise" },
];

const UploadPage: React.FC = () => {
    const [filesWithEmotions, setFilesWithEmotions] = useState<FileWithEmotions[]>([]);

    const getUsernameFromToken = (): string | null => {
        const token = localStorage.getItem("token");
        if (!token) return null;

        try {
            const decoded: JwtPayload = jwtDecode(token);
            return decoded.sub; // Повертаємо ім'я користувача
        } catch (error) {
            console.error("Invalid JWT token");
            return null;
        }
    };

    // Читання JWT токена з локального сховища
    const getToken = () => localStorage.getItem("token") || "";

    const handleFilesChange = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.files) {
            const newFiles = Array.from(event.target.files).map((file) => ({
                file,
                previewUrl: URL.createObjectURL(file),
                selectedEmotions: [],
            }));
            setFilesWithEmotions([...filesWithEmotions, ...newFiles]);
        }
    };

    const handleEmotionChange = (index: number, newValue: MultiValue<EmotionOption>) => {
        const updatedFiles = [...filesWithEmotions];
        updatedFiles[index].selectedEmotions = Array.from(newValue) as EmotionOption[];
        setFilesWithEmotions(updatedFiles);
    };

    const handleDeleteFile = (index: number) => {
        const updatedFiles = [...filesWithEmotions];
        URL.revokeObjectURL(updatedFiles[index].previewUrl);
        updatedFiles.splice(index, 1);
        setFilesWithEmotions(updatedFiles);
    };



    const handleSubmit = async (event: FormEvent) => {
        event.preventDefault();
        if (filesWithEmotions.length === 0) {
            alert("Please select files!");
            return;
        }

        const username = getUsernameFromToken();
        if (!username) {
            alert("Invalid user. Please login again.");
            return;
        }

        try {
            for (const { file, selectedEmotions } of filesWithEmotions) {
                const formData = new FormData();
                formData.append("file", file);

                // Завантаження файлу на S3
                const s3Response = await axios.post("http://localhost:8088/api/files/upload", formData, {
                    headers: {
                        "Content-Type": "multipart/form-data",
                        Authorization: `Bearer ${getToken()}`,
                    },
                });

                const fileUrl = s3Response.data.url;

                // Створення нового Asset з передачею імені користувача
                const assetResponse = await axios.post(
                    "http://localhost:8088/asset/create-asset",
                    {
                        url: fileUrl,
                        name: file.name,
                        username: username, // Передаємо ім'я користувача
                    },
                    {
                        headers: { Authorization: `Bearer ${getToken()}` },
                    }
                );

                const assetId = assetResponse.data.id;

                // Додавання емоцій до Asset
                await axios.post(
                    `http://localhost:8088/asset/${assetId}/add-emotions`,
                    {
                        emotions: selectedEmotions.map((emotion) => ({
                            id: emotion.id, // Якщо ID не потрібно, можна передати null або видалити це поле
                            emotionName: emotion.label,
                        })),
                        user: { username: username } // Передаємо користувача
                    },
                    {
                        headers: { Authorization: `Bearer ${getToken()}` },
                    }
                );

            }

            alert("Files uploaded successfully with emotions!");
        } catch (error) {
            console.error("Error uploading files:", error);
            alert("Failed to upload files. Check the console for details.");
        }
    };


    // Підрахунок кількості емоцій за типами
    const emotionCount: Record<string, number> = emotions.reduce((acc, emotion) => {
        acc[emotion.value] = 0;
        return acc;
    }, {} as Record<string, number>);

    filesWithEmotions.forEach((file) => {
        file.selectedEmotions.forEach((emotion) => {
            emotionCount[emotion.value]++;
        });
    });

    return (
        <div className="upload-page">
            <h1>Upload Images and Assign Emotions</h1>

            <div className="indicators">
                <h2>Emotion Usage:</h2>
                <ul>
                    {Object.entries(emotionCount).map(([emotion, count]) => (
                        <li key={emotion}>
                            {emotions.find((e) => e.value === emotion)?.label}: {count}
                        </li>
                    ))}
                </ul>
            </div>

            <form onSubmit={handleSubmit}>
                <div>
                    <label>Upload Images:</label>
                    <input type="file" accept="image/*" multiple onChange={handleFilesChange} />
                </div>
                {filesWithEmotions.map((fileData, index) => (
                    <div key={index} className="file-item">
                        <h3>{fileData.file.name}</h3>
                        <img
                            src={fileData.previewUrl}
                            alt={`Preview of ${fileData.file.name}`}
                            style={{ maxWidth: "200px", maxHeight: "200px", marginBottom: "10px" }}
                        />
                        <button
                            type="button"
                            onClick={() => handleDeleteFile(index)}
                            style={{ marginBottom: "10px", color: "red" }}
                        >
                            Delete
                        </button>
                        <label>Select Emotions:</label>
                        <Select
                            options={emotions}
                            isMulti
                            onChange={(newValue) => handleEmotionChange(index, newValue)}
                        />
                    </div>
                ))}
                <button type="submit">Upload All</button>
            </form>
        </div>
    );
};

export default UploadPage;
