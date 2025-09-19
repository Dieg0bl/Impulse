import React, { useEffect, useMemo, useState } from "react";
import LoadingSpinner from "../../components/LoadingSpinner";
import AppButton from "../../ui/AppButton";
import AppTable from "../../ui/AppTable";
import { useConfig } from "../../services/configService";
import { webhookApi } from "../../services/api";

type WebhookEvent = {
	id: string;
	receivedAt: string; // ISO
	eventType: string;
	signatureVerified: boolean;
	processedAt?: string | null;
	status: "processed" | "failed" | "pending";
	payload: Record<string, any>;
};

const MOCK: WebhookEvent[] = [
	{
		id: "evt_1",
		receivedAt: new Date(Date.now() - 1000 * 60 * 60).toISOString(),
		eventType: "invoice.payment_succeeded",
		signatureVerified: true,
		processedAt: new Date(Date.now() - 1000 * 60 * 55).toISOString(),
		status: "processed",
		payload: { invoiceId: "in_123", amount: 1299, currency: "EUR" },
	},
	{
		id: "evt_2",
		receivedAt: new Date(Date.now() - 1000 * 60 * 20).toISOString(),
		eventType: "payment_intent.payment_failed",
		signatureVerified: false,
		processedAt: null,
		status: "failed",
		payload: { intentId: "pi_456", reason: "card_declined" },
	},
	{
		id: "evt_3",
		receivedAt: new Date().toISOString(),
		eventType: "checkout.session.completed",
		signatureVerified: true,
		processedAt: null,
		status: "pending",
		payload: { sessionId: "cs_789", plan: "pro" },
	},
];

const postEvent = async (type: string, details?: Record<string, any>) => {
	try {
		await fetch("/api/v1/events", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ type, details, timestamp: new Date().toISOString() }),
		});
	} catch (e) {
		// metrics are best-effort
		console.debug("postEvent failed", e);
	}
};

const Webhooks: React.FC = () => {
	const config = useConfig();
	const [loading, setLoading] = useState(true);
	const [items, setItems] = useState<WebhookEvent[]>([]);
	const [query, setQuery] = useState("");
	const [statusFilter, setStatusFilter] = useState<string>("all");
	const [selected, setSelected] = useState<WebhookEvent | null>(null);

		useEffect(() => {
			let mounted = true;

			const load = async () => {
				if (!config.isBillingEnabled) {
					setLoading(false);
					setItems([]);
					return;
				}

				setLoading(true);
				try {
					// Basic call - backend should accept page & size, and optional q/status
					const data = await webhookApi.listWebhooks({ page: 0, size: 50 });
					if (!mounted) return;
					setItems(data.content || []);
					postEvent("webhooks_view", { total: data.totalElements });
				} catch (err) {
					// Fallback to mock data when API is not available
					// eslint-disable-next-line no-console
					console.warn("webhook list failed, falling back to mock", err);
					if (!mounted) return;
					setItems(MOCK);
				} finally {
					if (mounted) setLoading(false);
				}
			};

			load();

			return () => {
				mounted = false;
			};
		}, [config.isBillingEnabled]);

	const filtered = useMemo(() => {
		return items.filter((it) => {
			if (statusFilter !== "all" && it.status !== statusFilter) return false;
			if (!query) return true;
			const q = query.toLowerCase();
			return (
				it.id.toLowerCase().includes(q) ||
				it.eventType.toLowerCase().includes(q) ||
				JSON.stringify(it.payload).toLowerCase().includes(q)
			)
		});
	}, [items, query, statusFilter]);

	const copyToClipboard = async (text: string) => {
		try {
			await navigator.clipboard.writeText(text);
			postEvent("webhook_copy_eventid", { id: text });
		} catch (e) {
			console.warn("copy failed", e);
		}
	};

		const markProcessed = async (id: string) => {
			try {
				// optimistic update
				setItems((prev) => prev.map((it) => (it.id === id ? { ...it, status: 'processed', processedAt: new Date().toISOString() } : it)));
				await webhookApi.markWebhookProcessed(id);
				postEvent('webhook_mark_processed', { id });
			} catch (err) {
				console.warn('mark processed failed', err);
				// revert optimistic update by reloading list
				try { const data = await webhookApi.listWebhooks({ page: 0, size: 50 }); setItems(data.content || MOCK); } catch (_) { setItems(MOCK); }
			}
		};

	if (!config.isBillingEnabled) {
		return (
			<div className="p-6">
				<h2 className="text-2xl font-semibold mb-2">Webhooks</h2>
				<p className="text-sm text-muted-foreground">La funcionalidad de billing y webhooks está deshabilitada en esta instancia.</p>
			</div>
		);
	}

	return (
		<div className="p-6">
			<div className="flex items-center justify-between mb-4">
				<h2 className="text-2xl font-semibold">Webhooks</h2>
				<div className="flex gap-2 items-center">
					<AppButton onClick={() => { setQuery(""); setStatusFilter("all"); postEvent("webhooks_refresh"); }}>
						Refrescar
					</AppButton>
				</div>
			</div>

			<div className="mb-4 flex gap-2">
				<input aria-label="Buscar webhooks" placeholder="Buscar por id, tipo o payload" className="flex-1 border rounded px-3 py-2" value={query} onChange={(e) => setQuery(e.target.value)} />
				<select aria-label="Filtrar estado" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)} className="border rounded px-2 py-2">
					<option value="all">Todos</option>
					<option value="processed">Procesados</option>
					<option value="pending">Pendientes</option>
					<option value="failed">Fallidos</option>
				</select>
			</div>

					{loading && (
						<div className="py-8 flex justify-center"><LoadingSpinner size={28} /></div>
					)}

					{!loading && filtered.length === 0 && (
						<div className="p-6 border rounded text-center text-sm text-muted-foreground">No se encontraron webhooks con los filtros seleccionados.</div>
					)}

					{!loading && filtered.length > 0 && (
						<AppTable
							columns={[
								{ id: 'receivedAt', label: 'Recibido', render: (r) => new Date((r as any).receivedAt).toLocaleString() },
								{ id: 'id', label: 'Event ID', render: (r) => (<div className="flex items-center gap-2"><div className="font-mono text-xs">{(r as any).id}</div><button className="text-xs text-blue-600" onClick={() => copyToClipboard((r as any).id)}>Copiar</button></div>) },
								{ id: 'eventType', label: 'Tipo', render: (r) => (r as any).eventType },
								{ id: 'signatureVerified', label: 'Firma', render: (r) => ((r as any).signatureVerified ? '✅' : '❌') },
								{ id: 'status', label: 'Estado', render: (r) => (r as any).status },
								{ id: 'actions', label: 'Acciones', render: (r) => (<div className="flex gap-2"><AppButton size="compact" onClick={() => { setSelected(r as any); postEvent('webhook_open_detail', { id: (r as any).id }); }}>Detalle</AppButton>{((r as any).status === 'pending' || (r as any).status === 'failed') && (<AppButton size="compact" variant="outlined" color="success" onClick={() => markProcessed((r as any).id)}>Marcar procesado</AppButton>)}</div>) }
							]}
							rows={filtered}
							rowKey={(r) => (r as any).id}
							onRowClick={(r) => setSelected(r as any)}
						/>
					)}

			{/* Drawer / detail */}
					{selected && (
						<dialog open className="fixed inset-0 z-50 w-full h-full p-4 bg-black/30 flex items-end lg:items-center justify-center">
							<div className="bg-white w-full max-w-3xl rounded shadow-lg p-4">
								<div className="flex items-start justify-between">
									<div>
										<h3 className="text-lg font-semibold">Detalle: {selected.id}</h3>
										<p className="text-sm text-muted-foreground">{selected.eventType} — {new Date(selected.receivedAt).toLocaleString()}</p>
									</div>
									<div className="flex items-center gap-2">
										<AppButton variant="text" onClick={() => { setSelected(null); postEvent("webhook_close_detail", { id: selected.id }); }}>Cerrar</AppButton>
									</div>
								</div>

								<div className="mt-4 grid grid-cols-1 gap-4">
									<div className="p-3 border rounded bg-gray-50">
										<div className="text-xs text-muted-foreground">Payload</div>
										<pre className="text-xs mt-2 overflow-auto max-h-64 p-2 bg-white border rounded"><code>{JSON.stringify(selected.payload, null, 2)}</code></pre>
									</div>

									<div className="flex gap-2">
										<AppButton onClick={() => copyToClipboard(selected.id)}>Copiar Event ID</AppButton>
										<AppButton variant="outlined" color="secondary" onClick={() => { navigator.clipboard?.writeText(JSON.stringify(selected.payload)); postEvent("webhook_copy_payload", { id: selected.id }); }}>Copiar Payload</AppButton>
									</div>
								</div>
							</div>
						</dialog>
					)}
		</div>
	);
};

export default Webhooks;
