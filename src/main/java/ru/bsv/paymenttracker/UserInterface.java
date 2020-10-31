package ru.bsv.paymenttracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

@Controller
public class UserInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInterface.class);

    @Autowired
    private Dao dao;
    @Value("${last.payments.size}")
    private int lastPaymentsSize;

    @GetMapping
    public String index() {
        return "index";
    }

    @PostMapping(value = "add-payment")
    public ResponseEntity<String> addPayment(@RequestBody PaymentDTO paymentDTO) {
        if (dao.fetchCurrentDailyLimit() < paymentDTO.getValue() && StringUtils.isEmpty(paymentDTO.getComment())) {
            return ResponseEntity.status(400).body("Comment is mandatory when payment is greater than daily limit");
        } else {
            dao.addPayment(paymentDTO.getValue(), paymentDTO.getComment());
            return ResponseEntity.ok("");
        }
    }

    @PostMapping(value = "fetch-balance", produces = "text/plain")
    @ResponseBody
    public String fetchBalance() {
        double dailyLimit = dao.fetchCurrentDailyLimit();
        LocalDate intervalStartDate = dao.fetchCurrentIntervalStartDate();
        double expenses = dao.calcCurrentIntervalExpenses();
        long daysPassed = ChronoUnit.DAYS.between(intervalStartDate, LocalDate.now()) + 1; // Count the current day also by adding 1.
        return new Formatter(Locale.ENGLISH).format("%.2f", ((daysPassed * dailyLimit) - expenses)).toString();
    }

    @PostMapping(value = "start-interval")
    @ResponseBody
    public void startInterval() {
        dao.startInterval();
    }

    @PostMapping(value = "list-last-payments")
    @ResponseBody
    public List<PaymentDTO> listLastPayments() {
        return dao.listLastPayments(lastPaymentsSize);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception e) {
        LOGGER.error("Failure", e);
        return "Failure: " + e.toString();
    }
}
