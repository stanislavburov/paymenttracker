<!DOCTYPE html>
<html>
    <head>
        <title>Payment Tracker</title>
        <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
        <script src="src.js"></script>
        <script>$(document).ready(function() {
              var intervalStartDate = new URLSearchParams(window.location.search).get('intervalStartDate');
              listIntervalPayments(intervalStartDate);
            });
        </script>
    </head>
    <body>
        <table id="ip" cols="3">
            <caption>Full list of interval payments</caption>
            <thead>
                <tr>
                    <td>Date</td>
                    <td>Amount</td>
                    <td>Comment</td>
                </tr>
            </thead>
            <tbody/>
        </table>
    </body>
    <div id="ipe"></div>
</html>