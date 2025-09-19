import React from "react";

const A11yHelper: React.FC = () => {
  return (
    <div aria-hidden="true" style={{ position: "absolute", left: "-10000px" }}>
      Accessibility helper component
    </div>
  );
};
export { A11yHelper };
export default A11yHelper;
