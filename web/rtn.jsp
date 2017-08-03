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

    <!-- VIS -->
    <link rel="stylesheet" href="visjs/vis.min.css">
    <script src="visjs/vis.min.js"></script>

    <!-- CSS -->
    <style>
        body {
            padding: 0;
            margin: 0;
        }

        #page {
            width: 100vw;
            height: 100vh;
            margin: 0;
        }

        #network {
            margin: 0;
            background-color: white;
            width: 100%;
            height: 100%;
        }

        canvas {
            display: inline;
        }

        #rightColumn {
            width: 500px;
            float: left;
            display: none;
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

        .infoLine {
            width: 650px;
            padding: 3px 10px;
            overflow: auto;
            color: #171717;
        }

        .item {
            float: left;
            text-align: right;
            width: 120px;
            margin-right: 20px;
            color: #ec0000;

        }
    </style>

</head>

<body>

    <div id="page">

        <!-- Canvas -->

        <div id="network">
            <div class="vis-network" tabindex="900" style="position: relative; overflow: hidden; touch-action: pan-y; user-select: none; -webkit-user-drag: none; -webkit-tap-highlight-color: rgba(0, 0, 0, 0); width: 100%; height: 100%;">
                <canvas style="position: relative; touch-action: none; user-select: none; -webkit-user-drag: none; -webkit-tap-highlight-color: rgba(0, 0, 0, 0); width: 100%; height: 100%;">Your browser does not support the required technology</canvas>
            </div>
        </div>

        <!-- Right column -->
        <div id="rightColumn">

            <!-- Title -->
            <div id="title">
                <div id="mainTitle"><span id="eve"> EVE for XS17</span></div>
                <div id="secondaryTitle"> Events Visualization Environment</div>
            </div>


            <!-- Popup -->
            <%@include file="popup/evePopup.html" %>
            <div id="infoAttack">
                <div class="infoLine">
                    <div class="item">Attack:</div>
                    <div id="attackName"></div>
                </div>
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
            </div>
        </div>

    </div>

<!--textarea id="message_data"></textarea-->
    <!-- Script -->
    <script type="text/javascript">
        var nodes;
        var edges;
        var attacks;
        var network;
        var redTeamNodes;

        $(document).ready(function () {
            $("#infoAttack").hide();
            initCanvas();
            openWebSocket();
        });


        var webSocket;
        function openWebSocket() {

            // Ensures only one connection is open at a time
            if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) return;

            <jsp:useBean id="conf" class="eve.var.Configuration" />
            webSocket = new WebSocket("ws://<%=conf.getUrlToEVE()%>/eve/ws/rt");

            webSocket.onmessage = function (message) {
                if (network) {
                    $("#network").show();
                    $("#rightColumn").hide();

                    //$("#message_data").val(message.data);

                    var data = JSON.parse(message.data);
                    if (data.attacks) {
                        data.attacks.forEach(function(attack){
                            attacks.update(attack);
                        });
                    }
                    if (data.nodes) {
                        data.nodes.forEach(drawNode)
                    }
                    if (data.edges) {
                        data.edges.forEach(drawEdge)
                    }

                    network.fit();
                }
            };
        }

        function drawNode(node) {
            if (node.type == "redTeamNode") {
                redTeamNodes.push(node.id);
            } else {
                var label = "Unknown";
                var group = "unknown";
                if (node.IPV4 && node.IPV4.length) {
                    label = node.IPV4;
                    group = undefined;
                } else if (node.IPV6 && node.IPV6.length) {
                    label = node.IPV6;
                    group = undefined;
                }

                try {
                    nodes.update({
                        id: String(node.id),
                        label: label,
                        group: group,
                    });
                }
                catch (err) {
                    console.log(err);
                }
            }
        }

        function drawEdge(edge) {
            try {
                // show all red team nodes as one big node
                var fromNode = nodes.get(edge.originID);
                var fromId;
                if (fromNode && fromNode.type != "redTeamNode") {
                    fromId = fromNode.id;
                } else {
                    fromId = "redTeamNode";
                }

                var toNode = nodes.get(edge.destinationID);
                var toId;
                if (toNode && toNode.type != "redTeamNode") {
                    toId = toNode.id;
                } else {
                    toId = "redTeamNode";
                }

                //var attackCount = countAttacks(fromId, toId);
                if (fromId != toId) {
                    edges.update({
                        id: edge.id,
                        from: fromId,
                        to: toId,
                        //label: new String(attackCount),
                    });
                }
            }
            catch (err) {
                console.log(err);
            }
        }

        function countAttacks(fromId, toId) {
            var count = 0;

            attacks.forEach(function(attack) {
                var sourceId = attack.sourceId;
                if (redTeamNodes.indexOf(sourceId) >= 0) {
                    sourceId = "redTeamNode";
                }

                var targetId = attack.targetId;
                if (redTeamNodes.indexOf(targetId) >= 0) {
                    targetId = "redTeamNode";
                }

                if (sourceId == fromId && targetId == toId) {
                    count++;
                }
            });

            return count;
        }

        function findEdgeByNodeIds(fromId, toId) {
            var foundEdge;
            if (redTeamNodes.indexOf(fromId) >= 0) {
                fromId = "redTeamNode";
            }
            if (redTeamNodes.indexOf(toId) >= 0) {
                toId = "redTeamNode";
            }
            edges.forEach(function(edge) {
                if (edge.from === fromId && edge.to === toId) {
                    foundEdge = edge;
                }
            });
            return foundEdge;
        }

        function initCanvas() {
            nodes = new vis.DataSet([
                {id: "redTeamNode", label: "Red team", color: "#ff8888", physics: false, font: "32px Arial"}
            ]);
            edges = new vis.DataSet();
            attacks = new vis.DataSet();
            redTeamNodes = new Array();

            // create a network
            var container = document.getElementById("network");
            var data = {
                nodes: nodes,
                edges: edges,
            };

            var options = {
                layout: {
                    hierarchical: {
                        enabled: true,
                        nodeSpacing: 300,
                        levelSeparation: 100,
                    }
                },
                nodes: {
                    color: "#8888ff",
                    font: "24px",
                },
                edges: {
                    arrows: "to",
                    length: 1000,
                },
                groups: {
                    unknown: {
                        color: "#999999"
                    }
                },
                interaction: {
                    tooltipDelay: 1,
                },
                "physics": false,
            };
            network = new vis.Network(container, data, options);

            network.on('selectNode', function (event) {
                if (event.nodes && event.nodes.length > 0) {
                    var id = event.nodes[0];
                    showNodeInfo(id);
                }
            });

            network.on('selectEdge', function (event) {
                // if there are more than 1 selected edges then someone selected a node,
                // and we let the 'selectNode' event handle that
                if (event.edges && event.edges.length == 1) {
                    var id = event.edges[0];
                    showEdgeInfo(id);
                }
            });

            window.onresize = function() {
                network.fit();
            }
        }

        function showNodeInfo(id) {
            var lastAttack;
            attacks.forEach(function(attack){
                var targetId = attack.targetId;
                if (redTeamNodes.indexOf(targetId) >= 0) {
                    targetId = "redTeamNode";
                }
                if (targetId == id) {
                    lastAttack = attack;
                }
            });
            if (lastAttack) {
                var positions = {xt: 0, yt: 0};
                showInfoAttack(lastAttack, positions);
            }
        }

        function showEdgeInfo(id) {
            var edge = edges.get(id);
            if (edge) {
                var lastAttack;
                attacks.forEach(function (attack) {
                    var sourceId = attack.sourceId;
                    if (redTeamNodes.indexOf(sourceId) >= 0) {
                        sourceId = "redTeamNode";
                    }

                    var targetId = attack.targetId;
                    if (redTeamNodes.indexOf(targetId) >= 0) {
                        targetId = "redTeamNode";
                    }
                    if (sourceId == edge.from && targetId == edge.to) {
                        lastAttack = attack;
                    }
                });
                if (lastAttack) {
                    var positions = {xt: 0, yt: 0};
                    showInfoAttack(lastAttack, positions);
                }
            }
        }

        function showInfoAttack(attack, positions) {
            var sourceInfo = "Unknown";
            var sourceNode = nodes.get(attack.sourceId);
            if (sourceNode) {
                sourceInfo = sourceNode.label;
            } else if (redTeamNodes.indexOf(attack.sourceId) >= 0) {
                sourceInfo = "Red team";
            }
            $("#source").text(sourceInfo);

            var targetInfo = "Unknown";
            var targetNode = nodes.get(attack.targetId);
            if (targetNode) {
                targetInfo = targetNode.label;
            } else if (redTeamNodes.indexOf(attack.targetId) >= 0) {
                targetInfo = "Red team";
            }
            $("#target").text(targetInfo);

            $("#at").html(formatDate(new Date(attack.firstNotificationDate)));
            $("#by").html(attack.probes);

            $("#reported").html(attack.totalNumberOfEvents + " time(s)");

            $("#evidence").html(attack.firstEvidence);


            var title = attack.name;
            if (title == undefined || title.trim().length == 0) title = "Undefined Attack";
            title = title.trim();
            if (title.length > 50) title = title.substr(0, 50) + "...";
            $("#attackName").text(title);

            var x = positions.xt - 200;
            if (x == undefined || x < 20) x = 20;
            var y = positions.yt + 90;
            if (y == undefined) y = 50;

            popup (title, $("#infoAttack").html(), x, y);

            $(".popup").css({
                "position" : "absolute",
                "left" : "50%",
                "top" : "50%",
                "height" : "auto",
                "margin-left" : -$(".popup").width()/2,
                "margin-top" : -$(".popup").height()/2
            });
        }
    </script>

</body>
</html>