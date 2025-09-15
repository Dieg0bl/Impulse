import React from "react";
import mockStore from "../services/mockStore";

const ValidatorPanel: React.FC = () => {
  const [items, setItems] = React.useState<any[]>([]);

  React.useEffect(() => {
    // For demo, list all challenges as items to validate
    mockStore.listChallenges().then((ch) => setItems(ch));
  }, []);

  const approve = (id: string) => {
    alert("Aprobado (simulado) " + id);
  };

  const reject = (id: string) => {
    const reason = prompt("Motivo de rechazo") || "sin motivo";
    alert("Rechazado (simulado) " + id + "\n" + reason);
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Panel de validadores</h2>
      <ul>
        {items.map((i) => (
          <li key={i.id}>
            <strong>{i.title}</strong> — {i.description}
            <div style={{ marginTop: 6 }}>
              <button onClick={() => approve(i.id)}>Aprobar</button>
              <button onClick={() => reject(i.id)} style={{ marginLeft: 8 }}>
                Rechazar
              </button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ValidatorPanel;
