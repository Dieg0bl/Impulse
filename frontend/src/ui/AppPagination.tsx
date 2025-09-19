import React from 'react'

export interface AppPaginationProps {
  page: number
  pageSize: number
  total: number
  onPageChange: (p: number) => void
}

export const AppPagination: React.FC<AppPaginationProps> = ({ page, pageSize, total, onPageChange }) => {
  const totalPages = Math.max(1, Math.ceil(total / pageSize))
  return (
    <div className="flex items-center justify-between p-2">
      <div className="text-sm text-muted-foreground">Página {page + 1} de {totalPages}</div>
      <div className="flex gap-2">
        <button disabled={page <= 0} className="px-2 py-1 border rounded" onClick={() => onPageChange(page - 1)} aria-label="Página anterior">Anterior</button>
        <button disabled={page >= totalPages - 1} className="px-2 py-1 border rounded" onClick={() => onPageChange(page + 1)} aria-label="Página siguiente">Siguiente</button>
      </div>
    </div>
  )
}

export default AppPagination
