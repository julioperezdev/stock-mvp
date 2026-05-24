import { expect, test } from '@playwright/test';

test('closed MVP flow works through the Angular UI', async ({ page }) => {
  const suffix = Date.now();
  const productSku = `UI-${suffix}`;
  const productName = `Producto UI ${suffix}`;
  const storeCode = `UI-${suffix}`;
  const storeName = `Local UI ${suffix}`;

  await page.goto('http://localhost:4200/');
  await expect(page.getByRole('heading', { name: 'Stock MVP' })).toBeVisible();
  await page.locator('#email').fill('admin@stock.local');
  await page.locator('#password').fill('admin123');
  await page.getByRole('button', { name: 'Ingresar' }).click();
  await expect(page.getByRole('heading', { name: 'Dashboard' })).toBeVisible();

  const productsLoad = page.waitForResponse((response) =>
    response.url().includes('/api/products') && response.request().method() === 'GET'
  );
  await page.getByRole('link', { name: 'Productos', exact: true }).click();
  await productsLoad;
  await expect(page.getByRole('heading', { name: 'Productos' })).toBeVisible();
  await page.locator('#sku').fill(productSku);
  await page.locator('#name').fill(productName);
  await page.locator('#description').fill('Producto creado por prueba UI');
  await page.getByRole('button', { name: 'Crear' }).click();
  await expect(page.getByText('Producto creado.')).toBeVisible();
  await expect(page.getByText(productName)).toBeVisible();

  const storesLoad = page.waitForResponse((response) =>
    response.url().includes('/api/stores') && response.request().method() === 'GET'
  );
  await page.getByRole('link', { name: 'Locales', exact: true }).click();
  await storesLoad;
  await expect(page.getByRole('heading', { name: 'Locales' })).toBeVisible();
  await page.locator('#code').fill(storeCode);
  await page.locator('#name').fill(storeName);
  await expect(page.locator('#code')).toHaveValue(storeCode);
  await expect(page.locator('#name')).toHaveValue(storeName);
  await page.getByRole('button', { name: 'Crear' }).click();
  await expect(page.getByText('Local creado.')).toBeVisible();
  await expect(page.getByText(storeName)).toBeVisible();

  const stockInitialLoad = Promise.all([
    page.waitForResponse((response) => response.url().includes('/api/products') && response.request().method() === 'GET'),
    page.waitForResponse((response) => response.url().includes('/api/stores') && response.request().method() === 'GET'),
    page.waitForResponse((response) => response.url().includes('/api/stocks') && response.request().method() === 'GET')
  ]);
  await page.getByRole('link', { name: 'Stock', exact: true }).click();
  await stockInitialLoad;
  await expect(page.getByRole('heading', { name: 'Stock', exact: true })).toBeVisible();
  await page.locator('#productId').selectOption({ label: productName });
  await page.locator('#storeId').selectOption({ label: storeName });
  await page.locator('#currentQuantity').fill('4');
  await page.locator('#minimumQuantity').fill('3');
  await page.getByRole('button', { name: 'Guardar stock' }).click();
  await expect(page.getByText('Stock creado.')).toBeVisible();
  await expect(page.getByRole('row').filter({ hasText: productName }).filter({ hasText: storeName })).toBeVisible();

  const salesInitialLoad = Promise.all([
    page.waitForResponse((response) => response.url().includes('/api/products') && response.request().method() === 'GET'),
    page.waitForResponse((response) => response.url().includes('/api/stores') && response.request().method() === 'GET'),
    page.waitForResponse((response) => response.url().includes('/api/sales') && response.request().method() === 'GET')
  ]);
  await page.getByRole('link', { name: 'Venta', exact: true }).click();
  await salesInitialLoad;
  await expect(page.getByRole('heading', { name: 'Registrar venta' })).toBeVisible();
  await page.locator('#storeId').selectOption({ label: storeName });
  await page.locator('#productId').selectOption({ label: productName });
  await page.locator('#quantity').fill('2');
  await page.getByRole('button', { name: 'Agregar' }).click();
  await page.getByRole('button', { name: 'Confirmar venta' }).click();
  await expect(page.getByText(/Venta #\d+ confirmada\./)).toBeVisible();

  const stockReload = Promise.all([
    page.waitForResponse((response) => response.url().includes('/api/products') && response.request().method() === 'GET'),
    page.waitForResponse((response) => response.url().includes('/api/stores') && response.request().method() === 'GET'),
    page.waitForResponse((response) => response.url().includes('/api/stocks') && response.request().method() === 'GET')
  ]);
  await page.getByRole('link', { name: 'Stock', exact: true }).click();
  await stockReload;
  await expect(page.getByRole('heading', { name: 'Stock', exact: true })).toBeVisible();
  await page.locator('#filterStore').selectOption({ label: storeName });
  await page.getByRole('button', { name: 'Filtrar' }).click();
  const stockRow = page.getByRole('row').filter({ hasText: productName }).filter({ hasText: storeName });
  await expect(stockRow).toContainText('2');
  await expect(stockRow).toContainText('Critico');

  const lowStockLoad = page.waitForResponse((response) =>
    response.url().includes('/api/reports/low-stock') && response.request().method() === 'GET'
  );
  await page.getByRole('link', { name: 'Criticos', exact: true }).click();
  await lowStockLoad;
  await expect(page.getByRole('heading', { name: 'Stock critico' })).toBeVisible();
  const lowStockRow = page.getByRole('row').filter({ hasText: productName }).filter({ hasText: storeName });
  await expect(lowStockRow).toContainText('1');

  const salesReload = Promise.all([
    page.waitForResponse((response) => response.url().includes('/api/products') && response.request().method() === 'GET'),
    page.waitForResponse((response) => response.url().includes('/api/stores') && response.request().method() === 'GET'),
    page.waitForResponse((response) => response.url().includes('/api/sales') && response.request().method() === 'GET')
  ]);
  await page.getByRole('link', { name: 'Venta', exact: true }).click();
  await salesReload;
  await expect(page.getByRole('heading', { name: 'Registrar venta' })).toBeVisible();
  await page.locator('#storeId').selectOption({ label: storeName });
  await page.locator('#productId').selectOption({ label: productName });
  await page.locator('#quantity').fill('99');
  await page.getByRole('button', { name: 'Agregar' }).click();
  await page.getByRole('button', { name: 'Confirmar venta' }).click();
  await expect(page.getByText('No hay stock suficiente para completar la venta.')).toBeVisible();
});
