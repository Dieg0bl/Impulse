import React from 'react'

export interface PaginationProps {
  page: number
  pageSize: number
  total?: number
  onChange: (newPage: number) => void
}

export const Pagination: React.FC<PaginationProps> = ({ page, pageSize, total, onChange }) => {
  const totalPages = total ? Math.max(1, Math.ceil(total / pageSize)) : undefined

  const prev = () => onChange(Math.max(1, page - 1))
  const next = () => onChange(totalPages ? Math.min(totalPages, page + 1) : page + 1)

  return (
    <nav aria-label="Paginación" className="flex items-center justify-between mt-4">
      <div className="flex items-center gap-2">
        <button onClick={prev} aria-label="Página anterior" className="px-3 py-1 bg-gray-100 rounded">Anterior</button>
        <span className="text-sm text-gray-600">Página {page}{totalPages ? ` de ${totalPages}` : ''}</span>
        <button onClick={next} aria-label="Página siguiente" className="px-3 py-1 bg-gray-100 rounded">Siguiente</button>
      </div>
      {total !== undefined && (
        <div className="text-sm text-gray-600">Mostrando {Math.min(total, page * pageSize)} de {total}</div>
      )}
    </nav>
  )
}

export default Pagination
