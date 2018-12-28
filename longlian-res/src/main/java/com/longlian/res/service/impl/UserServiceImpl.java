package com.longlian.res.service.impl;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huaxin.type.MResType;
import com.huaxin.type.MuserStatusEnum;
import com.huaxin.util.ActResult;
import com.huaxin.util.constant.CecurityConst;
import com.huaxin.util.constant.Const;
import com.huaxin.util.constant.RedisKey;
import com.huaxin.util.page.DatagridRequestModel;
import com.huaxin.util.page.DatagridResponseModel;
import com.huaxin.util.redis.RedisUtil;
import com.huaxin.util.security.MD5PassEncrypt;
import com.longlian.model.MRes;
import com.longlian.model.MResRel;
import com.longlian.model.MUser;
import com.longlian.res.dao.MResMapper;
import com.longlian.res.dao.MResRelMapper;
import com.longlian.res.dao.MUserMapper;
import com.longlian.res.service.ResService;
import com.longlian.res.service.ThirdUserService;
import com.longlian.res.service.UserService;
import com.longlian.token.ConsoleUserIdentity;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 系统用户服务
 *
 * @author lh
 */
@Service("userService")
public class UserServiceImpl implements UserService , ThirdUserService{
    @Autowired
    private MUserMapper userDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ResService resService;
    @Autowired
    private MResMapper mResMapper;
    @Autowired
    private MResRelMapper mResRelMapper;

    /**
     * 密码校验
     *
     * @param user
     * @param password
     * @return
     * @throws Exception
     */
    public boolean checkPassword(MUser user, String password) throws Exception {
        if (MD5PassEncrypt.checkCrypt(password, user.getPassword())) {
            return true;
        }
        return false;
    }

    /**
     * 登录方法
     */
    @Override
    @Transactional(readOnly = true)
    public ActResult<Map> login(ConsoleUserIdentity user) throws Exception {
        ActResult<Map> result = new ActResult();
        MUser muser = userDao.selectByUserId(user.getAccount(), true);
        if (muser == null) {
            result.setMsg("账号或密码错误！");
            result.setSuccess(false);
            return result;
        }
        if (!checkPassword(muser, user.getPassword())) {
            result.setMsg("账号或者密码错误！");
            result.setSuccess(false);
            return result;
        }
        if (!muser.getStatus().equals(MuserStatusEnum.muser_permit.getValue())) {
            result.setMsg("账号被禁用！");
            result.setSuccess(false);
            return result;
        }

        String token =  createToken(muser.getId());
        user.setId(muser.getId());
        user.setToken(token);
        user.setAccount(muser.getUserId() == null ? "" : muser.getUserId());
        user.setId(muser.getId());
        user.setName(muser.getName() == null ? "" : muser.getName());

        Map<String, String> map = user.toMap();
        //不是管理员
        if (!MUser.isAdmin(muser.getUserId())) {
            //取得人员有的角色
            List<MRes> roleList = resService.getRes(muser.getResId() + "", MResType.role.getType());
            String roles = this.getIds(roleList);
            map.put("role", roles);
            //取得角色下面的菜单
            List<MRes> menuList = resService.getRes(roles, MResType.menu.getType());
            map.put("menu", this.getIds(menuList));
            map.put("employeeType", "0");
        } else {
            map.put("employeeType", "1");
        }

        //放到redis
        redisUtil.hmset(RedisKey.user_login_res_prefix + muser.getId(), map, RedisKey.user_login_valid_time);

        result.setData(map);
        result.setSuccess(true);
        return result;
    }

    private String getIds(List<MRes> list) {
        StringBuffer sb = new StringBuffer();
        if (list == null) return "";
        for (MRes r : list) {
            sb.append(r.getId()).append(",");
        }
        if (list.size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }


    /**
     * 退出登录，清理掉redis
     */
    @Override
    public ActResult logout(ConsoleUserIdentity user) {
        redisUtil.del(RedisKey.user_login_res_prefix + user.getId());
        return ActResult.success();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MUser> getMUserByRole(Long roleId) {
        return userDao.getMUserByRole(roleId);
    }

    @Override
    public String getUserName(Long userId) {
        MUser u = userDao.selectByPrimaryKey(userId);
        if (u != null) {
            return u.getName();
        }
        return "";
    }

    @Override
    @Transactional(readOnly = true)
    public DatagridResponseModel getListPage(DatagridRequestModel page, MUser mUser) {
        DatagridResponseModel responseModel = new DatagridResponseModel();

        List<MUser> mUserList = userDao.getListPage(page, mUser);
        String roleName = "";
        for (MUser user : mUserList) {
            if (user.getResId() != null) {
                roleName = findRoleName(user.getResId());
                user.setIdCard(roleName);
            }

        }
        responseModel.setTotal(page.getTotal());
        responseModel.setRows(mUserList);
        return responseModel;
    }

    @Transactional(readOnly = true)
    public String findRoleName(long resId) {
        return mResRelMapper.findRoleName(resId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdate(MUser mUser) {
        if (mUser.getId() == 0) {   //添加
            try {
                mUser.setPassword(MD5PassEncrypt.crypt(Const.emp_init_password));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            long relId = mUser.getResId();         //1.获得选中的角色ID
            long resId = createMres(mUser);        //2.在Mres表中插入该用户信息,获得在该表中该人员所对应的Mres_ID
            createMresRel(resId, relId);            //3.在中间表中创建信息
            mUser.setResId(resId);                 //4.把在Mres中该人员的ID赋值给Muser中的resId
            mUser.setStatus("0");
            userDao.insert(mUser);
        } else {
            MResRel mResRel = new MResRel();
            long relId = mUser.getResId();                      //1.获得新的角色ID
            long resId = findMuserResId(mUser.getId()).getResId();    //2.获取该用户在Muser中的resId
            mResRel.setRelId(relId);
            mResRel.setResId(resId);
            mResRelMapper.updateRelId(mResRel);                 //3.在中间表中去修改数据
            mUser.setResId(0L);                                 //4.不去修改原来该用户的resId
            userDao.updateByPrimaryKeySelective(mUser);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MRes> getAllRole(String type) {
        return mResMapper.selectByType(type);
    }

    @Transactional(readOnly = true)
    public MUser findMuserResId(long id) {
        return userDao.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(String ids, String status) {
        if ("2".equals(status) && !"".equals(ids) && ids != null) {
            MUser m = userDao.selectByPrimaryKey(Long.parseLong(ids));
            mResRelMapper.deleteByRelId(m.getResId());
        }
        userDao.deleteByIds(ids, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void passwordReset(String ids) {
        String password = "";
        try {
            password = MD5PassEncrypt.crypt(Const.emp_init_password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        userDao.passwordReset(ids, password);
    }

    @Override
    @Transactional(readOnly = true)
    public MUser findUserId(String userId) {
        return userDao.findUserId(userId);
    }

    /**
     * 修改密码
     *
     * @param userId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassWord(long userId, String passWord) {
        String newPassword = null;
        try {
            newPassword = MD5PassEncrypt.crypt(passWord);
            userDao.passwordReset(userId + "", newPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public long createMres(MUser mUser) {
        MRes mRes = new MRes();
        mRes.setName(mUser.getName());
        mRes.setParentId(0L);
        mRes.setType("001");
        mRes.setSort(0);
        mRes.setStatus(0);
        mRes.setUrl("0");
        mRes.setResPic("");
        mResMapper.insert(mRes);
        return mRes.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void createMresRel(long resId, long relId) {
        MResRel mResRel = new MResRel();
        mResRel.setResId(resId);
        mResRel.setRelId(relId);
        mResRelMapper.insert(mResRel);
    }

    @Override
    @Transactional(readOnly = true)
    public MUser findById(long id) {
        MUser mUser = userDao.selectByPrimaryKey(id);
        long relId = findroleId(mUser.getResId());       //根据resId去中间表中查所对应的角色Id
        mUser.setResId(relId);                           //把角色ID赋值给Muser表中的resId
        return mUser;
    }

    @Transactional(readOnly = true)
    public long findroleId(long resId) {
        MResRel mResRel = mResRelMapper.findRelId(resId);
        if (mResRel == null) {
            return 0;
        }
        return mResRel.getRelId();
    }
    
    public String createToken(long userid) {
       return  CecurityConst.OAUTH_TOKEN_PREFIX + Jwts.builder().setSubject(userid +"").claim(
                "roles", userid).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256,
                "secretkey").compact();
    }

}