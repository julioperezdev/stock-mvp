import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { toUserMessage } from '../../core/services/api-error.util';
import { StoreService } from '../../core/services/store.service';
import { Store } from '../../shared/models/store.model';

@Component({
  selector: 'app-stores',
  imports: [ReactiveFormsModule],
  templateUrl: './stores.component.html'
})
export class StoresComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly cdr = inject(ChangeDetectorRef);

  stores: Store[] = [];
  loading = false;
  message = '';
  errorMessage = '';

  readonly form = this.fb.nonNullable.group({
    code: ['', [Validators.required, Validators.maxLength(50)]],
    name: ['', [Validators.required, Validators.maxLength(150)]]
  });

  constructor(
    private readonly storeService: StoreService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.storeService.list().subscribe({
      next: (stores) => {
        this.stores = stores;
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
    this.storeService.create(this.form.getRawValue()).subscribe({
      next: (store) => {
        this.message = 'Local creado.';
        this.stores = [...this.stores, store].sort((a, b) => a.name.localeCompare(b.name));
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
