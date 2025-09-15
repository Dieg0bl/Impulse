import React from "react";
import { FEATURE_FLAGS } from "../config";

const Plans: React.FC = () => {
  return (
    <div>
      <h2>Planes</h2>
      <p>Basic (gratis) — Pro — Teams — Coach (off)</p>
      {FEATURE_FLAGS.BILLING_ON ? (
        <button className="btn">Suscribirme</button>
      ) : (
        <button className="btn" disabled>
          Próximamente
        </button>
      )}
    </div>
  );
};

export default Plans;
