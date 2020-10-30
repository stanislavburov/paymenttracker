package ru.bsv.paymenttracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
public class Dao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                new RowMapper<PaymentDTO>() {
                    @Override
                    public PaymentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PaymentDTO result = new PaymentDTO();
                        result.setPaymentDate(rs.getString("p_date"));
                        result.setValue(rs.getDouble("p_value"));
                        result.setComment(rs.getString("p_comment"));
                        return result;
                    }
                },
                size
        );
    }
}
