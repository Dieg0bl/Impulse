import React from 'react'
import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { AppTable } from '../ui/AppTable'

type Row = { id: string; name: string; value: number }

describe('AppTable', () => {
  it('renders empty message when no rows', () => {
    render(<AppTable<Row> columns={[{ id: 'name', label: 'Name' }]} rows={[]} />)
    expect(screen.getByText(/no hay datos/i)).toBeTruthy()
  })

  it('renders rows and columns', () => {
    const rows: Row[] = [{ id: 'r1', name: 'One', value: 1 }, { id: 'r2', name: 'Two', value: 2 }]
    render(
      <AppTable<Row>
        columns={[{ id: 'name', label: 'Name' }, { id: 'value', label: 'Value' }]}
        rows={rows}
        rowKey={r => r.id}
      />
    )

    expect(screen.getByText('Name')).toBeTruthy()
    expect(screen.getByText('Value')).toBeTruthy()
    expect(screen.getByText('One')).toBeTruthy()
    expect(screen.getByText('Two')).toBeTruthy()
  })
})
