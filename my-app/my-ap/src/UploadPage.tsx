import React, { useState, ChangeEvent, FormEvent } from "react";
import axios from "axios";
import Select, { MultiValue } from "react-select";

type EmotionOption = {
    value: string;
    label: string;
};

type FileWithEmotions = {
    file: File;
    previewUrl: string;
    selectedEmotions: EmotionOption[];
    intensities: Record<string, number>;
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
    const [filesWithEmotions, setFilesWithEmotions] = useState<FileWithEmotions[]>([]);

    const handleFilesChange = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.files) {
            const newFiles = Array.from(event.target.files).map((file) => ({
                file,
                previewUrl: URL.createObjectURL(file),
                selectedEmotions: [],
                intensities: {},
            }));
            setFilesWithEmotions([...filesWithEmotions, ...newFiles]);
        }
    };

    const handleEmotionChange = (index: number, newValue: MultiValue<EmotionOption>) => {
        const updatedFiles = [...filesWithEmotions];
        const selected = Array.from(newValue) as EmotionOption[];
        updatedFiles[index].selectedEmotions = selected;

        // Ініціалізація інтенсивностей для кожної емоції
        const initialIntensities: Record<string, number> = {};
        selected.forEach((option) => {
            initialIntensities[option.value] = 50;
        });
        updatedFiles[index].intensities = initialIntensities;
        setFilesWithEmotions(updatedFiles);
    };

    const handleIntensityChange = (fileIndex: number, emotion: string, intensity: number) => {
        const updatedFiles = [...filesWithEmotions];
        updatedFiles[fileIndex].intensities[emotion] = intensity;
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

        try {
            for (const { file, selectedEmotions, intensities } of filesWithEmotions) {
                const formData = new FormData();
                formData.append("file", file);

                const s3Response = await axios.post("http://localhost:8080/api/files/upload", formData, {
                    headers: { "Content-Type": "multipart/form-data" },
                });

                const fileUrl = s3Response.data.url;

                const assetResponse = await axios.post("http://localhost:8080/asset/create-asset", {
                    url: fileUrl,
                    name: file.name,
                });
                const assetId = assetResponse.data.id;

                const emotionsWithIntensity = selectedEmotions.map((emotion) => ({
                    emotionName: emotion.label,
                    intensity: intensities[emotion.value],
                }));
                await axios.post(`http://localhost:8080/asset/${assetId}/add-emotions`, emotionsWithIntensity);
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

            {/* Індикатори */}
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
                        {fileData.selectedEmotions.map((emotion) => (
                            <div key={emotion.value}>
                                <label>
                                    {emotion.label} Intensity: {fileData.intensities[emotion.value]}
                                </label>
                                <input
                                    type="range"
                                    min="1"
                                    max="100"
                                    value={fileData.intensities[emotion.value]}
                                    onChange={(e) =>
                                        handleIntensityChange(index, emotion.value, parseInt(e.target.value))
                                    }
                                />
                            </div>
                        ))}
                    </div>
                ))}
                <button type="submit">Upload All</button>
            </form>
        </div>
    );
};

export default UploadPage;
