package com.brother.security;

import com.brother.common.constants.Constant;
import com.brother.membercenter.api.IMemberService;
import com.brother.membercenter.model.entity.YhMember;
import com.brother.membercenter.model.enumeration.MemberState;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.com.brother.common.exception.BaseKnownException;

/**
 * Created by Coldmoon on 2016/3/5.
 */
@Component
public class TokenRecognizer {
    @Autowired
    private IMemberService memberService;

    @Autowired
    private TokenManager tokenManager;

    public Long getMemberId(String accessToken, boolean ignoreNotLogged) {
        // 参数非空检查
        if (StringUtils.isEmpty(accessToken)) {
            if (ignoreNotLogged) {
                return null;
            } else {
                throw new BaseKnownException(
                        Constant.MEMBER_NOT_LOGGED_CODE,
                        Constant.MEMBER_NOT_LOGGED_MESSAGE
                );
            }
        }

        // 检查token是否在服务器缓存中，是则获取会员id
        Long memberId = tokenManager.verifyAccessToken(accessToken);
        if (memberId == null) {
            if (ignoreNotLogged) {
                return null;
            } else {
                throw new BaseKnownException(
                        Constant.ACCESS_TOKEN_EXPIRED_CODE,
                        Constant.MEMBER_NOT_LOGGED_MESSAGE
                );
            }
        }

        // 检查会员id是否存在
        YhMember member = memberService.getMember(memberId);
        if (member == null) {
            throw new BaseKnownException(
                    Constant.MEMBER_NOT_FOUND_CODE,
                    Constant.MEMBER_NOT_FOUND_MESSAGE
            );
        }
        // 检查会员状态是否正常
        if (member.getStatus() == MemberState.LOCKED.getValue()) {
            throw new BaseKnownException(
                    Constant.MEMBER_NOT_ACTIVE_CODE,
                    Constant.MEMBER_NOT_ACTIVE_MESSAGE
            );
        }

        // 返回会员id
        return memberId;
    }

}
