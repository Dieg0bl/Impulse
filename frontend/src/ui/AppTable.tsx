import React from 'react'

export type Column<T> = { id: string; label: string; render?: (row: T) => React.ReactNode; sortable?: boolean }

export interface AppTableProps<T> {
  columns: Column<T>[]
  rows: T[]
  rowKey?: (r: T) => string
  onRowClick?: (r: T) => void
  emptyMessage?: string
}

export function AppTable<T>({ columns, rows, rowKey, onRowClick, emptyMessage }: Readonly<AppTableProps<T>>) {
  if (!rows || rows.length === 0) return <div className="p-6 border rounded text-center text-sm text-muted-foreground">{emptyMessage || 'No hay datos'}</div>

  return (
    <div className="overflow-auto border rounded">
      <table className="w-full text-sm" role="table">
        <thead className="bg-gray-50">
          <tr>
            {columns.map(c => (
              <th key={c.id} className="text-left px-3 py-2">{c.label}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map(r => {
            const key = rowKey ? rowKey(r) : (r as any).id || JSON.stringify(r)
            return (
              <tr key={key} className="border-t hover:bg-gray-50">
                {columns.map((c, idx) => {
                  const content = c.render ? c.render(r) : (r as any)[c.id]
                  return (
                    <td key={c.id} className="px-3 py-2 align-top">
                      {idx === 0 && onRowClick ? (
                        <button className="w-full text-left" onClick={() => onRowClick?.(r)}>{content}</button>
                      ) : (
                        content
                      )}
                    </td>
                  )
                })}
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}

export default AppTable
