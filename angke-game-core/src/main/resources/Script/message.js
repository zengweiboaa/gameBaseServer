//登录消息
function showLoginMessageJson() {
	
	
	var ta_nickname = document.getElementById('t_nickname');
	var ta_playerid = document.getElementById('t_playerId');
	var nickname = ta_nickname.value;
	var playerId = ta_playerid.value;
	if(playerId =="" ||undefined || null){
		alert("请输入玩家id");
		return;
	} else {
		var msg = "{\"businessCode\":1000,\"playerId\":" + playerId 
		+ ",\"requestJsonStr\":\"{\\\"jinbi\\\":10000,\\\"nickname\\\":\\\"" 
		+ nickname + "\\\",\\\"playerId\\\":" + playerId + "}\"}";
		
		//document.getElementById('content').innerHTML = "<div style='width: 135px'>"+ msg +"</div>";
		document.getElementById('content').innerHTML = msg;
	}
}

//重连消息
function showReconnection() {
	var ta_playerId = document.getElementById('t_playerId').value;
	
	if (ta_playerId =="" ||undefined || null) {
		alert("请输入玩家id");
		return;
	} else {
		var msg = {"businessCode":1001,"playerId":ta_playerId,"requestJsonStr":"{\"playerId\":" + ta_playerId + "}"};
		var msgJsonStr = JSON.stringify(msg);
		document.getElementById('content').innerHTML = msgJsonStr;
	}
}

//创建游戏消息
function showCreateGameMessageJson() {
	var ta_playerid = document.getElementById('t_playerId');
	var playerId = ta_playerid.value;
	
	if (playerId =="" ||undefined || null) {
		alert("请输入玩家id");
		return;
	} else {
		var msg = "{\"businessCode\":3101,\"playerId\":" + playerId 
		+",\"requestJsonStr\":" 
		+ "\"{\\\"businessCode\\\":3101,"
		+ "\\\"optional\\\":{1:[2],2:[3],3:[10],4:[1],5:[15],6:[1],7:[-1],8:[1],9:[1],10:[10]},"
		+"\\\"gameType\\\":{\\\"game\\\":2,\\\"level\\\":0,\\\"mode\\\":3}," 
		+ "\\\"isCreateGame\\\":true,\\\"playerId\\\":" 
		+ playerId + ",\\\"subjectContext\\\":\\\"房卡游戏\\\"}\"}";
		
		document.getElementById('content').innerHTML = msg;
	}
	
}

//加入游戏
function showJoinGameMessageJson() {
	var ta_playerid = document.getElementById('t_playerId').value;
	var t_tableId = document.getElementById('t_tableId').value;
	
	if (ta_playerid =="" ||undefined || null) {
		alert("请输入玩家id");
		return;
	}
	if (t_tableId =="" ||undefined || null) {
		alert("请输入牌桌id");
		return;
	}
	
	var msg = {"businessCode":3101,"playerId":ta_playerid,
	"requestJsonStr":"{\"businessCode\":3101,\"optional\":{},\"isCreateGame\":false,\"playerId\":" 
		+ ta_playerid + ",\"tableId\":"+ t_tableId +"}"};

	var msgJsonStr = JSON.stringify(msg);
	
	document.getElementById('content').innerHTML = msgJsonStr;
}

//退出游戏
function showExitGameMessageJson() {
	
	var ta_playerid = document.getElementById('t_playerId').value;
	var ta_gameid = document.getElementById('t_gameId').value;
	var t_tableId = document.getElementById('t_tableId').value;
	var t_changeTable = document.getElementById('t_changeTable').value;
	var t_targetSeatIndex = document.getElementById('t_targetSeatIndex').value;
	
	var msg = {"businessCode":3102,
			"playerId":ta_playerid,"requestJsonStr":
				"{\"changeTable\":"
				+ t_changeTable +",\"gameId\":"
				+ ta_gameid +",\"playerId\":"
				+ ta_playerid +",\"tableId\":"+ t_tableId +",\"targetSeatIndex\":"+ t_targetSeatIndex +"}"};

	var msgJsonStr = JSON.stringify(msg);
	
	document.getElementById('content').innerHTML = msgJsonStr;
}

//准备
function showReadyStartGameMessageJson() {
	
	var ta_playerid = document.getElementById('t_playerId').value;
	var ta_gameid = document.getElementById('t_gameId').value;
	var t_tableId = document.getElementById('t_tableId').value;
	var t_isready = document.getElementById('t_isready').value;
	
	var msg = {"businessCode":3103,
			"playerId":ta_playerid,"requestJsonStr":"{\"gameId\":"
				+ ta_gameid +",\"playerId\":"
				+ ta_playerid +",\"readyStart\":"
				+ t_isready +",\"tableId\":"
				+ t_tableId +"}"};

	var msgJsonStr = JSON.stringify(msg);
	
	document.getElementById('content').innerHTML = msgJsonStr;
}