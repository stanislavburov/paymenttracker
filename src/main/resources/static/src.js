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
