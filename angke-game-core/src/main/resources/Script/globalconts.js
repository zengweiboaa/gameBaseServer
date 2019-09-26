/**
 * 全局常量
 */
var GloBalConts = {};
///////////////////////////////////////////////////////////////////
	/** 登录状态 */
	GloBalConts.OP_PLAYERONLOGIN							= 1 << 0;
	
	/** 连接状态 */
	GloBalConts.OP_PLAYERCONNECTION						= 1 << 1;
	
	///////////////////////////////////////////////////////////////////
	/** 上线状态 */
	GloBalConts.OP_GAMEONLINE							= 1 << 2;
	
	/** 游戏状态:开放注册(可加入，可报名) */
	GloBalConts.OP_GAMEOPEN_SIGN							= 1 << 3;
	
	/** 游戏状态:可以观战 */
	GloBalConts.OP_GAME_CANVISIT							= 1 << 4;
	
	/** 暂停状态;休息状态(比赛模式可能会用到) */
	GloBalConts.OP_GAMEPAUSE								= 1 << 5;
	
	/** 游戏状态:正在游戏中 */
	GloBalConts.OP_INGAME								= 1 << 6;
	console.info(GloBalConts.OP_INGAME);
	/** 游戏已经完成,保存的时候或者出现故障恢复数据的时候可以根据这个字段决定要不要重启该局游戏，要不要持久化数据 */
	GloBalConts.OP_COMPLETE								= 1 << 7;
	
	///////////////////////////////////////////////////////////////////
	/** 游戏房间一个人也没有 */
	GloBalConts.OP_NULLPLAYER							= 1 << 8;
	/** 游戏房间已满人 */
	GloBalConts.OP_FULLPLAYER							= 1 << 9;
	
	///////////////////////////////////////////////////////////////////
	
	/** 是不是庄家 */
	GloBalConts.OP_BUTTON								= 1 << 10;
	
	///////////////////////////////////////////////////////////////////
	/** 准备状态 */
	GloBalConts.OP_READYSTART							= 1 << 11;
	/** 进入操作状态 */
	GloBalConts.OP_GAMEACTION							= 1 << 12;
	
	/** 留坐离桌:玩家在线,继续保有该游戏座位,但是类似于托管的一种状态 */
	GloBalConts.OP_SAVESEAT_LIVEROOM						= 1 << 13;
	
	/** 留坐离桌:托管状态 */
	GloBalConts.OP_AUTOACTION							= 1 << 14;
		///////////////////////////////////////////////////////////////////
		
		//跑得快状态常量
		GloBalConts.PaoDeKuaiStates = {};
		/** 当前最大的牌,当玩家拥有这个状态的时候,桌面上应该展示该玩家出的牌 */
		GloBalConts.PaoDeKuaiStates.OP_NOW_MAXCARDSTYPE		= 1 << 15;
		
		/** 可以出牌 */
		GloBalConts.PaoDeKuaiStates.OP_MOVE_CARDS			= 1 << 16;
		
		/** 可以接牌 */
		GloBalConts.PaoDeKuaiStates.OP_CALL_CARDS			= 1 << 17;
		
		/** 已经过牌,当玩家本轮过牌后,展示过牌状态 */
		GloBalConts.PaoDeKuaiStates.OP_ALREADY_PASS			= 1 << 18;
		
		/** 报牌了,只剩最后一手牌了 */
		GloBalConts.PaoDeKuaiStates.OP_LAST_ACTION			= 1 << 19;
		
