<!DOCTYPE html>
<html>
    <head>
        <title>Payment Tracker</title>
        <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
        <script src="src.js"></script>
        <script>$(document).ready(function() {
              updateBalance();
              listLatestPayments();
              listIntervalStats();
            });
        </script>
    </head>

    <body>
        <p>
            <div>
                <span>Current balance: </span><span id="balance">...</span>
            </div>
            <div>
                <span id="currentIntervalStats">...</span>
            </div>
        </p>
        <p>
            <form action="javascript:addPayment();">
                <div>Add payment:</div>
                <label>Amount: <input id="pv" name="payment-value" autofocus/></label>
                <label>Comment: <input id="pc" name="payment-comment"/></label>
                <input type="submit" value="Add"/>
                <div id="pMsg"></div>
            </form>
        </p>
        <p>
            <div>
                <input type="button" value="Start new interval" onClick="startInterval()"/>
                <div id="iMsg"/>
            </div>
        </p>
        <p>
            <a href="interval-payments">Full list of payments for the current interval</a>
        </p>
        <p>
            <table id="lp" cols="3">
                <caption>List of last payments</caption>
                <thead>
                    <tr>
                        <td>Date</td>
                        <td>Amount</td>
                        <td>Comment</td>
                    </tr>
                </thead>
                <tbody/>
            </table>
        </p>
        <p>
            <table id="li" cols="3">
                <caption>List of all interval statistics</caption>
                <thead>
                    <tr>
                        <td>Interval start date</td>
                        <td>Days passed</td>
                        <td>Average daily expense</td>
                    </tr>
                </thead>
                <tbody/>
            </table>
            <div id="lie"></div>
        </p>
    </body>
</html>
