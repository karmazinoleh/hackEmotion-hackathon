import { FC } from "react";
import "./LevelBanner.css";

interface LevelBannerProps {
    score: number;
    addedDatasets: number;
    ratedDatasets: number;
}

const LevelBanner: FC<LevelBannerProps> = ({ score, addedDatasets, ratedDatasets }) => {
    return (
        <div className="level-banner">
            <div className="level-circle">{score}</div>
            <p className="level-score">Your current score is: {score}</p>
            <p>You added {addedDatasets} datasets</p>
            <p>You rated {ratedDatasets} datasets</p>
        </div>
    );
};

export default LevelBanner;
