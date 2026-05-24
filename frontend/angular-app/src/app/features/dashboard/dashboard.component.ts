import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { toUserMessage } from '../../core/services/api-error.util';
import { ReportService } from '../../core/services/report.service';
import { DailyStockReport } from '../../shared/models/report.model';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly cdr = inject(ChangeDetectorRef);

  report?: DailyStockReport;
  loading = false;
  errorMessage = '';

  constructor(private readonly reportService: ReportService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.reportService.dailyStock().subscribe({
      next: (report) => {
        this.report = report;
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
