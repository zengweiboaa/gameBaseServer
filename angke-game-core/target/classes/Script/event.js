/**
 * 
 */
 
function loginByGameAccountDigLog(){
	var mainStage = document.getElementById("mainStage");
	var gameAccountDigLog = document.createElement("div");
	gameAccountDigLog.id = "login_gameAccount";
	gameAccountDigLog.style.backgroundColor = "red";
	gameAccountDigLog.style.backgroundImage = "url(../images/logindialogbg.jpg)";
	gameAccountDigLog.style.backgroundColor = "transparent";
	gameAccountDigLog.style.backgroundRepeat = "no-repeat";
	gameAccountDigLog.style.borderRadius = "15px 15px 15px 15px";
	gameAccountDigLog.style.border="3px solid #FFF";
//	gameAccountDigLog.style.backgroundSize = "100% 100%";
	mainStage.appendChild(gameAccountDigLog);
	$('#login_gameAccount').dialog({    
	    title: '游戏账户登录',
	    noheader: true,
	    border: false,
	    openAnimation: 'fade',
	    width: 400,
	    height: 200,
	    style: {backgroundColor:'transparent',padding:0,borderRadius:'15px 15px 15px 15px'},
	    inline: true,	//以父类容器来定位
	    closed: false,
	    cache: false,
	    modal: true
	});
	//$('#login_gameAccount').dialog('refresh', 'new_content.php');
	document.getElementById("")
	var digIdLabel = document.createElement("label");
	digIdLabel.innerHTML = "playerID:";
	
//	digIdLabel.id = "login_gameAccount_id";
	
//	$('#login_gameAccount').
	
}

function updateGame(fullGameJsonStr){
	var gameJsonObj = JSON.parse(fullGameJsonStr);
	alert(gameJsonObj);
	alert(gameJsonObj.toString());
	alert(gameJsonObj.gameId);
}

function updateGameTable(gameTableJsonStr){
	
	alert("updateGameTable\n" + gameTableJsonStr);
	console.info(gameTableJsonStr);
	var gameTableJsonObj = JSON.parse(gameTableJsonStr);
	
	var tableId = gameTableJsonObj.tableId;
	
	console.info(gameTableJsonObj);
	var isInGame = hasState(gameTableJsonObj.gameTableStates, GloBalConts.OP_INGAME);
	var tableScene = document.getElementById(getTableSceneId(tableId));
	
	//加载座位:
	if(gameTableJsonObj.seatTotalLength == 3){//三个座位的加载方式

        var allSeatInfo = gameTableJsonObj.allSeatInfo;

        var selfSeatIndex = 0;

        for(var i = 0; i < allSeatInfo.length; i++){
            if(allSeatInfo[i].playerId == window.globalData.self.playerId){
                selfSeatIndex = allSeatInfo[i].seatIndex;
            }
        }

		createDownSeatView(tableScene, tableId);
		//createLeftSeatView();
		//createRightSeatView();
		var initDownSeatValue = false;
        var initLeftSeatValue = false;
        var initRightSeatValue = false;



        for(var i = 0; i < allSeatInfo.length; i++){
            var seatInfo = allSeatInfo[i];
            alert("seatInfo.seatIndex - selfSeatIndex == 0 -->:" + (seatInfo.seatIndex - selfSeatIndex == 0));
        	if(seatInfo.seatIndex - selfSeatIndex == 0){
        		alert("给自己位置赋值");
                $("#" + getDownSeatValueCacheId(tableId)).val(JSON.stringify(seatInfo)).change();
                initDownSeatValue = true;
            } else if (seatInfo.seatIndex - selfSeatIndex == -1 || allSeatInfo[i].seatIndex - selfSeatIndex == 2){
                $("#" + getLeftSeatValueCacheId(tableId)).val(JSON.stringify(seatInfo)).change();
                initLeftSeatValue = true;
			} else if (seatInfo.seatIndex - selfSeatIndex == 1 || allSeatInfo[i].seatIndex - selfSeatIndex == -2){
                $("#" + getRightSeatValueCacheId(tableId)).val(JSON.stringify(seatInfo)).change();
                initRightSeatValue = true;
			}
        }

        if (!initDownSeatValue){
            $("#" + getDownSeatValueCacheId(tableId)).val("null").change();
            initDownSeatValue = true;
		}
        if (!initLeftSeatValue){
            $("#" + getLeftSeatValueCacheId(tableId)).val("null").change();
            initLeftSeatValue = true;
        }
        if (!initRightSeatValue){
            $("#" + getRightSeatValueCacheId(tableId)).val("null").change();
            initRightSeatValue = true;
        }

	}
	
	if(isInGame){//如果是在游戏中
		alert("正在游戏中");
	} else {//没在游戏中
		//创建一个准备按钮
		var readyButton = document.createElement("input");
		var readyButtonWidth = tableScene.offsetWidth * 0.15;
		readyButton.type = "button";
		readyButton.id = "" + tableId + "readybutton";//设置组件的id
		readyButton.style.border = "none";
		readyButton.style.width = "15%";
		readyButton.style.height = "7%";
		readyButton.style.backgroundImage = "url(../images/cacelready_red.png)";
		readyButton.style.backgroundColor = "transparent";
		readyButton.style.backgroundRepeat = "no-repeat";
		readyButton.style.backgroundSize = "100% 100%";
		readyButton.style.left = tableScene.offsetLeft + tableScene.offsetWidth / 2 - readyButtonWidth / 2 +"px";
		readyButton.style.top = tableScene.offsetTop + tableScene.offsetHeight / 3 + "px";
		readyButton.style.position = "absolute";
		readyButton.style.borderRadius = "5px 5px 5px 5px";
		readyButton.style.boxShadow="3px 5px 3px #000";
		readyButton.style.display = "";
		tableScene.appendChild(readyButton);
	}
}

function updateMySeatInfo(downSeatValueCacheId, mySeatInfoJsonStr){
	alert("mySeatInfo : " + mySeatInfoJsonStr);

	var mySeatInfoJsonObj = JSON.parse(mySeatInfoJsonStr);
	
}

function updateCard(cardValueCacheId, cards){
	cards = [-1, -1, -1, -1, -1, -1, 14, 17, 18, 23, 25, 35, 38, 45, 46, 47];
	var cardContainerId = getCardContainerIdByCardValueCacheId(cardValueCacheId);
	console.info(cardContainerId);
	var cardContainer = document.getElementById(cardContainerId);
	for (var i = 0; i < cards.length; i++) {
		var card = createCardDiv(cards[i], i);
		cardContainer.appendChild(card);
		if(cards[i] == -1){
			card.style.left = cardContainer.offsetWidth / 2 - card.offsetWidth / 2 - ((cards.length-1) / 2 - i) * card.offsetWidth * 0.1 + 'px';
		} else {
			card.style.left = cardContainer.offsetWidth / 2 - card.offsetWidth / 2 - ((cards.length-1) / 2 - i) * card.offsetWidth * 0.3 + 'px';
		}
	}
}

function createCardDiv(card, index){
	var cardDiv = document.createElement("div");
	cardDiv.id = "" + index;
	cardDiv.style.backgroundImage = "url(../images/poker/poker_num_" + card + ".png)";
	cardDiv.style.backgroundSize = "100% 100%";
	cardDiv.style.width = "15.5%";
	cardDiv.style.height = "100%";
	cardDiv.style.boxShadow="2px 3px 2px #000";
	cardDiv.style.position = 'absolute';
	return cardDiv;
}

function createDownSeatView(gameTableView, tableId) {
	//先吧自己的座位框子创建出来,并定位在牌桌上
	var downSeatContainer = document.createElement("div");
    downSeatContainer.id = getDownSeatContainerId(tableId);
	gameTableView.appendChild(downSeatContainer);
    downSeatContainer.style.width = "40%";
    downSeatContainer.style.height = "25%";
    var downSeatDivX = gameTableView.offsetLeft + gameTableView.offsetWidth / 2 - downSeatContainer.offsetWidth / 2;
    var downSeatDivY = gameTableView.offsetTop + gameTableView.offsetHeight / 2;
    downSeatContainer.style.left = downSeatDivX + 'px';
    downSeatContainer.style.top = downSeatDivY + 'px';
    //z-index:5;
    downSeatContainer.style.position = 'absolute';
    //downSeatContainer.style.backgroundColor = '#FFFF99';
    //为div添加样式
    //downSeatDiv.style.display = 'block';

    var downSeatValueCache = document.createElement("input");
    downSeatContainer.appendChild(downSeatValueCache);
    downSeatValueCache.id = getDownSeatValueCacheId(tableId);
    downSeatValueCache.onchange =  function(){updateMySeatInfo(this.id, this.value);};
    downSeatValueCache.type = "hidden";


	//创建放牌的区域:
	var cardContainer = document.createElement("div");
    downSeatContainer.appendChild(cardContainer);
	cardContainer.id = getCardContainerId(downSeatContainer.id);
	cardContainer.style.width = "100%";
	cardContainer.style.height = "50%";
	cardContainer.style.position = 'absolute';
    //cardContainer.style.backgroundColor = "#FF4411";

    var cardValueCache = document.createElement("input");
    cardContainer.appendChild(cardValueCache);
    cardValueCache.id = getCardValueCacheId(cardContainer.id);
    cardValueCache.type = "hidden";
    cardValueCache.onchange = function() {updateCard(this.id, this.value);};

    $("#" + getCardValueCacheId(cardContainer.id)).val("null").change();

    var seatInfo = document.createElement("div");
    downSeatContainer.appendChild(seatInfo);
    seatInfo.id = getSeatInfoId(downSeatContainer.id);
    seatInfo.style.width = "80%";
    seatInfo.style.height = "60%";
    seatInfo.style.position = 'absolute';
    seatInfo.style.backgroundImage = "" 
    	+ "url(../images/profile-state-frame.png), " 
    	+ "url(../images/tourn-lobby-logo.png), " 
    	+ "url(../images/user-name-plaque.png)";
    seatInfo.style.backgroundSize = "" 
    	+ "35% 100%, " 
    	+ "30% 80%, " 
    	+ "100% 100%";
    seatInfo.style.backgroundPosition="" 
    	+ "3% -3%, " 
    	+ "7% 40%, " 
    	+ "0% 0%"
    seatInfo.style.backgroundRepeat = "no-repeat";
    seatInfo.style.position = 'absolute';
    seatInfo.style.left = "10%";
    seatInfo.style.top = "40%";
    
    var nickname = document.createElement("a");
    seatInfo.appendChild(nickname);
    nickname.id = getNicknameId(seatInfo.id);
    nickname.style.position = 'absolute';
    nickname.className = "nickname";
    nickname.style.left = "50%";
    nickname.style.top = "30%";
    //nickname.style.color = "red";
    //nickname.style.boxShadow="2px 3px 2px #fff";
    nickname.innerHTML = "hahahah3213";
    
    
}


function readyToStartGame(gameId, tableId, playerId, isReady){
	var readyMsg = {"businessCode":3103,
	"playerId":3000,
	"requestJsonStr":
		"{\"gameId\":" + gameId + ",\"playerId\":" + playerId + ",\"readyStartGame\":"
		+ isReady +",\"tableId\":" + tableId + "}"};

	var msgJsonStr = JSON.stringify(readyMsg);
	send(msgJsonStr);
}


function getGameTopValueCacheId(tableId){
	return "" + tableId + "top_game_value";
}
function getTableSceneValueCacheId(tableId){
	return "" + tableId + "scene_table_value";
}
function getTableSceneId(tableId){
	return "" + tableId + "table_scene";
}
function getDownSeatContainerId(tableId){
	return "" + tableId + "downseatcontainer";
}
function getDownSeatValueCacheId(tableId){
	return "" + tableId + "downseatvaluecache";
}
function getLeftSeatValueCacheId(tableId){
    return "" + tableId + "leftseatvaluecache";
}
function getRightSeatValueCacheId(tableId){
    return "" + tableId + "rightseatvaluecache";
}

function getCardContainerId(seatContainerId){
	return "" + seatContainerId + "cardcontainer";
}

function getCardValueCacheId(cardContainerId){
	return "" + cardContainerId + "_cardvaluecache";
}

function getSeatInfoId(downSeatContainerId){
	return "" + downSeatContainerId + "seatinfo";
}
function getNicknameId(seatInfoId){
	return "" + seatInfoId + "nickname";
}
function getCardContainerIdByCardValueCacheId(cardValueCacheId) {
	console.info(cardValueCacheId);
	var cardContainerId = "";
    if(cardValueCacheId.indexOf("_cardvaluecache") != -1){
    	cardContainerId = cardValueCacheId.substring(0, cardValueCacheId.length - 15);
    }
	console.info(cardContainerId);
	return cardContainerId;
}
