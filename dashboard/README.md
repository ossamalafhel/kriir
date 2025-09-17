# KRIIR Dashboard

React-based dashboard for the KRIIR ransomware prediction and prevention platform.

## Prerequisites

- Node.js 18+
- Docker (optional, for containerized development)

## Development Setup

### Using npm

```bash
# Install dependencies
npm install

# Start development server
npm start
```

### Using Docker

**Build the container:**

```bash
docker build -t kriir-dashboard .
```

**Run the container:**

```bash
docker run -it -v ${PWD}:/usr/src/app -p 3000:3000 --rm kriir-dashboard
```

## Access

Open [http://localhost:3000](http://localhost:3000) in your browser.

## Features

- Real-time ransomware threat monitoring
- Risk assessment visualization
- Security incident tracking
- Asset management interface
- Predictive analytics dashboard

## Technology Stack

- React 18.3.1
- TypeScript
- Material-UI
- WebSocket for real-time updates
- Chart.js for data visualization

## License

Apache License 2.0

---

*Last updated: September 2025*