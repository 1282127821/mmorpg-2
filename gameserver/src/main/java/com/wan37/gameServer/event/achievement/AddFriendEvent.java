package com.wan37.gameServer.event.achievement;

import com.wan37.gameServer.event.Event;
import com.wan37.gameServer.game.gameRole.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2019/1/2 9:53
 * @version 1.00
 * Description: mmorpg
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class AddFriendEvent extends Event {
    Player player;

}
