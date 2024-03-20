var socket = null;

var playerElement = null;
var playerContentElement = null;
var playerTitleElement = null;
var playerSubtitleElement = null;
var playerNameInputElement = null;
var playerNameSubmitElement = null;

var createRoomElement = null;
var createRoomTitleElement = null;
var createRoomSubtitleElement = null;
var createRoomInputElement = null;
var createRoomSubmitElement = null;
var joinRoomElement = null;
var joinRoomTitleElement = null;
var joinRoomSubtitleElement = null;
var joinRoomInputElement = null;
var joinRoomSubmitElement = null;

var currentRoomContentElement = null;
var currentRoomElement = null;
var currentRoomTitleElement = null;
var currentRoomSubtitleElement = null;
var currentRoomIdElement = null;
var currentRoomPlayersElement = null;
var currentRoomActionsElement = null;
var currentRoomStartElement = null;
var currentRoomLeaveElement = null;

var currentTableElement = null;
var currentTableMoneyElement = null;
var currentTableMoneyInputElement = null;
var currentTableMoneyValueElement = null;
var currentTableTitleElement = null;
var currentTableSubtitleElement = null;
var currentTableContentElement = null;
var currentTableFoldElement = null;
var currentTableCheckElement = null;
var currentTableCallElement = null;
var currentTableBetElement = null;
var currentTableRaiseElement = null;
var currentTableAllInElement = null;
var currentTableContinueElement = null;

function attachElements() {
    playerElement = document.getElementById("player");
    playerContentElement = document.getElementById("playerContent");
    playerTitleElement = document.getElementById("playerTitle");
    playerSubtitleElement = document.getElementById("playerSubtitle");
    playerNameInputElement = document.getElementById("playerNameInput");
    playerNameSubmitElement = document.getElementById("playerNameSubmit");
    createRoomElement = document.getElementById("createRoom");
    createRoomTitleElement = document.getElementById("createRoomTitle");
    createRoomSubtitleElement = document.getElementById("createRoomSubtitle");
    createRoomInputElement = document.getElementById("createRoomInput");
    createRoomSubmitElement = document.getElementById("createRoomSubmit");
    joinRoomElement = document.getElementById("joinRoom");
    joinRoomTitleElement = document.getElementById("joinRoomTitle");
    joinRoomSubtitleElement = document.getElementById("joinRoomSubtitle");
    joinRoomInputElement = document.getElementById("joinRoomInput");
    joinRoomSubmitElement = document.getElementById("joinRoomSubmit");
    currentRoomElement = document.getElementById("currentRoom");
    currentRoomTitleElement = document.getElementById("currentRoomTitle");
    currentRoomSubtitleElement = document.getElementById("currentRoomSubtitle");
    currentRoomContentElement = document.getElementById("currentRoomContent");
    currentRoomIdElement = document.getElementById("currentRoomId");
    currentRoomPlayersElement = document.getElementById("currentRoomPlayers");
    currentRoomActionsElement = document.getElementById("currentRoomActions");
    currentRoomStartElement = document.getElementById("currentRoomStart");
    currentRoomLeaveElement = document.getElementById("currentRoomLeave");
    currentTableElement = document.getElementById("currentTable");
    currentTableTitleElement = document.getElementById("currentTableTitle");
    currentTableSubtitleElement = document.getElementById("currentTableSubtitle");
    currentTableContentElement = document.getElementById("currentTableContent");
    currentTableMoneyElement = document.getElementById("currentTableMoney");
    currentTableMoneyInputElement = document.getElementById("currentTableMoneyInput");
    currentTableMoneyValueElement = document.getElementById("currentTableMoneyValue");
    currentTableFoldElement = document.getElementById("currentTableFold");
    currentTableCheckElement = document.getElementById("currentTableCheck");
    currentTableCallElement = document.getElementById("currentTableCall");
    currentTableBetElement = document.getElementById("currentTableBet");
    currentTableRaiseElement = document.getElementById("currentTableRaise");
    currentTableAllInElement = document.getElementById("currentTableAllIn");
    currentTableLeaveElement = document.getElementById("currentTableLeave");

    playerTitleElement.onclick = function() {  showPlayerContent(!isPlayerContentVisible); };
    currentRoomTitleElement.onclick = function() {  showRoomContent(!isRoomContentVisible); };
    playerNameSubmitElement.onclick = function() { updateName(); showPlayerContent(playerNameInputElement.value == ""); };
    createRoomSubmitElement.onclick = function() { createRoom(); };
    joinRoomSubmitElement.onclick = function() { joinRoom(); };
    currentRoomStartElement.onclick = function() { startRoom(); };
    currentRoomLeaveElement.onclick = function() { leaveRoom(); };
    currentTableMoneyInputElement.onchange = function() { currentTableMoneyValueElement.innerHTML = currentTableMoneyInputElement.value };
    currentTableMoneyInputElement.oninput = function() { currentTableMoneyValueElement.innerHTML = currentTableMoneyInputElement.value };
    currentTableFoldElement.onclick = function() { fold(); };
    currentTableCheckElement.onclick = function() { check(); };
    currentTableCallElement.onclick = function() { call(); };
    currentTableBetElement.onclick = function() { bet(); };
    currentTableRaiseElement.onclick = function() { raise(); };
    currentTableAllInElement.onclick = function() { allin(); };
    currentTableLeaveElement.onclick = function() { leaveTable(); };
};

var isPlayerContentVisible = true;


function showPlayerContent(value) {
    isPlayerContentVisible = value;
    playerContentElement.setAttribute("style", "display: " + (value ? "visible" : "none") + ";");
};

var isRoomContentVisible = true;

function showRoomContent(value) {
    isRoomContentVisible = value;
    currentRoomContentElement.setAttribute("style", "display: " + (value ? "block" : "none") + ";");
};

function connectSocket() {
    //var protocol = null;
    //if (location.protocol === 'https:') { protocol = "wss://"; } else { protocol = "ws://"; };
    var path = "$websocketServerUrl"; //protocol + window.location.host + window.location.pathname + "server/";
    console.log("Connect: \"" + path + "\"");
    socket = new WebSocket(path);
    socket.onopen = function() {
        ping();
         console.log("Success: \"" + path + "\"");
         var roomId = getParameterByName("id");
         if (roomId) {
            joinRoomInputElement.value = roomId;
            joinRoom();
         };
     };
    socket.onclose = function(event) { console.log("Close: \"" + path + "\""); };
    socket.onerror = function() { console.log("Failure: \"" + path + "\""); };
    socket.onmessage = function(event) {
    if(event.data.toString() == "pong"){
        console.log("Pong");
    } else {
        onUpdate(JSON.parse(event.data.toString()));
        };
     };
};

function ping() {
  if (!socket) return;
  if (socket.readyState !== 1) return;
  socket.send("ping");
  console.log("Ping");
  setTimeout(ping, 1000);
};

function getParameterByName(name, url = window.location.href) {
    name = name.replace(/[\[\]]/g, '\\$&');
    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
};

function onUpdate(data) {
    if (data.message != null) {
    onMessage(data.message);
    } else if (data.log != null) {
    onLog(data.log);
    } else {
    updatePlayer(data.player);
    updateCurrentRoom(data.currentRoom);
    updateTable(data.currentTable);

    var hasPlayer = data.player != null;
    var hasCurrentRoom = data.currentRoom != null;

    createRoomElement.setAttribute("style", "display: " + (hasPlayer && !hasCurrentRoom ? "block" : "none") + ";");
    joinRoomElement.setAttribute("style", "display: " + (hasPlayer && !hasCurrentRoom ? "block" : "none") + ";");
    currentRoomElement.setAttribute("style", "display: " + (hasPlayer && hasCurrentRoom ? "block" : "none") + ";");
    };
};

function onLog(message) {
    if (message != null) { console.log(message); };
};

function onMessage(message) {
    if (message != null) { alert(message); };
};

function updatePlayer(player) {
    playerNameInputElement.value = player ? player.name : "";
    playerSubtitleElement.innerHTML = escapeHtml(player ? player.name : "");
};

function updateCurrentRoom(room) {
    var players = room ? room.players : [];
    var uid = escapeHtml(room ? room.uid : "-");
    var playerSize = (players ? players.length : 0);
    var playerLimit = room ? room.playerLimit : null;
    var actions = room ? room.actions : [];
    var roomElement = document.createElement("li");
    currentRoomIdElement.innerHTML = uid;
    currentRoomSubtitleElement.innerHTML = playerSize + "/" + (playerLimit ? playerLimit.toString() : "-");
    currentRoomPlayersElement.innerHTML = "";

    currentRoomStartElement.setAttribute("style", "display: " + (actions.indexOf("Start") != -1 ? "inline-block" : "none") + ";");
    currentRoomLeaveElement.setAttribute("style", "display: " + (actions.indexOf("Leave") != -1 ? "inline-block" : "none") + ";");

    if (players != null) { for (var index = 0; index < players.length; ++index) { addCurrentRoomClient(players[index]); } };
};

function addCurrentRoomClient(player) {
            var uid = escapeHtml(player.uid);
            var name = escapeHtml(player.name);
            var playerElement = document.createElement("li");
            playerElement.innerHTML = name + " - " + uid;
            currentRoomPlayersElement.appendChild(playerElement);
};

function updateTable(table) {
      currentTableElement.setAttribute("style", "display: " + (table ? "block" : "none") + ";");
                var actions = table ? table.actions : [];
             currentTableFoldElement.setAttribute("style", "display: " + (actions.indexOf("Fold") != -1 ? "inline-block" : "none") + ";");
             currentTableCheckElement.setAttribute("style", "display: " + (actions.indexOf("Check") != -1 ? "inline-block" : "none") + ";");
             currentTableCallElement.setAttribute("style", "display: " + (actions.indexOf("Call") != -1 ? "inline-block" : "none") + ";");
             currentTableBetElement.setAttribute("style", "display: " + (actions.indexOf("Bet") != -1 ? "inline-block" : "none") + ";");
             currentTableRaiseElement.setAttribute("style", "display: " + (actions.indexOf("Raise") != -1 ? "inline-block" : "none") + ";");
             currentTableAllInElement.setAttribute("style", "display: " + (actions.indexOf("AllIn") != -1 ? "inline-block" : "none") + ";");
            currentTableLeaveElement.setAttribute("style", "display: " + (actions.indexOf("Leave") != -1 ? "inline-block" : "none") + ";");

             currentTableMoneyElement.setAttribute("style", "display: " + (actions.indexOf("Bet") != -1 || actions.indexOf("Raise") != -1 ? "inline-block" : "none") + ";");

      currentTableContentElement.innerHTML = "";
      currentTableContentElement.className = "table";
      if (table != null) {
            var currentPlayer = table.currentPlayer;
            if (currentPlayer != null) {
                var uid = escapeHtml(currentPlayer.uid);
                var name = escapeHtml(currentPlayer.name);
                var money = escapeHtml(currentPlayer.money);
                var hand = player.hand;
                var action = escapeHtml(currentPlayer.action);
                var playerElement = document.createElement("li");
                playerElement.innerHTML = "Current: "+ name;
                currentTableContentElement.appendChild(playerElement);
            };



            currentTableMoneyInputElement.min = table.highestBet * 2;
            currentTableMoneyInputElement.max = table.currentPlayer.money;
            currentTableMoneyInputElement.step = 10;
            currentTableMoneyInputElement.value = table.highestBet * 2;
             currentTableMoneyValueElement.innerHTML = currentTableMoneyInputElement.value;

            var pots = table.pots;
            if (pots != null) {
                for (var index = 0; index < pots.length; ++index) { addTablePot(pots[index]); };
            };
            var phase = table.phase;
            if (phase != null) {
                var playerElement = document.createElement("li");
                playerElement.innerHTML = "Phase: "+ escapeHtml(phase);
                currentTableContentElement.appendChild(playerElement);
            };
            var hand = table.hand;
            if (hand != null) {
                var divElement = document.createElement("div");
                divElement.className = "table-hand";
                currentTableContentElement.appendChild(divElement);
                //var playerElement = document.createElement("li");
                //playerElement.innerHTML = "Hand: "+ escapeHtml(hand);
                //currentTableContentElement.appendChild(playerElement);
                for (var index = 0; index < hand.length; ++index) { addTablePlayerHand(hand[index], divElement); };
            };
            var players = table.players;
            if (players != null) {
                for (var index = 0; index < players.length; ++index) { addTablePlayer(players[index]); };
            };
      };
};


function addTablePot(pot) {
            var amount = escapeHtml(pot.amount);
            var eligible = escapeHtml(pot.eligible);
            var winning = escapeHtml(pot.winning);
            var playerElement = document.createElement("li");
            playerElement.innerHTML = "Pot: " + amount + " - "+ eligible  + "("+ winning + ")";
            currentTableContentElement.appendChild(playerElement);
};

function addTablePlayer(player) {
            var uid = escapeHtml(player.uid);
            var name = escapeHtml(player.name);
            var money = escapeHtml(player.money);
            var betMoney = escapeHtml(player.betMoney);
            var hand = player.hand;
            var action = escapeHtml(player.action);
            var divElement = document.createElement("div");
            var playerElement = document.createElement("li");

            var tag = "";
            var tagStyle = "";
            var tags = player.tags;

            divElement.className = "player";
            if (tags != null) {
                if (arrayContains(tags, "dealer")){
                    tag = tag + "(D) ";
                    divElement.className = "player-dealer";
                }
                if (arrayContains(tags, "current")){
                    //tag = tag + "(C) ";
                    divElement.className = "player-current";
                }
                if (arrayContains(tags, "smallBlind")){
                    //tag = tag + "(S) ";
                }
                if (arrayContains(tags, "largeBlind")){
                    //tag = tag + "(L) ";
                }
                if (arrayContains(tags, "winner")){
                    //tag = tag + "(W) ";
                    divElement.className = "player-winner";
                }
            }

            playerElement.innerHTML = name + " " + tag + money + "¤" + "<br>" + action + "("+ betMoney + "¤)";
            divElement.appendChild(playerElement);
            divElement.setAttribute("style", "display: inline-block;");
            currentTableContentElement.appendChild(divElement);

            if (hand != null) {
                for (var index = 0; index < hand.length; ++index) { addTablePlayerHand(hand[index], divElement); };
            };
};

function addTablePlayerHand(hand, divElement) {
            var element = document.createElement("div");
            var data = getCard(hand.src);
            if (data != null) {
            element.innerHTML = data;
            element.children[0].setAttribute("width", "128px");
            element.children[0].setAttribute("height", "170px");
            divElement.appendChild(element.children[0]);
            };
};

function createRoom() {
    socket.send("create " + createRoomInputElement.value);
};

function joinRoom() {
     socket.send("join " + joinRoomInputElement.value);
};

function startRoom() {
    socket.send("start");
};

function leaveRoom() {
    socket.send("leave room");
};

function leaveTable() {
    socket.send("leave table");
};

function updateName() {
    socket.send("name " + playerNameInputElement.value);
};

function fold() { socket.send("fold") };
function check() { socket.send("check") };
function call() { socket.send("call " + currentTableMoneyInputElement.value) };
function bet() { socket.send("bet " + currentTableMoneyInputElement.value) };
function raise() { socket.send("raise " + currentTableMoneyInputElement.value) };
function allin() { socket.send("allin") };

function escapeHtml (unsafe_str) {
    if (unsafe_str == null) { return null; };
    return unsafe_str.toString()
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\"/g, '&quot;')
      .replace(/\'/g, '&#39;')
      .replace(/\//g, '&#x2F;');
};

function initLoop() { if (document.getElementById("currentTable")) { attachElements(); connectSocket(); } else { setTimeout(initLoop, 300); }; };

function replaceAll(str, find, replace) { return str.replace(new RegExp(find, 'g'), replace); };

function currentTime() { return replaceAll(replaceAll(new Date().toISOString(), "T", " "), "Z", " "); };

function arrayContains(array, value) { return (array.indexOf(value) > -1); };

initLoop();