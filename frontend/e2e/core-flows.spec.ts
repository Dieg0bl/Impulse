import { test, expect } from '@playwright/test';

test.describe('Flujos principales de la app', () => {
  test('Login, ver retos, detalle, reportar avance, sesiones, validadores', async ({ page }) => {
    await page.goto('http://localhost:3000/login');
    await page.fill('input[name="email"]', 'demo@demo.com');
    await page.fill('input[name="password"]', 'demo123');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL(/dashboard|mis-retos/);

    // Ver retos
    await page.click('text=Mis Retos');
    await expect(page.locator('.reto-card')).toHaveCountGreaterThan(0);

    // Detalle de reto
    await page.click('.reto-card >> nth=0');
    await expect(page.locator('.reto-detalle-page')).toBeVisible();

    // Reportar avance
    await page.click('text=Reportar Avance');
    await expect(page.locator('form')).toBeVisible();
    await page.fill('textarea[name="descripcion"]', 'Avance de prueba');
    await page.fill('input[name="fecha"]', '2025-08-12');
    await page.click('button[type="submit"]');
    await expect(page.locator('.success-message')).toBeVisible();

    // Sesiones
    await page.click('text=Sesiones');
    await expect(page.locator('.sesiones-page')).toBeVisible();

    // Validadores
    await page.click('text=Validadores');
    await expect(page.locator('.validadores-page')).toBeVisible();
  });
});
