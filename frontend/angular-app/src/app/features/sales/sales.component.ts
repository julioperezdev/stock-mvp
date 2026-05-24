import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { toUserMessage } from '../../core/services/api-error.util';
import { ProductService } from '../../core/services/product.service';
import { SaleService } from '../../core/services/sale.service';
import { StoreService } from '../../core/services/store.service';
import { Product } from '../../shared/models/product.model';
import { CreateSaleItemRequest, SaleResponse } from '../../shared/models/sale.model';
import { Store } from '../../shared/models/store.model';

@Component({
  selector: 'app-sales',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './sales.component.html',
  styleUrl: './sales.component.scss'
})
export class SalesComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly cdr = inject(ChangeDetectorRef);

  products: Product[] = [];
  stores: Store[] = [];
  saleItems: CreateSaleItemRequest[] = [];
  recentSales: SaleResponse[] = [];
  message = '';
  errorMessage = '';
  loading = false;

  readonly saleForm = this.fb.nonNullable.group({
    storeId: [0, [Validators.required, Validators.min(1)]],
    createdBy: ['local-user', Validators.maxLength(100)]
  });

  readonly itemForm = this.fb.nonNullable.group({
    productId: [0, [Validators.required, Validators.min(1)]],
    quantity: [1, [Validators.required, Validators.min(1)]]
  });

  constructor(
    private readonly productService: ProductService,
    private readonly storeService: StoreService,
    private readonly saleService: SaleService
  ) {}

  ngOnInit(): void {
    this.loadInitialData();
  }

  productName(productId: number): string {
    return this.products.find((product) => product.id === productId)?.name ?? `#${productId}`;
  }

  loadInitialData(): void {
    this.loading = true;
    this.errorMessage = '';
    forkJoin({
      products: this.productService.list(),
      stores: this.storeService.list(),
      sales: this.saleService.list()
    }).subscribe({
      next: ({ products, stores, sales }) => {
        this.products = products;
        this.stores = stores;
        this.recentSales = sales.slice(0, 8);
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = toUserMessage(error);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  addItem(): void {
    if (this.itemForm.invalid) {
      this.itemForm.markAllAsTouched();
      return;
    }

    const item = this.itemForm.getRawValue();
    const normalizedItem = {
      productId: Number(item.productId),
      quantity: Number(item.quantity)
    };
    const existing = this.saleItems.find((saleItem) => saleItem.productId === normalizedItem.productId);
    if (existing) {
      existing.quantity += normalizedItem.quantity;
    } else {
      this.saleItems = [...this.saleItems, normalizedItem];
    }
    this.itemForm.reset({ productId: 0, quantity: 1 });
  }

  removeItem(productId: number): void {
    this.saleItems = this.saleItems.filter((item) => item.productId !== productId);
  }

  confirmSale(): void {
    if (this.saleForm.invalid || this.saleItems.length === 0) {
      this.saleForm.markAllAsTouched();
      this.errorMessage = this.saleItems.length === 0 ? 'Agrega al menos un producto a la venta.' : '';
      return;
    }

    this.message = '';
    this.errorMessage = '';
    this.saleService
      .create({
        storeId: Number(this.saleForm.getRawValue().storeId),
        createdBy: this.saleForm.getRawValue().createdBy,
        items: this.saleItems
      })
      .subscribe({
        next: (sale) => {
          this.message = `Venta #${sale.saleId} confirmada.`;
          this.saleItems = [];
          this.recentSales = [sale, ...this.recentSales].slice(0, 8);
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.errorMessage = toUserMessage(error);
          this.cdr.detectChanges();
        }
      });
  }
}
