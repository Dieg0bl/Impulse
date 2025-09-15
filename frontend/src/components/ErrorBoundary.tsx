import React from "react";

type Props = { children: React.ReactNode };

class ErrorBoundary extends React.Component<Props, { hasError: boolean }> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false };
  }
  static getDerivedStateFromError() {
    return { hasError: true };
  }
  componentDidCatch(error: any) {
    // placeholder: could send to telemetry
    console.error("ErrorBoundary caught", error);
  }
  render() {
    if (this.state.hasError) return <div className="card error">Ha ocurrido un error.</div>;
    return this.props.children;
  }
}

export default ErrorBoundary;
