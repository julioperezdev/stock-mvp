# Cloud Notes

- Backend target: EC2 or container platform behind Nginx/API Gateway.
- Database target: RDS PostgreSQL.
- Lambdas should call backend report endpoints instead of duplicating business rules.
- CloudWatch Logs should capture daily report and low-stock alert execution output.
