package com.wan37.gameserver.game.user.service;




import com.wan37.gameserver.manager.notification.NotificationManager;
import com.wan37.gameserver.model.User;
import com.wan37.gameserver.game.user.manager.PlayerListCacheMgr;
import com.wan37.gameserver.game.user.manager.UserCacheManger;
import com.wan37.mysql.pojo.entity.TPlayer;
import com.wan37.mysql.pojo.entity.TPlayerExample;

import com.wan37.mysql.pojo.entity.TUser;
import com.wan37.mysql.pojo.entity.TUserExample;
import com.wan37.mysql.pojo.mapper.TPlayerMapper;
import com.wan37.mysql.pojo.mapper.TUserMapper;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author gonefuture  gonefuture@qq.com
 * time 2018/9/26 18:02
 * @version 1.00
 * Description: mmorpg
 */
@Slf4j
@Service
public class UserService {

    @Resource
    private TUserMapper tUserMapper;

    @Resource
    private TPlayerMapper tPlayerMapper;


    @Resource
    private PlayerListCacheMgr playerListCacheMgr;

    @Resource
    private NotificationManager notificationManager;



    /**
     * 判断用户id和密码是否正确
     */
    public void login(Long userId, String password, ChannelHandlerContext ctx) {
        User userCache = UserCacheManger.getUserByUserId(userId);

        User user = Optional.ofNullable(userCache)
                .orElseGet(
                        () -> {
                            // 缓存中不存在用户,或者当前连接在线的不是现在要登陆的账号，如果缓存中不存在用户返回null
                            TUser tUser = tUserMapper.selectByPrimaryKey(userId);
                            return Optional.ofNullable(tUser)
                                    .map(tU -> {
                                        User u = new User();
                                        BeanUtils.copyProperties(tUser, u);
                                        return  u;
                                    }).orElse(null);
                        }
                );
        if(Objects.isNull(user)) {
            NotificationManager.notifyByCtx(ctx,"玩家id不存在");
            return ;
        }
        // 用户名或密码错误，或没注册
        if (!user.getPassword().equals(password)) {
            NotificationManager.notifyByCtx(ctx,"密码或用户名错误");
            return ;
        }

        //  替换channelId和用户缓存的联系,关闭连接
        UserCacheManger.putCtxUser(ctx,user);
        // 替换channel上下文和用户缓存的联系
        UserCacheManger.putUserIdCtx(user.getId(),ctx);
        user.setChannelHandlerContext(ctx);
        NotificationManager.notifyByCtx(ctx, MessageFormat.format(
                "用户{0}登陆成功,请发送指令 list_roles 加载角色列表",user.getName()));
    }



    /**
     *  根据用户id产找游戏角色列表
     */
    public List<TPlayer> findPlayers(Long userId) {
        return Optional.ofNullable(playerListCacheMgr.get(userId))
                .orElseGet(() -> {
                    // 通过t_player表的player_id 查找与之相关的角色
                    TPlayerExample tPlayerExample = new TPlayerExample();
                    tPlayerExample.createCriteria().andUserIdEqualTo(userId);
                    List<TPlayer> tPlayerList = tPlayerMapper.selectByExample(tPlayerExample);
                    log.debug("查出的角色列表： "+ tPlayerList);
                    // 将角色列表加入缓存
                    playerListCacheMgr.put(userId, tPlayerList);
                    return tPlayerList;
                });
    }


    /**
     *      用户注销
     * @param userId    用户id
     */
    public void logoutUser(long userId) {

        ChannelHandlerContext ctx = UserCacheManger.getCtxByUserId(userId);
        if (ctx != null) {
            UserCacheManger.removeUserByChannelId(ctx.channel().id().asLongText());
            // 关闭连接
            ctx.close();
        }

    }


    public boolean isUserOnline(ChannelHandlerContext ctx) {
        return UserCacheManger.getUserByCtx(ctx) != null;
    }


    public void register(ChannelHandlerContext ctx,String userName, String password,String phone) {
        TUser tUser = new TUser();
        tUser.setName(userName);
        tUser.setPassword(password);
        tUser.setPhone(phone);
        try {
            tUserMapper.insertSelective(tUser);
        } catch(DuplicateKeyException e) {
            NotificationManager.notifyByCtx(ctx,"用户名已经存在");
            return;
        }
        TUserExample tUserExample = new TUserExample();
        tUserExample.or().andNameEqualTo(userName);
        List<TUser> newUserList = tUserMapper.selectByExample(tUserExample);
        if (newUserList.size()> 0) {
            TUser newUser = newUserList.get(0);
            NotificationManager.notifyByCtx(ctx,MessageFormat.format("账户注册成功，你的登陆id是{0},用户名是{1}",
            newUser.getId(),newUser.getName()));
        } else {
            NotificationManager.notifyByCtx(ctx,"没有注册成功");
        }
    }

    /**
     *  通过连接上下文找到用户
     */
    public User getUserByCxt(ChannelHandlerContext ctx) {
        return UserCacheManger.getUserByCtx(ctx);
    }
}