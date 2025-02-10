import { FC } from "react";
import "./Table.css";

interface TableProps {
    config: { label: string; render: (row: any) => any }[];
    data: any[];
}

const Table: FC<TableProps> = ({ config, data }) => {
    return (
        <div className="table-container">
            <h1 className="global-rating-h1">Global Rating</h1>
            <table className="custom-table">
                <thead>
                <tr>
                    {config.map((column, index) => (
                        <th key={index}>{column.label}</th>
                    ))}
                </tr>
                </thead>
                <tbody>
                {data.map((row, rowIndex) => (
                    <tr key={rowIndex}>
                        {config.map((column, colIndex) => (
                            <td key={colIndex}>{column.render(row)}</td>
                        ))}
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default Table;
