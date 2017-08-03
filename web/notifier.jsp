<!DOCTYPE html>

<html>

<head>
    <!-- Title -->
    <title>EVE - Events Notifier</title>

    <!-- Metas -->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width">

    <!-- CSS -->
    <link rel="stylesheet" href="css/eve.css">

    <!-- JQuery -->
    <script src="jquery/jquery-1.11.3.min.js"></script>


    <!-- CSS -->
    <style type="text/css">
        #title {
            background-image: url("../img/eve.jpg");
            background-position: 0 -80px;
            background-repeat: no-repeat;
            overflow: auto;
            height: 170px;
            width: 950px;
            padding: 0 0 100px 0;
            margin: auto;
        }

        #mainTitle {
            margin: 70px 0 0 350px;
            font-size: 35px;
        }

        #eve {
            color: #ca0000;
            font-size: 40px;
            font-weight: bold;
        }

        #secondaryTitle {
            margin: 20px 0 0 480px;
        }

        #main {
            margin: auto;
            width: 1100px;
            font-size: 18px;
        }

        #left {
            width: 530px;
            float: left;
            margin: 25px 0 50px 0;
        }

        #right {
            width: 520px;
            float: left;
        }

        #json {
            color: red;
        }

        #eventArea {
            width: 500px;
            height: 380px;
            margin: 8px 0 12px 0;
            padding: 5px;
        }

        #result {
            font-size: 14px;
        }

        li {
            margin-top: 7px;
        }

    </style>

    <!-- Javascript -->
    <script type="text/javascript">
        function sendEvent() {
            $("#result").html("");

            $.ajax({
                type: "post",
                url: "newEvent",
                data: $("#eventArea").val(),
                success: showResult,
                error: showResult
            });
        }

        function showExample() {
            $("#result").html("");

            var example = "{\n" +
                    "    \"source\": {\n" +
                    "            \"IPV4\": \"100.64.8.17\", \n" +
                    "            \"IPV6\": \"2a07:1181:150::17\"\n" +
                    "    },\n" +
                    "\n" +
                    "    \"target\": {\n" +
                    "            \"type\": \"host\",\n" +
                    "            \"IPV4\": \"100.64.8.33\",\n" +
                    "            \"IPV6\": \"2a07:1181:150::33\",\n" +
                    "            \"name\": \"news.ex\"\n" +
                    "    },\n" +
                    "\n" +
                    "     \"payload\": {\n" +
                    "            \"name\": \"SQLi\",\n" +
                    "            \"probe\": \"IDS\",\n" +
                    "            \"evidence\": \"SQL command 'or 1=1' found in URL\",\n" +
                    "            \"url\": \"http://elastic.search.com/query\"\n" +
                    "     }, \n" +
                    "\n" +
                    "    \"related\": {\n" +
                    "            \"type\": \"honeystuff\",\n" +
                    "            \"IPV4\": \"100.64.8.171\", \n" +
                    "            \"IPV6\": \"\", \n" +
                    "            \"description\": \"Some description here\"\n" +
                    "    }\n" +
                    "}";

            $("#eventArea").val(example);
        }

        function showResult(response, state) {
            $("#result").html(state == "success" ? "Event has been registered" : "Error: " + response.responseText);
        }

    </script>

</head>


<body>

    <!-- Title -->
    <div id="title">
        <div id="mainTitle"><span id="eve"> EVE </span> Events Visualization Environment</div>
        <div id="secondaryTitle"> Advanced Intrusion Detection for Crossed Swords 2017</div>
    </div>


    <!-- Main -->
    <div id="main">

        <div id="left">
            <h1>Events notification</h1>
            There are two ways to notify "events" to EVE:
            <ol>
                <li>Manually, using the form provided by this page</li>
                <li>Automatically, send POST requests to EVE. Make use, for example, of Teemu's Python script to send event's</li>
            </ol>
            <br><br>
            Notes:
            <ul>
                <li>The events will be grouped into "attacks" by EVE</li>
                <li>An attack is defined as a collection of related events (same source, same target and reported within 5 secs. from the last event)</li>
            </ul>

        </div>

        <div id="right">
            <label id="eventLabel" for="eventArea">Notify an event using <span id="json">JSON</span> (<a onclick="showExample()" target="_blank">show an example</a>) </label>
            <textarea id="eventArea">Enter event here...</textarea>
            <span class="button" onclick="sendEvent()">Notify event</span>&nbsp;&nbsp; <span id="result"></span>
        </div>

    </div>


</body>

</html>