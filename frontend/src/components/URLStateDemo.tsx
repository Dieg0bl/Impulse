import React, { useState, useEffect } from 'react';
import { useURLState, useURLFilters, useFormRecovery } from '../hooks/useURLState';

/**
 * Componente de demostración para gestión avanzada de estado en URLs
 */
export default function URLStateDemo() {
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [sortBy, setSortBy] = useState('date');

  // Hook principal para gestión de estado
  const {
    pageState,
    isLoading,
    error,
    updateViewState,
    createBookmark,
    getViewState,
  } = useURLState('url-state-demo', {
    autoSync: true,
    enableBookmarking: true,
  });

  // Hook para filtros con sincronización automática
  const {
    filters,
    updateFilter,
    clearAllFilters,
    applyFilters,
  } = useURLFilters('url-state-demo', {
    category: '',
    status: '',
    dateRange: '',
  });

  // Hook para recuperación de formularios
  const {
    saveForm,
    recoverForm,
    hasRecoveryData,
  } = useFormRecovery('url-state-demo', 'search-form');

  // Restaurar estado de vista al cargar
  useEffect(() => {
    if (!isLoading && pageState) {
      const restoredSearchTerm = getViewState('searchTerm') || '';
      const restoredPage = getViewState('currentPage') || 1;
      const restoredSort = getViewState('sortBy') || 'date';

      setSearchTerm(restoredSearchTerm);
      setCurrentPage(restoredPage);
      setSortBy(restoredSort);
    }
  }, [isLoading, pageState, getViewState]);

  // Recuperar datos de formulario si existen
  useEffect(() => {
    if (hasRecoveryData()) {
      const recovered = recoverForm();
      if (recovered.searchTerm) {
        setSearchTerm(recovered.searchTerm);
      }
    }
  }, [hasRecoveryData, recoverForm]);

  // Manejar cambios de búsqueda
  const handleSearchChange = async (value: string) => {
    setSearchTerm(value);
    await updateViewState('searchTerm', value);
    
    // Guardar en formulario para recuperación
    await saveForm({ searchTerm: value });
  };

  // Manejar cambio de página
  const handlePageChange = async (page: number) => {
    setCurrentPage(page);
    await updateViewState('currentPage', page);
  };

  // Manejar cambio de ordenamiento
  const handleSortChange = async (sort: string) => {
    setSortBy(sort);
    await updateViewState('sortBy', sort);
  };

  // Manejar filtros específicos
  const handleCategoryFilter = async (category: string) => {
    await updateFilter('category', category);
  };

  const handleStatusFilter = async (status: string) => {
    await updateFilter('status', status);
  };

  // Aplicar filtros complejos
  const handleAdvancedFilter = async () => {
    await applyFilters({
      category: 'technology',
      status: 'active',
      dateRange: 'last-month',
      priority: 'high'
    });
  };

  // Crear bookmark de la vista actual
  const handleCreateBookmark = () => {
    const bookmarkUrl = createBookmark('Mi Vista Personalizada');
    
    // Copiar al portapapeles
    navigator.clipboard.writeText(bookmarkUrl).then(() => {
      alert('Bookmark copiado al portapapeles');
    });
  };

  // Simular datos para mostrar
  const mockData = [
    { id: 1, title: 'Reto de programación', category: 'technology', status: 'active' },
    { id: 2, title: 'Desafío de diseño', category: 'design', status: 'completed' },
    { id: 3, title: 'Competencia de marketing', category: 'marketing', status: 'pending' },
  ];

  // Filtrar datos según el estado actual
  const filteredData = mockData.filter(item => {
    const matchesSearch = searchTerm ? 
      item.title.toLowerCase().includes(searchTerm.toLowerCase()) : true;
    const matchesCategory = filters.category ? 
      item.category === filters.category : true;
    const matchesStatus = filters.status ? 
      item.status === filters.status : true;
    
    return matchesSearch && matchesCategory && matchesStatus;
  });

  if (isLoading) {
    return <div className="loading">Cargando estado de página...</div>;
  }

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  return (
    <div className="url-state-demo">
      <h2>Demo: Gestión Avanzada de Estado en URLs</h2>
      
      {/* Información del estado actual */}
      <div className="state-info">
        <h3>Estado Actual</h3>
        <p><strong>Página ID:</strong> {pageState?.pageId}</p>
        <p><strong>Timestamp:</strong> {pageState?.timestamp}</p>
        <p><strong>Búsqueda:</strong> {searchTerm}</p>
        <p><strong>Página:</strong> {currentPage}</p>
        <p><strong>Ordenar por:</strong> {sortBy}</p>
        <p><strong>Filtros activos:</strong> {JSON.stringify(filters)}</p>
      </div>

      {/* Controles de búsqueda */}
      <div className="search-controls">
        <h3>Búsqueda</h3>
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => handleSearchChange(e.target.value)}
          placeholder="Buscar..."
          className="search-input"
        />
        {hasRecoveryData() && (
          <span className="recovery-indicator">📝 Datos recuperados</span>
        )}
      </div>

      {/* Controles de vista */}
      <div className="view-controls">
        <h3>Vista</h3>
        <select value={sortBy} onChange={(e) => handleSortChange(e.target.value)}>
          <option value="date">Fecha</option>
          <option value="title">Título</option>
          <option value="category">Categoría</option>
        </select>
        
        <div className="pagination">
          <button 
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage <= 1}
          >
            Anterior
          </button>
          <span>Página {currentPage}</span>
          <button 
            onClick={() => handlePageChange(currentPage + 1)}
          >
            Siguiente
          </button>
        </div>
      </div>

      {/* Filtros */}
      <div className="filters">
        <h3>Filtros</h3>
        
        <div className="filter-group">
          <label htmlFor="category-filter">Categoría:</label>
          <select 
            id="category-filter"
            value={filters.category || ''} 
            onChange={(e) => handleCategoryFilter(e.target.value)}
          >
            <option value="">Todas</option>
            <option value="technology">Tecnología</option>
            <option value="design">Diseño</option>
            <option value="marketing">Marketing</option>
          </select>
        </div>

        <div className="filter-group">
          <label htmlFor="status-filter">Estado:</label>
          <select 
            id="status-filter"
            value={filters.status || ''} 
            onChange={(e) => handleStatusFilter(e.target.value)}
          >
            <option value="">Todos</option>
            <option value="active">Activo</option>
            <option value="completed">Completado</option>
            <option value="pending">Pendiente</option>
          </select>
        </div>

        <div className="filter-actions">
          <button onClick={handleAdvancedFilter}>
            Aplicar Filtro Avanzado
          </button>
          <button onClick={clearAllFilters}>
            Limpiar Filtros
          </button>
        </div>
      </div>

      {/* Resultados */}
      <div className="results">
        <h3>Resultados ({filteredData.length})</h3>
        {filteredData.map(item => (
          <div key={item.id} className="result-item">
            <h4>{item.title}</h4>
            <p>Categoría: {item.category} | Estado: {item.status}</p>
          </div>
        ))}
      </div>

      {/* Acciones de bookmarking */}
      <div className="bookmark-actions">
        <h3>Bookmarking</h3>
        <button onClick={handleCreateBookmark}>
          📖 Crear Bookmark de esta Vista
        </button>
        <p>
          <small>
            Los bookmarks incluyen todos los filtros, búsqueda y estado de paginación.
            Puedes compartir la URL o guardarla para restaurar exactamente esta vista.
          </small>
        </p>
      </div>

      {/* URL actual */}
      <div className="current-url">
        <h3>URL Actual</h3>
        <code>{window.location.href}</code>
      </div>

      <style>{`
        .url-state-demo {
          max-width: 800px;
          margin: 0 auto;
          padding: 20px;
          font-family: Arial, sans-serif;
        }

        .state-info, .search-controls, .view-controls, .filters, .results, .bookmark-actions, .current-url {
          margin-bottom: 30px;
          padding: 20px;
          border: 1px solid #ddd;
          border-radius: 8px;
          background: #f9f9f9;
        }

        .search-input {
          width: 100%;
          padding: 10px;
          border: 1px solid #ccc;
          border-radius: 4px;
          font-size: 16px;
        }

        .recovery-indicator {
          color: #28a745;
          font-size: 12px;
          margin-left: 10px;
        }

        .pagination {
          display: flex;
          align-items: center;
          gap: 10px;
          margin-top: 10px;
        }

        .filter-group {
          margin-bottom: 15px;
        }

        .filter-group label {
          display: inline-block;
          width: 100px;
          font-weight: bold;
        }

        .filter-group select {
          width: 200px;
          padding: 5px;
        }

        .filter-actions {
          margin-top: 15px;
        }

        .filter-actions button {
          margin-right: 10px;
          padding: 8px 16px;
          border: 1px solid #ccc;
          border-radius: 4px;
          background: #fff;
          cursor: pointer;
        }

        .filter-actions button:hover {
          background: #f0f0f0;
        }

        .result-item {
          padding: 15px;
          margin-bottom: 10px;
          border: 1px solid #eee;
          border-radius: 4px;
          background: white;
        }

        .result-item h4 {
          margin: 0 0 5px 0;
        }

        .current-url code {
          display: block;
          padding: 10px;
          background: #fff;
          border: 1px solid #ddd;
          border-radius: 4px;
          word-break: break-all;
          font-size: 12px;
        }

        .loading, .error {
          text-align: center;
          padding: 50px;
          font-size: 18px;
        }

        .error {
          color: #dc3545;
        }
      `}</style>
    </div>
  );
}
