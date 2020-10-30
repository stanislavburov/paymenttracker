insert into t_daily_limit (
  dl_start_date,
  dl_limit
)
 values (
  FORMATDATETIME(CURDATE(), 'yyyy-MM-dd'),
  3000
);
