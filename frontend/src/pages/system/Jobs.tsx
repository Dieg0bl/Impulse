import React, { useState } from 'react';

const Jobs: React.FC = () => {
	const [tab, setTab] = useState<'definitions' | 'runs'>('definitions');
	const mockDefs = [
		{ name: 'Cleanup', cron: '0 0 * * *', owner: 'system', active: true, lastRun: '2025-09-18T10:00:00Z', p95: '1.2s' },
	];
	const mockRuns = [
		{ job: 'Cleanup', status: 'SUCCESS', started: '2025-09-18T10:00:00Z', finished: '2025-09-18T10:00:02Z', duration: '2s' },
	];

	return (
		<div className="container-app">
			<header className="flex items-center justify-between mb-4">
				<h1 className="text-2xl font-bold">Jobs</h1>
				<div className="tabs">
					<button onClick={() => setTab('definitions')} className={`px-3 py-1 rounded ${tab==='definitions'?'bg-primary-600 text-white':''}`}>Definiciones</button>
					<button onClick={() => setTab('runs')} className={`px-3 py-1 rounded ${tab==='runs'?'bg-primary-600 text-white':''}`}>Ejecuciones</button>
				</div>
			</header>

			{tab === 'definitions' && (
				<section>
					<div className="filter-bar mb-3">Filtros (job, owner, activo)</div>
					<table className="w-full table-auto border-collapse">
						<thead>
							<tr>
								<th>Nombre</th>
								<th>Cron</th>
								<th>Owner</th>
								<th>Activo</th>
								<th>Última ejecución</th>
								<th>p95</th>
							</tr>
						</thead>
						<tbody>
							{mockDefs.map(d => (
								<tr key={d.name} className="border-t">
									<td>{d.name}</td>
									<td>{d.cron}</td>
									<td>{d.owner}</td>
									<td>{d.active ? 'Sí' : 'No'}</td>
									<td>{d.lastRun}</td>
									<td>{d.p95}</td>
								</tr>
							))}
						</tbody>
					</table>
				</section>
			)}

			{tab === 'runs' && (
				<section>
					<div className="filter-bar mb-3">Filtros (job, status, fecha)</div>
					<table className="w-full table-auto border-collapse">
						<thead>
							<tr>
								<th>Job</th>
								<th>Status</th>
								<th>Iniciado</th>
								<th>Finalizado</th>
								<th>Duración</th>
							</tr>
						</thead>
						<tbody>
							{mockRuns.map(r => (
								<tr key={r.job} className="border-t">
									<td>{r.job}</td>
									<td>{r.status}</td>
									<td>{r.started}</td>
									<td>{r.finished}</td>
									<td>{r.duration}</td>
								</tr>
							))}
						</tbody>
					</table>
				</section>
			)}
		</div>
	);
};

export default Jobs;
