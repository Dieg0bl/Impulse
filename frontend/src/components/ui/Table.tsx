import React from 'react'

export type Column<T> = {
  key: string
  label: string
  sortable?: boolean
  render?: (row: T) => React.ReactNode
}

export interface TableProps<T> {
  columns: Column<T>[]
  data: T[]
  rowKey?: (row: T) => string | number
  className?: string
}

export function Table<T>({ columns, data, rowKey, className }: TableProps<T>) {
  return (
    <div className={className}>
      <table className="w-full table-auto border-collapse" role="table">
        <thead>
          <tr>
            {columns.map(col => (
              <th key={col.key} scope="col" className="text-left font-medium p-2 text-sm text-gray-700">
                {col.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.length === 0 ? (
            <tr>
              <td colSpan={columns.length} className="p-4 text-sm text-gray-500">No hay datos</td>
            </tr>
          ) : (
            data.map((row, idx) => (
              <tr key={rowKey ? String(rowKey(row)) : idx} className="odd:bg-white even:bg-gray-50">
                {columns.map(col => (
                  <td key={col.key} className="p-2 align-top text-sm text-gray-800">
                    {col.render ? col.render(row) : (row as any)[col.key]}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )
}

export default Table
