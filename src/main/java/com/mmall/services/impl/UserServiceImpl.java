package com.mmall.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCacha;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.services.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selsectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    public ServerResponse<String> register(User user){

        ServerResponse vaildRespond = this.checkValid(user.getUsername(),Const.USERNAME);
        if (!vaildRespond.isSuccess()){
            return vaildRespond;
        }

        vaildRespond = this.checkValid(user.getEmail(),Const.EMAIL);
        if (!vaildRespond.isSuccess()){
            return vaildRespond;
        }

        vaildRespond = this.checkValid(user.getPhone(),Const.PHONE);
        if (!vaildRespond.isSuccess()){
            return vaildRespond;
        }


        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int result = userMapper.insert(user);
        if(result == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkValid(String str ,String type){
        //空字符串也返回false
        if (org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            if (Const.USERNAME.equals(type)) {
                if (userMapper.checkUsername(str)> 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            else if (Const.EMAIL.equals(type)){
                if (userMapper.checkEmail(str)> 0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
            else if (Const.PHONE.equals(type)){
                if (userMapper.checkPhone(str)> 0){
                    return ServerResponse.createByErrorMessage("手机号码已存在");
                }
            }
        }
        else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse<String> passwordQuestion(String username){
        ServerResponse validResult = this.checkValid(username,Const.USERNAME);

        if (validResult.isSuccess()){
            //用户不存在时
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.getQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)){
            //问题不为空
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("问题不存在或者为空");
    }

    public ServerResponse<String> checkQuestion(String username,String question,String answer){
        int result = userMapper.checkAnswer(username, question, answer);
        if (result>0){
            //验证成功
            String token = UUID.randomUUID().toString();
            TokenCacha.setKey(TokenCacha.TOKEN_PREFIX+username,token);
            TokenCacha.setKey("username",username);
            return ServerResponse.createBySuccess(token);
        }
        return ServerResponse.createByErrorMessage("密码答案错误");
    }


    public ServerResponse<String> resetPassword (String username,String newPassword,String paramToken){
        if (StringUtils.isBlank(paramToken)){
            return ServerResponse.createByErrorMessage("未能获取到Token");
        }
        ServerResponse result = this.checkValid(username,Const.USERNAME);
        if (result.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCacha.getKey(TokenCacha.TOKEN_PREFIX+username);
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token已过期或token无效");
        }
        if (StringUtils.equals(paramToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
            if (rowCount > 0){
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPasswordByOldPassword(String newPassword,String oldPassword,User user){
        int resultCount = userMapper.checkPassword(user.getUsername(),MD5Util.MD5EncodeUtf8(oldPassword));
        if (resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int reslut = userMapper.updateByPrimaryKeySelective(user);
        if (reslut > 0 ){
            return ServerResponse.createBySuccessMessage("密码修改成功");
        }
        return ServerResponse.createByErrorMessage("密码修改失败");
    }

    public ServerResponse<User> updateInfor(User user){
        //用户名不能更改
        //email更改后不能重复，email更改不能重复

//        ServerResponse vaildRespond = this.checkValid(user.getUsername(),Const.USERNAME);
//        if (!vaildRespond.isSuccess()){
//            return vaildRespond;
//        }

        ServerResponse vaildRespond;
//        vaildRespond = this.checkValid(user.getEmail(),Const.EMAIL);
//        if (!vaildRespond.isSuccess()){
//            return vaildRespond;
//        }

//        vaildRespond = this.checkValid(user.getPhone(),Const.PHONE);
//        if (!vaildRespond.isSuccess()){
//            return vaildRespond;
//        }
        User currentUser = new User();
        currentUser.setUsername(user.getUsername());
        currentUser.setId(user.getId());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhone(user.getPhone());
        currentUser.setQuestion(user.getQuestion());
        currentUser.setAnswer(user.getAnswer());

        int updateUser = userMapper.updateByPrimaryKeySelective(currentUser);
        if (updateUser > 0){
            return ServerResponse.createBySuccessMessage("用户信息更新成功");
        }
        return  ServerResponse.createByErrorMessage("用户信息更新失败");
    }

    public ServerResponse<User> getInformation (Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }


    public ServerResponse<String> checkAdmin(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if (user.getRole() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    public ServerResponse<PageInfo> userList(int pageNum,int pageSize){
        //只要你可以保证在 PageHelper 方法调用后紧跟 MyBatis 查询方法，这就是安全的。因为 PageHelper 在 finally 代码段中自动清除了 ThreadLocal 存储的对象。
        PageHelper.startPage(pageNum,pageSize);
        List<User> userList = userMapper.selectUser();
        PageInfo pageInfo = new PageInfo(userList);
        List<UserVo> userVoList = this.setUserVo(userList);
        pageInfo.setList(userVoList) ;
        return ServerResponse.createBySuccess(pageInfo);

    }

    public ServerResponse<PageInfo> searchUser(String username,Integer pageNum,Integer pageSize){
        String searchName = null;
        if (StringUtils.isNotBlank(username)){
            searchName="%"+username+"%";
        }
        List<User> userList = userMapper.selectUserByUsername(searchName);
        PageHelper.startPage(pageNum,pageSize);
        PageInfo pageInfo = new PageInfo(userList);
        List<UserVo> userVoList = this.setUserVo(userList);
        pageInfo.setList(userVoList) ;
        return ServerResponse.createBySuccess(pageInfo);
    }

    private List<UserVo> setUserVo(List<User>userList){
        List<UserVo> userVoList = Lists.newArrayList();
        for(User user : userList){
            UserVo userVo = new UserVo();
            userVo.setId(user.getId());
            userVo.setEmail(user.getEmail());
            userVo.setPhone(user.getPhone());
            userVo.setUsername(user.getUsername());
            userVo.setRole(user.getRole());
            userVoList.add(userVo);
        }
        return userVoList;
    }
}
