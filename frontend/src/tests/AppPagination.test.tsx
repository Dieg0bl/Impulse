import React from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import { describe, it, expect, vi } from 'vitest'
import { AppPagination } from '../ui/AppPagination'

describe('AppPagination', () => {
  it('shows page info and navigation', () => {
    const onPageChange = vi.fn()
    render(<AppPagination page={0} pageSize={10} total={25} onPageChange={onPageChange} />)
    expect(screen.getByText(/página 1 de 3/i)).toBeTruthy()
    const next = screen.getByLabelText(/página siguiente/i)
    fireEvent.click(next)
    expect(onPageChange).toHaveBeenCalledWith(1)
  })
})
