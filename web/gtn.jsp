<!DOCTYPE html>

<html>


<head>

    <!-- Title and icon -->
    <title>EVE - Events Visualization Environment</title>
    <link rel="icon" type="image/gif" href="img/eve2.ico"/>

    <!-- Metas-->
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width">

    <!-- Common CSS and JS -->
    <link rel="stylesheet" href="css/eve.css">
    <script src="js/eve.js"></script>


    <!-- JQuery, Font Awesome -->
    <script src="jquery/jquery-1.11.3.min.js"></script>
    <link rel="stylesheet" href="fontawesome/css/font-awesome.css">


    <!-- CSS -->
    <style>
        #page {
            width: 2100px;
            background-color: black;
            overflow: auto;
        }

        #canvas-container {
            float: left;
            margin: 5px;
        }

        #rightColumn {
            width: 500px;
            /*height: 1800px;*/
            overflow: auto;
        }

        #title {
            margin: 20px 0 50px 0;
        }

        #mainTitle {
            font-size: 35px;
            text-align: center;
        }

        #eve {
            color: #ca0000;
            font-size: 40px;
            font-weight: bold;
        }

        #secondaryTitle {
            text-align: center;
        }

        #historyHeader {
            color: #ca0000;
            margin: 0 0 15px 20px;
            font-size: 25px;
        }

        #noHistory {
            margin: 0 0 0 20px;
            font-size: 16px;
            color: white;
        }

        #historyContainer {
            height: 905px;
            font-size: 16px;
            color: white;
            margin: 0 0 0 -5px;
            overflow: auto;
        }

        .infoLine {
            width: 650px;
            padding: 3px 10px;
            overflow: auto;
            color: #171717 !important;
        }

        .item {
            float: left;
            text-align: right;
            width: 120px;
            margin-right: 20px;
            color: #ec0000;

        }

        a {
            border-bottom: 0;
        }
    </style>

</head>

<body>

    <div id="page">

        <!-- Canvas -->
        <div id="canvas-container">
            <canvas id="netCanvas" width="1543px" height="1078px">Your browser does not support the required technology</canvas>
        </div>

        <!-- Right column -->
        <div id="rightColumn">

            <!-- Title -->
            <div id="title">
                <div id="mainTitle"><span id="eve"> EVE for XS17</span></div>
                <div id="secondaryTitle"> Events Visualization Environment</div>
            </div>

            <!-- History -->
            <div id="historyHeader">Past attacks</div>
            <div id="noHistory">None</div>
            <div id="historyContainer">
                <ul id="historyList">
                </ul>
            </div>

        </div>

        <!-- Popup -->
        <%@include file="popup/evePopup.html" %>
        <div id="infoAttack">

            <div class="infoLine">
                <div class="item">Source:</div>
                <div id="source"></div>
            </div>
            <div class="infoLine">
                <div class="item">Target:</div>
                <div id="target"></div>
            </div>
            <div class="infoLine">
                <div class="item">Detected at:</div>
                <div id="at"></div>
            </div>
            <div class="infoLine">
                <div class="item">By probe(s):</div>
                <div id="by"></div>
            </div>
            <div class="infoLine">
                <div class="item">Reported:</div>
                <div id="reported"></div>
            </div>
            <div class="infoLine">
                <div class="item">Evidence:</div>
                <div id="evidence" class="message"></div>
            </div>
            <div class="infoLine">
                <div class="item">Related:</div>
                <div id="related" class="message"></div>
            </div>
            <div class="infoLine">
                <div class="item">See also:</div>
                <div id="urls"></div>
            </div>

        </div>

    </div>


    <!-- Script -->
    <script type="text/javascript">

        var networkImg;

        var sourceAndTargetColorLastAttack = "rgba(255, 0, 0, 0.2)";
        var sourceAndTargetColorHistoric = "rgba(150, 150, 150, 0.2)";
        var relatedColorLastAttack = "rgb(170, 113, 13, 0.2)";
        var relatedColorHistoric = "rgba(150, 150, 150, 0.2)";
        var largeRadius = 60;
        var smallRadius = 30;
        var arrowWidth = 3;

        var arrowColorLastAttack = "#900000";
        var arrowColorHistoric = "rgb(150, 150, 150)";
        var arrowHeadLength = 20;   // length of head arrow in pixels

        var ctx;


        $(document).ready(function () {
            $("#infoAttack").hide();
            openWebSocket();
            initCanvas();
        });


        var webSocket;
        function openWebSocket() {

            // Ensures only one connection is open at a time
            if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) return;

            <jsp:useBean id="conf" class="eve.var.Configuration" />
            webSocket = new WebSocket("ws://<%=conf.getUrlToEVE()%>/eve/ws/gt");

            webSocket.onmessage = function (message) {
                var data = JSON.parse(message.data);

                if (data.type != undefined && data.type != "historic")
                    drawAttack(data.attack, "LastAttack");

                handleHistory(data.attack, data.type);
            };
        }


        function initCanvas() {

            networkImg = new Image();
            networkImg.src = "img/network.jpg";
            networkImg.onload = function () {
                document.getElementById("netCanvas").getContext("2d").drawImage(networkImg, 0, 0);
            }
        }


        function drawAttack(attack, type) {
            var positions = {};

            if (attack.source != undefined) {
                positions.xs = attack.source.x;
                positions.ys = attack.source.y;
            }
            else positions.xs = positions.ys = -1000;

            if (attack.target != undefined) {
                positions.xt = attack.target.x;
                positions.yt = attack.target.y;
            }
            else positions.xt = positions.yt = -1000;

            if (attack.related != undefined) {
                positions.xr = attack.related.x;
                positions.yr = attack.related.y;
            }
            else positions.xr = positions.yr = -1000;


            var ctx = document.getElementById("netCanvas").getContext("2d");
            ctx.clearRect(0, 0, networkImg.width, networkImg.height);
            ctx.drawImage(networkImg, 0, 0);


            // Draw the source
            ctx.beginPath();
            if (positions.xs > 0) {
                ctx.arc(positions.xs, positions.ys, smallRadius, 0, 2 * Math.PI, false);

            }

            // Draw the target
            if (positions.xt > 0) {
                ctx.moveTo(positions.xt + largeRadius - arrowWidth, positions.yt);
                ctx.arc(positions.xt, positions.yt, largeRadius - arrowWidth, 0, 2 * Math.PI, false);
            }

            ctx.lineWidth = 2;
            ctx.fillStyle = eval("sourceAndTargetColor" + type);
            ctx.strokeStyle = eval("arrowColor" + type);
            ctx.fill();
            ctx.stroke();


            // Draw the related
            if (positions.xr > 0) {
                ctx.beginPath();
                ctx.fillStyle = eval("relatedColor" + type);
                ctx.arc(positions.xr, positions.yr, largeRadius - arrowWidth, 0, 2 * Math.PI, false);
                ctx.fill();
            }


            // Draw arrow
            if (positions.xs > 0 && positions.xt > 0) {
                ctx.lineWidth = arrowWidth;
                drawArrow(positions.xs, positions.ys, positions.xt, positions.yt);
                ctx.stroke();
            }

            // Show info on popup
            showInfoAttack(attack, positions);
        }

        function drawArrow(xs, ys, xt, yt) {
            var ctx = document.getElementById("netCanvas").getContext("2d");

            var fromX, fromY, toX, toY; // Variables defining origin and end of arrow (they must be calculated)

            var origin = findPointOnCircleClosestToPoint(xs, ys, smallRadius, xt, yt);
            fromX = origin.ax;
            fromY = origin.ay;

            var destination = findPointOnCircleClosestToPoint(xt, yt, largeRadius, xs, ys);
            toX = destination.ax;
            toY = destination.ay;

            var angle = Math.atan2(toY - fromY, toX - fromX);
            ctx.beginPath();
            ctx.moveTo(fromX, fromY);
            ctx.lineTo(toX, toY);
            ctx.lineTo(toX - arrowHeadLength * Math.cos(angle - Math.PI / 6), toY - arrowHeadLength * Math.sin(angle - Math.PI / 6));
            ctx.moveTo(toX, toY);
            ctx.lineTo(toX - arrowHeadLength * Math.cos(angle + Math.PI / 6), toY - arrowHeadLength * Math.sin(angle + Math.PI / 6));
        }

        // http://stackoverflow.com/questions/300871/best-way-to-find-a-point-on-a-circle-closest-to-a-given-point
        function findPointOnCircleClosestToPoint(cX, cY, radius, pX, pY) {
            var vX = pX - cX;
            var vY = pY - cY;
            var magV = Math.sqrt(vX * vX + vY * vY);
            var aX = cX + vX / magV * radius;
            var aY = cY + vY / magV * radius;
            return {ax: aX, ay: aY};
        }

        function showInfoAttack(attack, positions) {

            var title = attack.name;
            if (title == undefined || title.trim().length == 0) title = "Undefined Attack";
            title = title.trim();
            if (title.length > 50) title = title.substr(0, 50) + "...";

            if (attack.source != undefined)
                $("#source").html(attack.source.name != undefined ? attack.source.name : (attack.source.IPV4 != undefined ? attack.source.IPV4 : "Unknown"));
            else
                $("#source").html("");

            if (attack.target != undefined) {
                var target = attack.target.name != undefined ? attack.target.name : (attack.target.IPV4 != undefined ? attack.target.IPV4 : "Unknown");
                target += attack.target.network != null ? " (" + attack.target.network.toLowerCase() + ")" : "";
                $("#target").html(target);
            }
            else
                $("#target").html("");

            $("#at").html(formatDate(new Date(attack.firstNotificationDate)));
            $("#by").html(attack.probes);
            $("#reported").html(attack.totalNumberOfEvents + " time(s)");
            $("#evidence").html(attack.evidences);
            $("#urls").html(attack.linksToURLs);

            if (attack.related != undefined)
                $("#related").html(attack.related.description);
            else
                $("#related").html("");

            var x = positions.xt - 200;
            if (x == undefined || x < 20) x = 20;
            var y = positions.yt + 90;
            if (y == undefined) y = 50;

            popup(title, $("#infoAttack").html(), x, y);
        }

        /*
         History handling
         */
        var registry = [];
        function handleHistory(attack, type) {
            if (attack == undefined) return;

            if (type != undefined && type == "update")
                for (var i = 0; i < registry.length; i++) {
                    if (registry[i].id == attack.id) {
                        registry[i] = attack;
                        break;
                    }
                }
            else
                registry.push(attack);

            if (registry.length > 0) $("#noHistory").hide();
            $("#historyList").prepend("<li>" + getHistoryLine(attack) + "</li>");
        }

        function getHistoryLine(attack) {
            var res = "<a onclick='historyClickedOn (" + attack.id + ")'>";
            var date = new Date(attack.firstNotificationDate);
            res += pad(date.getHours()) + ":" + pad(date.getMinutes()) + ":" + pad(date.getSeconds()) + " > ";
            res += attack.name;
            res += "</a>";
            return res;
        }

        function historyClickedOn(id) {
            for (var i = 0; i < registry.length; i++) {
                if (registry[i].id == id) {
                    drawAttack(registry[i], "Historic");
                    break;
                }
            }
        }


    </script>

</body>


</html>