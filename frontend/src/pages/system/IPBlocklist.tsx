import React from 'react';

const IPBlocklist: React.FC = () => {
	const mock = [
		{ ip: '192.0.2.1', reason: 'Brute force', by: 'admin', createdAt: '2025-09-01', expiresAt: '2025-10-01' },
	];
	return (
		<div className="container-app">
			<header className="flex items-center justify-between mb-4">
				<h1 className="text-2xl font-bold">IP Blocklist</h1>
			</header>
			<div className="filter-bar mb-3">Filtro: IP / razón</div>
			<table className="w-full table-auto">
				<thead>
					<tr>
						<th>IP</th>
						<th>Razón</th>
						<th>Creado por</th>
						<th>Creado en</th>
						<th>Expira</th>
					</tr>
				</thead>
				<tbody>
					{mock.map(row => (
						<tr key={row.ip} className="border-t">
							<td>{row.ip}</td>
							<td>{row.reason}</td>
							<td>{row.by}</td>
							<td>{row.createdAt}</td>
							<td>{row.expiresAt}</td>
						</tr>
					))}
				</tbody>
			</table>
		</div>
	);
};

export default IPBlocklist;
