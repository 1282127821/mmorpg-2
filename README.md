# mmorpg
A game server for mmorpg game based on Netty, SpringBoot, MyBatis。

## 简介

这是一个多人在线文字冒险游戏，

## 安装运行



## 游戏配置

-gameData
    ├── buffer.xlsx
    ├── gameScene.xlsx
    ├── roleProperty.xlsx
    ├── sceneObject.xlsx
    ├── shop.xlsx
    ├── skill.xlsx
    └── thingInfo.xlsx
    
 分别是角色状态、游戏场景、角色属性、场景对象（怪物和NPC）,游戏商店，角色技能，游戏物品（武器和普通物品）的配置表




## 客户端指令

客户端通过MsgId和参数来发送服务指令，MsgId(整型)部分如下
```
    // 创建用户
    USER_CREATE("user_create",900,"创建用户"),

    // 创建角色
    ROLE_CREATE("role_create",9001,"创建角色"),


    // 玩家登陆, 参数为 用户账号和密码，例： login 2 123456
    USER_LOGIN("login",1001,"玩家登陆, 参数为 用户账号和密码，例： login 2 123456"),

    // 加载游戏列表 无参数，例： list_roles
    LIST_GAME_ROLES("list_roles",1002,"加载游戏列表 无参数，例： list_roles"),

    // 角色登陆 ，参数为 当前用户下的角色id，例： 2001 1313
    PLAYER_LOGIN("load",2001," 角色登陆 ，参数为 当前用户下的角色id，例： 2001 1313"),

    // 玩家退出，无参数， 例： exit
    PLAYER_EXIT("exit",2002,"玩家退出，无参数， 例： exit"),

    // 展示背包
    SHOW_BAGS("bags",2003,"展示背包"),

    // 整理背包
    PACK_BAG("pack_bag",12003,"整理背包"),

    // 显示当前位置和相邻场景
    LOCATION("location",2004,"显示当前位置和相邻场景"),

    // 展示装备栏
    SHOW_EQUIPMENT_BAR("equip_bar",2005,"展示装备栏"),

    /** 展示玩家属性 **/
    SHOW_PLAYER("player",2006,"展示玩家属性"),

    /** 角色移动， 参数 场景id ， 例： move 2 **/
    MOVE("move",3001,"角色移动， 参数 场景id ， 例： move 2"),

    /** AOI, 显示场景内各种游戏对象 **/
    AOI("aoi",3002,"AOI, 显示场景内各种游戏对象"),

    /** 与npc谈话 **/
    TALK_WITH_NPC("talk",13002,"与npc谈话"),

    /** 查看npc任务 **/
    SHOW__NPC_QUEST("npc_quest",130021,"查看npc任务"),

    /** 使用技能 **/
    USE_SKILL("skill",13003,"使用技能"),

    /** 使用技能攻击怪物 **/
    PVE_SKILL("skill_e",3003),

    /** buffer开始 **/
    START_BUFFER("start_buffer",3004,"buffer开始"),

    /** 装备武器装备 **/
    EQUIP("equip",3005,"装备武器装备"),

    /** 使用道具物品 **/
    USE_ITEM("use_item",3006,"使用道具物品"),

    /** 角色使用普通攻击怪物 **/
    COMMON_ATTACK("attack",3007,"色使用普通攻击怪物"),

    /** 进入副本 **/
    ENTER_INSTANCE("instance",3008,"进入副本"),

    /** 退出副本 **/
    EXIT_INSTANCE("exit_instance",3009,"退出副本"),

    /** 卸下武器，放回背包 **/
    REMOVE_EQUIP("remove_equip",3010,"卸下武器，放回背包"),

    /** 普通攻击玩家 **/
    PVP("pvp",3011,"普通攻击玩家"),

    /** 技能攻击玩家 **/
    SKILL_PVP("skill_p",30012,"技能攻击玩家"),

    /** 私聊 **/
    WHISPER("whisper",4001,"私聊"),

    /** 公共聊天 **/
    PUBLIC_CHAT("public_chat",4002,"公共聊天"),

    /** 购买货物 **/
    BUY_GOODS("buy",4003,"购买货物"),

    /** 展示商店货物 **/
    SHOW_SHOP("shop",4004,"展示商店货物"),

    /** 发送邮件 **/
    SEND_MAIL("send_mail",4005,"发送邮件"),

    /** 邮件列表 **/
    MAIL_LIST("mail_list",4006,"邮件列表"),

    /** 接收邮件 **/
    GET_MAIL("get_mail",4007,"接收邮件"),

    /** 邀请组队 **/
    INVITE_TEAM("invite",4008,"邀请组队"),

    /** 加入队伍 **/
    JOIN_TEAM("join",4009,"加入队伍"),

    /** 离开队伍 **/
    LEAVE_TEAM("leave",4010,"离开队伍"),

    /** 查看队伍 **/
    TEAM_SHOW("team",4011,"查看队伍"),

    /** 团队副本 **/
    TEAM_INSTANCE("team_instance",4012,"团队副本"),


    /** 发起交易 **/
    INITIATE_TRADE("trade",4013,"发起交易"),

    /** 开始交易 **/
    BEGIN_TRADE("begin_trade",4014,"开始交易"),

    /** 交易货物 **/
    TRADE_GOODS("trade_goods",4015,"交易货物"),

    /** 交易金币 **/
    TRADE_MONEY("trade_money",4016,"交易金币"),

    /** 确定交易 **/
    CONFIRM_TRADE("trade_confirm",4017,"确定交易"),

    // 创建公会
    GUILD_CREATE("guild_create",4018,"创建公会"),

    // 查看公会
    GUILD_SHOW("guild_show",4019,"查看公会"),

    // 公会捐献
    GUILD_DONATE("guild_donate",14019,"公会捐献"),

    // 加入公会
    GUILD_JOIN("guild_join",4020,"加入公会"),

    // 授权公会成员
    GUILD_GRANT("guild_grant",4021,"授权公会成员"),

    // 获取公会物品
    GUILD_TAKE("guild_take",4022,"获取公会物品"),

    // 允许入会
    GUILD_PERMIT("guild_permit", 4023,"允许入会"),

    // 退出公会
    GUILD_QUIT("guild_quit", 4024,"退出公会"),

    /** 显示已接任务 **/
    QUEST_SHOW("quest", 4025,"显示已接任务"),

    /**  显示所有任务和成就 **/
    QUEST_ALL("quest_all",4026,"显示游戏内所有任务和成就"),

    /** 接受的任务 **/
    QUEST_ACCEPT("accept",14026,"接受任务"),

    /** 查看任务详情 **/
    QUEST_DESCRIBE("quest_describe",14027,"查看任务详情 "),

    /** 放弃任务 **/
    QUEST_GIVE_UP("quest_give_up",14028,"放弃任务"),

    FRIEND_LIST("friend_list",4027,"好友列表"),

    FRIEND_ADD("friend_add",4028,"添加好友"),

    /** 竞拍 */
    AUCTION_BID("auction_bid",4029,"竞拍"),

    /** 发布拍卖品 */
    AUCTION_PUSH("auction_push",4030,"发布拍卖品"),

    /** 查看拍卖品 */
    AUCTION_SHOW("auction_show",4031,"查看拍卖品"),
    
    /** 展示所有命令 **/
    SHOW_CMD("show_cmd",100000,"展示所有命令"),

   
   /** 未知的命令 */
    UNKNOWN("unknown", 9999,"未知的命令")
    
```