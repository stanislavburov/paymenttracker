package ru.bsv.paymenttracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class Dao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<PaymentDTO> PAYMENT_DTO_ROW_MAPPER = (rs, rowNum) -> {
        PaymentDTO result = new PaymentDTO();
        result.setPaymentDate(rs.getString("p_date"));
        result.setValue(rs.getDouble("p_value"));
        result.setComment(rs.getString("p_comment"));
        return result;
    };

    public void addPayment(double value, String comment) {
        jdbcTemplate.update(
                "insert into t_payment (p_date, p_value, p_comment) values (FORMATDATETIME(CURDATE(), 'yyyy-MM-dd'), ?, ?)",
                value,
                comment
        );
    }

    public double fetchCurrentDailyLimit() {
        Double currentDailyLimit = jdbcTemplate.queryForObject(
                "select dl_limit from t_daily_limit where dl_start_date = (select max(dl_start_date) from t_daily_limit)",
                Double.class
        );
        if (currentDailyLimit == null) {
            throw new IllegalStateException("No daily limit in DB");
        }
        return currentDailyLimit;
    }

    public LocalDate fetchCurrentIntervalStartDate() {
        String currentIntervalStartDateString = jdbcTemplate.queryForObject(
                "select max(i_start) from t_interval",
                String.class
        );
        if (currentIntervalStartDateString == null) {
            startInterval();
            return LocalDate.now();
        } else {
            return LocalDate.parse(currentIntervalStartDateString);
        }
    }

    public double calcCurrentIntervalExpenses() {
        Double expenses = jdbcTemplate.queryForObject(
                "select sum(p_value) from t_payment where p_date >= (select max(i_start) from t_interval)",
                Double.class
        );
        return expenses == null ? 0.0 : expenses;
    }

    public void startInterval() {
        jdbcTemplate.update("insert into t_interval (i_start) values (FORMATDATETIME(CURDATE(), 'yyyy-MM-dd'))");
    }

    public List<PaymentDTO> listLastPayments(int size) {
        return jdbcTemplate.query(
                "select p_date, p_value, p_comment from t_payment where id >= (select max(id) from t_payment) - ? order by id desc",
                PAYMENT_DTO_ROW_MAPPER,
                size
        );
    }

    public List<PaymentDTO> listIntervalPayments(LocalDate intervalStartDate) {
        if (intervalStartDate != null) {
            throw new RuntimeException("Not yet implemented");
        }
        return jdbcTemplate.query(
                "select p_date, p_value, p_comment from t_payment where p_date >= (select max(i_start) from t_interval) order by id desc",
                PAYMENT_DTO_ROW_MAPPER
        );
    }

    public List<IntervalStatsDTO> listIntervalStatsWithoutCurrent() {
        return jdbcTemplate.query(
                "select interval_start, interval_finish, sum(p.p_value) as expenses from t_payment p right join (select i1.i_start as interval_start, min(i2.i_start) as interval_finish from t_interval i1, t_interval i2 where i1.i_start < i2.i_start group by i1.i_start) i on i.interval_start <= p.p_date and p.p_date < i.interval_finish group by interval_start, interval_finish order by interval_start desc;",
                (rs, rowNum) -> {
                    LocalDate intervalStart = LocalDate.parse(rs.getString("interval_start"));
                    LocalDate intervalFinish = LocalDate.parse(rs.getString("interval_finish"));
                    long daysPassed = ChronoUnit.DAYS.between(intervalStart, intervalFinish);
                    double avgExpenses = rs.getDouble("expenses") / ((double) daysPassed);
                    IntervalStatsDTO result = new IntervalStatsDTO();
                    result.setStartDate(UserInterface.INTERVAL_START_DATE_FORMATTER.format(intervalStart));
                    result.setDaysPassed(daysPassed);
                    result.setAvgDailyExpense(UserInterface.formatExpense(avgExpenses));
                    return result;
                }
        );
    }
}
