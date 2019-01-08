package com.wan37.gameServer.game.scene.servcie;


import com.wan37.gameServer.game.sceneObject.model.Monster;

import com.wan37.gameServer.game.player.model.Player;
import com.wan37.gameServer.game.player.service.PlayerDataService;
import com.wan37.gameServer.game.sceneObject.model.NPC;
import com.wan37.gameServer.game.sceneObject.service.GameObjectService;
import com.wan37.gameServer.game.scene.model.GameScene;

import com.wan37.gameServer.game.player.manager.PlayerCacheMgr;
import com.wan37.gameServer.game.scene.manager.SceneCacheMgr;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2018/9/29 19:36
 * @version 1.00
 * Description: AOI(Area Of Interest)，中文就是感兴趣区域。通
 * 俗一点说，感兴趣区域就是玩家在场景实时看到的区域；也就是AOI会随着英雄的移动改变而改变。
 */
@Service
public class AOIService {

    @Resource
    private SceneCacheMgr sceneCacheMgr;

    @Resource
    private GameSceneService gameSceneService;

    @Resource
    private GameObjectService gameObjectService;

    @Resource
    private PlayerCacheMgr playerCacheMgr;


    @Resource
    private PlayerDataService playerDataService;

    // 获取场景内的NPC
    public Map<Long,NPC> getNPCs(GameScene gameScene) {
            return gameScene.getNpcs();

    }

    //  获取场景内怪物
    public Map<Long,Monster> getMonsters(GameScene gameScene) {
        return gameScene.getMonsters();

    }



    public List<Player> getPlayerInScene(int sceneId) {
        List<Player> playerList = new ArrayList<>();
        GameScene gameScene = sceneCacheMgr.getScene(sceneId);
        Map<Long,Player> playerMap = gameScene.getPlayers();


        for (Long playerId: playerMap.keySet())  {
            ChannelHandlerContext ctx =   playerCacheMgr.getCxtByPlayerId(playerId);
            Player player = playerDataService.getPlayer(ctx);
            playerList.add(player);
        }

        return playerList;
    }

}
