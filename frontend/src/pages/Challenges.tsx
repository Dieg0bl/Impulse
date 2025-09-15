import React from "react";
import mockStore, { Challenge } from "../services/mockStore";
import { Link } from "react-router-dom";

const Challenges: React.FC = () => {
  const [challenges, setChallenges] = React.useState<Challenge[]>([]);

  React.useEffect(() => {
    mockStore.listChallenges().then(setChallenges);
  }, []);

  return (
    <div style={{ padding: 20 }}>
      <h2>Retos</h2>
      <Link to="/challenges/create">
        <button>Crear reto</button>
      </Link>
      <ul>
        {challenges.map((c) => (
          <li key={c.id}>
            <Link to={`/challenges/${c.id}`}>{c.title}</Link> — <small>{c.description}</small>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default Challenges;
