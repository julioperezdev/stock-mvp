import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { toUserMessage } from '../../core/services/api-error.util';
import { ProductService } from '../../core/services/product.service';
import { Product } from '../../shared/models/product.model';

@Component({
  selector: 'app-products',
  imports: [ReactiveFormsModule],
  templateUrl: './products.component.html'
})
export class ProductsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly cdr = inject(ChangeDetectorRef);

  products: Product[] = [];
  loading = false;
  message = '';
  errorMessage = '';

  readonly form = this.fb.nonNullable.group({
    sku: ['', [Validators.required, Validators.maxLength(50)]],
    name: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', Validators.maxLength(255)]
  });

  constructor(
    private readonly productService: ProductService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.productService.list().subscribe({
      next: (products) => {
        this.products = products;
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

  create(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.message = '';
    this.errorMessage = '';
    this.productService.create(this.form.getRawValue()).subscribe({
      next: (product) => {
        this.message = 'Producto creado.';
        this.products = [...this.products, product].sort((a, b) => a.name.localeCompare(b.name));
        this.form.reset();
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = toUserMessage(error);
        this.cdr.detectChanges();
      }
    });
  }
}
