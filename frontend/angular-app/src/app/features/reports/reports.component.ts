import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { toUserMessage } from '../../core/services/api-error.util';
import { ReportService } from '../../core/services/report.service';
import { LowStockReportItem } from '../../shared/models/report.model';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html'
})
export class ReportsComponent implements OnInit {
  private readonly cdr = inject(ChangeDetectorRef);

  items: LowStockReportItem[] = [];
  loading = false;
  errorMessage = '';

  constructor(private readonly reportService: ReportService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.reportService.lowStock().subscribe({
      next: (items) => {
        this.items = items;
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
}
