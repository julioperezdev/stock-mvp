import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { toUserMessage } from '../../core/services/api-error.util';
import { ProductService } from '../../core/services/product.service';
import { StockService } from '../../core/services/stock.service';
import { StoreService } from '../../core/services/store.service';
import { Product } from '../../shared/models/product.model';
import { StockItem } from '../../shared/models/stock.model';
import { Store } from '../../shared/models/store.model';

@Component({
  selector: 'app-stock',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './stock.component.html'
})
export class StockComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly cdr = inject(ChangeDetectorRef);

  products: Product[] = [];
  stores: Store[] = [];
  stocks: StockItem[] = [];
  loading = false;
  message = '';
  errorMessage = '';

  readonly filterForm = this.fb.nonNullable.group({
    storeId: [0],
    productId: [0],
    criticalOnly: [false]
  });

  readonly createForm = this.fb.nonNullable.group({
    productId: [0, [Validators.required, Validators.min(1)]],
    storeId: [0, [Validators.required, Validators.min(1)]],
    currentQuantity: [0, [Validators.required, Validators.min(0)]],
    minimumQuantity: [0, [Validators.required, Validators.min(0)]]
  });

  constructor(
    private readonly productService: ProductService,
    private readonly storeService: StoreService,
    private readonly stockService: StockService
  ) {}

  ngOnInit(): void {
    this.loadInitialData();
  }

  get visibleStocks(): StockItem[] {
    return this.filterForm.controls.criticalOnly.value
      ? this.stocks.filter((stock) => stock.critical)
      : this.stocks;
  }

  loadInitialData(): void {
    this.loading = true;
    this.errorMessage = '';
    forkJoin({
      products: this.productService.list(),
      stores: this.storeService.list()
    }).subscribe({
      next: ({ products, stores }) => {
        this.products = products;
        this.stores = stores;
        this.cdr.detectChanges();
        this.loadStocks();
      },
      error: (error) => {
        this.errorMessage = toUserMessage(error);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  loadStocks(): void {
    this.loading = true;
    this.errorMessage = '';
    const { storeId, productId } = this.filterForm.getRawValue();
    this.stockService
      .list({
        storeId: Number(storeId) || undefined,
        productId: Number(productId) || undefined
      })
      .subscribe({
        next: (stocks) => {
          this.stocks = stocks;
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

  clearFilters(): void {
    this.filterForm.reset({ storeId: 0, productId: 0, criticalOnly: false });
    this.loadStocks();
  }

  createStock(): void {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }

    this.message = '';
    this.errorMessage = '';
    const raw = this.createForm.getRawValue();
    this.stockService.create({
      productId: Number(raw.productId),
      storeId: Number(raw.storeId),
      currentQuantity: Number(raw.currentQuantity),
      minimumQuantity: Number(raw.minimumQuantity)
    }).subscribe({
      next: (stock) => {
        this.message = 'Stock creado.';
        this.stocks = [...this.stocks.filter((item) => item.stockId !== stock.stockId), stock];
        this.createForm.reset({
          productId: 0,
          storeId: 0,
          currentQuantity: 0,
          minimumQuantity: 0
        });
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = toUserMessage(error);
        this.cdr.detectChanges();
      }
    });
  }
}
