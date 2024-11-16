import React, { useState, ChangeEvent, FormEvent } from "react";
import axios from "axios";
import Select, { MultiValue } from "react-select";

type EmotionOption = {
    value: string;
    label: string;
};

const emotions: EmotionOption[] = [
    { value: "HAPPINESS", label: "Happiness" },
    { value: "SADNESS", label: "Sadness" },
    { value: "FEAR", label: "Fear" },
    { value: "ANGER", label: "Anger" },
    { value: "REVULSION", label: "Revulsion" },
    { value: "SURPRISE", label: "Surprise" },
];

const UploadPage: React.FC = () => {
    const [file, setFile] = useState<File | null>(null);
    const [selectedEmotions, setSelectedEmotions] = useState<EmotionOption[]>([]);
    const [intensities, setIntensities] = useState<Record<string, number>>({});

    const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.files) {
            setFile(event.target.files[0]);
        }
    };

    // Оновлена функція для обробки вибору емоцій
    const handleEmotionChange = (newValue: MultiValue<EmotionOption>) => {
        const selected = Array.from(newValue) as EmotionOption[]; // Приведення до змінного масиву
        setSelectedEmotions(selected);

        // Ініціалізація інтенсивностей для кожної емоції
        const initialIntensities: Record<string, number> = {};
        selected.forEach((option) => {
            initialIntensities[option.value] = 50; // Значення за замовчуванням
        });
        setIntensities(initialIntensities);
    };

    const handleIntensityChange = (emotion: string, intensity: number) => {
        setIntensities({ ...intensities, [emotion]: intensity });
    };

    const handleSubmit = async (event: FormEvent) => {
        event.preventDefault();
        if (!file) {
            alert("Please select a file!");
            return;
        }

        try {
            const formData = new FormData();
            formData.append("file", file);

            // Завантаження файлу на сервер
            const s3Response = await axios.post("http://localhost:8080/api/files/upload", formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });
            // @ts-ignore
            //const s3Response = await axios.postForm("api/files/upload", {file: this.file})

            const fileUrl = s3Response.data.url;

            const assetResponse = await axios.post("http://localhost:8080/asset/create-asset", {
                url: fileUrl,
                name: file.name,
            });
            const assetId = assetResponse.data.id;

            // Додавання емоцій до активу
            const emotionsWithIntensity = selectedEmotions.map((emotion) => ({
                emotionName: emotion.label,
                intensity: intensities[emotion.value],
            }));
            await axios.post(`http://localhost:8080/asset/${assetId}/add-emotions`, emotionsWithIntensity);

            alert("Image uploaded successfully with emotions!");
        } catch (error) {
            console.error("Error uploading file:", error);
            alert("Failed to upload file. Check the console for details.");
        }
    };

    return (
        <div className="upload-page">
            <h1>Upload Image and Assign Emotions</h1>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Upload Image:</label>
                    <input type="file" accept="image/*" onChange={handleFileChange} />
                </div>
                <div>
                    <label>Select Emotions:</label>
                    <Select
                        options={emotions}
                        isMulti
                        onChange={handleEmotionChange}
                    />
                </div>
                {selectedEmotions.map((emotion) => (
                    <div key={emotion.value}>
                        <label>
                            {emotion.label} Intensity: {intensities[emotion.value]}
                        </label>
                        <input
                            type="range"
                            min="1"
                            max="100"
                            value={intensities[emotion.value]}
                            onChange={(e) =>
                                handleIntensityChange(emotion.value, parseInt(e.target.value))
                            }
                        />
                    </div>
                ))}
                <button type="submit">Upload</button>
            </form>
        </div>
    );
};

export default UploadPage;
