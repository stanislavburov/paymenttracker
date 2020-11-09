function addPayment() {
  setMsg('#pMsg', '');
  $.ajax({
    url: "add-payment",
    method: "post",
    contentType: "application/json",
    data: JSON.stringify({value: $('#pv').val(), comment: $('#pc').val()}),
    success: function(result) {
      setMsg('#pMsg', 'Success');
      updateBalance();
      listLatestPayments();
      listIntervalStats();
    },
    error: function(result) {
      setMsg('#pMsg', result.responseText);
    },
    complete: function() {
      $('#pv').val('');
      $('#pc').val('');
      $('#pv').focus();
    }
  });
};

function startInterval() {
  setMsg('#iMsg', '');
  $.ajax({
    url: "start-interval",
    method: "post",
    contentType: "text/plain",
    success: function(result) {
      setMsg('#iMsg', 'Started a new interval');
      updateBalance();
    },
    error: function(result) {
      setMsg('#iMsg', result.responseText);
    }
  });
};

function setMsg(selector, msg) {
  $(selector).text(msg);
};

function updateBalance() {
  setMsg('#balance', '...');
  setMsg('#currentIntervalStats', '...');
  $.ajax({
    url: "fetch-balance",
    method: "post",
    contentType: "text/plain",
    success: function(result) {
      setMsg('#balance', result);
    },
    error: function(result) {
      setMsg('#balance', 'Unable to calculate the current balance: ' + result.responseText);
    }
  });
  $.ajax({
    url: "fetch-current-interval-stats",
    method: "post",
    success: function(result) {
      setMsg('#currentIntervalStats', `Current interval started on ${result.startDate}, since then ${result.daysPassed} days passed, average daily expense is ${result.avgDailyExpense}.`);
    },
    error: function(result) {
      setMsg('#currentIntervalStats', 'Unable to fetch the stats for the current interval: ' + result.responseText);
    }
  });
};

function listLatestPayments() {
  return queryPayments("list-last-payments", 'lp');
};

function listIntervalPayments(intervalStartDate) {
  var url = 'list-interval-payments';
  if (intervalStartDate != null) {
    url += '?intervalStartDate=' + intervalStartDate;
  }
  return queryPayments(url, 'ip', 'ipe');
}

function printIntervalPaymentTableCaption(intervalStartDate) {
  var currentInterval = intervalStartDate == null;
  setMsg('#ipc', `Full list of payments for${currentInterval ? ' current' : ''} interval${currentInterval ? '' : ' started on ' + intervalStartDate}`);
}

function queryPayments(url, tableId, errorId) {
  $.ajax({
    url: url,
    method: "post",
    contentType: "application/json",
    success: function(result) {
      var rows = [];
      result.forEach((e) => {
        var pd = e.paymentDate;
        var v = e.value;
        var c = e.comment;
        rows.push(`<tr><td>${pd}</td><td>${v}</td><td>${c}</td></tr>`)
      });
      $('#' + tableId + '>tbody').html(rows.join(''));
    },
    error: function(result) {
      if(errorId) {
        setMsg('#' + errorId, result.responseText)
      }
    }
  });
}

function listIntervalStats() {
  setMsg('#lie', '');
  $.ajax({
    url: "list-interval-stats",
    method: "post",
    success: function(result) {
      var rows = [];
      result.forEach((e) => {
        var sd = e.startDate;
        var dp = e.daysPassed;
        var ade = e.avgDailyExpense;
        rows.push(`<tr><td>${sd}</td><td>${dp}</td><td>${ade}</td><td><a href='interval-payments?intervalStartDate=${sd}'>link</a></td></tr>`)
      });
      $('#li>tbody').html(rows.join(''));
    },
    error: function(result) {
      setMsg('#lie', result.responseText);
    }
  });
}
