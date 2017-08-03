<!DOCTYPE html>

<html>

<head>
    <!-- Title and icon -->
    <title>EVE - Events Visualization Environment</title>
    <link rel="icon" type="image/gif" href="../img/eve2.ico" />

    <!-- Metas-->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width">

    <!-- CSS -->
    <link rel="stylesheet" href="../css/eve.css">

    <!-- JQuery, Font Awesome -->
    <script src="../jquery/jquery-1.11.3.min.js"></script>
    <link rel="stylesheet" href="../fontawesome/css/font-awesome.css">

    <!-- Textillate -->
    <link href="textillate/animate.css" rel="stylesheet">
    <script src="textillate/jquery.lettering.js"></script>
    <script src="textillate/jquery.textillate.js"></script>

    <!-- CSS -->
    <style>
        .eventHeader {
            font-size: 28px;
            color: #CA0000;
            margin: 0 0 5px 0;
        }

        #noMainEvent, #noEventHistory {
            font-size: 16px;
        }

        #mainEventFirstLine {
            padding: 15px 0 0 50px;
            font-size: 35px;
            color: white;
        }

        #mainEventFirstLine, #IP {
            font-size: 30px;
            color: white;
        }

        #mainEventSecondLine {
            margin: 5px 0 30px 50px;
            font-size: 20px;
            color: white;
        }

        #eventsHistory {
            margin: 0 0 0 30px;
            font-size: 20px;
            color: white;
        }

    </style>

</head>

<body>

    <!-- Title -->
    <%@include file="eveHeader.html" %>


    <%--<div class="eventHeader fa fa-snowflake-o  fa-bell-o" aria-hidden="true">&nbsp; Last alert</div>--%>
    <div class="eventHeader" aria-hidden="true">Current alert</div>
    <div id="noMainEvent">No alert currently</div>
    <div id="mainEventFirstLine">&nbsp;</div>
    <div id="mainEventSecondLine">&nbsp;</div>

    <%--<div class="eventHeader fa fa-paperclip"  aria-hidden="true">&nbsp; Past alerts</div>--%>
    <div class="eventHeader">Past alerts</div>
    <div id="noEventHistory">None</div>
    <ul id="eventsHistory"></ul>


    <!-- Script to handle the WebSocket -->
    <script type="text/javascript">

        $(document).ready(function () {
            openWebSocket();
            initHistory();
        });


        var webSocket;
        var registry = [];

        var currentInitiated = false ;
        function openWebSocket() {

            // Ensures only one connection is open at a time
            if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) return;

            webSocket = new WebSocket("ws://100.64.9.44:7777/eve/ws/xx");
            webSocket.onmessage = function (message) {

                var attack = JSON.parse(message.data);

                if (!currentInitiated) {
                    initCurrent () ;
                    currentInitiated = false ;
                }
                writeCurrentEvent(attack);

                registry.push(attack);
                updateHistory();
            };
        }


        function initCurrent () {
            $("#noMainEvent").hide();

            var textillateOptions = {
                initialDelay: 0,
                autoStart: false,
                in: {effect: 'fadeInLeftBig', delayScale: 1.5, delay: 20, callback: function () { } },
                out: {effect: 'hinge', delayScale: 1.5, delay: 50, sync: false, shuffle: false, reverse: false, callback: function () { } },
                type: 'char' // 'char' and 'word'
            };

            $("#mainEventFirstLine").textillate(textillateOptions) ;
            $("#mainEventSecondLine").textillate(textillateOptions) ;
        }


        function writeCurrentEvent(attack) {
            var first = $("#mainEventFirstLine");
            first.find('.texts li:first').html(getFirstLineForCurrentAlert(attack));
            first.textillate('start');

            var second = $("#mainEventSecondLine");
            second.find('.texts li:first').html(getSecondLineForLastAlert(attack));
            second.textillate('start');
        }

        function getFirstLineForCurrentAlert(attack) {
            var res = "";
            if (nonEmpty(attack.target.name)) res += attack.target.name + " ";
            if (nonEmpty(attack.target.IP)) res += "<span id=\"IP\"> (" + attack.target.IP + ") </span> -> ";
            if (nonEmpty(attack.what.description)) res += attack.what.description;
            else res += "Unspecified alert";

            return res;
        }

        function getSecondLineForLastAlert(attack) {
            var res = "";

            res += "Detected by: " + attack.evidence.type + ". ";
            var date = new Date(attack.date);
            res += "At: " + pad(date.getHours()) + ":" + pad(date.getMinutes()) + ":" + pad(date.getSeconds()) + ". ";

            if (nonEmpty(attack.source.IP))
                res += "Source: " + attack.source.IP + ". ";
            else
                res += "Source: Unknown. ";

            if (nonEmpty(attack.evidence.description))
                res += "Detailed info: (" + ")";

            return res;
        }

        function initHistory() {
            $.ajax({type: "get", url: "getEvents", success: function (data) {
                registry = data;
                writeHistory();
            }});
        }

        function writeHistory() {
            var evh = $("#eventsHistory");

            if (registry.length > 1)
                $("#noEventHistory").hide();
            else
                return;

            for (var i = 0; i < registry.length; i++)
                evh.append("<li>" + getHistoryLine(registry[i]) + "</li>");
        }

        function updateHistory() {
            var evh = $("#eventsHistory");
            var lastEvent = registry[registry.length - 2];
            evh.prepend("<li>" + getHistoryLine(lastEvent) + "</li>");
        }

        function getHistoryLine(attack) {
            var res = "";
            var date = new Date(attack.date);
            res += pad(date.getHours()) + ":" + pad(date.getMinutes()) + ":" + pad(date.getSeconds()) + " > ";
            res += attack.what.description;
            return res;
        }

        function pad(number) {
            return (number < 10) ? '0' + number : number;
        }

        function nonEmpty(string) {
            return string != null && string != undefined && string.length > 0;
        }

    </script>

</body>
</html>