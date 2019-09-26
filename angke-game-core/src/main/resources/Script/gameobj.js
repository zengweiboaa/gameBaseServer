/**
 * 游戏牌桌
 */
//玩家
var Player = {
		newObject : function(){
			var player = {};
			player.playerId;
			player.nickname;
			player.jinbi = 10000;
			
			return player;
		}
}

var GameContainer = {
		newObject : function(gameId, gameFullMsg){
			var game = {};
			game.gameId = gameId;
			game.gameFullObj = gameFullMsg;
			return game;
		}
}

var Game = {
		newObject : function(gameId, gameConfig){
			var game = {};
			game.gameId = gameId,
			game.gameConfig = gameConfig;
			game.gameTables = [];
			
			game.addGameTables = function(table){
				game.gameTables.push(table);
			};
			
			return game;
		}
	};
	var game = Game.newObject(1111,2222);
	console.log(game.gameId);
	console.log(game.gameConfig);
	console.log(game.gameTables);
	game.addGameTables("111111")
	console.log(game.gameTables);
	console.log(game.constructor);


var GameTable = {
		newObject : function(isJoin, tableId) {
			var gameTable = {};
			gameTable.isJoin = isJoin;
			gameTable.tableId = tableId;
			gameTable.seatTotalLength = 0;
			gameTable.openView = true;
			gameTable.gameBaseSocre = 0;
			gameTable.gameTableStates = 0;
			gameTable.allSeatInfo = [];
			
			return gameTable;
		}
}


var SeatInfo = {
		newObject : function(seatIndex, effective) {
			var seatInfo = {};
			seatInfo.seatIndex = seatIndex;
			seatInfo.effective = effective;
			seatInfo.playerId = null;
			seatInfo.nickname = '';
			seatInfo.playMoneyScore = 0;
			seatInfo.playMoneyCacheScore = 0;
			seatInfo.seatState = 0;
			seatInfo.seatCardContainer = [];
			seatInfo.elementId;	//位置绑定的网页组件
			
			return seatInfo;
		}
}

