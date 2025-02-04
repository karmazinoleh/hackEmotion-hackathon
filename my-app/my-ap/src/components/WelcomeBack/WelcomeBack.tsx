import { FC } from "react";
import "./WelcomeBack.css";

interface WelcomeBackProps {
    username: string;
}

const WelcomeBack: FC<WelcomeBackProps> = ({ username }) => {
    return (
        <div className="welcome-back-container">
            <h1>
                Welcome back, <span className="username">{username}</span>
            </h1>
        </div>
    );
};

export default WelcomeBack;
