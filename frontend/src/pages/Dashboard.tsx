import React, { useEffect, useState } from "react";
import { tryListChallenges } from "../services/api";
import { ChallengeResponseDto } from "../types/dtos";

const Dashboard: React.FC = () => {
  const [challenges, setChallenges] = useState<ChallengeResponseDto[]>([]);
  useEffect(() => {
    tryListChallenges().then(setChallenges);
  }, []);

  return (
    <div>
      <h2>Dashboard</h2>
      <button className="btn">Nuevo reto</button>
      <section>
        <h3>Retos activos</h3>
        {challenges.length === 0 ? (
          <p>No hay retos</p>
        ) : (
          <ul>
            {challenges.map((c) => (
              <li key={c.id}>
                <strong>{c.title}</strong> — {c.description}
                <span style={{ marginLeft: "10px", fontSize: "0.8em", color: "#666" }}>
                  {c.category} | {c.difficulty} | {c.status}
                </span>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
};

export default Dashboard;
